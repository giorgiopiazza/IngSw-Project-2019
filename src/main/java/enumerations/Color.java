package enumerations;

import exceptions.game.InexistentColorException;

public enum Color {
    RED, YELLOW, GREEN, PURPLE, GREY, BLUE;

    public static Color getColor(String colorChosen) throws InexistentColorException {
        Color[] enumColors = values();
        for(Color color : enumColors) {
            if(color.name().equalsIgnoreCase(colorChosen)) {
                return color;
            }
        }

        throw new InexistentColorException(colorChosen);
    }
}
