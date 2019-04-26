package utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enumerations.Ammo;
import enumerations.Color;
import enumerations.TargetType;
import exceptions.file.JsonFileNotFoundException;
import model.cards.Card;
import model.cards.Deck;
import model.cards.PowerupCard;
import model.cards.effects.Effect;
import model.cards.effects.PowerupBaseEffect;
import model.cards.effects.WeaponBaseEffect;
import model.player.AmmoQuantity;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PowerupParser {

    private static final String PATH = "/json/powerups.json";

    private PowerupParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @return {@code deck} with all the necessary powerups cards in a game
     */
    public static Deck parsePowerUpCards() {
        Deck deck = new Deck(true);
        InputStream is = PowerupParser.class.getResourceAsStream(PATH);

        if(is == null) throw new JsonFileNotFoundException("File " + PATH + " not found");

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(new InputStreamReader(is)).getAsJsonArray();

        for (JsonElement je : array) {
            JsonObject jo = je.getAsJsonObject();
            String name = jo.get("title").getAsString();

            List<PowerupCard> cards = parsePowerupsColor(name, jo.getAsJsonArray("values"), jo.getAsJsonObject("properties"));

            for (Card card : cards) {
                deck.addCard(card);
            }
        }

        return deck;
    }

    /**
     *
     * @param name
     * @param values
     * @param properties
     * @return
     */
    private static List<PowerupCard> parsePowerupsColor(String name, JsonArray values, JsonObject properties) {
        TargetType[] target = new TargetType[0];
        List<PowerupCard> cards = new ArrayList<>();

        if (properties.has("target")) {
            JsonArray targets = properties.getAsJsonArray("target");
            target = WeaponParser.parseTargetTypeJsonArray(targets);
        }

        Map<String, String> powerupProperties;
        powerupProperties = WeaponParser.getProperties(properties);

        Effect effect;

        if (properties.has("cost")) {
            effect = new PowerupBaseEffect(properties.get("cost").getAsInt(), powerupProperties, target);
        } else {
            effect = new PowerupBaseEffect(powerupProperties, target);
        }

        effect = WeaponParser.decorateSingleEffect(effect, properties);

        for (JsonElement je : values) {
            JsonObject jo = je.getAsJsonObject();

            File image = new File(PowerupParser.class.getResource(jo.get("image").getAsString()).getFile());
            Ammo ammo = Ammo.valueOf(jo.get("color").getAsString());

            cards.add(new PowerupCard(name, image, ammo, effect));
            cards.add(new PowerupCard(name, image, ammo, effect));
        }

        return cards;
    }
}
