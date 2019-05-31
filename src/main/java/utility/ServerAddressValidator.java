package utility;

import java.util.Arrays;

public class ServerAddressValidator {
    private ServerAddressValidator()  {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isAddressValid(String address) {
        if (address == null || address.equals("localhost")) {
            return true;
        }

        String[] groups = address.split("\\.");

        if (groups.length != 4)
            return false;

        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() > 1 && s.startsWith("0"))
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPortValid(String portString) {
        try {
            int port = Integer.parseInt(portString);
            if (port >= 1 && port <= 25565) {
                return true;
            }
        } catch (NumberFormatException e) {
            // Handled with the return
        }

        return false;
    }
}
