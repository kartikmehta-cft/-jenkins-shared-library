def call(Map config = [:]) {

    echo "Backend deployment started"
    echo "Application: ${config.appName}"

    deployApp(
        appName: config.appName
    )
}
