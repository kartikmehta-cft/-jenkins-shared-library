def call(Map config = [:]) {
    echo "Frontend deployment started"
    echo "Application: ${config.appName}"
    echo "Server: ${config.server}"
    echo "Path: ${config.deployPath}"
}
