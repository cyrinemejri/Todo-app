pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "nassimayadi/todo-app"
        VERSION = "${new Date().format('yyyyMMdd-HHmm')}"
    }

    tools {
        maven 'Maven-3.9.6'
    }

    stages {

        stage('Build Maven') {
            steps {
                bat 'mvn clean install'
            }
        }

        stage('Exécuter les tests') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Construire l’image Docker') {
          // Utilise un conteneur Docker client qui monte le socket du host
              steps {
                script {
                  // Récupère le user du shell (ex. "jenkins")
                  //def buildUser = sh(script: 'echo $USER', returnStdout: true).trim()
        
                  // Build de l’image avec --build-arg USER=<jenkins>
                  bat """
                    docker build \
                      -t ${DOCKER_IMAGE}:${VERSION} \
                      .
                  """
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
                    bat "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
                }
            }
        }

        stage('Pousser l’image sur Docker Hub') {
            steps {
                script {
                    docker.withRegistry('', 'docker-hub-credentials') {
                        bat "docker tag ${DOCKER_IMAGE}:${VERSION} ${DOCKER_IMAGE}:latest"
                        bat "docker push ${DOCKER_IMAGE}:${VERSION}"
                        bat "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }

        stage('Déployer sur Kubernetes') {
               steps {
                   script {
                       bat 'kubectl apply -f deployment.yaml'
                       bat 'kubectl apply -f service.yaml'
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
