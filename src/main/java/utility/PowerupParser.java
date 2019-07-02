package utility;

import com.google.gson.*;

import enumerations.Ammo;
import enumerations.MoveTarget;
import enumerations.Properties;
import enumerations.TargetType;
import exceptions.file.JsonFileNotFoundException;
import model.cards.Card;
import model.cards.Deck;
import model.cards.PowerupCard;
import model.cards.effects.*;
import model.player.AmmoQuantity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utility.WeaponParser.parseIntJsonArray;

public class PowerupParser {
    private static final String TARGET = "target";
    private static int ids = 0;

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
        JsonObject json;
        json = parser.parse(new InputStreamReader(is)).getAsJsonObject();
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
        String description = jsonObject.get("description").getAsString();
        JsonArray values = jsonObject.getAsJsonArray("values");
        JsonObject properties = jsonObject.getAsJsonObject("properties");

        TargetType[] target = new TargetType[0];
        List<PowerupCard> cards = new ArrayList<>();

        if (properties.has(TARGET)) {
            JsonArray targets = properties.getAsJsonArray(TARGET);
            target = WeaponParser.parseTargetTypeJsonArray(targets);
        }

        HashMap<String, String> powerupProperties;
        powerupProperties = WeaponParser.getProperties(properties);

        Effect effect;

        if (jsonObject.has("cost")) {
            effect = new PowerupBaseEffect((jsonObject.get("cost").getAsBoolean()) ? new AmmoQuantity(1, 0, 0) : new AmmoQuantity(),
                    powerupProperties, target, description);
        } else {
            effect = new PowerupBaseEffect(powerupProperties, target, description);
        }

        effect = decorateSingleEffect(effect, properties);

        int quantity = jsonObject.get("quantity").getAsInt();

        for (JsonElement je : values) {
            JsonObject jo = je.getAsJsonObject();

            String imagePath = jo.get("image").getAsString();
            Ammo ammo = Ammo.valueOf(jo.get("color").getAsString());

            for (int i = 0; i < quantity; ++i) {
                cards.add(new PowerupCard(name, imagePath, ammo, effect, ids));
                ++ids;
            }
        }

        return cards;
    }

    /**
     * Decorates the base effect with a single effect. Care, this is a Powerup Decoration
     *
     * @param effect     base effect
     * @param properties JsonObject of the properties of the effect
     * @return the decorated effect
     */
    private static Effect decorateSingleEffect(Effect effect, JsonObject properties) {
        TargetType targetType = TargetType.valueOf(properties.getAsJsonArray(TARGET).get(0).getAsString());

        if (properties.has(Properties.DAMAGE_DISTRIBUTION.getJKey())) {
            effect = new ExtraDamageNoMarkDecorator(effect,
                    parseIntJsonArray(properties.get(Properties.DAMAGE_DISTRIBUTION.getJKey()).getAsJsonArray()));
        }

        if (properties.has(Properties.MARK_DISTRIBUTION.getJKey())) {
            effect = new ExtraMarkDecorator(effect,
                    parseIntJsonArray(properties.get(Properties.MARK_DISTRIBUTION.getJKey()).getAsJsonArray()),
                    targetType);
        }

        if (properties.has(Properties.TP.getJKey())) {
            effect = new ExtraMoveDecorator(effect, MoveTarget.PLAYER);
        }

        if (properties.has(Properties.MAX_MOVE_TARGET.getJKey())) {
            effect = new ExtraMoveDecorator(effect, MoveTarget.TARGET);
        }

        return effect;
    }
}
