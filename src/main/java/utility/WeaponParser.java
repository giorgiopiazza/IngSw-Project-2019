package utility;

import com.google.gson.*;
import enumerations.Ammo;
import enumerations.MoveTarget;
import enumerations.Properties;
import enumerations.TargetType;
import exceptions.file.JsonFileNotFoundException;
import model.cards.Deck;
import model.cards.WeaponCard;
import model.cards.effects.*;
import model.cards.weaponstates.SemiChargedWeapon;
import model.player.AmmoQuantity;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class WeaponParser {
    private static final String COST = "cost";
    private static final String TARGET = "target";
    private static final String SUB_EFFECTS = "subEffects";

    private WeaponParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Parse all the weapons from weapons.json
     *
     * @return a deck of all the WeaponCard
     */
    public static Deck parseCards() {
        Deck deck = new Deck();

        String path = "json/weapons.json";

        InputStream is = WeaponParser.class.getClassLoader().getResourceAsStream(path);

        if (is == null) {
            throw new JsonFileNotFoundException("File " + path + " not found");
        }

        JsonParser parser;
        parser = new JsonParser();

        JsonObject json = parser.parse(new InputStreamReader(is)).getAsJsonObject();
        JsonArray weapons = json.getAsJsonArray("weapons");

        for (JsonElement weapElem : weapons) {
            JsonObject weapon = weapElem.getAsJsonObject();

            String name = weapon.get("name").getAsString();
            File image = null;
            int id = weapon.get("id").getAsInt();
            Ammo[] cost = parseAmmoJsonArray(weapon.getAsJsonArray(COST));

            // Effects Parse
            JsonArray effects = weapon.getAsJsonArray("effects");

            // First effect is the base effect
            Effect baseEffect = parseEffect(effects.get(0).getAsJsonObject());

            // The others are secondary effects
            ArrayList<Effect> secondaryEffects = new ArrayList<>();
            for (int i = 1; i < effects.size(); ++i) {
                secondaryEffects.add(parseEffect(effects.get(i).getAsJsonObject()));
            }

            // Card creation
            deck.addCard((new WeaponCard(name, image, baseEffect, id, cost, secondaryEffects, new SemiChargedWeapon())));
        }

        deck.shuffle();
        return deck;
    }

    /**
     * Parses an effect from a JsonObject of the effect
     *
     * @param jsonEffect jsonObject of the effect
     * @return the parsed effect
     */
    private static Effect parseEffect(JsonObject jsonEffect) {
        Ammo[] cost = new Ammo[0];
        TargetType[] target = new TargetType[0];

        if (jsonEffect.has(COST)) {
            cost = parseAmmoJsonArray(jsonEffect.getAsJsonArray(COST));
        }

        JsonObject properties = jsonEffect.getAsJsonObject("properties");

        if (properties.has(TARGET)) {
            JsonArray targets = properties.getAsJsonArray(TARGET);
            target = parseTargetTypeJsonArray(targets);
        }

        Map<String, String> weaponProperties;
        if (properties.has(SUB_EFFECTS)) {
            weaponProperties = getPropertiesWithSubEffects(properties);
        } else {
            weaponProperties = getProperties(properties);
        }

        Effect effect = new WeaponBaseEffect(new AmmoQuantity(cost), weaponProperties, target);

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
     * @param effect     base effect
     * @param properties JsonObject of the properties of the effect
     * @return the decorated effect
     */
    static Effect decorateSingleEffect(Effect effect, JsonObject properties) {
        TargetType targetType = TargetType.valueOf(properties.getAsJsonArray(TARGET).get(0).getAsString());

        if (properties.has(Properties.MARK_DISTRIBUTION.getJKey())) {
            effect = new ExtraMarkDecorator(effect,
                    parseIntJsonArray(properties.get(Properties.MARK_DISTRIBUTION.getJKey()).getAsJsonArray()),
                    targetType);
        }

        if (properties.has(Properties.DAMAGE_DISTRIBUTION.getJKey())) {
            effect = new ExtraDamageDecorator(effect,
                    parseIntJsonArray(properties.get(Properties.DAMAGE_DISTRIBUTION.getJKey()).getAsJsonArray()),
                    targetType);
        }

        if (properties.has(Properties.MOVE.getJKey())) {
            effect = new ExtraMoveDecorator(effect, MoveTarget.PLAYER);
        }

        if (properties.has(Properties.MOVE_TARGET.getJKey()) || properties.has(Properties.MAX_MOVE_TARGET.getJKey())) {
            effect = new ExtraMoveDecorator(effect, MoveTarget.TARGET);
        }

        return effect;
    }

    /**
     * Decorates the base effect with a multiple effect
     *
     * @param effect     base effect
     * @param properties JsonObject of the properties of the effect
     * @return the decorated effect
     */
    private static Effect decorateMultipleEffect(Effect effect, JsonObject properties) {
        TargetType[] targets = parseTargetTypeJsonArray(properties.getAsJsonArray(TARGET));
        JsonArray subEffects = properties.getAsJsonArray(SUB_EFFECTS);

        for (int i = targets.length - 1; i >= 0; --i) {
            JsonObject subEffect = subEffects.get(i).getAsJsonObject();

            if (subEffect.has(Properties.MARK_DISTRIBUTION.getJKey())) {
                effect = new ExtraMarkDecorator(effect,
                        parseIntJsonArray(subEffect.get(Properties.MARK_DISTRIBUTION.getJKey()).getAsJsonArray()),
                        targets[i]);
            }

            if (subEffect.has(Properties.DAMAGE_DISTRIBUTION.getJKey())) {
                effect = new ExtraDamageDecorator(effect,
                        parseIntJsonArray(subEffect.get(Properties.DAMAGE_DISTRIBUTION.getJKey()).getAsJsonArray()),
                        targets[i]);
            }

            if (subEffect.has(Properties.MOVE_TARGET.getJKey()) || subEffect.has(Properties.MAX_MOVE_TARGET.getJKey())) {
                effect = new ExtraMoveDecorator(effect, MoveTarget.TARGET);
            }
        }

        if (properties.has(Properties.MOVE.getJKey())) {
            effect = new ExtraMoveDecorator(effect, MoveTarget.PLAYER);
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
    static TargetType[] parseTargetTypeJsonArray(JsonArray jsonArray) {
        List<TargetType> list = new ArrayList<>();

        for (JsonElement elem : jsonArray) {
            list.add(TargetType.valueOf(elem.getAsString()));
        }

        return list.toArray(new TargetType[0]);
    }

    /**
     * Parses a Map of properties from a JsonObject
     *
     * @param properties JsonObject that contains visibility properties
     * @return a LinkedHashMap<String,String> where the key is the visibility rule and the value is its definition
     */
    static Map<String, String> getProperties(JsonObject properties) {
        // I create a linked hash map as I can iterate on it with the order I put his elements
        Map<String, String> effectProperties = new LinkedHashMap<>();
        JsonObject justVisibilityProperties = properties.deepCopy();
        Set<String> keys;

        if (properties.has(TARGET)) {
            justVisibilityProperties.remove(TARGET);
        }

        if (properties.has(Properties.DAMAGE_DISTRIBUTION.getJKey())) {
            justVisibilityProperties.remove(Properties.DAMAGE_DISTRIBUTION.getJKey());
        }

        if (properties.has(Properties.MARK_DISTRIBUTION.getJKey())) {
            justVisibilityProperties.remove(Properties.MARK_DISTRIBUTION.getJKey());
        }

        keys = justVisibilityProperties.keySet();

        for (String tempKey : keys) {
            String tempValue = justVisibilityProperties.get(tempKey).getAsString();
            effectProperties.put(tempKey, tempValue);
        }

        return effectProperties;
    }


    /**
     * Parses a Map of properties including in it even the subEffects' ones by separating them with a non valid
     * couple of (KEY,VALUE) in which both attributes have the name of the target to be validated
     *
     * @param properties JsonObject that contains visibility properties
     * @return a LinkedHashMap<String, String> where the key is the visibility rule and the value is its definition
     */
    private static Map<String, String> getPropertiesWithSubEffects(JsonObject properties) {

        JsonObject justVisibilityProperties = properties.deepCopy();
        Set<String> keys;

        JsonArray subEffects = properties.getAsJsonArray(SUB_EFFECTS);
        JsonArray targets = properties.getAsJsonArray(TARGET);
        justVisibilityProperties.remove(SUB_EFFECTS);
        justVisibilityProperties.remove(TARGET);

        Map<String, String> effectProperties = new LinkedHashMap<>(getProperties(justVisibilityProperties));
        TargetType[] separators = parseTargetTypeJsonArray(targets);
        for (int i = 0; i < separators.length; ++i) {
            keys = subEffects.get(i).getAsJsonObject().keySet();
            effectProperties.put(separators[i].toString(), "stop");
            for (String tempKey : keys) {
                String tempValue = subEffects.get(i).getAsJsonObject().get(tempKey).getAsString();
                effectProperties.put(tempKey, tempValue);
            }
        }

        return effectProperties;
    }
}
