package utility;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ClientsStateTest {

    @Test
    void saveClientsTest() {
        List<String> list = new ArrayList<>();

        list.add("tose");
        list.add("piro");
        list.add("gio");

        ClientsStateParser.saveClientsState(list);
    }

}
