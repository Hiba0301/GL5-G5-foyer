pipeline {
    agent any
    tools { maven 'maven3' }
    stages {
        stage('CHECKOUT GIT') {
            steps { checkout scm }
        }
        stage('PUBLISH TO NEXUS') {
            steps {
                configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -s $MAVEN_SETTINGS clean deploy -DskipTests -DaltDeploymentRepository=nexus-snapshots::default::http://nexus:8081/repository/maven-snapshots/'
                }
            }
        }
    }
}