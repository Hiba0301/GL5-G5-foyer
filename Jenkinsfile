def dockerImage

pipeline {
    agent any

    parameters {
        booleanParam(name: 'RUN_TERRAFORM', defaultValue: false, description: 'Create the EKS cluster with Terraform')
        booleanParam(name: 'DESTROY_INFRA', defaultValue: false, description: 'Destroy the EKS cluster')
    }

    environment {
        registry = 'chaymasb/foyer-bloc'
        registryCredential = 'dockerhub'
        AWS_DEFAULT_REGION = 'us-east-1'
    }

    stages {
        stage('CHECKOUT GIT') {
            steps {
                checkout scm
            }
        }

        stage('MVN CLEAN') {
            steps { sh 'mvn clean' }
        }

        stage('ARTIFACT CONSTRUCTION') {
            steps { sh 'mvn package -DskipTests' }
        }

        stage('UNIT TESTS') {
            steps { sh 'mvn test jacoco:report -Dtest="BlocServiceTest,BlocControllerTest,BlocMapperTest,AOPConfigTest"' }
        }

        stage('MVN SONARQUBE') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.host.url=http://host.docker.internal:9000 -Dsonar.token=$SONAR_TOKEN'
                }
            }
        }

        stage('PUBLISH TO NEXUS') {
            steps {
                script {
                    def groupId    = sh(script: 'mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout', returnStdout: true).trim()
                    def artifactId = sh(script: 'mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout', returnStdout: true).trim()
                    def version    = sh(script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim()

                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: 'host.docker.internal:8081',
                        groupId: groupId,
                        version: version,
                        repository: version.endsWith('SNAPSHOT') ? 'maven-snapshots' : 'maven-releases',
                        credentialsId: 'nexus-credentials',
                        artifacts: [[
                            artifactId: artifactId,
                            classifier: '',
                            file: "target/${artifactId}-${version}.jar",
                            type: 'jar'
                        ]]
                    )
                }
            }
        }

        stage('BUILDING OUR IMAGE') {
            steps {
                script { dockerImage = docker.build("${registry}:${BUILD_NUMBER}") }
            }
        }

        stage('DEPLOY OUR IMAGE') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
        }

        stage('Test AWS Credentials') {
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_CRED_FILE')]) {
                    withEnv(['AWS_SHARED_CREDENTIALS_FILE=' + AWS_CRED_FILE]) {
                        sh 'aws sts get-caller-identity'
                    }
                }
            }
        }

        stage('SETUP TERRAFORM') {
            when { expression { params.RUN_TERRAFORM } }
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_CRED_FILE')]) {
                    withEnv(['AWS_SHARED_CREDENTIALS_FILE=' + AWS_CRED_FILE]) {
                        dir('terraform') {
                            sh '''
                                set -eu
                                TF_VERSION=1.10.5
                                TF_SHA256=0566a24f5332098b15716ebc394be503f4094acba5ba529bf5eb0698ed5e2a90
                                TF_DIR="$WORKSPACE/.tools/terraform-$TF_VERSION"
                                TF_RUN_DIR="$(mktemp -d "$WORKSPACE/.terraform-run.XXXXXX")"
                                TF_ARCHIVE="$(mktemp)"
                                trap 'rm -f "$TF_ARCHIVE"; rm -rf "$TF_RUN_DIR"' EXIT

                                mkdir -p "$TF_DIR"
                                curl -fsSL "https://releases.hashicorp.com/terraform/$TF_VERSION/terraform_${TF_VERSION}_linux_amd64.zip" -o "$TF_ARCHIVE"
                                echo "$TF_SHA256  $TF_ARCHIVE" | sha256sum -c -
                                unzip -oq "$TF_ARCHIVE" -d "$TF_DIR"
                                export PATH="$TF_DIR:$PATH"
                                cp ./*.tf .terraform.lock.hcl terraform.tfvars "$TF_RUN_DIR/"
                                cd "$TF_RUN_DIR"

                                terraform version
                                terraform init -input=false
                                terraform validate

                                if ! terraform state show aws_security_group.eks_cluster_sg >/dev/null 2>&1; then
                                    if aws ec2 describe-security-groups --region us-east-1 --group-ids sg-06a7ad240e516c53b >/dev/null; then
                                        terraform import -input=false aws_security_group.eks_cluster_sg sg-06a7ad240e516c53b
                                    fi
                                fi

                                if ! terraform state show aws_eks_cluster.my_cluster >/dev/null 2>&1; then
                                    if aws eks describe-cluster --region us-east-1 --name mykubernetes >/dev/null; then
                                        terraform import -input=false aws_eks_cluster.my_cluster mykubernetes
                                    fi
                                fi

                                if ! terraform state show aws_eks_node_group.my_node_group >/dev/null 2>&1; then
                                    if aws eks describe-nodegroup --region us-east-1 --cluster-name mykubernetes --nodegroup-name noeud1 >/dev/null; then
                                        terraform import -input=false aws_eks_node_group.my_node_group mykubernetes:noeud1
                                    fi
                                fi

                                terraform plan -input=false -out=tfplan
                                terraform apply -input=false -auto-approve tfplan
                                rm -f tfplan
                            '''
                        }
                    }
                }
            }
        }

        stage('UPDATE KUBECONFIG') {
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_CRED_FILE')]) {
                    withEnv(['AWS_SHARED_CREDENTIALS_FILE=' + AWS_CRED_FILE]) {
                        sh 'aws eks update-kubeconfig --name mykubernetes --region us-east-1 --kubeconfig $WORKSPACE/kubeconfig'
                    }
                }
            }
        }

        stage('DEPLOY TO AWS KUBERNETES') {
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_CRED_FILE')]) {
                    withEnv(['AWS_SHARED_CREDENTIALS_FILE=' + AWS_CRED_FILE, 'KUBECONFIG=' + env.WORKSPACE + '/kubeconfig']) {

                        withCredentials([string(credentialsId: 'mysql-root-password', variable: 'DB_PASSWORD')]) {
                            sh 'kubectl create secret generic mysql-secret --from-literal=MYSQL_ROOT_PASSWORD=$DB_PASSWORD --dry-run=client -o yaml | kubectl apply -f -'
                        }

                        sh "sed -i 's#IMAGE_PLACEHOLDER#${registry}:${BUILD_NUMBER}#' deployment.yaml"
                        sh 'kubectl apply -f deployment.yaml'
                        sh 'kubectl apply -f service.yaml'
                        sh 'kubectl rollout status deployment/mysql --timeout=180s'
                        sh 'kubectl rollout status deployment/foyer-bloc --timeout=300s'
                        sh 'kubectl get pods -o wide'
                        sh 'kubectl get nodes -o wide'
                    }
                }
            }
        }

        stage('TEARDOWN TERRAFORM') {
            when { expression { params.DESTROY_INFRA } }
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_CRED_FILE')]) {
                    withEnv(['AWS_SHARED_CREDENTIALS_FILE=' + AWS_CRED_FILE]) {
                        dir('terraform') {
                            sh 'terraform destroy -auto-approve'
                        }
                    }
                }
            }
        }
        
    }
    post {
            success {
                mail to: 'chaima.sebri@etudiant-fst.utm.tn',
                    subject: "SUCCESS : ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "Le build #${env.BUILD_NUMBER} a réussi.\nDétails : ${env.BUILD_URL}"
            }
            failure {
                mail to: 'chaima.sebri@etudiant-fst.utm.tn',
                    subject: "FAILURE : ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "Le build #${env.BUILD_NUMBER} a échoué.\nConsultez les logs : ${env.BUILD_URL}console"
            }
        }
}