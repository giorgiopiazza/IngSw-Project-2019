package model.cards;

import exceptions.cards.DamageDistributionException;
import exceptions.cards.MarkDistributionException;
import exceptions.cards.PositionDistributionException;
import model.map.Square;
import model.player.Player;
import model.player.PlayerPosition;

import java.util.ArrayList;
import java.util.Optional;

public class Target {
    /**
     * Target object is possessed by the effect of a weapon and changed for every attack
     */
    private final Player[] targets;
    private Optional<ArrayList<Square>> room;   // a target can optionally be a room, if a square an ArrayList of a single element then

    public Target(Player[] targets) {
        this.targets = targets;
        this.room = Optional.empty();
    }

    public Target(Player[] targets, ArrayList<Square> room) {
        this.targets = targets;
        this.room = Optional.of(room);
    }

    public Player[] getTargets() {
        return targets;
    }

    public Optional<ArrayList<Square>> getRoom() {
        return this.room;
    }
}
