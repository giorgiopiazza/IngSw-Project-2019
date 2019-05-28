package view.cli;

public class AnsiCode {
    public static final String RESET = "\u001B[0m";
    public static final String CLEAR_LINE = "\33[1A\33[2K";

    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_WHITE = "\u001B[37m";

    public static final String BACKGROUND_BLACK = "\u001B[40m";
    public static final String BACKGROUND_RED = "\u001B[41m";
    public static final String BACKGROUND_GREEN = "\u001B[42m";
    public static final String BACKGROUND_YELLOW = "\u001B[43m";
    public static final String BACKGROUND_BLUE = "\u001B[44m";
    public static final String BACKGROUND_PURPLE = "\u001B[45m";
    public static final String BACKGROUND_CYAN = "\u001B[46m";
    public static final String BACKGROUND_WHITE = "\u001B[47m";

    public static String getTextColorCodeByName(String color, boolean background) {
        color = color.toUpperCase();

        switch (color) {
            case "YELLOW":
                return (background) ? BACKGROUND_YELLOW : TEXT_YELLOW;
            case "GREEN":
                return (background) ? BACKGROUND_GREEN : TEXT_GREEN;
            case "PURPLE":
                return (background) ? BACKGROUND_PURPLE : TEXT_PURPLE;
            case "BLUE":
                return (background) ? BACKGROUND_BLUE : TEXT_BLUE;
            case "RED":
                return (background) ? BACKGROUND_RED : TEXT_RED;
            default:
                return (background) ? BACKGROUND_WHITE : TEXT_WHITE;
        }
    }
}
