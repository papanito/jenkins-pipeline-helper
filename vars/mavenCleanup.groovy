/**
 * Cleans (deletes) an artifact from the local maven repo
 * @param groupId artifact group id
 * @param artifactId id of artifact
 */
def call(String groupId, String artifactId) {

    echo "### [INFO] Remove ${groupId}:${artifactId} from local maven repo"

    try {
        //isDebug allows to debug the deploy stage without actually deplying
        if (!params.isDebug) {
            runCommand("mvn dependency:purge-local-repository -DmanualInclude='${groupId}:${artifactId}'")
        }
    } catch (error) {
        currentBuild.result = 'FAILURE'
        echo "[ERROR] Unable to delete ${groupId}.${artifactId}:\n ${error}"
    }
}