def call(Map config = [:]) {
    echo "Backend deployment started"
    echo "Application: ${config.appName}"
    echo "Server: ${config.server}"
    echo "Path: ${config.deployPath}"
}
#comment: This function is responsible for deploying the backend application. It takes a configuration map as an argument and prints out the deployment details such as application name, server, and deployment path.