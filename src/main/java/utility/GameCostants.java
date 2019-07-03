package utility;

import java.util.List;

public class GameCostants {
    private GameCostants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BOT_NAME = "bot";
    public static final String GOD_NAME = "god";
    public static final String CANCEL_KEYWORD = "cancel";

    private static final List<String> FORBIDDEN_USERNAME = List.of(BOT_NAME, GOD_NAME, CANCEL_KEYWORD);

    public static List<String> getForbiddenUsernames() {
        return List.copyOf(FORBIDDEN_USERNAME);
    }
}
