/**
 * Prints information about the environment
 */
def call() {
    if (isUnix()) {
        sh "printenv"
    } else {
        powershell returnStatus: false, script: '''
                $ErrorActionPreference = "Stop"
                Write-Output "[DEBUG] ### Environment:"
                Get-ChildItem Env:
            '''
    }
}