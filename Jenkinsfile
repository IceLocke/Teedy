pipeline {
   agent any

   environment {
      DOCKER_HUB_CREDENTIALS = credentials('dockerhub_credentials')
      DOCKER_IMAGE = 'icelocke/teedy-app'
      DOCKER_TAG = "${env.BUILD_NUMBER}"
      DEPLOYMENT_NAME = "hello-node"
      // hello-node-c56464b79-glpbt
      CONTAINER_NAME = "teedy"
      IMAGE_NAME = 'icelocke/teedy-app'
   }

   stages {
      stage('Build') {
         steps {
            checkout scmGit(
            branches: [[name: '*/master']],
            extensions: [],
            userRemoteConfigs: [[url: 'https://github.com/IceLocke/Teedy.git']]
            )
            sh 'mvn -B -DskipTests clean package'
         }
      }

      // Building Docker images
      stage('Building image') {
         steps {
            script {
            // assume Dockerfile locate at root
            docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
            }
         }
      }

   //  // Uploading Docker images into Docker Hub
   //  stage('Upload image') {
   //  steps {
   //  script {
   //  // sign in Docker Hub
   //  docker.withRegistry('https://registry.hub.docker.com', 'dockerhub_credentials') {
   //  // push image
   // docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push()

   // // ：optional: label latest
   // docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").push('latest')
   //  }
   //  }
   //  }
   //  }

   // Running Docker container
   stage('Run containers') {
      steps {
         script {
            // stop then remove containers if exists
            sh 'docker stop teedy-container-8081 || true'
            sh 'docker rm teedy-container-8081 || true'
            // run Container
            docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run(
            '--name teedy-container-8081 -d -p 8081:8080'
            )
            // Optional: list all teedy-containers
            sh 'docker ps --filter "name=teedy-container"'
         }
      }
   }
   stage('Start Minikube') {
      steps {
         sh '''
         if ! minikube status | grep -q "Running"; then
         echo "Starting Minikube..."
         minikube start
         else
         echo "Minikube already running."
         fi
         '''
      }
   }
   stage('Set Image') {
      steps {
         sh '''
         echo "Setting image for deployment..."
         kubectl set image deployments/${DEPLOYMENT_NAME} ${CONTAINER_NAME}=${IMAGE_NAME}:${DOCKER_TAG}
         '''
      }
   }
   stage('Verify') {
      steps {
         sh 'kubectl rollout status deployment/${DEPLOYMENT_NAME}'
         sh 'kubectl get pods'
         }
   }
}
}