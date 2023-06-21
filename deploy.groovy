pipeline{
    agent {
         label 'master'
    }
    /*
    tools {
         maven 'maven 3.6'
         jdk 'java'
    }
    */
    environment {
        // This can be nexus3 or nexus2
        NEXUS_VERSION = "nexus3"
        // This can be http or https
        NEXUS_PROTOCOL = "http"
        // Where your Nexus is running. 'nexus-3' is defined in the docker-compose file
        NEXUS_URL = "172.25.109.16:8081"
        // Repository where we will upload the artifact
        NEXUS_REPOSITORY = "maven-releases"
        // Jenkins credential id to authenticate to Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexus_jenkins"
        
        // Workfolder
        //WORKFOLDER = "/usr/jenkins/node_agent/workspace"
    }

    stages{
        stage('Checkout'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'github_ldgnu', url: 'git@github.com:ldgnu/holamundo.git']]])
            }
        }
        stage('Download artifact from nexus'){
            agent {
                label 'dockers'
            }
            steps{
                sh '''
                    pwd 
                    curl -v -u admin:Hola1234 -o app.jar http://172.25.109.16:8081/repository/maven-public/org/springframework/jb-hello-world-maven/0.2.1/jb-hello-world-maven-0.2.1.jar
                '''
            }
        }
        stage('Build container'){
            agent {
                label 'dockers'
            }
            steps{
                sh '''
                    docker build -t holamundo .
                '''

            }
        } //fin stage build container
        stage('Deploy container'){
            agent {
                label 'dockers'
            }
            steps{
                sh '''
                    docker run --name holamundo -p 8080:80 holamundo
                '''

            }
        } //fin stage build container
        
        stage("Post") {
            agent {
                label 'dockers'
            }
            steps {
                sh '''
                    pwd
                    echo "Clean up workfolder"
                    rm -Rf *
                '''
            }
        } //fin stage post
        
    }
}
