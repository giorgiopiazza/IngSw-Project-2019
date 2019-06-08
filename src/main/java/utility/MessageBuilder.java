package utility;

import enumerations.PlayerColor;
import exceptions.actions.PowerupCardsNotFoundException;
import exceptions.actions.WeaponCardsNotFoundException;
import model.cards.PowerupCard;
import model.cards.WeaponCard;
import model.map.Square;
import model.player.*;
import network.message.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {

    @Contract(" -> fail")
    private MessageBuilder() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Create a {@link ConnectionRequest ConnectionRequest} object from {@code username}
     *
     * @param username username chosen by the user to request from the server if available
     * @return the {@link ConnectionRequest ConnectionRequest} object to send to the server
     */
    @NotNull
    @Contract("_ -> new")
    public static ConnectionRequest buildConnectionRequest(String username) {
        return new ConnectionRequest(username);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static ColorRequest buildColorRequest(String token, String username) {
        return new ColorRequest(username, token);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static LobbyMessage buildGetInLobbyMessage(String token, String username, PlayerColor color, boolean disconnection) {
        return new LobbyMessage(username, token, color, disconnection);
    }

    /**
     * Create a {@link DiscardPowerupRequest DiscardPowerupRequest} object from the actual
     * {@code player} and his {@code powerupCard}
     *
     * @param powerupCards      the actual player
     * @param powerupCard the card to discard
     * @return the {@link DiscardPowerupRequest DiscardPowerupRequest} object to send to the server
     * @throws PowerupCardsNotFoundException if the player does not have that {@code powerupCard}
     */
    @NotNull
    @Contract("_, null, _, _ -> fail; _, !null, null, _ -> fail")
    public static DiscardPowerupRequest buildDiscardPowerupRequest(String token, List<PowerupCard> powerupCards, PowerupCard powerupCard, String username) throws PowerupCardsNotFoundException {
        if (powerupCards == null || powerupCard == null)
            throw new NullPointerException("player and powerupCard cannot be null");

        for (int i = 0; i < powerupCards.size(); i++) {
            if (powerupCards.get(i).equals(powerupCard)) {
                return new DiscardPowerupRequest(username, token, i);
            }
        }

        throw new PowerupCardsNotFoundException("powerupCard not found in " + powerupCards);
    }

    /**
     * Create a {@link MovePickRequest MovePickupRequest} object from the actual {@code player},
     * his {@code newPos}, {@code paymentPowerups}, {@code addingWeapon} and {@code discardingWeapon}
     *
     * @param player           the actual player
     * @param newPos           the new position where pick up something ({@link PowerupCard PowerupCard} or {@link WeaponCard WeaponCard})
     * @param paymentPowerups  the powerUps to pay the {@link WeaponCard WeaponCard}
     * @param addingWeapon     the {@link WeaponCard WeaponCard} to add to the player's {@link model.player.PlayerBoard PlayerBoard}
     * @param discardingWeapon the {@link WeaponCard WeaponCard} to remove to the player's {@link model.player.PlayerBoard PlayerBoard}
     * @return the {@link MovePickRequest MovePickRequest} generated object
     * @throws PowerupCardsNotFoundException if the player does not have that {@code paymentsPowerups}
     */
    @NotNull
    @Contract("_, null, _, _, _, _ -> fail; _, !null, null, _, _, _ -> fail; _, !null, !null, _, null, _ -> fail; _, !null, !null, null, !null, _ -> fail")
    public static MovePickRequest buildMovePickRequest(String token, UserPlayer player, PlayerPosition newPos, List<PowerupCard> paymentPowerups, WeaponCard addingWeapon, WeaponCard discardingWeapon) throws PowerupCardsNotFoundException {
        if (player == null || newPos == null || addingWeapon == null || paymentPowerups == null)
            throw new NullPointerException("player, newPos and addingWeapon cannot be null");

        List<Integer> powerupIndexes = powerupListToIndexes(player, paymentPowerups);

        if (powerupIndexes.isEmpty()) throw new PowerupCardsNotFoundException();

        return new MovePickRequest(player.getUsername(), token, newPos, (ArrayList<Integer>) powerupIndexes, addingWeapon, discardingWeapon);
    }

    /**
     * Create a {@link MovePickRequest MovePickupRequest} object from the actual {@code player},
     * his {@code newPos}, {@code addingWeapon} and {@code discardingWeapon}
     *
     * @param player           the actual player
     * @param newPos           the new position where pick up something ({@link PowerupCard PowerupCard} or {@link WeaponCard WeaponCard})
     * @param addingWeapon     the {@link WeaponCard WeaponCard} to add to the player's {@link model.player.PlayerBoard PlayerBoard}
     * @param discardingWeapon the {@link WeaponCard WeaponCard} to remove to the player's {@link model.player.PlayerBoard PlayerBoard}
     * @return the {@link MovePickRequest MovePickRequest} generated object
     */
    @NotNull
    @Contract("_, null, _, _, _ -> fail; _, !null, null, _, _ -> fail; _, !null, !null, null, _ -> fail; _, !null, !null, !null, _ -> new")
    public static MovePickRequest buildMovePickRequest(String token, UserPlayer player, PlayerPosition newPos, WeaponCard addingWeapon, WeaponCard discardingWeapon) {
        if (player == null || newPos == null || addingWeapon == null)
            throw new NullPointerException("player, newPos and addingWeapon cannot be null");

        return new MovePickRequest(player.getUsername(), token, newPos, null, addingWeapon, discardingWeapon);
    }

    /**
     * Create a {@link MovePickRequest MovePickupRequest} object from the actual {@code player},
     * his {@code newPos} and {@code addingWeapon}
     *
     * @param player       the actual player
     * @param newPos       the new position where pick up something ({@link PowerupCard PowerupCard} or {@link WeaponCard WeaponCard})
     * @param addingWeapon the {@link WeaponCard WeaponCard} to add to the player's {@link model.player.PlayerBoard PlayerBoard}
     * @return the {@link MovePickRequest MovePickRequest} generated object
     */
    @NotNull
    public static MovePickRequest buildMovePickRequest(String token, UserPlayer player, PlayerPosition newPos, WeaponCard addingWeapon) {
        return buildMovePickRequest(token, player, newPos, addingWeapon, null);
    }

    /**
     * Create a {@link MovePickRequest MovePickupRequest} object from the actual {@code player},
     * his {@code newPos}, {@code paymentPowerups} and {@code addingWeapon}
     *
     * @param player          the actual player
     * @param newPos          the new position where pick up something ({@link PowerupCard PowerupCard} or {@link WeaponCard WeaponCard})
     * @param paymentPowerups the powerUps to pay the {@link WeaponCard WeaponCard}
     * @param addingWeapon    the {@link WeaponCard WeaponCard} to add to the player's {@link model.player.PlayerBoard PlayerBoard}
     * @return the {@link MovePickRequest MovePickRequest} generated object
     * @throws PowerupCardsNotFoundException if the player does not have that {@code paymentsPowerups}
     */
    @NotNull
    public static MovePickRequest buildMovePickRequest(String token, UserPlayer player, PlayerPosition newPos, List<PowerupCard> paymentPowerups, WeaponCard addingWeapon) throws PowerupCardsNotFoundException {
        return buildMovePickRequest(token, player, newPos, paymentPowerups, addingWeapon, null);
    }

    @NotNull
    @Contract("_, null, _ -> fail; _, !null, null -> fail; _, !null, !null -> new")
    public static MoveRequest buildMoveRequest(String token, Player player, PlayerPosition newPos) {
        if (player == null || newPos == null) throw new NullPointerException("player, newPos cannot be null");

        return new MoveRequest(player.getUsername(), token, newPos);
    }

    @NotNull
    @Contract("_, null -> fail; _, !null -> new")
    public static PassTurnRequest buildPassTurnRequest(String token, UserPlayer player) {
        if (player == null) throw new NullPointerException("player cannot be null");

        return new PassTurnRequest(player.getUsername(), token);
    }

    @NotNull
    @Contract("_, null, _ -> fail; _, !null, null -> fail")
    public static PowerupRequest buildPowerupRequest(String token, String username,  ArrayList<PowerupCard> playerPowerups, ArrayList<PowerupCard> toUse) throws PowerupCardsNotFoundException {
        if (playerPowerups == null || toUse == null)
            throw new NullPointerException("player and powerupCard cannot be null");

        ArrayList<Integer> powerupsIndexes = new ArrayList<>();

        for (int i = 0; i < playerPowerups.size(); i++) {
            for (PowerupCard powerupCard : toUse) {
                if (playerPowerups.get(i).equals(powerupCard)) {
                    powerupsIndexes.add(i);
                }
            }

        }

        if (powerupsIndexes.isEmpty()) throw new PowerupCardsNotFoundException();

        return new PowerupRequest(new PowerupRequest.PowerupRequestBuilder(username, token, powerupsIndexes));
    }

    @NotNull
    @Contract("_, _, _, _, _, null -> fail; _, null, _, _, _, !null -> fail")
    public static ReloadRequest buildReloadRequest(String token, UserPlayer player, WeaponCard weapon1, WeaponCard weapon2, WeaponCard weapon3, List<PowerupCard> paymentPowerups) throws PowerupCardsNotFoundException, WeaponCardsNotFoundException {
        if (paymentPowerups == null || player == null)
            throw new NullPointerException("player, paymentPowerups cannot be null");

        List<Integer> powerupIndexes = powerupListToIndexes(player, paymentPowerups);

        if (powerupIndexes.isEmpty()) throw new PowerupCardsNotFoundException();

        List<Integer> weaponIndexes = weaponsToIndexes(player, weapon1, weapon2, weapon3);

        if (weaponIndexes.isEmpty()) throw new WeaponCardsNotFoundException();

        return new ReloadRequest(player.getUsername(), token, (ArrayList<Integer>) weaponIndexes, (ArrayList<Integer>) powerupIndexes);
    }

    @NotNull
    public static ReloadRequest buildReloadRequest(String token, UserPlayer player, WeaponCard weaponCard, List<PowerupCard> paymentsPowerups) throws PowerupCardsNotFoundException, WeaponCardsNotFoundException {
        return buildReloadRequest(token, player, weaponCard, null, null, paymentsPowerups);
    }

    @NotNull
    public static ReloadRequest buildReloadRequest(String token, UserPlayer player, WeaponCard weapon1, WeaponCard weapon2, List<PowerupCard> paymentsPowerups) throws PowerupCardsNotFoundException, WeaponCardsNotFoundException {
        return buildReloadRequest(token, player, weapon1, weapon2, null, paymentsPowerups);
    }

    @NotNull
    @Contract("_, null, _, _, _ -> fail")
    public static ReloadRequest buildReloadRequest(String token, UserPlayer player, WeaponCard weapon1, WeaponCard weapon2, WeaponCard weapon3) throws WeaponCardsNotFoundException {
        if (player == null) throw new NullPointerException("player cannot be null");

        List<Integer> weaponIndexes = weaponsToIndexes(player, weapon1, weapon2, weapon3);

        if (weaponIndexes.isEmpty()) throw new WeaponCardsNotFoundException();

        return new ReloadRequest(player.getUsername(), token, (ArrayList<Integer>) weaponIndexes, null);
    }

    @NotNull
    public static ReloadRequest buildReloadRequest(String token, UserPlayer player, WeaponCard weaponCard) throws WeaponCardsNotFoundException {
        return buildReloadRequest(token, player, weaponCard, null, (WeaponCard) null);
    }

    @NotNull
    public static ReloadRequest buildReloadRequest(String token, UserPlayer player, WeaponCard weapon1, WeaponCard weapon2) throws WeaponCardsNotFoundException {
        return buildReloadRequest(token, player, weapon1, weapon2, (WeaponCard) null);
    }


    @NotNull
    @Contract("_, null, _, _, _, _, _ -> fail; _, !null, null, _, _, _, _ -> fail; _, !null, !null, _, null, null, null -> fail")
    public static ShootRequest buildShootRequest(String token, UserPlayer player, WeaponCard weaponCard, int effect, WeaponCard recharge1, WeaponCard recharge2, WeaponCard recharge3) throws WeaponCardsNotFoundException {
        if (player == null || weaponCard == null || (recharge1 == null && recharge2 == null && recharge3 == null))
            throw new NullPointerException();

        List<Integer> rechargingWeapons = weaponsToIndexes(player, recharge1, recharge2, recharge3);

        int index = -1;

        for (int i = 0; i < player.getWeapons().length; i++) {
            if (player.getWeapons()[i].equals(weaponCard)) {
                index = i;
                break;
            }
        }

        if (index < 0 || rechargingWeapons.isEmpty()) throw new WeaponCardsNotFoundException();

        return new ShootRequest(new ShootRequest.ShootRequestBuilder(player.getUsername(), token, index, effect, (ArrayList<Integer>) rechargingWeapons));
    }

    @NotNull
    @Contract("_, null, _, _, _ -> fail; _, !null, null, _, _ -> fail; _, !null, !null, _, null -> fail")
    public static ShootRequest buildShootRequest(String token, UserPlayer player, WeaponCard weaponCard, int effect, WeaponCard recharge) throws WeaponCardsNotFoundException {
        return buildShootRequest(token, player, weaponCard, effect, recharge, null, null);
    }

    @NotNull
    @Contract("_, null, _, _, _, _ -> fail; _, !null, null, _, _, _ -> fail; _, !null, !null, _, null, null -> fail")
    public static ShootRequest buildShootRequest(String token, UserPlayer player, WeaponCard weaponCard, int effect, WeaponCard recharge1, WeaponCard recharge2) throws WeaponCardsNotFoundException {
        return buildShootRequest(token, player, weaponCard, effect, recharge1, recharge2, null);
    }

    @NotNull
    @Contract("_, null, _, _ -> fail; _, !null, null, _ -> fail")
    public static ShootRequest buildShootRequest(ShootRequest.ShootRequestBuilder fireRequestBuilt) {
        if (fireRequestBuilt.getUsername() == null)
            throw new NullPointerException("player userName can not be null");

        if(fireRequestBuilt.getWeaponID() < 0 || fireRequestBuilt.getWeaponID() > 3)
            throw new IndexOutOfBoundsException("Invalid index for maximum number of weapons allowed in hand");

        if(fireRequestBuilt.getEffect() < 0 || fireRequestBuilt.getEffect() > 3)
            throw new IndexOutOfBoundsException("Invalid index for maximum number od powerups allowed in hand!");

        return fireRequestBuilt.build();
    }

    @NotNull
    @Contract("_, null, _ -> fail; _, !null, null -> fail; _, !null, !null -> new")
    public static TerminatorSpawnRequest buildTerminatorSpawnRequest(String token, Bot bot, Square spawnSquare) {
        if (bot == null || spawnSquare == null)
            throw new NullPointerException("terminator and spawnSquare cannot be null");

        return new TerminatorSpawnRequest(bot.getUsername(), token, spawnSquare.getRoomColor());
    }

    @NotNull
    @Contract("_, null, _, _ -> fail; _, !null, null, _ -> fail; _, !null, !null, null -> fail; _, !null, !null, !null -> new")
    public static UseTerminatorRequest buildUseTerminatorRequest(String token, Bot bot, PlayerPosition newPos, UserPlayer target) {
        if (bot == null || newPos == null || target == null)
            throw new NullPointerException("Terminator, newPos and target cannot be null");

        return new UseTerminatorRequest(bot.getUsername(), token, newPos, target.getUsername());
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