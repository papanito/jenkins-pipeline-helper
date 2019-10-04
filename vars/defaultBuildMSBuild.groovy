import org.apache.commons.lang.StringUtils
/**
 * Builds the application based on parameters
 */
def call() {
    this.call(null, null)
}

/**
 * @param pipelineParams list of all pipeline parameters
 */
def call(pipelineParams) {
    this.call(pipelineParams.msbuildBuildOptions, pipelineParams.msbuildReleaseOptions)
}


 /**
 * Builds the application based on parameters.
 * @param strBuildOptions if not null, will override the default build options
 * @param stReleaseOptions if not null, will override the default release options
 */
def call(String strBuildOptions, String stReleaseOptions) {

    def solutionFile = detectBuildType.checkForSolutionFile()

    if (solutionFile == null) {
        error("Problems detecting solution file, please check log")
    }

    def branch_type = getGitBranchType "${env.BRANCH_NAME}"
    //TODO read version from assembly file
    def version = "UNKNOWN"
    //currentBuild.displayName = "${env.BRANCH_NAME}-${version}-${env.BUILD_NUMBER}"
    currentBuild.displayName = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
    
    echo "[INFO] Build started on ${env.NODE_NAME}"
    def strDebug = ""
    try {
        if ("${params.isDebug}" == "true") {
            strDebug = " /verbosity:diag "
        }
    } catch (error) {
        echo "[Warning] parameter 'isDebug' is not defined in pipeline: ${error}"
    }

    if ("${params.isRelease}" == "true") {
        this.doBranchBuild(solutionFile, "Release", strDebug, strBuildOptions)
    } else {
        if (branch_type == "master" || branch_type == "develop" ) {
           this.doBranchBuild(solutionFile, "Debug", strDebug, strBuildOptions)
        } else {
           this.doBranchBuild(solutionFile, "Debug", strDebug, strBuildOptions)
        }
    }
}


/**
 * Creates a release using the pre-defined version from the pom
 * @param strSolutionFile name of the .sln file
 * @param strConfig configuration to run
 * @param strDebug Debug option -X
 * @param strBuildOptions if not null, will override the default release options
 */
def doBranchBuild(String strSolutionFile, String strConfig, String strDebug, String strBuildOptions) {
    echo("[Info] ### Doing a Release.")
    currentBuild.displayName = "Release-${currentBuild.displayName}"
    
    if (StringUtils.isEmpty(strBuildOptions)) {
        strBuildOptions = ""
    }

    //t:Build /p:Configuration=Release 
    def msBuildExe = tool getPipelineParams().msbuild
    strBuildCmd = "\"${msBuildExe}\" ${files[0].name} /t:Build /p:Configuration=${strConfig} ${strBuildOptions} ${strDebug}"
    //currentBuild.displayName = "Release-${env.BRANCH_NAME}-${env.MVN_RELEASE_VERSION}-${env.BUILD_NUMBER}"

    runCommand(strBuildCmd)
    echo("[Info] ### TODO: Doing a Publish.")
}

return this