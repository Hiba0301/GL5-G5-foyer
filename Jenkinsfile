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
}

// DEVELOP VERSION - preserved as comments
// pipeline {
//     agent any
//
//     tools {
//         maven 'maven3'
//     }
//
//     parameters {
//         choice(name: 'AWS_ACTION', choices: ['none', 'deploy', 'destroy'],
//                description: 'none = CI seule, deploy = EKS + déploiement, destroy = tout supprimer')
//     }
//
//     triggers {
//         githubPush()
//     }
//
//     environment {
//         SPRING_DATASOURCE_URL = 'jdbc:mysql://mysql:3306/tpFoyer17?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
//         SPRING_DATASOURCE_USERNAME = 'root'
//         SPRING_DATASOURCE_PASSWORD = ''
//         AWS_REGION   = 'us-east-1'
//         CLUSTER_NAME = 'mykubernetes'
//         TF_STATE     = '/var/jenkins_home/tfstate/foyer.tfstate'
//     }
//
//     stages {
//         stage('CHECKOUT GIT') {
//             steps {
//                 checkout scm
//             }
//         }
//
//         stage('MVN CLEAN') {
//             steps {
//                 sh 'mvn clean'
//             }
//         }
//
//         stage('ARTIFACT CONSTRUCTION') {
//             steps {
//                 echo 'ARTIFACT CONSTRUCTION...'
//                 sh 'mvn package -DskipTests'
//             }
//         }
//
//         stage('UNIT TESTS') {
//             steps {
//                 echo 'Launching Unit Tests...'
//                 sh 'mvn test'
//             }
//             post {
//                 always {
//                     junit 'target/surefire-reports/*.xml'
//                 }
//             }
//         }
//
//         stage('MVN SONARQUBE') {
//             steps {
//                 withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
//                     sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar -Dsonar.host.url=http://sonar:9000 -Dsonar.login=$SONAR_TOKEN'
//                 }
//             }
//         }
//
//         stage('PUBLISH TO NEXUS') {
//             steps {
//                 sh 'mvn deploy -DskipTests -DaltDeploymentRepository=nexus-snapshots::default::http://nexus:8081/repository/maven-snapshots/'
//             }
//         }
//
//         stage('BUILDING OUR IMAGE') {
//             steps {
//                 withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_TOKEN')]) {
//                     // amd64 : les serveurs AWS (t3.medium) ne sont pas en ARM comme le Mac
//                     sh 'docker build --platform linux/amd64 -t $DOCKERHUB_USER/gl5-g5-foyer:$BUILD_NUMBER -t $DOCKERHUB_USER/gl5-g5-foyer:latest .'
//                 }
//             }
//         }
//
//         stage('DEPLOY OUR IMAGE') {
//             steps {
//                 withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_TOKEN')]) {
//                     sh '''
//                         echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
//                         docker push $DOCKERHUB_USER/gl5-g5-foyer:$BUILD_NUMBER
//                         docker push $DOCKERHUB_USER/gl5-g5-foyer:latest
//                         docker logout
//                     '''
//                 }
//             }
//         }
//
//         stage('RUN CONTAINER') {
//             steps {
//                 withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_TOKEN')]) {
//                     sh '''
//                         docker rm -f foyer-app || true
//                         docker run -d --name foyer-app --network devops -p 8082:8082 \
//                           -e SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
//                           -e SPRING_DATASOURCE_USERNAME=root \
//                           -e SPRING_DATASOURCE_PASSWORD= \
//                           $DOCKERHUB_USER/gl5-g5-foyer:$BUILD_NUMBER
//                     '''
//                 }
//             }
//         }
//
//         stage('TEST AWS CREDENTIALS') {
//             when { expression { params.AWS_ACTION != 'none' } }
//             steps {
//                 withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
//                     sh 'aws sts get-caller-identity'
//                 }
//             }
//         }
//
//         stage('SETUP TERRAFORM') {
//             when { expression { params.AWS_ACTION == 'deploy' } }
//             steps {
//                 withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
//                     dir('terraform') {
//                         sh 'mkdir -p $(dirname $TF_STATE)'
//                         sh 'terraform init -input=false -backend-config=path=$TF_STATE'
//                         sh 'terraform validate'
//                         sh 'terraform apply -auto-approve -input=false'
//                     }
//                 }
//             }
//         }
//
//         stage('UPDATE KUBECONFIG') {
//             when { expression { params.AWS_ACTION == 'deploy' } }
//             steps {
//                 withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
//                     sh 'aws eks update-kubeconfig --name $CLUSTER_NAME --region $AWS_REGION --kubeconfig $WORKSPACE/kubeconfig'
//                 }
//             }
//         }
//
//         stage('DEPLOY TO AWS KUBERNETES') {
//             when { expression { params.AWS_ACTION == 'deploy' } }
//             steps {
//                 withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
//                     sh '''
//                         export KUBECONFIG=$WORKSPACE/kubeconfig
//                         kubectl get nodes -o wide
//                         kubectl apply -f deployment.yaml
//                         kubectl apply -f service.yaml
//                         kubectl rollout restart deployment/foyer-app
//                         kubectl rollout status deployment/foyer-app --timeout=300s
//                         kubectl get pods,svc -o wide
//                     '''
//                 }
//             }
//         }
//
//         stage('TEARDOWN TERRAFORM') {
//             when { expression { params.AWS_ACTION == 'destroy' } }
//             steps {
//                 withCredentials([file(credentialsId: 'aws-credentials', variable: 'AWS_SHARED_CREDENTIALS_FILE')]) {
//                     dir('terraform') {
//                         sh 'terraform init -input=false -backend-config=path=$TF_STATE'
//                         sh 'terraform destroy -auto-approve -input=false'
//                     }
//                 }
//             }
//         }
//     }
//
//     post {
//         success {
//             emailext to: 'hibachaari2004@gmail.com',
//                 subject: "✅ ${env.JOB_NAME} #${env.BUILD_NUMBER} réussi (${params.AWS_ACTION})",
//                 body: "Build réussi.<br>Détails : <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a>"
//         }
//         failure {
//             emailext to: 'hibachaari2004@gmail.com',
//                 subject: "❌ ${env.JOB_NAME} #${env.BUILD_NUMBER} en échec",
//                 body: "Le build a échoué.<br>Console : <a href='${env.BUILD_URL}console'>${env.BUILD_URL}console</a>",
//                 attachLog: true
//         }
//     }
// }
