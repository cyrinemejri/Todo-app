pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "cyrinemejri/todo-app"
        VERSION = "${new Date().format('yyyyMMdd-HHmm')}"
    }

    tools {
        maven 'Maven-3.9.6'
    }

    stages {

        stage('Build Maven') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Exécuter les tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Construire l’image Docker') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${VERSION}", "--build-arg USER=${USER} .")
                }
            }
        }

        stage('Se connecter à Docker Hub') {
            steps {
                withCredentials([usernamePassword(

                    credentialsId: 'docker-hub-credentials', 

                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                }
            }
        }

        stage('Pousser l’image sur Docker Hub') {
            steps {
                script {
                    docker.withRegistry('', 'docker-hub-credentials') {
                        sh "docker tag ${DOCKER_IMAGE}:${VERSION} ${DOCKER_IMAGE}:latest"
                        sh "docker push ${DOCKER_IMAGE}:${VERSION}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline terminé avec succès. L’image est publiée sur Docker Hub."
        }
        failure {
            echo "Échec du pipeline."
        }
    }
}
