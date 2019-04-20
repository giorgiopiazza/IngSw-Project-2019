package utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enumerations.Ammo;
import enumerations.MoveTarget;
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
    /**
     * Parse all the weapons from weapons.json
     *
     * @return a list of all the WeaponCard
     */
    public static List<WeaponCard> parseCards() {
        List<WeaponCard> cards = new ArrayList<>();

        InputStream is = WeaponParser.class.getResourceAsStream("/json/weapons.json");

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
            Ammo[] cost = parseAmmoJsonArray(weapon.get("cost").getAsJsonArray());

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

    private static Effect parseEffect(JsonObject jsonEffect) {
        Ammo[] cost = new Ammo[0];

        if (!jsonEffect.has("cost")) {
            cost = parseAmmoJsonArray(jsonEffect.get("cost").getAsJsonArray());
        }

        Effect effect = new BaseEffect(new AmmoQuantity(cost));
        JsonObject properties = jsonEffect.getAsJsonObject("properties");

        if (properties.get("target").getAsJsonArray().size() == 1) {
            effect = decorateSingleEffect(effect, properties);
        } else {
            effect = decorateMultipleEffect(effect, properties);
        }

        return effect;
    }

    private static Effect decorateSingleEffect(Effect effect, JsonObject properties) {
        TargetType targetType = TargetType.valueOf(properties.get("target").getAsJsonArray().get(0).getAsString());

        if (properties.has("damageDistribution")) {
            effect = new ExtraDamageDecorator(effect,
                    parseIntJsonArray(properties.get("damageDistribution").getAsJsonArray()),
                    targetType);
        }

        if (properties.has("markDistribution")) {
            effect = new ExtraMarkDecorator(effect,
                    parseIntJsonArray(properties.get("markDistribution").getAsJsonArray()),
                    targetType);
        }

        if (properties.has("move")) {
            effect = new ExtraMoveDecorator(effect, MoveTarget.PLAYER);
        }

        if (properties.has("moveTarget") || properties.has("maxMoveTarget")) {
            effect = new ExtraMoveDecorator(effect, MoveTarget.TARGET);
        }

        return effect;
    }

    private static Effect decorateMultipleEffect(Effect effect, JsonObject properties) {
        return effect;
    }

    private static int[] parseIntJsonArray(JsonArray jsonArray) {
        List<Integer> list = new ArrayList<>();

        for (JsonElement elem : jsonArray) {
            list.add(elem.getAsInt());
        }

        return list.stream().mapToInt(i -> i).toArray();
    }


    private static Ammo[] parseAmmoJsonArray(JsonArray jsonArray) {
        List<Ammo> list = new ArrayList<>();

        for (JsonElement elem : jsonArray) {
            list.add(Ammo.valueOf(elem.getAsString()));
        }

        return list.toArray(new Ammo[0]);
    }
}
