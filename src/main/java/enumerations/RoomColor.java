package enumerations;

import exceptions.game.InexistentColorException;

import static model.Game.rand;

/**
 * Represents all the possible RoomColors that can appear on the Map
 */
public enum RoomColor {
    RED, YELLOW, GREEN, PURPLE, GREY, BLUE;

    /**
     * Returns the RoomColor corresponding to the String passed
     *
     * @param colorChosen the String containing the needed RoomColor
     * @return the corresponding RoomColor, if present
     * @throws InexistentColorException in case no RoomColor matches the String passed
     */
    public static RoomColor getColor(String colorChosen) throws InexistentColorException {
        RoomColor[] enumColors = values();
        for(RoomColor color : enumColors) {
            if(color.name().equalsIgnoreCase(colorChosen)) {
                return color;
            }
        }

        throw new InexistentColorException(colorChosen);
    }

    /**
     * Utility method used to randomly obtain a RoomColor when a Player needs to respawn but he is disconnected
     *
     * @return the randomly picked RoomColor
     */
    public static RoomColor getRandomSpawnColor() {

        switch (rand.nextInt(2)) {
            case 0:
                return RED;
            case 1:
                return YELLOW;
            default:
                return BLUE;
        }
    }
}
