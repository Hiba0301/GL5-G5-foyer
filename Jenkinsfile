pipeline {
    agent any

    tools {
        maven 'maven3'
    }

    parameters {
        choice(name: 'AWS_ACTION', choices: ['none', 'deploy', 'destroy'],
               description: 'none = CI only, deploy = EKS + Kubernetes deployment, destroy = delete the AWS infrastructure')
    }

    triggers {
        githubPush()
    }

    environment {
        MAVEN_ARGS = '-B -ntp'

        // ---- Module identity (everything below is derived from it) ----
        MODULE        = 'chambre'
        SONAR_KEY     = 'tpFoyer-17'

        // ---- Docker Hub ----
        REGISTRY            = 'meriemmejri/tpfoyer'
        REGISTRY_CREDENTIAL = 'dockerhub-credentials'

        // ---- Kubernetes (APP_DEPLOYMENT must match the name in deployment.yaml) ----
        K8S_NAMESPACE  = 'chambre'
        APP_DEPLOYMENT = 'foyer-chambre'

        // ---- AWS / Terraform ----
        AWS_REGION   = 'us-east-1'
        CLUSTER_NAME = 'mykubernetes'
        TF_STATE     = '/var/jenkins_home/tfstate/foyer.tfstate'

        // ---- Notifications ----
        NOTIFY_EMAIL = 'meriem.mejri@etudiant-fst.utm.tn'
    }

    stages {
        stage('CHECKOUT GIT') {
            steps {
                checkout scm
            }
        }

        stage('MVN CLEAN') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('ARTIFACT CONSTRUCTION') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('UNIT TESTS') {
            steps {
                // Only the Chambre test classes (with and without Mockito)
                sh 'mvn test jacoco:report -Dtest="Chambre*Test"'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('MVN SONARQUBE') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                            -Dsonar.host.url=http://sonar:9000 \
                            -Dsonar.projectKey=$SONAR_KEY \
                            -Dsonar.projectName=$SONAR_KEY \
                            -Dsonar.token=$SONAR_TOKEN
                    '''
                }
            }
        }

        stage('PUBLISH TO NEXUS') {
            steps {
                withCredentials([file(credentialsId: 'maven-settings-file', variable: 'MAVEN_SETTINGS')]) {
                    sh '''
                        mvn -s "$MAVEN_SETTINGS" deploy -DskipTests \
                            -DaltDeploymentRepository=nexus-snapshots::default::http://nexus:8081/repository/maven-snapshots/
                    '''
                }
            }
        }

        stage('BUILDING OUR IMAGE') {
            steps {
                // amd64: AWS nodes are not ARM
                sh '''
                    docker build --platform linux/amd64 \
                        -t $REGISTRY:$MODULE-$BUILD_NUMBER \
                        -t $REGISTRY:$MODULE .
                '''
            }
        }

        stage('DEPLOY OUR IMAGE') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${REGISTRY_CREDENTIAL}",
                                                  usernameVariable: 'DOCKERHUB_USER',
                                                  passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    sh '''
                        echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
                        docker push $REGISTRY:$MODULE-$BUILD_NUMBER
                        docker push $REGISTRY:$MODULE
                        docker logout
                    '''
                }
            }
        }

        stage('TEST AWS CREDENTIALS') {
            when { expression { params.AWS_ACTION != 'none' } }
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
                    sh 'aws sts get-caller-identity'
                }
            }
        }

        stage('SETUP TERRAFORM') {
            when { expression { params.AWS_ACTION == 'deploy' } }
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
                    dir('terraform') {
                        sh 'mkdir -p $(dirname $TF_STATE)'
                        sh 'terraform init -input=false -backend-config=path=$TF_STATE'
                        sh 'terraform validate'
                        sh 'terraform apply -auto-approve -input=false'
                    }
                }
            }
        }

        stage('UPDATE KUBECONFIG') {
            when { expression { params.AWS_ACTION == 'deploy' } }
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
                    sh 'aws eks update-kubeconfig --name $CLUSTER_NAME --region $AWS_REGION --kubeconfig $WORKSPACE/kubeconfig'
                }
            }
        }

        stage('DEPLOY TO AWS KUBERNETES') {
            when { expression { params.AWS_ACTION == 'deploy' } }
            steps {
                withCredentials([
                    file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE'),
                    string(credentialsId: 'mysql-root-password', variable: 'DB_PASSWORD')
                ]) {
                    sh '''
                        export KUBECONFIG=$WORKSPACE/kubeconfig

                        kubectl create namespace $K8S_NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

                        kubectl -n $K8S_NAMESPACE create secret generic mysql-secret \
                            --from-literal=MYSQL_ROOT_PASSWORD=$DB_PASSWORD \
                            --dry-run=client -o yaml | kubectl apply -f -

                        sed -i "s#IMAGE_PLACEHOLDER#$REGISTRY:$MODULE-$BUILD_NUMBER#" deployment.yaml

                        kubectl -n $K8S_NAMESPACE apply -f deployment.yaml
                        kubectl -n $K8S_NAMESPACE apply -f service.yaml
                        kubectl -n $K8S_NAMESPACE rollout status deployment/mysql --timeout=180s
                        kubectl -n $K8S_NAMESPACE rollout status deployment/$APP_DEPLOYMENT --timeout=300s
                        kubectl -n $K8S_NAMESPACE get pods,svc -o wide
                        kubectl get nodes -o wide
                    '''
                }
            }
        }

        stage('TEARDOWN TERRAFORM') {
            when { expression { params.AWS_ACTION == 'destroy' } }
            steps {
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
                    dir('terraform') {
                        sh 'terraform init -input=false -backend-config=path=$TF_STATE'
                        sh 'terraform destroy -auto-approve -input=false'
                    }
                }
            }
        }
    }

    post {
        success {
            emailext to: "${env.NOTIFY_EMAIL}",
                recipientProviders: [developers(), requestor()],
                mimeType: 'text/html',
                subject: "SUCCESS | ${env.JOB_NAME} #${env.BUILD_NUMBER} (${params.AWS_ACTION})",
                body: """
                    <h2 style="color:#2e7d32;">Build succeeded</h2>
                    <table cellpadding="6" style="border-collapse:collapse;">
                      <tr><td><b>Job</b></td><td>${env.JOB_NAME}</td></tr>
                      <tr><td><b>Build</b></td><td>#${env.BUILD_NUMBER}</td></tr>
                      <tr><td><b>Module</b></td><td>${env.MODULE}</td></tr>
                      <tr><td><b>AWS action</b></td><td>${params.AWS_ACTION}</td></tr>
                      <tr><td><b>Docker image</b></td><td>${env.REGISTRY}:${env.MODULE}-${env.BUILD_NUMBER}</td></tr>
                      <tr><td><b>Duration</b></td><td>${currentBuild.durationString}</td></tr>
                    </table>
                    <p><a href="${env.BUILD_URL}">Open the build</a></p>
                """
        }
        failure {
            emailext to: "${env.NOTIFY_EMAIL}",
                recipientProviders: [developers(), requestor()],
                mimeType: 'text/html',
                subject: "FAILED | ${env.JOB_NAME} #${env.BUILD_NUMBER} (${params.AWS_ACTION})",
                attachLog: true,
                body: """
                    <h2 style="color:#c62828;">Build failed</h2>
                    <table cellpadding="6" style="border-collapse:collapse;">
                      <tr><td><b>Job</b></td><td>${env.JOB_NAME}</td></tr>
                      <tr><td><b>Build</b></td><td>#${env.BUILD_NUMBER}</td></tr>
                      <tr><td><b>AWS action</b></td><td>${params.AWS_ACTION}</td></tr>
                      <tr><td><b>Duration</b></td><td>${currentBuild.durationString}</td></tr>
                    </table>
                    <p><a href="${env.BUILD_URL}console">Open the console output</a></p>
                    <p>The full log is attached.</p>
                """
        }
    }
}