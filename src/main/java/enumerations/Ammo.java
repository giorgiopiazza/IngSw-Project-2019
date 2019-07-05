package enumerations;

import exceptions.game.InexistentColorException;

/**
 * Enumeration representing the Ammo a player can use to pay his actions
 */
public enum Ammo {
    RED, YELLOW, BLUE;

    /**
     * Returns the corresponding {@link RoomColor RoomColor} of the current Ammo
     *
     * @param ammo the Ammo whose roomColor is needed
     * @return the corresponding RoomColor
     */
    public static RoomColor toColor(Ammo ammo) {
        if(ammo.toString().equals(RoomColor.RED.toString())) {
            return RoomColor.RED;
        }

        if(ammo.toString().equals(RoomColor.YELLOW.toString())) {
            return RoomColor.YELLOW;
        }

        if(ammo.toString().equals(RoomColor.BLUE.toString())) {
            return RoomColor.BLUE;
        }

        throw new NullPointerException("An ammo has always a color to be returned!");
    }

    /**
     * Returns the corresponding RoomColor of an Ammo from a String if present!
     *
     * @param colorChosen the String representing the chosen color
     * @return the {@link RoomColor RoomColor} corresponding to the Ammo with colorChosen name
     * @throws InexistentColorException in case the String passed does not have a corresponding room color
     */
    public static RoomColor getColor(String colorChosen) throws InexistentColorException {
        Ammo[] enumColors = values();
        for(Ammo color : enumColors) {
            switch (color) {
                case RED:
                    if(color.toString().equalsIgnoreCase(colorChosen)) {
                        return RoomColor.RED;
                    }
                    break;
                case YELLOW:
                    if(color.toString().equalsIgnoreCase(colorChosen)) {
                        return RoomColor.YELLOW;
                    }
                    break;
                default:    // BLUE
                    if(color.toString().equalsIgnoreCase(colorChosen)) {
                        return RoomColor.BLUE;
                    }
            }
        }

        throw new InexistentColorException(colorChosen);
    }
}
