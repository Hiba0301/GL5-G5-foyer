pipeline {
    agent any

    tools {
        maven 'maven3'
    }

    environment {
        SPRING_DATASOURCE_URL = 'jdbc:mysql://mysql:3306/tpFoyer17?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
        SPRING_DATASOURCE_USERNAME = 'root'
        SPRING_DATASOURCE_PASSWORD = ''
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
                    sh 'docker build -t $DOCKERHUB_USER/gl5-g5-foyer:$BUILD_NUMBER -t $DOCKERHUB_USER/gl5-g5-foyer:latest .'
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
    }
}