/**
 * Reads a resource file and stores it to the current pwd
 * 
 * @param script path to resource file like ch/wyssmann/common/scripts/example.ps1. Script has to exist in resource folder of the library
 * @param target target path to store resource file
 */
def call(String script, String target) {
    echo "Write '" + script + "' to '" + target + "'"
    def functions = libraryResource script
    def filename = script.substring(script.lastIndexOf( '/' ) + 1, script.length())
    writeFile file: target + "/" + filename, text: functions
}