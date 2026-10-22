pipeline {
    agent any

    tools {
        maven 'maven3'
    }

    parameters {
        choice(name: 'AWS_ACTION', choices: ['none', 'deploy', 'destroy'],
               description: 'none = CI seule, deploy = EKS + déploiement, destroy = tout supprimer')
    }
    triggers {
            githubPush()
        }

    environment {
        SPRING_DATASOURCE_URL = 'jdbc:mysql://mysql:3306/tpFoyer17?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
        SPRING_DATASOURCE_USERNAME = 'root'
        SPRING_DATASOURCE_PASSWORD = ''
        AWS_REGION   = 'us-east-1'
        CLUSTER_NAME = 'mykubernetes'
        TF_STATE     = '/var/jenkins_home/tfstate/foyer.tfstate'
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
                echo 'ARTIFACT CONSTRUCTION...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('UNIT TESTS') {
            steps {
                echo 'Launching Unit Tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('MVN SONARQUBE') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar -Dsonar.host.url=http://sonar:9000 -Dsonar.login=$SONAR_TOKEN'
                }
            }
        }

        stage('PUBLISH TO NEXUS') {
            steps {
                sh 'mvn deploy -DskipTests -DaltDeploymentRepository=nexus-snapshots::default::http://nexus:8081/repository/maven-snapshots/'
            }
        }

        stage('BUILDING OUR IMAGE') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    // amd64 : les serveurs AWS (t3.medium) ne sont pas en ARM comme le Mac
                    sh 'docker build --platform linux/amd64 -t $DOCKERHUB_USER/gl5-g5-foyer:$BUILD_NUMBER -t $DOCKERHUB_USER/gl5-g5-foyer:latest .
                     # Image frontend Angular
                                    docker build --platform linux/amd64 \
                                      -t "$DOCKERHUB_USER/foyer-front:$BUILD_NUMBER" \
                                      -t "$DOCKERHUB_USER/foyer-front:latest" \
                                      ./frontend
                    '
                }
            }
        }

        stage('DEPLOY OUR IMAGE') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    sh '''
                        echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
                        docker push $DOCKERHUB_USER/gl5-g5-foyer:$BUILD_NUMBER
                        docker push $DOCKERHUB_USER/gl5-g5-foyer:latest

                         # Publier le frontend
                                        docker push "$DOCKERHUB_USER/foyer-front:$BUILD_NUMBER"
                                        docker push "$DOCKERHUB_USER/foyer-front:latest"
                        docker logout
                    '''
                }
            }
        }

        stage('RUN CONTAINER') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    sh '''
                        docker rm -f foyer-app || true
                        docker run -d --name foyer-app --network devops -p 8082:8082 \
                          -e SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
                          -e SPRING_DATASOURCE_USERNAME=root \
                          -e SPRING_DATASOURCE_PASSWORD= \
                          $DOCKERHUB_USER/gl5-g5-foyer:$BUILD_NUMBER
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
                withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
                    sh '''
                         export KUBECONFIG=$WORKSPACE/kubeconfig

                                            kubectl get nodes -o wide

                                            # Appliquer les Deployments et Services
                                            kubectl apply -f deployment.yaml
                                            kubectl apply -f service.yaml

                                            # Utiliser les images produites par ce build
                                            kubectl set image deployment/foyer-app \
                                              foyer-app=$DOCKERHUB_USER/gl5-g5-foyer:$BUILD_NUMBER

                                            kubectl set image deployment/foyer-front \
                                              foyer-front=$DOCKERHUB_USER/foyer-front:$BUILD_NUMBER

                                            # Attendre le démarrage des applications
                                            kubectl rollout status deployment/foyer-app --timeout=300s
                                            kubectl rollout status deployment/foyer-front --timeout=300s
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
                emailext to: 'bettaherchayma77@gmail.com', subject: "SUCCESS : ${env.JOB_NAME} #${env.BUILD_NUMBER}", body: "Build reussi.\nDetails : ${env.BUILD_URL}"
            }
            failure {
                emailext to: 'bettaherchayma77@gmail.com', subject: "FAILURE : ${env.JOB_NAME} #${env.BUILD_NUMBER}", body: "Build en echec.\nConsole : ${env.BUILD_URL}console", attachLog: true
            }
        }
}