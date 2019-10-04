import org.apache.commons.lang.StringUtils
import groovy.transform.Field
@Field errorUnableToDetectBuildType = "[ERROR] Unable to autmatically detect build type. Set this by providing env.buildType variable in the pipeline"
/*
 * Helper class to detect whether it is a maven, msbuild, or whatever project
 */
def call() {
    echo "[INFO] Build type is: " + (this.isMaven() ? "Maven" : "not Maven")
}

/*
 * returns true if project is a maven project
 */
def isMaven() {
    currentBuildType = env.buildType
    echo "[INFO] isMaven(): Current build type is ${currentBuildType}"
    if ((currentBuildType == "") || (currentBuildType == null)) {
        try {
            echo "[INFO] Trying to detect build type"
            if (fileExists('pom.xml')) {
                env.buildType = "maven"
                return true
            } 
            return false
        } catch(err) {
            echo errorUnableToDetectBuildType
            throw err
        }
    } else {
        return (currentBuildType.toLowerCase() == "maven")
    }
}


/*
 * returns true if project is a msbuild project
 */
def isMsBuild() {
    currentBuildType = env.buildType
    echo "[INFO] isMsBuild(): Current build type is ${currentBuildType}"
    if ((currentBuildType == "") || (currentBuildType == null)) {
        echo "[INFO] Trying to detect build type"
        if (checkForSolutionFile()) {
            env.buildType = "msbuild"
            return true
        }
        return false
    } else {
        return (currentBuildType.toLowerCase() == "msbuild")
    }
}


/*
 * checks whether a .sln file is present and if not, fails the pipeline
 * @return filename of the .sln file
 */
def checkForSolutionFile() {
    echo("[Info] ### Checking for `*.sln` files")
    files = findFiles(glob: '*.sln')
    switch (files.length) {
        case 0:
            echo "No `*.sln` file found, checking for `*.csproj` files"
            files = findFiles(glob: '*.csproj')
            switch (files.length) {
                case 0:
                    echo('[WARNING] Nothing to build, check that your project has *.sln or *.csproj files')
                    return null
                    break
                case 1:
                    echo "1 `*.csproj` file found"
                    break
                default:
                    echo('[WARNING] Multiple *.csproj files found so not clear what to build')
                    return null
                    break
            }
            //error('Nothing to build, check that your project has a *.sln file')
            break
        case 1:
            echo "1 `*.sln` file found"
            break
        default:
            currentBuild.result = 'ABORTED'
            echo('[WARNING] Multiple *.sln files found so not clear what to build')
            return null
            break
    }
    solutionFile = files[0].name
    getPipelineParams.addValue("sln", solutionFile[0..-5])
    return files[0].name
}

return this