package utility;

public class CommandUtility {
    /**
     * Utility method to obtain the position of the section needed in the array of string obtained by splitting
     * the original command on blanks
     *
     * @param splitCommand the array of String in which a command is divided
     * @param section the String whose position you need
     * @return the position of the Section in the split Command. If the section is missing -1 is returned
     */
    public static int getCommandSplitPosition(String[] splitCommand, String section) {
        for(int i = 0; i < splitCommand.length; ++i) {
            if(splitCommand[i] == section) {
                return i;
            }
        }
        return -1;
    }
}
