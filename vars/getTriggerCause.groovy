/**
* Checks for cause of the job trigger and returns respective cause
* @return user, scm, time or other
*/
def String call() {
    echo "CAUSE ${currentBuild.rawBuild.getCauses().properties}"
    /*
     * http://javadoc.jenkins-ci.org/hudson/model/Cause.html
     * https://stackoverflow.com/questions/42790966/how-to-call-a-groovy-function-from-a-jenkinsfile?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    */
    def cause = "${currentBuild.rawBuild.getCauses()}"
     if (cause =~ "UserIdCause") {
         return "user"
    } else if ((cause =~ "BranchEventCause") || (cause =~ "SCMTriggerCause")) {
         return "scm"
    } else if (cause =~ "TimerTriggerCause") {
        return "time"
    } else {
        return "other"
    }
}

/**
* Checks if trigger cause of the job is the timer
* @return true if trigger is timer
*/
def boolean isTime() {
    return this.call() == "time"
}

return this
