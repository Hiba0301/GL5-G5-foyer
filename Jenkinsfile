def dockerImage

pipeline {
    agent any

    environment {
        registry = 'chaymasb/foyer-bloc'
        registryCredential = 'dockerhub'
    }

    stages {
        stage('MVN CLEAN') {
            steps { sh 'mvn clean' }
        }

        stage('ARTIFACT CONSTRUCTION') {
            steps { sh 'mvn package -DskipTests' }
        }

        stage('UNIT TESTS') {
            steps { sh 'mvn test -Dtest=BlocServiceTest' }
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
    }
}