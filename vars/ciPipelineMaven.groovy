/**
 * Shared ci pipeline for maven projects
 * Source: https://jenkins.io/blog/2017/10/02/pipeline-templates-with-shared-libraries/
 */
def call(body) {
    // evaluate the body block, and collect configuration into the object
    pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipelineParams = initializePipeline(pipelineParams)

    pipeline {
        agent {
            label pipelineParams.nodes
        }
        triggers {
            cron(pipelineParams.schedule)
        }
        options {
            /*
            - daysToKeepStr: history is only kept up to this days.
            - numToKeepStr: only this number of build logs are kept.
            - artifactDaysToKeepStr: artifacts are only kept up to this days.
            - artifactNumToKeepStr: only this number of builds have their artifacts kept.
            */
            buildDiscarder(logRotator(artifactNumToKeepStr: '1', numToKeepStr: '10'))
            disableConcurrentBuilds()
            timestamps()
            timeout(time: pipelineParams.timeout, unit: 'MINUTES')
        }
        tools {
            maven "${pipelineParams.maven}"
            jdk  "${pipelineParams.java}"
        }
        parameters {
            booleanParam(defaultValue: false, description: 'This is a Release build', name: 'isRelease')
            booleanParam(defaultValue: false, description: 'Enables debug information in the log', name: 'isDebug')
            booleanParam(defaultValue: false, description: 'Removes wyssmann parent pom from local maven repo and forces a re-download', name: 'cleanupParentPom')
            booleanParam(defaultValue: false, description: 'Force to run stage "report"', name: 'forceReport')
            string(defaultValue: '', description: 'Override the release version, keep empty if use version number from pom', name: 'releaseVersion')
            string(defaultValue: '', description: 'Override the development version, is only used if releaseVersion is not empty', name: 'developmentVersion')
        }

        stages {
            stage('Build') {
                steps {
                    showInfo()
                    defaultBuildMaven(
                        pipelineParams.mavenBuildOptions,
                        pipelineParams.mavenReleaseOptions
                    )
                }
            }
            stage('initiate QA') {
                steps {
                    runQualityChecks("${pipelineParams.strIqApp}")
                }
            }
            stage("Report") {
                when {
                    anyOf {
                        allOf {
                            branch 'master'
                            expression {
                                getTriggerCause.isTime()
                            }
                            expression {
                                "${pipelineParams.skipReportStage}" != "true"
                            }
                        }
                        expression {
                            "${params.forceReport}" == "true"
                        }
                    }
                }
                steps {
                    defaultRunReport()
                }
            }
        }
        post {
            failure {
                notifyBuildStatus "FAILURE", pipelineParams.email
            }
            unstable {
                notifyBuildStatus "UNSTABLE", pipelineParams.email
            }
        }
    }
}

return this