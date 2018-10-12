import org.apache.commons.lang.StringUtils
/**
 * Shared ci pipeline for maven projects
 * Source: https://jenkins.io/blog/2017/10/02/pipeline-templates-with-shared-libraries/
 */
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    //Set and override some default parameters
    if (!pipelineParams.schedule) {
        pipelineParams.schedule = "H 2 * * *"
    }
    echo "Schedule set to ${pipelineParams.schedule}"
    
    if (!pipelineParams.timeout) {
        pipelineParams.timeout = 45
    }
    echo "Timeout set to ${pipelineParams.timeout}"
    
    if (!pipelineParams.maven) {
        pipelineParams.maven = 'MAVEN-3.5.4'
    }
    echo "Maven set to ${pipelineParams.maven}"
    
    if (!pipelineParams.java) {
        pipelineParams.java = 'JAVA-8'
    }
    echo "Java set to ${pipelineParams.java}"

    pipeline {
        agent {
            label pipelineParams.nodes
        }
        environment {
            String credential = 'user'
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
            maven pipelineParams.maven
            java pipelineParams.java
        }
        parameters {
            booleanParam(defaultValue: false, description: 'This is a Release build', name: 'isRelease')
            booleanParam(defaultValue: false, description: 'Shows debug information', name: 'isDebug')
        }
        stages {
            //step which handels an array parameter
            stage("Array Parameter") { 
                steps {
                    script {
                        if (pipelineParams.exampleList) {
                            echo "[Info] ### exampleList set to ${pipelineParams.exampleList} ###"
                            if (ArrayList.isInstance(pipelineParams.exampleList)){
                                (pipelineParams.exampleList).each {
                                    echo "Item: ${it}"
                                }
                            } else {
                                echo "Item: ${pipelineParams.exampleList}"
                            } 
                        }
                    }
                }
            }
        }
        post {
            always {
                echo "DONE"
            }
            failure {
                //notifyBuildStatus "FAILURE", pipelineParams.email
            }
            unstable {
                //notifyBuildStatus "UNSTABLE", pipelineParams.email
            }
        }
    }
}

return this