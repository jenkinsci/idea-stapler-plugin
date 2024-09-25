// do no bother launching Gradle Daemon
def gradleOptions = ['--no-daemon']
// do not print the Welcome Gradle message. Seems pointless in CI
gradleOptions += "-Dorg.gradle.internal.launcher.welcomeMessageEnabled=false"
// more logs for troubleshooting
gradleOptions += "--info"
// tell gradle-intellij-plugin not to download sources
def extraEnv = ["CI=true"]

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
                                String command = "gradlew ${gradleOptions.join ' '} check assemble"
                                if (isUnix()) {
                                    command = "./${command}"
                                }
                                infra.runWithJava(command, "17", extraEnv)
                            }
                        }
                        post {
                            always {
                                junit('**/build/test-results/**/*.xml')
                            }
                        }
                    }
                    stage('Verify Plugin') {
                        steps {
                            script {
                                String command = "gradlew ${gradleOptions.join ' '} verifyPlugin"
                                if (isUnix()) {
                                    command = "./" + command
                                }
                                infra.runWithJava(command, "17", extraEnv)
                            }
                            archiveArtifacts artifacts: '**/build/reports/pluginVerifier/**', fingerprint: false
                            // Look for presence of compatibility warnings or problems
                            sh "./script/check-plugin-verification.sh"
                        }
                        post {
                            always {
                                discoverGitReferenceBuild()
                                recordIssues(tool: java(pattern: 'build/javac.log'),
                                        sourceCodeEncoding: 'UTF-8',
                                        skipBlames: true)
                                recordIssues(tool: taskScanner(
                                            includePattern: '**/*.java',
                                            excludePattern: '**/build/**,gradle/**,.gradle/**',
                                            highTags: 'FIXME',
                                            normalTags: 'TODO'),
                                        sourceCodeEncoding: 'UTF-8',
                                        skipBlames: true)

                            }
                        }
                        when {
                            expression {
                                return isUnix()
                            }
                        }
                    }
                }
            }
        }
    }
}
