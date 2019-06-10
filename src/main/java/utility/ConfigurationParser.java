package utility;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigurationParser {

    private ConfigurationParser() {
        throw new IllegalStateException("utility class");
    }

    public static JsonObject parseConfiguration(String path) {
        JsonParser jp = new JsonParser();
        JsonObject jsonObject;

        try {
            InputStream is = new FileInputStream(path);

            jsonObject = jp.parse(new InputStreamReader(is)).getAsJsonObject();
            is.close();
        } catch (IOException e) {
            jsonObject = null;
        }

        return jsonObject;
    }
}
