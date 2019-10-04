import hudson.AbortException

/*
 * Checks and sets defaults for the pipeline. 
 * This simplifies handling of additional parameters introduced in the pipelineParams
 * @param pipelineParams list of all parameters provided by the Jenkinsfile
 */
def call(pipelineParams) {
    //assuming maven - default behaviour to not brake existing pipelines
    return this.call(pipelineParams, "maven")
}

/*
 * Checks and sets defaults for the pipeline. 
 * This simplifies handling of additional parameters introduced in the pipelineParams
 * @param pipelineParams list of all parameters provided by the Jenkinsfile
 * @param buildType initalization depends on build type
 */
def call(pipelineParams, String buildType) {
    echo("[Info] ### initializing pipeline parameter defaults ###")
    if (pipelineParams.schedule == null) {
        pipelineParams.schedule = "H 3 * * *"
    }
    if (!(env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop')) {
        pipelineParams.schedule = ''
    }
    echo("Schedule set to ${pipelineParams.schedule}")
    
    if (!pipelineParams.timeout) {
        pipelineParams.timeout = 90
    }
    echo("Timeout set to ${pipelineParams.timeout}")
   
    switch(buildType.toLowerCase()) {
        case "maven":
            return initalizePipelineMaven(pipelineParams)
            break
        case "msbuild":
            return initalizePipelineMSBuild(pipelineParams)
            break
        default:
            throw new AbortException("Build type '${buildType}' unknown")
    }
}

/*
 * Checks and sets defaults for the pipeline for Maven
 * This simplifies handling of additional parameters introduced in the pipelineParams
 */
def initalizePipelineMaven(pipelineParams) {
    echo("[Info] ### initializing pipeline parameter defaults for Maven pipelines ###")
    
    if (!pipelineParams.maven) {
        pipelineParams.maven = 'MAVEN-3.5.4'
    }
    echo("Maven set to ${pipelineParams.maven}")
    
    if (!pipelineParams.java) {
        pipelineParams.java = 'JAVA-8'
    }
    echo("Java set to ${pipelineParams.java}")

    if (!pipelineParams.skipReportStage) {
        pipelineParams.skipReportStage = false
    }
    echo("skip stage 'Report' ${pipelineParams.skipReportStage}")

    return pipelineParams
}

/*
 * Checks and sets defaults for the pipeline for MSBuild
 * This simplifies handling of additional parameters introduced in the pipelineParams
 */
def initalizePipelineMSBuild(pipelineParams) {
    echo("[Info] ### initializing pipeline parameter defaults for MSBuilds pipelines ###")
    
    if (!pipelineParams.msbuild) {
        pipelineParams.msbuild = 'MSBUILD-14.0'
    }
    echo("MsBuild set to ${pipelineParams.msbuild}")

    if (!pipelineParams.sonarscanner) {
        pipelineParams.sonarscanner = 'SonarScanner for MSBuild 4.6'
    }
    echo("Sonar scanner set to ${pipelineParams.sonarscanner}")
    return pipelineParams
}

return this