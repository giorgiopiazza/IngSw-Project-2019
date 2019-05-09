package enumerations;

public enum Ammo {
    RED, YELLOW, BLUE;

    public static Color toColor(Ammo ammo) {
        if(ammo.toString().equals(Color.RED.toString())) {
            return Color.RED;
        }

        if(ammo.toString().equals(Color.YELLOW.toString())) {
            return Color.YELLOW;
        }

        if(ammo.toString().equals(Color.BLUE.toString())) {
            return Color.BLUE;
        }

        throw new NullPointerException("An ammo has always a color to be returned!");
    }
}
