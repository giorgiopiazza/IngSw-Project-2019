package utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enumerations.Ammo;
import exceptions.file.JsonFileNotFoundException;
import model.cards.AmmoTile;
import model.player.AmmoQuantity;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AmmoTileParser {
    private AmmoTileParser() {
        throw new IllegalStateException("Utility class");
    }

    public static List<AmmoTile> parseCards() {
        List<AmmoTile> cards = new ArrayList<>();

        String path = File.separatorChar + "json" + File.separatorChar + "ammotiles.json";

        InputStream is = AmmoTileParser.class.getResourceAsStream(path);

        if (is == null) {
            throw new JsonFileNotFoundException("File " + path + " not found");
        }

        JsonParser parser = new JsonParser();

        JsonObject json = parser.parse(new InputStreamReader(is)).getAsJsonObject();
        JsonArray ammoTiles = json.getAsJsonArray("ammoTiles");

        for (JsonElement ammoTileElem : ammoTiles) {
            JsonObject ammoTile = ammoTileElem.getAsJsonObject();

            File image = null;
            boolean powerUp = ammoTile.get("powerUp").getAsBoolean();
            JsonArray ammoArray = ammoTile.getAsJsonArray("ammo");

            List<Ammo> list = new ArrayList<>();
            for (JsonElement elem : ammoArray) {
                list.add(Ammo.valueOf(elem.getAsString()));
            }
            Ammo[] ammo = list.toArray(new Ammo[0]);

            for (int i = 0; i < ammoTile.get("quantity").getAsInt(); ++i) {
                cards.add(new AmmoTile(image, new AmmoQuantity(ammo), powerUp));
            }
        }

        Collections.shuffle(cards);
        return cards;
    }
}
