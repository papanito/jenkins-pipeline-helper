/**
 * Replaces occurences of a string in a given file
 *  Mandatory Parameters:
 *      input      - File in which the string shall be replaced
 *      strFind    - String to search and replace for
 *      strReplace - String to which replaces strFind
 *
 * Example:
 *     replaceStringInFile("test.xml", "one", "two")
 */
def call(String input, String strFind, String strReplace) {
    echo("Replace '" + strFind + "' with '" + strReplace + "")
    File myFile =  new File( pwd() + "\\" + input )
    String contents = myFile.getText( 'UTF-8' ) 
    contents = contents.replaceAll( strFind , strReplace )
    myFile.write(contents)
}
