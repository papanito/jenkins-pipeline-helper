/**
 * Returns the branchtype
 *
 * @param branch_name Name of the git branch
 */
def call(String branch_name) {
    //Must be specified according to <flowInitContext> configuration of jgitflow-maven-plugin in pom.xml
    def dev_pattern = ".*develop*"
    def release_pattern = ".*release/.*"
    def feature_pattern = ".*feature/.*"
    def hotfix_pattern = ".*bugfix/.*"
    def pr_pattern = ".*PR.*"
    def master_pattern = ".*master"
    if (branch_name =~ dev_pattern) {
        return "develop"
    } else if (branch_name =~ release_pattern) {
        return "release"
    } else if (branch_name =~ master_pattern) {
        return "master"
    } else if (branch_name =~ feature_pattern) {
        return "feature"
    } else if (branch_name =~ hotfix_pattern) {
        return "bugfix"
    } else if (branch_name =~ pr_pattern) {
        return "pr"
    } else {
        echo "Unknown branch name ${branch_name}"
        return "unknown";
    }
}

/**
 * Returns true if branchtype is master
 *
 * @param branch_name Name of the git branch
 */
def isMaster(String branch_name) {
    return this.call(branch_name) == "master"
}
/**
 * Returns true if branchtype is pr
 *
 * @param branch_name Name of the git branch
 */
def isPR(String branch_name) {
    return this.call(branch_name) == "pr"
}

/**
 * Returns true if branchtype is release
 *
 * @param branch_name Name of the git branch
 */
def isRelease(String branch_name) {
    return this.call(branch_name) == "release"
}

/**
 * Returns true if branchtype is develop
 *
 * @param branch_name Name of the git branch
 */
def isDevelop(String branch_name) {
    return this.call(branch_name) == "develop"
}
/**
 * Returns true if branchtype is not master, pr, develop or release
 *
 * @param branch_name Name of the git branch
 */
def isBranch(String branch_name) {
    return (this.isDevelop(branch_name) || this.isMaster(branch_name) || this.isPR(branch_name) || this.isRelease(branch_name))
}

return this