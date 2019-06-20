package enumerations;

import exceptions.game.InexistentColorException;

import static model.Game.rand;

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
