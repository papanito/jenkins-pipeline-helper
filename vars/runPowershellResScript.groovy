/**
 * Runs a powershell script from a library resouce in a non-interative console
 * Source: https://stackoverflow.com/questions/42436370/executing-powershell-command-directly-in-jenkins-pipeline/42576572
 * 
 * @param script path to resource file like ch.wyssmann.common.scripts.example.ps1. Script has to exist in resource folder of the library
 * @param target target path to store resource file
 * @param arguments arguments to be passed along with the script
 * @param isAdmin When true console is run in admin mode (-Verb runAs)
 */
def call(String script, String target, String arguments, boolean isAdmin) {
    echo("Run script '${script}' with the following arguments '${arguments}'")
    def runas = ""
    if (isAdmin) {
        runas = "-Verb runAs"
    }
    def functions = libraryResource script
    def filename = script.substring(script.lastIndexOf( '/' ) + 1, script.length())
    def targetscript =  target + "\\" + filename
    echo("Script will be stored here: " + targetscript)
    writeFile file: targetscript, text: functions
    bat "powershell.exe -NonInteractive -ExecutionPolicy ${runas} Bypass -Command \"\$ErrorActionPreference='Stop';\$global:LASTEXITCODE = \$null;[Console]::OutputEncoding=[System.Text.Encoding]::UTF8;. '$targetscript' $arguments;EXIT \$global:LastExitCode\""
}