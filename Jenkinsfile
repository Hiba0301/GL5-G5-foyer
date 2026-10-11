def dockerImage

pipeline {
    agent any

    environment {
        registry = 'chaymasb/foyer-bloc'
        registryCredential = 'dockerhub'
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
    }
}