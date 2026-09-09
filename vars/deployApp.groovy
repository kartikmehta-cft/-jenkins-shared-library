def call(Map config = [:]) {

    def project = config.appName

    if (!project) {
        error "appName is required"
    }

    def deploymentConfig = readYaml(
        file: '/var/lib/jenkins/deploy-config/projects.yaml'
    )

    def appConfig = deploymentConfig[project]

    if (!appConfig) {
        error "No deployment configuration found for ${project}"
    }

    def host = appConfig.host
    def deployPath = appConfig.deployPath
    def sshCredId = appConfig.serverCredential

    echo "Application: ${project}"
    echo "Target Host: ${host}"
    echo "Deploy Path: ${deployPath}"
    echo "Credential: ${sshCredId}"

    stage('SSH Test') {

        withCredentials([
            sshUserPrivateKey(
                credentialsId: sshCredId,
                keyFileVariable: 'SSH_KEY',
                usernameVariable: 'SSH_USER'
            )
        ]) {

            sh '''
                chmod 600 "$SSH_KEY"

                ssh -o StrictHostKeyChecking=no \
                    -i "$SSH_KEY" \
                    "$SSH_USER@$TARGET_HOST" \
                    "whoami && hostname && docker ps"
            '''
        }
    }

    stage('Docker Compose Deploy') {

        withCredentials([
            sshUserPrivateKey(
                credentialsId: sshCredId,
                keyFileVariable: 'SSH_KEY',
                usernameVariable: 'SSH_USER'
            )
        ]) {

            withEnv([
                "TARGET_HOST=${host}",
                "DEPLOY_PATH=${deployPath}"
            ]) {

                sh '''
                    chmod 600 "$SSH_KEY"

                    ssh -o StrictHostKeyChecking=no \
                        -i "$SSH_KEY" \
                        "$SSH_USER@$TARGET_HOST" \
                        "cd '$DEPLOY_PATH' && docker compose up -d --build"
                '''
            }
        }
    }
}
