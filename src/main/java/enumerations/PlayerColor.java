package enumerations;

import exceptions.game.InexistentColorException;

/**
 * Enumeration representing all the possible colors that a player can choose to play a game
 */
public enum PlayerColor {
    YELLOW, GREEN, PURPLE, GREY, BLUE;

    /**
     * Utility method used to return the PlayerColor corresponding to the String passed.
     * In case the String has no match an exception is thrown
     *
     * @param colorChosen the String containing the needed PlayerColor
     * @return the PlayerColor corresponding to the String asked, if present
     * @throws InexistentColorException in case the String does not match any PlayerColor
     */
    public static PlayerColor getColor(String colorChosen) throws InexistentColorException {
        PlayerColor[] enumColors = values();
        for(PlayerColor color : enumColors) {
            if(color.name().equalsIgnoreCase(colorChosen)) {
                return color;
            }
        }

        throw new InexistentColorException(colorChosen);
    }
}
