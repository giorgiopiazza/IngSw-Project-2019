package model.cards.effects;

import enumerations.Properties;
import enumerations.TargetType;
import exceptions.command.InvalidCommandException;
import exceptions.player.NoDirectionException;
import exceptions.utility.InvalidWeaponPropertiesException;
import model.Game;
import model.player.AmmoQuantity;
import model.player.Player;
import model.player.PlayerPosition;
import utility.CommandUtility;
import utility.CommandValidator;
import utility.PropertiesValidator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeaponBaseEffect extends Effect {
    // TODO we can add a description of the effect to give a better understanding of the weapon while playing with CLI
    private AmmoQuantity cost;

    public WeaponBaseEffect(AmmoQuantity cost, Map<String, String> properties, TargetType[] targets) {
        setCost(cost);
        setProperties(properties);
        setTargets(targets);
    }

    /**
     * Setter of the cost of an Effect
     *
     * @param cost the cost of the effect
     */
    public void setCost(AmmoQuantity cost) {
        this.cost = cost;
    }

    /**
     * @return the cost of the Effect
     */
    public AmmoQuantity getCost() {
        return this.cost;
    }

    @Override
    public void execute(String command) {
        // Basic Effect does nothing
    }

    @Override
    public boolean validate(String command) throws NoDirectionException {

        // subEffects validation
        if (getTargets().length > 1) {
            Map<String, String> mapCopy = new LinkedHashMap<>(getProperties());
            TargetType[] targetCopy = new TargetType[getTargets().length];
            System.arraycopy(getTargets(), 0, targetCopy, 0, getTargets().length);

            for (int i = 0; i < getTargets().length; ++i) {
                PropertiesValidator.setTempMap(getProperties(), getTargets()[i]);
                setTargets(Arrays.copyOf(getTargets(), getTargets().length - 1));

                if (!validate(CommandUtility.setTempCommand(command, getTargets()[i]))) {
                    setProperties(mapCopy);
                    setTargets(targetCopy);
                    return false;
                }

                setProperties(mapCopy);
                setTargets(targetCopy);
            }
            return true;
        }

        // target and command validation
        if (!CommandValidator.isTargetTypeValid(command, getTargets()[0])) {
            return false;
        }

        // number of target validation
        if (getProperties().containsKey(Properties.TARGET_NUM.getJKey())) {
            int exactNumber = Integer.parseInt(getProperties().get(Properties.TARGET_NUM.getJKey()));
            if (!CommandValidator.isTargetNumValid(command, getTargets()[0], exactNumber, true)) {
                return false;
            }
        } else if (getProperties().containsKey(Properties.MAX_TARGET_NUM.getJKey())) {
            int number = Integer.parseInt(getProperties().get(Properties.MAX_TARGET_NUM.getJKey()));
            if (!CommandValidator.isTargetNumValid(command, getTargets()[0], number, false)) {
                return false;
            }
        } else {
            throw new InvalidWeaponPropertiesException();
        }

        // distance validation
        if (getProperties().containsKey(Properties.DISTANCE.getJKey())) {
            int exactDistance = Integer.parseInt(getProperties().get(Properties.DISTANCE.getJKey()));
            if (!CommandValidator.isTargetDistanceValid(command, getTargets()[0], exactDistance, true)) {
                return false;
            }
        } else if (getProperties().containsKey(Properties.MIN_DISTANCE.getJKey())) {
            int distance = Integer.parseInt(getProperties().get(Properties.MIN_DISTANCE.getJKey()));
            if (!CommandValidator.isTargetDistanceValid(command, getTargets()[0], distance, false)) {
                return false;
            }
        } else {
            throw new InvalidWeaponPropertiesException();
        }

        // inLine targets validation
        if (getProperties().containsKey(Properties.INLINE.getJKey())) {
            if (!PropertiesValidator.areInLine(command, getTargets()[0])) {
                return false;
            }
        }

        // move validation
        if (getProperties().containsKey(Properties.MOVE.getJKey())) {
            List<PlayerPosition> movingPos = CommandUtility.getPositions(command.split(" "), "-m");
            int playerID = CommandUtility.getPlayerID(command.split(" "));
            int exactMove = Integer.parseInt(getProperties().get(Properties.MOVE.getJKey()));

            if (movingPos.isEmpty()) throw new InvalidCommandException();
            if (!PropertiesValidator.canMove(playerID, movingPos.get(0), exactMove)) {
                return false;
            }
        }

        if (getProperties().containsKey(Properties.MOVE_TARGET.getJKey())) {
            List<Integer> targetsID = CommandUtility.getAttributesID(command.split(" "), "-t");
            List<PlayerPosition> movingPos = CommandUtility.getPositions(command.split(" "), "-u");
            int exactMove = Integer.parseInt(getProperties().get(Properties.MOVE_TARGET.getJKey()));

            if (movingPos.isEmpty()) throw new InvalidCommandException();
            if (!PropertiesValidator.canMove(targetsID, movingPos, exactMove)) {
                return false;
            }
        } else if (getProperties().containsKey(Properties.MAX_MOVE_TARGET.getJKey())) {
            List<Integer> targetsID = CommandUtility.getAttributesID(command.split(" "), "-t");
            List<PlayerPosition> movingPos = CommandUtility.getPositions(command.split(" "), "-u");
            int move = Integer.parseInt(getProperties().get(Properties.MAX_MOVE_TARGET.getJKey()));

            if (movingPos.isEmpty()) throw new InvalidCommandException();
            if (!PropertiesValidator.canMaxMove(targetsID, movingPos, move)) {
                return false;
            }
        } else {
            throw new InvalidWeaponPropertiesException();
        }

        // visibility validation
        if (getProperties().containsKey(Properties.VISIBLE.getJKey())) {
            Player shooter = Game.getInstance().getPlayerByID(CommandUtility.getPlayerID(command.split(" ")));

            if (getProperties().containsKey(Properties.MOVE_TARGET_BEFORE.getJKey()) && command.contains("-z")) {
                throw new InvalidCommandException();
            }

            // targets move or must move before
            if (getProperties().containsKey(Properties.MOVE_TARGET_BEFORE.getJKey()) ||
                    CommandUtility.getBoolParam(command.split(" "), "-z")) {

                List<PlayerPosition> movingPositions = CommandUtility.getPositions(command.split(" "), "-u");

                // also shooter must move before
                if (command.contains("-y") && CommandUtility.getBoolParam(command.split(" "), "-y")) {
                    if (!PropertiesValidator.areVisible(CommandUtility.getPositions(command.split(" "), "-m").get(0), movingPositions)) {
                        return false;
                    }
                } else {    // only targets must move before
                    if (!PropertiesValidator.areVisible(shooter.getPosition(), movingPositions)) {
                        return false;
                    }
                }

                // after the movement the targets are in the same position
                if (getProperties().containsKey(Properties.SAME_POSITION.getJKey())) {
                    if (!PropertiesValidator.inSamePosition(movingPositions)) {
                        return false;
                    }
                }
            } else {    // target does NOT move before
                List<Player> targets = CommandUtility.getPlayersByIDs(CommandUtility.getAttributesID(command.split(" "), "-t"));
                // shooter moves before
                if (command.contains("-y") && CommandUtility.getBoolParam(command.split(" "), "-y")) {
                    if (!PropertiesValidator.areVisible(command, getTargets()[0], true)) {
                        return false;
                    }
                }

                // shooter does NOT move
                if (!PropertiesValidator.areVisible(command, getTargets()[0], false)) {
                    return false;
                }

                // targets are in the same position
                if (getProperties().containsKey(Properties.SAME_POSITION.getJKey())) {
                    if (!PropertiesValidator.inSamePosition(Game.getInstance().getPlayersPositions(targets))) {
                        return false;
                    }
                }
            }
        } else if (getProperties().containsKey(Properties.CONCATENATED_VISIBLE.getJKey())) {
            // TODO exactly as the visible part
        } else throw new InvalidWeaponPropertiesException();

        // moveToLastTarget validation
        if (getProperties().containsKey(Properties.MOVE_TO_LAST_TARGET.getJKey())) {
            if (command.contains("-u") && CommandUtility.getAttributesID(command.split(" "), "-t").size() ==
                    CommandUtility.getPositions(command.split(" "), "-u").size()) {
                if (!PropertiesValidator.lastTargetPos(command, true)) {
                    return false;
                }
            } else {
                if (!PropertiesValidator.lastTargetPos(command, false)) {
                    return false;
                }
            }
        }

        // moveInLine validation (only targets can have this way of moving)
        if (getProperties().containsKey(Properties.MOVE_INLINE.getJKey())) {
            if (!PropertiesValidator.isMovingDirectionally(command)) {
                return false;
            }
        }

        // finally if everything is fine we can use our effect
        return true;
    }
}
