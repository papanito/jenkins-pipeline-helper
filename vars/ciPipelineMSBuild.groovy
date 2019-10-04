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

    this.pipelineParams = initializePipeline(pipelineParams, "msbuild")
    env.pipelineParams = pipelineParams

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
        parameters {
            booleanParam(defaultValue: false, description: 'This is a Release build', name: 'isRelease')
            booleanParam(defaultValue: false, description: 'Enables debug information in the log', name: 'isDebug')
        }
        stages {
            stage('Build') {
                steps {
                    showInfo()
                    defaultBuildMSBuild(pipelineParams)
                }
            }
            stage('initiate QA') {
                steps {
                    runQualityChecks(pipelineParams)
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