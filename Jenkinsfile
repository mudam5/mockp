pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        DOCKER_REGISTRY = "mudam5"
        IMAGE_NAME = "log-analyser"
        HOST_PORT = "8086"
        CONTAINER_PORT = "8086"   // match docker-compose.yml
        CONTAINER_NAME = "log-analyser"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'dev',
                    url: 'https://github.com/mudam5/mockp.git'
            }
        }

        stage('Set Commit Tag') {
            steps {
                script {
                    env.COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.DOCKER_TAG = env.COMMIT_ID
                    echo "✅ Docker tag set to: ${env.DOCKER_TAG}"
                }
            }
        }

        stage('Build Maven Project') {
            steps {
                dir('BACKEND/loganalyser') {   // pom.xml is here
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir("${WORKSPACE}") {   // Dockerfile is in repo root
                    sh """
                        echo "Building Docker image from repo root..."
                        docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${env.DOCKER_TAG} \
                                     -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:latest .
                    """
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${env.DOCKER_TAG}"
                    sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy to EC2 with Docker Compose') {
            steps {
                sh """
                    echo "Stopping old services..."
                    docker-compose down || true

                    echo "Updating docker-compose.yml with new image tag..."
                    sed -i 's|${DOCKER_REGISTRY}/${IMAGE_NAME}:.*|${DOCKER_REGISTRY}/${IMAGE_NAME}:${env.DOCKER_TAG}|' docker-compose.yml

                    echo "Starting services with docker-compose..."
                    docker-compose up -d
                """
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful! UI is live at http://<EC2-Public-IP>:${HOST_PORT}"
        }
        failure {
            echo "❌ Deployment failed. Check Jenkins logs."
        }
    }
}
