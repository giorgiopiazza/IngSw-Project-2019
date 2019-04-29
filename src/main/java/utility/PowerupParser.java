package utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enumerations.Ammo;
import enumerations.TargetType;
import exceptions.file.JsonFileNotFoundException;
import model.cards.Card;
import model.cards.Deck;
import model.cards.PowerupCard;
import model.cards.effects.Effect;
import model.cards.effects.PowerupBaseEffect;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PowerupParser {

    private PowerupParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Parse all the powerups from powerup.json
     *
     * @return {@code deck} of all the powerups
     */
    public static Deck parseCards() {
        Deck deck = new Deck(true);
        String path = "json/powerups.json";

        InputStream is = PowerupParser.class.getClassLoader().getResourceAsStream(path);

        if (is == null) {
            throw new JsonFileNotFoundException("File " + path + " not found");
        }

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new InputStreamReader(is)).getAsJsonObject();
        JsonArray powerups = json.getAsJsonArray("powerups");

        for (JsonElement je : powerups) {
            JsonObject jo = je.getAsJsonObject();

            List<PowerupCard> cards = parseColor(jo);

            for (Card card : cards) {
                deck.addCard(card);
            }
        }

        deck.shuffle();
        return deck;
    }

    /**
     * Parses PowerupCard for each color
     *
     * @param jsonObject JsonObject of a powerup
     * @return a list of PowerupCard
     */
    private static List<PowerupCard> parseColor(JsonObject jsonObject) {
        String name = jsonObject.get("title").getAsString();
        JsonArray values = jsonObject.getAsJsonArray("values");
        JsonObject properties = jsonObject.getAsJsonObject("properties");

        TargetType[] target = new TargetType[0];
        List<PowerupCard> cards = new ArrayList<>();

        if (properties.has("target")) {
            JsonArray targets = properties.getAsJsonArray("target");
            target = WeaponParser.parseTargetTypeJsonArray(targets);
        }

        Map<String, String> powerupProperties;
        powerupProperties = WeaponParser.getProperties(properties);

        Effect effect;

        if (jsonObject.has("cost")) {
            effect = new PowerupBaseEffect(jsonObject.get("cost").getAsBoolean(), powerupProperties, target);
        } else {
            effect = new PowerupBaseEffect(powerupProperties, target);
        }

        effect = WeaponParser.decorateSingleEffect(effect, properties);

        int quantity = jsonObject.get("quantity").getAsInt();

        for (JsonElement je : values) {
            JsonObject jo = je.getAsJsonObject();

            File image = new File(PowerupParser.class.getResource(jo.get("image").getAsString()).getFile());
            Ammo ammo = Ammo.valueOf(jo.get("color").getAsString());

            for (int i = 0; i < quantity; ++i) {
                cards.add(new PowerupCard(name, image, ammo, effect));
            }
        }

        return cards;
    }
}
