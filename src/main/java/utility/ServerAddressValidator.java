package utility;

import org.jetbrains.annotations.Contract;

public class ServerAddressValidator {
    public static final int MAX_ADDRESS_LENGTH = 15;
    public static final int MAX_PORT_LENGTH = 5;

    private ServerAddressValidator()  {
        throw new IllegalStateException("Utility class");
    }

    @Contract("null -> true")
    public static boolean isAddressValid(String address) {
        if (address == null || address.equals("localhost")) {
            return true;
        }

        return address.matches("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
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
