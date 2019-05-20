package utility;

import com.google.gson.stream.JsonWriter;
import exceptions.file.JsonFileNotFoundException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientsStateParser {
    private static final String PATH = "json/clientsState.json";

    private ClientsStateParser() {
        throw new IllegalStateException("Utility class");
    }


    public static boolean saveClientsState(List<String> clients) {

        URL url = ClientsStateParser.class.getClassLoader().getResource(PATH);

        if (url == null) throw new JsonFileNotFoundException("File " + PATH + " not found");

        File file;
        FileWriter fw;
        try {
            file = new File(url.toURI());
            fw = new FileWriter(file);
        } catch (URISyntaxException | IOException e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return false;
        }

        Logger.getGlobal().log(Level.INFO, "Opened: {0}", file);
        try (JsonWriter jsonWriter = new JsonWriter(fw)) {
            jsonWriter.beginArray();

            for (String client : clients) {
                jsonWriter.beginObject();
                jsonWriter.name("username").value(client);
                jsonWriter.endObject();
            }

            jsonWriter.endArray();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return false;
        }

        try {
            fw.close();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return false;
        }

        return true;
    }

}
