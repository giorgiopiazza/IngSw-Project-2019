package enumerations;

import exceptions.game.InexistentColorException;

public enum PlayerColor {
    YELLOW, GREEN, PURPLE, GREY, BLUE;

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
