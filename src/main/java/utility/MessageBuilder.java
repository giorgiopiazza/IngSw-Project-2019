package utility;

import exceptions.actions.PowerupCardsNotFoundException;
import exceptions.actions.WeaponCardsNotFoundException;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.map.Square;
import model.player.Player;
import model.player.PlayerPosition;
import model.player.Terminator;
import model.player.UserPlayer;
import network.message.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {

    @Contract(" -> fail")
    private MessageBuilder() {  throw new IllegalStateException("Utility class"); }

    @NotNull
    @Contract("_ -> new")
    public static ConnectionRequest buildConnectionRequest(String username) {
        return new ConnectionRequest(username);
    }

    @NotNull
    @Contract("null, _ -> fail; !null, null -> fail")
    public static DiscardPowerupRequest buildDiscardPowerupRequest(UserPlayer player, PowerupCard powerupCard) throws PowerupCardsNotFoundException {
        if (player == null || powerupCard == null) throw new NullPointerException("player and powerupCard cannot be null");

        for (int i = 0; i < player.getPowerups().length; i++) {
            if (player.getPowerups()[i].equals(powerupCard)) {
                return new DiscardPowerupRequest(player.getUsername(), i);
            }
        }

        throw new PowerupCardsNotFoundException("powerupCard not found in " + player);
    }

    @NotNull
    @Contract("null, _, _, _, _ -> fail; !null, null, _, _, _ -> fail; !null, !null, _, null, _ -> fail; !null, !null, null, !null, _ -> fail")
    public static MovePickRequest buildMovePickRequest(UserPlayer player, PlayerPosition newPos, List<PowerupCard> paymentPowerups, WeaponCard addingWeapon, WeaponCard discardingWeapon) throws PowerupCardsNotFoundException{
        if (player == null || newPos == null || addingWeapon == null || paymentPowerups == null) throw new NullPointerException("player, newPos and addingWeapon cannot be null");

        List<Integer> powerupIndexes = powerupListToIndexes(player, paymentPowerups);

        if (powerupIndexes.isEmpty()) throw new PowerupCardsNotFoundException();

        return new MovePickRequest(player.getUsername(), newPos, (ArrayList<Integer>) powerupIndexes, addingWeapon, discardingWeapon);
    }

    @NotNull
    @Contract("null, _, _, _ -> fail; !null, null, _, _ -> fail; !null, !null, null, _ -> fail; !null, !null, !null, _ -> new")
    public static MovePickRequest buildMovePickRequest(UserPlayer player, PlayerPosition newPos, WeaponCard addingWeapon, WeaponCard discardingWeapon) {
        if (player == null || newPos == null || addingWeapon == null) throw new NullPointerException("player, newPos and addingWeapon cannot be null");

        return new MovePickRequest(player.getUsername(), newPos, null, addingWeapon, discardingWeapon);
    }

    @NotNull
    @Contract("null, _, _ -> fail; !null, null, _ -> fail; !null, !null, null -> fail")
    public static MovePickRequest buildMovePickRequest(UserPlayer player, PlayerPosition newPos, WeaponCard addingWeapon) {
        return buildMovePickRequest(player, newPos, addingWeapon, null);
    }

    @NotNull
    @Contract("null, _, _, _ -> fail; !null, null, _, _ -> fail; !null, !null, _, null -> fail; !null, !null, null, !null -> fail")
    public static MovePickRequest buildMovePickRequest(UserPlayer player, PlayerPosition newPos, List<PowerupCard> paymentPowerups, WeaponCard addingWeapon) throws PowerupCardsNotFoundException {
        return buildMovePickRequest(player, newPos, paymentPowerups, addingWeapon, null);
    }

    @NotNull
    @Contract("null, _ -> fail; !null, null -> fail; !null, !null -> new")
    public static MoveRequest buildMoveRequest(Player player, PlayerPosition newPos) {
        if (player == null || newPos == null) throw new NullPointerException("player, newPos cannot be null");

        return new MoveRequest(player.getUsername(), newPos);
    }

    @NotNull
    @Contract("null -> fail; !null -> new")
    public static PassTurnRequest buildPassTurnRequest(UserPlayer player) {
        if (player == null) throw new NullPointerException("player cannot be null");

        return new PassTurnRequest(player.getUsername());
    }

    @NotNull
    @Contract("null, _ -> fail; !null, null -> fail")
    public static PowerupRequest buildPowerupRequest(UserPlayer player, PowerupCard powerupCard) throws PowerupCardsNotFoundException {
        if (player == null || powerupCard == null) throw new NullPointerException("player and powerupCard cannot be null");

        int index = -1;

        for (int i = 0; i < player.getPowerups().length; i++) {
            if (player.getPowerups()[i].equals(powerupCard)) index = i;
        }

        if (index < 0) throw new PowerupCardsNotFoundException();

        return new PowerupRequest(new PowerupRequest.PowerupRequestBuilder(player.getUsername(), index));
    }

    @NotNull
    @Contract("_, _, _, _, null -> fail; null, _, _, _, !null -> fail; !null, null, null, null, !null -> fail")
    public static ReloadRequest buildReloadRequest(UserPlayer player, WeaponCard weapon1, WeaponCard weapon2, WeaponCard weapon3, List<PowerupCard> paymentPowerups) throws PowerupCardsNotFoundException, WeaponCardsNotFoundException {
        if (paymentPowerups == null || player == null) throw new NullPointerException("player, paymentPowerups cannot be null");

        List<Integer> powerupIndexes = powerupListToIndexes(player, paymentPowerups);

        if (powerupIndexes.isEmpty()) throw new PowerupCardsNotFoundException();

        List<Integer> weaponIndexes = weaponsToIndexes(player, weapon1, weapon2, weapon3);

        if (weaponIndexes.isEmpty()) throw new WeaponCardsNotFoundException();

        return new ReloadRequest(player.getUsername(), (ArrayList<Integer>) weaponIndexes, (ArrayList<Integer>) powerupIndexes);
    }

    @NotNull
    @Contract("_, _, null -> fail; null, _, !null -> fail; !null, null, !null -> fail")
    public static ReloadRequest buildReloadRequest(UserPlayer player, WeaponCard weaponCard, List<PowerupCard> paymentsPowerups) throws  PowerupCardsNotFoundException, WeaponCardsNotFoundException {
        return buildReloadRequest(player, weaponCard, null, null, paymentsPowerups);
    }

    @NotNull
    @Contract("_, _, _, null -> fail; null, _, _, !null -> fail; !null, null, null, !null -> fail")
    public static ReloadRequest buildReloadRequest(UserPlayer player, WeaponCard weapon1, WeaponCard weapon2, List<PowerupCard> paymentsPowerups) throws  PowerupCardsNotFoundException, WeaponCardsNotFoundException {
        return buildReloadRequest(player, weapon1, weapon2, null, paymentsPowerups);
    }

    @NotNull
    @Contract("null, _, _, _ -> fail; !null, null, null, null -> fail")
    public static ReloadRequest buildReloadRequest(UserPlayer player, WeaponCard weapon1, WeaponCard weapon2, WeaponCard weapon3) throws WeaponCardsNotFoundException {
        if (player == null) throw new NullPointerException("player cannot be null");

        List<Integer> weaponIndexes = weaponsToIndexes(player, weapon1, weapon2, weapon3);

        if (weaponIndexes.isEmpty()) throw new WeaponCardsNotFoundException();

        return new ReloadRequest(player.getUsername(), (ArrayList<Integer>) weaponIndexes, null);
    }

    @NotNull
    @Contract("null, _ -> fail; !null, null -> fail")
    public static ReloadRequest buildReloadRequest(UserPlayer player, WeaponCard weaponCard) throws WeaponCardsNotFoundException {
        return buildReloadRequest(player, weaponCard, null, (WeaponCard) null);
    }

    @NotNull
    @Contract("null, _, _ -> fail")
    public static ReloadRequest buildReloadRequest(UserPlayer player, WeaponCard weapon1, WeaponCard weapon2) throws WeaponCardsNotFoundException {
        return buildReloadRequest(player, weapon1, weapon2, (WeaponCard) null);
    }

    @NotNull
    @Contract("null, _, _, _, _, _ -> fail; !null, null, _, _, _, _ -> fail; !null, !null, _, null, null, null -> fail")
    public static ShootRequest buildShootRequest(UserPlayer player, WeaponCard weaponCard, int effect, WeaponCard recharge1, WeaponCard recharge2, WeaponCard recharge3) throws WeaponCardsNotFoundException {
        if (player == null || weaponCard == null || (recharge1 == null && recharge2 == null && recharge3 == null)) throw new NullPointerException();

        List<Integer> rechargingWeapons = weaponsToIndexes(player, recharge1, recharge2, recharge3);

        int index = -1;

        for (int i = 0; i < player.getWeapons().length; i++) {
            if (player.getWeapons()[i].equals(weaponCard)) {
                index = i;
                break;
            }
        }

        if (index < 0 || rechargingWeapons.isEmpty()) throw new WeaponCardsNotFoundException();

        return new ShootRequest(new ShootRequest.FireRequestBuilder(player.getUsername(), index, effect, (ArrayList<Integer>) rechargingWeapons));
    }

    @NotNull
    @Contract("null, _, _, _ -> fail; !null, null, _, _ -> fail; !null, !null, _, null -> fail")
    public static ShootRequest buildShootRequest(UserPlayer player, WeaponCard weaponCard, int effect, WeaponCard recharge) throws WeaponCardsNotFoundException {
        return buildShootRequest(player, weaponCard, effect, recharge, null, null);
    }

    @NotNull
    @Contract("null, _, _, _, _ -> fail; !null, null, _, _, _ -> fail; !null, !null, _, null, null -> fail")
    public static ShootRequest buildShootRequest(UserPlayer player, WeaponCard weaponCard, int effect, WeaponCard recharge1, WeaponCard recharge2) throws WeaponCardsNotFoundException {
        return buildShootRequest(player, weaponCard, effect, recharge1, recharge2, null);
    }

    @NotNull
    @Contract("null, _, _ -> fail; !null, null, _ -> fail")
    public static ShootRequest buildShootRequest(UserPlayer player, WeaponCard weaponCard, int effect) throws WeaponCardsNotFoundException {
        if (player == null || weaponCard == null) throw new NullPointerException("player and weaponCard cannot be null");

        int index = -1;

        for (int i = 0; i < player.getWeapons().length; i++) {
            if (weaponCard.equals(player.getWeapons()[i])) {
                index = i;
                break;
            }
        }

        if (index < 0) throw new WeaponCardsNotFoundException();

        return new ShootRequest(new ShootRequest.FireRequestBuilder(player.getUsername(), index, effect, null));
    }

    @NotNull
    @Contract("null, _ -> fail; !null, null -> fail; !null, !null -> new")
    public static TerminatorSpawnRequest buildTerminatorSpawnRequest(Terminator terminator, Square spawnSquare) {
        if (terminator == null || spawnSquare == null) throw new NullPointerException("terminator and spawnSquare cannot be null");

        return new TerminatorSpawnRequest(terminator.getUsername(), spawnSquare.getRoomColor());
    }

    @NotNull
    @Contract("null, _, _ -> fail; !null, null, _ -> fail; !null, !null, null -> fail; !null, !null, !null -> new")
    public static UseTerminatorRequest buildUseTerminatorRequest(Terminator terminator, PlayerPosition newPos, UserPlayer target) {
        if (terminator == null || newPos == null || target == null) throw new NullPointerException("Terminator, newPos and target cannot be null");

        return new UseTerminatorRequest(terminator.getUsername(), newPos, target.getUsername());
    }

    private static List<Integer> powerupListToIndexes(@NotNull UserPlayer player, List<PowerupCard> powerupCards) {
        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < player.getPowerups().length; i++) {
            for (PowerupCard powerupCard : powerupCards) {
                if (powerupCard.equals(player.getPowerups()[i])) indexes.add(i);
            }
        }

        return indexes;
    }

    @Contract("_, null, null, null -> fail")
    private static List<Integer> weaponsToIndexes(@NotNull UserPlayer player, WeaponCard weapon1, WeaponCard weapon2, WeaponCard weapon3) {
        if (weapon1 == null && weapon2 == null && weapon3 == null) throw new NullPointerException();

        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < player.getWeapons().length; i++) {
            if (weapon1 != null && weapon1.equals(player.getWeapons()[i])) indexes.add(i);
            if (weapon2 != null && weapon2.equals(player.getWeapons()[i])) indexes.add(i);
            if (weapon3 != null && weapon3.equals(player.getWeapons()[i])) indexes.add(i);
        }

        return indexes;
    }
}