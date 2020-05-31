pipeline {
    agent none
    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }
    stages {
        stage('Platform Matrix Build') {
            matrix {
                axes {
                    axis {
                        name 'PLATFORM'
                        values 'linux', 'windows'
                    }
                }
                agent { label "${PLATFORM}" }
                options {
                    timeout(time: 1, unit: 'HOURS')
                }
                stages {
                    stage('Build Plugin') {
                        steps {
                            script {
                                // do no bother launching Gradle Daemon
                                def gradleOptions = ['--no-daemon']
                                // do not print the Welcome Gradle message. Seems pointless in CI
                                gradleOptions += "-Dorg.gradle.internal.launcher.welcomeMessageEnabled=false"
                                // more logs for troubleshooting
                                gradleOptions += "--info"
                                String command = "gradlew ${gradleOptions.join ' '} clean check assemble"
                                if (isUnix()) {
                                    command = "./" + command
                                }
                                infra.runWithJava(command, "8")
                            }
                        }
                    }
                }
            }
        }
    }
}
