/*
 * Runs a specific command with the correct call i.e. sh on Linux, bat on Windows
 * @param command command to be execute
 */
def call(String command) {
    if (isUnix()) {
        sh command
    } else {
        bat command
    }
}

/*
 * Runs a specific command with the correct call i.e. sh on Linux, bat on Windows.
 * The output can be returned in case it is needed for further processing
 * @param command command to be execute
 * @param isReturnStdout Whether to return the output (true) or simply print it in the console
 */
def call(String command, boolean isReturnStdout) {
    if (isUnix()) {
        sh(returnStdout: isReturnStdout, script: command).trim()
    } else {
        bat(returnStdout: isReturnStdout, script: command).trim()
    }
}