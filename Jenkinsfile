pipeline {
    agent none
    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }
    stages {
        stage('BuildAndTest') {
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
                    stage('Build') {
                        steps {
                            script {
                                String command = "gradlew --no-daemon clean assemble"
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
