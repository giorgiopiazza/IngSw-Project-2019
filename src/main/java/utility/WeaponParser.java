package utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enumerations.Ammo;
import enumerations.TargetType;
import model.cards.WeaponCard;
import model.cards.effects.*;
import model.cards.weaponstates.SemiChargedWeapon;
import model.player.AmmoQuantity;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WeaponParser {
    private static final String PATH = "/json/weapons.json";
    private static final String COST = "cost";
    private static final String TARGET = "target";
    private static final String DAMAGE_DISTRIBUTION = "damageDistribution";
    private static final String MARK_DISTRIBUTION = "markDistribution";
    private static final String MOVE = "move";
    private static final String MOVE_TARGET = "moveTarget";
    private static final String MAX_MOVE_TARGET = "moveTarget";

    private WeaponParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Parse all the weapons from weapons.json
     *
     * @return a list of all the WeaponCard
     */
    public static List<WeaponCard> parseWeaponCards() {
        List<WeaponCard> cards = new ArrayList<>();

        InputStream is = WeaponParser.class.getResourceAsStream(PATH);

        if (is == null) {
            return cards;
        }

        JsonParser parser = new JsonParser();

        JsonObject json = parser.parse(new InputStreamReader(is)).getAsJsonObject();
        JsonArray weapons = json.getAsJsonArray("weapons");

        for (JsonElement weapElem : weapons) {
            JsonObject weapon = weapElem.getAsJsonObject();

            String name = weapon.get("name").getAsString();
            File image = null;
            Ammo[] cost = parseAmmoJsonArray(weapon.getAsJsonArray(COST));

            // Effects Parse
            JsonArray effects = weapon.getAsJsonArray("effects");

            // First effect is the base effect
            Effect baseEffect = parseEffect(effects.get(0).getAsJsonObject());

            // The others are secondary effects
            List<Effect> secondaryEffects = new ArrayList<>();
            for (int i = 1; i < effects.size(); ++i) {
                secondaryEffects.add(parseEffect(effects.get(i).getAsJsonObject()));
            }

            // Card creation
            cards.add(new WeaponCard(name, image, baseEffect, cost, secondaryEffects, new SemiChargedWeapon()));
        }

        return cards;
    }

    /**
     * Parses an effect from a JsonObject of the effect
     *
     * @param jsonEffect jsonObject of the effect
     * @return the parsed effect
     */
    private static Effect parseEffect(JsonObject jsonEffect) {
        Ammo[] cost = new Ammo[0];

        if (!jsonEffect.has(COST)) {
            cost = parseAmmoJsonArray(jsonEffect.getAsJsonArray(COST));
        }

        Effect effect = new BaseEffect(new AmmoQuantity(cost));
        JsonObject properties = jsonEffect.getAsJsonObject("properties");

        if (properties.get(TARGET).getAsJsonArray().size() == 1) {
            effect = decorateSingleEffect(effect, properties);
        } else {
            effect = decorateMultipleEffect(effect, properties);
        }

        return effect;
    }

    /**
     * Decorates the base effect with a single effect
     *
     * @param effect base effect
     * @param properties JsonObject of the properties of the effect
     * @return the decorated effect
     */
    private static Effect decorateSingleEffect(Effect effect, JsonObject properties) {
        TargetType targetType = TargetType.valueOf(properties.getAsJsonArray(TARGET).get(0).getAsString());

        if (properties.has(DAMAGE_DISTRIBUTION)) {
            effect = new ExtraDamageDecorator(effect,
                    parseIntJsonArray(properties.get(DAMAGE_DISTRIBUTION).getAsJsonArray()),
                    targetType);
        }

        if (properties.has(MARK_DISTRIBUTION)) {
            effect = new ExtraMarkDecorator(effect,
                    parseIntJsonArray(properties.get(MARK_DISTRIBUTION).getAsJsonArray()),
                    targetType);
        }

        if (properties.has(MOVE_TARGET) || properties.has(MAX_MOVE_TARGET) || properties.has(MOVE)) {
            effect = new ExtraMoveDecorator(effect);
        }

        return effect;
    }

    /**
     * Decorates the base effect with a multiple effect
     *
     * @param effect base effect
     * @param properties JsonObject of the properties of the effect
     * @return the decorated effect
     */
    private static Effect decorateMultipleEffect(Effect effect, JsonObject properties) {
        TargetType[] targets = parseTargetTypeJsonArray(properties.getAsJsonArray(TARGET));
        JsonArray subeffects = properties.getAsJsonArray("subEffects");

        for (int i = targets.length - 1; i >= 0; --i) {
            JsonObject subeffect = subeffects.get(i).getAsJsonObject();

            if (subeffect.has(DAMAGE_DISTRIBUTION)) {
                effect = new ExtraDamageDecorator(effect,
                        parseIntJsonArray(properties.get(DAMAGE_DISTRIBUTION).getAsJsonArray()),
                        targets[i]);
            }

            if (subeffect.has(MARK_DISTRIBUTION)) {
                effect = new ExtraMarkDecorator(effect,
                        parseIntJsonArray(properties.get(MARK_DISTRIBUTION).getAsJsonArray()),
                        targets[i]);
            }

            if (subeffect.has(MOVE_TARGET) || properties.has(MAX_MOVE_TARGET) || properties.has(MOVE)) {
                effect = new ExtraMoveDecorator(effect);
            }
        }

        return effect;
    }

    /**
     * Parses an array of int from a JsonArray
     *
     * @param jsonArray JsonArray made of int
     * @return the parsed array made of int
     */
    private static int[] parseIntJsonArray(JsonArray jsonArray) {
        List<Integer> list = new ArrayList<>();

        for (JsonElement elem : jsonArray) {
            list.add(elem.getAsInt());
        }

        return list.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Parses an array of Ammo from a JsonArray
     *
     * @param jsonArray JsonArray made of Ammo
     * @return the parsed array made of Ammo
     */
    private static Ammo[] parseAmmoJsonArray(JsonArray jsonArray) {
        List<Ammo> list = new ArrayList<>();

        for (JsonElement elem : jsonArray) {
            list.add(Ammo.valueOf(elem.getAsString()));
        }

        return list.toArray(new Ammo[0]);
    }

    /**
     * Parses an array of TargetType from a JsonArray
     *
     * @param jsonArray JsonArray made of TargetType
     * @return the parsed array made of TargetType
     */
    private static TargetType[] parseTargetTypeJsonArray(JsonArray jsonArray) {
        List<TargetType> list = new ArrayList<>();

        for (JsonElement elem : jsonArray) {
            list.add(TargetType.valueOf(elem.getAsString()));
        }

        return list.toArray(new TargetType[0]);
    }
}
