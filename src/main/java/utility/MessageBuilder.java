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

    public static GameVoteMessage buildVoteMessage(String token, String username, int vote) {
        return new GameVoteMessage(username, token, vote);
    }

    /**
     * Create a {@link DiscardPowerupRequest DiscardPowerupRequest} object from the actual
     * {@code player} and his {@code powerupCard}
     *
     * @param powerupCards the actual player
     * @param powerupCard  the card to discard
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

    public static DiscardPowerupRequest buildSpawnDiscardPowerupRequest(String token, List<PowerupCard> powerups, PowerupCard spawnPowerup, PowerupCard choosen, String username) throws PowerupCardsNotFoundException {
        if (powerups == null || choosen == null)
            throw new NullPointerException("powerups and choosen powerup cannot be null");

        if (spawnPowerup == null || !spawnPowerup.equals(choosen))
            return buildDiscardPowerupRequest(token, powerups, choosen, username);

        return new DiscardPowerupRequest(username, token, 3);
    }

    public static MovePickRequest buildMovePickRequest(String token, UserPlayer player, PlayerPosition newPos) {
        if (player == null || newPos == null) {
            throw new NullPointerException("player and newPos cannot be null");
        }

        return new MovePickRequest(player.getUsername(), token, newPos, new ArrayList<>(), null, null);
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

        return new MovePickRequest(player.getUsername(), token, newPos, new ArrayList<>(), addingWeapon, discardingWeapon);
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
    public static MovePickRequest buildMovePickRequest(String token, UserPlayer player, PlayerPosition newPos, ArrayList<Integer> paymentPowerups, WeaponCard addingWeapon, WeaponCard discardingWeapon) throws PowerupCardsNotFoundException {
        if (player == null || newPos == null || addingWeapon == null)
            throw new NullPointerException("player and newPos cannot be null");

        if (paymentPowerups.size() > 3) throw new PowerupCardsNotFoundException();

        return new MovePickRequest(player.getUsername(), token, newPos, paymentPowerups, addingWeapon, discardingWeapon);
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

    public static PowerupRequest buildPowerupRequest(@NotNull PowerupRequest.PowerupRequestBuilder powerupRequestBuilder) throws PowerupCardsNotFoundException {
        PowerupRequest powerupRequest = powerupRequestBuilder.build();

        if (powerupRequest.getPowerup() == null)
            throw new NullPointerException("powerupRequestBuilder cannot be null");

        return powerupRequest;
    }

    @NotNull
    @Contract("_, _, _, _, _, null -> fail; _, null, _, _, _, !null -> fail")
    public static ReloadRequest buildReloadRequest(String token, UserPlayer player, ArrayList<Integer> reloadingWeapons, ArrayList<Integer> paymentPowerups) throws PowerupCardsNotFoundException, WeaponCardsNotFoundException {
        if (paymentPowerups == null || player == null)
            throw new NullPointerException("player, paymentPowerups cannot be null");

        if (reloadingWeapons.isEmpty() || reloadingWeapons.size() > 3) throw new WeaponCardsNotFoundException();
        if (paymentPowerups.size() > 3) throw new PowerupCardsNotFoundException();

        return new ReloadRequest(player.getUsername(), token, reloadingWeapons, paymentPowerups);
    }

    @NotNull
    @Contract("_, null, _, _, _ -> fail")
    public static ReloadRequest buildReloadRequest(String token, UserPlayer player, ArrayList<Integer> reloadingWeapons) throws WeaponCardsNotFoundException {
        if (player == null) throw new NullPointerException("player cannot be null");

        if (reloadingWeapons.isEmpty() || reloadingWeapons.size() > 3) throw new WeaponCardsNotFoundException();

        return new ReloadRequest(player.getUsername(), token, reloadingWeapons, null);
    }

    @NotNull
    @Contract("_, null, _, _ -> fail; _, !null, null, _ -> fail")
    public static ShootRequest buildShootRequest(ShootRequest.ShootRequestBuilder shootRequestBuilt) {
        ShootRequest shootRequest = shootRequestBuilt.build();

        if (shootRequest.getSenderUsername() == null)
            throw new NullPointerException("player userName can not be null");

        if (shootRequest.getWeaponID() < 0 || shootRequest.getWeaponID() > 2)
            throw new IndexOutOfBoundsException("Invalid index for maximum number of weapons allowed in hand");

        if (shootRequest.getEffect() < 0 || shootRequest.getEffect() > 3)
            throw new IndexOutOfBoundsException("Invalid index for maximum number od powerups allowed in hand!");

        return shootRequest;
    }

    @NotNull
    @Contract("_, null, _ -> fail; _, !null, null -> fail; _, !null, !null -> new")
    public static BotSpawnRequest buildBotSpawnRequest(UserPlayer player, String token, Square spawnSquare) {
        if (player == null || spawnSquare == null)
            throw new NullPointerException("Player and spawnSquare cannot be null");

        return new BotSpawnRequest(player.getUsername(), token, spawnSquare.getRoomColor());
    }

    public static UseTerminatorRequest buildUseTerminatorRequest(UserPlayer player, String token, PlayerPosition newPos, UserPlayer target) {
        if (player == null || newPos == null)
            throw new NullPointerException("Player and newPos cannot be null");

        return new UseTerminatorRequest(player.getUsername(), token, newPos, target == null ? null : target.getUsername());
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