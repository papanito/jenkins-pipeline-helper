/**
 * Notify about build status
 * @param buildStatus status of job e.g. UNSTABLE or FAILED
 * @param toMail e-mail address of recepients e.g. team email
 */
def call(String buildStatus, String toMail) {
    echo "[Info] Start notification of build status over different channels"
    if (buildStatus == "FAILURE") {
        notifyFailure(toMail)
    }
    if (buildStatus == "SUCCESS") {
        notifySuccess(toMail)
    }
    if (buildStatus == "UNSTABLE") {
        notifyUnstable(toMail)
    }
}

/**
 * Notify about build status "FAILURE"
 * @param toMail e-mail address of recepients e.g. team email
 */
def notifyFailure(String toMail) {
    this.notifyMail("FAILURE", toMail, "The build has failed.", true)
}

/**
 * Notify about build status "UNSTABLE"
 * @param toMail e-mail address of recepients e.g. team email
 */
def notifyUnstable(String toMail) {
    //this.notifyMail("UNSTABLE", toMail, "The build is currently unstable.", false)
    echo "[WARNING] Until we get stable builds, notifications for 'UNSATBLE' are disabled"
}

/**
 * Notify about build status "SUCCESS"
 * @param toMail e-mail address of recepients e.g. team email
 */
def notifySuccess(String toMail) {
    this.notifyMail("SUCCESS", toMail, "The build has successfully passed.", false)
}

/**
 * Notify about build status by mail
 * @param buildStatus status of job e.g. UNSTABLE or FAILED
 * @param message additional message to appear in email body
 * @param toMail e-mail address of recepients e.g. team email
* @param bAttachLog true if log file shall be attached
 */
def notifyMail(String buildStatus, String toMail, String message, boolean bAttachLog) {
    echo "Send e-mail notification"

    //skip mail notification to team for branches
    def branch_type = this.getGitBranchType("${env.BRANCH_NAME}")
    if (!(
        (branch_type  == "master") || 
        (branch_type  == "pr") || 
        (branch_type  == "dev") || 
        (branch_type  == "release"))) {
        toMail = ""
    }

    emailext (
        subject: "[JENKINS-CI] ${buildStatus}: ${env.JOB_NAME}",
        body: """
${message}
Job URL:
${env.BUILD_URL}
""",
        to: toMail,
        recipientProviders: [[$class: 'CulpritsRecipientProvider'],[$class: 'RequesterRecipientProvider']],
        attachLog: bAttachLog
    )
}

return this