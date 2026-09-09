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
        sshagent([sshCredId]) {
            sh """
                ssh -o StrictHostKeyChecking=no \
                deployer@${host} \
                'whoami && hostname && docker ps'
            """
        }
    }

    stage('Docker Compose Deploy') {
        sshagent([sshCredId]) {
            sh """
                ssh -o StrictHostKeyChecking=no \
                deployer@${host} \
                'cd ${deployPath} && docker compose up -d --build'
            """
        }
    }
}
