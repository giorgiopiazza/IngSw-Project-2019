package enumerations;

import exceptions.game.InexistentColorException;

public enum Ammo {
    RED, YELLOW, BLUE;

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
