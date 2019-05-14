package enumerations;

import exceptions.game.InexistentColorException;

public enum RoomColor {
    RED, YELLOW, GREEN, PURPLE, GREY, BLUE;

    public static RoomColor getColor(String colorChosen) throws InexistentColorException {
        RoomColor[] enumColors = values();
        for(RoomColor color : enumColors) {
            if(color.name().equalsIgnoreCase(colorChosen)) {
                return color;
            }
        }

        throw new InexistentColorException(colorChosen);
    }
}
