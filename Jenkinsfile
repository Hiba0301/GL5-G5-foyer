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
            steps { sh 'mvn test jacoco:report -Dtest=BlocServiceTest' }
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
                            sh 'terraform init'
                            sh 'terraform validate'
                            sh 'terraform apply -auto-approve'
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
}