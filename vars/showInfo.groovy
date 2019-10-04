/*
 * Prints all parameters in the Jenkins console log. In addition, add also add additional pom
 * info in case the build is run in debug mode
 */
def call(pipelineParams) {

    echo("[Info] ### Show all pipeline parameters ###")
    for (paremeterItem in params) {
        echo("[Info] ### ParameterItem: ${paremeterItem} ###")
    }
    
    for (paremeterItem in pipelineParams) {
        echo("[Info] ### PipelineParameterItem: ${paremeterItem} ###")
    }

    echo("[Info] ### Show all environment variables ###")
    try {
        showEnvironment()

        //include below code will not fail the pipeline if showInfo() is not within node context. 
        //The following exception will happen in case isMaven() is called outside node context
        //org.jenkinsci.plugins.workflow.steps.MissingContextVariableException: Required context class hudson.FilePath is missing
        //Perhaps you forgot to surround the code with a step that provides this, such as: dockerNode, node, dockerNode
        if (detectBuildType.isMaven() && "${params.isDebug}" == "true") {
            echo("[Info] ### Start effective POM ###")
            runCommand("mvn help:effective-pom -e")
            echo("[Info] ### End effective POM ###")
            
            echo("[Info] ### Start effectve Settings ###")
            runCommand("mvn help:effective-settings")
            echo("[Info] ### End effective Settings ###")
            
            echo("[Info] ### Start effectve Settings ###")
            runCommand("mvn help:all-profiles")
            echo("[Info] ### End effective Settings ###")
            
            echo("[Info] ### Start System Settings ###")
            runCommand("mvn help:system")
            echo("[Info] ### End System Settings ###")

            echo("[Info] ### Start Show Dependency tree ###")
            runCommand("mvn dependency:tree")
            echo("[Info] ### End Show Dependency tree ###")
        }
    } catch (ex) {
        echo "[WARNING] The method 'showInfo()' shall be called within a 'node' element in the pipeline"
    }    

}