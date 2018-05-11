/**
 * Runs a powershell command or script
 * Source: //https://stackoverflow.com/questions/42436370/executing-powershell-command-directly-in-jenkins-pipeline/42576572
 *  Mandatory Parameters:
 *      psCmd      - Command or powershell script
 *      isElevated - If true, script will run with elevated privileges (-Verb runAs)
 *
 * Example:
 *     runPowershell("Write-Hoste 'test output'", true)
 *     runPowershell(". './test.ps1' -param1 test", true)
 */
def call(String psCmd, boolean isElevated) {
    runAs = ""
    if (isElevated) {
      runAs = "-Verb runAs"
    }
    psCmd=psCmd.replaceAll("%", "%%")
    bat "powershell.exe -NonInteractive -ExecutionPolicy " + runAs + " Bypass -Command \"\$ErrorActionPreference='Stop';[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;$psCmd;EXIT \$global:LastExitCode\""
}
