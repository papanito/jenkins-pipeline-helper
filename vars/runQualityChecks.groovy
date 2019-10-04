import org.apache.commons.lang.StringUtils

/**
 * Calls all quality checks related stages
 * This can be used as an abstraction within the pipeline to avoid that teams disable single stages
 * @param pipelineParams
 */
def call(pipelineParams) {
    this.call("", pipelineParams.strIqApp)
}

/**
 * Calls all quality checks related stages
 * This can be used as an abstraction within the pipeline to avoid that teams disable single stages
 * @param language Code language which sonarQube shall scan
 * @param iqapp IQ application identifier 
 */
def call(String language, String iqapp) {
    if ((language == "null") || (language == null)) {
        language = ""
    }

    echo "[INFO] This stage initates the quality checks i.e. SonarQube and Nexus IQ scan"
    stage('Sonar Analysis') {
        withSonarQubeEnv('Sonar') {
            if (detectBuildType.isMaven()) {
                this.runSonarQubeAnalysis("${language}")
            } else if (detectBuildType.isMsBuild()) {
                this.runSonarQubeAnalysisMsBuild("${language}")
            } else {
                this.runSonarQubeAnalysisGeneric("${language}")
            }
        }
    }

    stage("SonarQube Quality Gate") {
        //Workaround https://stackoverflow.com/questions/45693418/sonarqube-quality-gate-not-sending-webhook-to-jenkins
        sleep 20
        withSonarQubeEnv('Sonar') {
            this.checkQualityGate()
        }
    }
    stage("Nexus IQ Scan") {
        this.runNexusIQScan("${iqapp}")
    }
}
/**
 * Checks the quality gate and prints an error if not OK
 */
def checkQualityGate() {
    script {
        echo "[INFO] Perform QualityGate check with recent SonarQube Analysis"
        def retry = 0
        def MAXRETRY = 5
        def qualityGate
        while (retry <= MAXRETRY) {
            try{
                timeout(time: 30, unit: 'SECONDS') {
                    echo "[INFO] wait for quality gate"
                    qualityGate = waitForQualityGate()
                }
                break
            } catch (err) {
                if (retry < MAXRETRY) {
                    echo "Quality gate status could not be determined: retry #${retry + 1}"
                } else {
                    echo "Quality gate status could not be determined: inform team but do not fail stage"
                    return //skip checkQualityGateStatus() in case quality gate cannot be determined
                }
            } finally {
                retry = retry+1
            }
        }
        this.checkQualityGateStatus(qualityGate.status)
    }
}

/**
 * Checks the status of Quality gate and fails pipeline if necessary
 * @status QGate status
 */
def checkQualityGateStatus(String status) {
    //try to write status into property
    try {
        writeFile file: sonarFile, text: "build.id=${env.BUILD_ID}\nbuild.branch=${env.BRANCH_NAME}\nsonar.status=${status}"
    } catch(err) {
        echo "[INFO] Problem writing file '$sonarFile': ${err}"
        writeFile file: sonarFile, text: ""
    }
    if (status != "OK") {
        error "Pipeline aborted due to quality gate coverage failure: ${status}"
    } else {
        echo "Quality gate check passed: ${status}"
    }
}

/**
 * Scans the current projects with NexusIQ
 * @param iqApp target IQ application for results
 */
def runNexusIQScan(String iqApp) {
    echo "[INFO] Scan dependencies/artifacts with Nexus IQ"
    
    // if iqApp is not specified pipeline shall not fail but only be unstable
    if (!iqApp || "${iqApp}" == "null") {
        echo "[WARNING] Missing specification of Nexus-IQ-app, please specify one as future builds may fail."
        currentBuild.result = "UNSTABLE"
        return
    }
    def branch_type = this.getGitBranchType("${env.BRANCH_NAME}")

    /*
     * https://help.sonatype.com/iqserver/policy-management/understanding-the-parts-of-a-policy
     * Stages are defined according to the internal process
     */
    switch (branch_type) {
        case "master":
            iqStage = "stage-release"
            break
        case "release":
            iqStage = "release"
            break
        case "pr":
            iqStage = "build"
            break
        default:
            // CLI scans made with the Develop stage won't show up in the dashboard or the reporting view.
            // therefore sancs from feature and bugfix branches are put in it
            iqStage = "develop"
            break
    }
    echo "Nexus IQ Application: '${iqApp}' - stage '${iqStage}'"
    nexusPolicyEvaluation failBuildOnNetworkError: true, iqApplication: "${iqApp}", iqStage: "${iqStage}", jobCredentialsId: ''
    writeFile file: nexusFile, text: "build.id=${env.BUILD_ID}\nbuild.branch=${env.BRANCH_NAME}\niq.app=${iqApp}'\niq.stage=${iqStage}"
}

/**
 * Runs SonarQube Analysis for maven
 * @param language Language of the code to be scanned
 */
def runSonarQubeAnalysis(String language) {
    script {
        echo "[INFO] Scan code with SonarQube (Maven) for branch ${env.BRANCH_NAME}"
        String scanCmd = "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.5.0.1254:sonar " +
                "-f pom.xml " +
                "-Dsonar.branch=${env.BRANCH_NAME} " +
                "-Dsonar.test.inclusions=**/*Test*/** " +
                "-Dsonar.exclusions=**/*Test*/** " +
                "-Dsonar.skipDesign=true "

        //force language to be scanned
        if (!StringUtils.isEmpty(language)) {
            scanCmd = scanCmd + " -Dsonar.language=${language} "
        }
        runCommand(scanCmd)
    }
}

/**
 * Runs SonarQube Analysis for msBuild
 * @param language Language of the code to be scanned
 */
def runSonarQubeAnalysisMsBuild(String language) {
    script {
        echo "[INFO] Scan code with SonarQube (MSBuild) for branch ${env.BRANCH_NAME}"

        //force language to be scanned
        if (!StringUtils.isEmpty(language)) {
            //scanCmd = scanCmd + " -Dsonar.language=${language} "
            scanCmd = ""
        }

        strDebug = ""
        if (params.isDebug) {
            strDebug = "/d:sonar.verbose=true"
        }

        def key =  getPipelineParams().sln

        //https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-jenkins/#header-5
        def sqScannerMsBuildHome = tool getPipelineParams().sonarscanner
        iPipelineMavenr-scanner.bat\""rMsBuildHome}\\SonarQube.MSBuild.exe\""
        iPipelineMavenr-scanner.bat\""
        iPipelineMavenr-scanner.bat\""rMsBuildHome}\\SonarQube.Scanner.MSBuild.exe\""
        iPipelineMavenr-scanner.bat\""
        def msBuildExe = tool getPipelineParams().msbuild
        bat "${sqScanner} begin /k:${key} ${strDebug}"
        bat "\"${msBuildExe}\" /t:Rebuild"
        bat "${sqScanner} end"
    }
}

/**
 * Runs SonarQube Analysis for generic builds
 * @param language Language of the code to be scanned
 */
def runSonarQubeAnalysisGeneric(String language) {
    script {
        echo "[INFO] Scan code with SonarQube (generic) for branch ${env.BRANCH_NAME}"

        //force language to be scanned
        if (!StringUtils.isEmpty(language)) {
            //scanCmd = scanCmd + " -Dsonar.language=${language} "
            scanCmd = ""
        }

        strDebug = ""
        if (params.isDebug) {
            strDebug = " -X"
        }

        //https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/
        def sqScannerMsBuildHome = tool "SonarQube Scanner 4.0"

     ciPipelineMavenr-scanner\""
     ciPipelineMaven
     ciPipelineMavenr-scanner.bat\""

return this