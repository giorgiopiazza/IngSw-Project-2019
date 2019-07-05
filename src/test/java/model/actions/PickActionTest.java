package model.actions;

import enumerations.PlayerColor;
import enumerations.PossibleAction;
import exceptions.actions.IncompatibleActionException;
import exceptions.actions.InvalidActionException;
import exceptions.game.InvalidMapNumberException;
import model.Game;
import model.cards.Card;
import model.cards.WeaponCard;
import model.map.GameMap;
import model.player.Bot;
import model.player.PlayerBoard;
import model.player.PlayerPosition;
import model.player.UserPlayer;
import network.message.MovePickRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.WeaponParser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PickActionTest {
    Game game;
    UserPlayer p1;
    UserPlayer p2;
    UserPlayer p3;
    List<Card> weapons;

    @BeforeEach
    void before() throws InvalidMapNumberException {
        game = Game.getInstance();
        game.init();
        game.initializeDecks();

        weapons = WeaponParser.parseCards().toList();

        p1 = new UserPlayer("1", PlayerColor.GREEN, new PlayerBoard());
        p2 = new UserPlayer("2", PlayerColor.YELLOW, new PlayerBoard());
        p3 = new UserPlayer("3", PlayerColor.GREY, new PlayerBoard());

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.setGameMap(GameMap.MAP_3);
    }


    @Test
    void pickAction() throws InvalidActionException {
        p1.setPosition(new PlayerPosition(0,0));

        MovePickRequest request = new MovePickRequest(p1.getUsername(), null, null, List.of(5), null, null);
        PickAction action = new PickAction(p1, PossibleAction.MOVE_AND_PICK, request);
        assertThrows(InvalidActionException.class, action::validate);

        request = new MovePickRequest(p1.getUsername(), null, new PlayerPosition(2,3), null, null, null);
        action = new PickAction(p1, PossibleAction.ADRENALINE_PICK, request);
        action.validate();

        action = new PickAction(p1, PossibleAction.FRENZY_PICK, request);
        action.validate();

        action = new PickAction(p1, PossibleAction.LIGHT_FRENZY_PICK, request);
        action.validate();

        action = new PickAction(p1, PossibleAction.ADRENALINE_PICK, request);
        action.validate();

        action = new PickAction(p1, PossibleAction.SHOOT, request);
        assertThrows(IncompatibleActionException.class, action::validate);

        request = new MovePickRequest(p1.getUsername(), null, new PlayerPosition(1,0), null, null, null);
        action = new PickAction(p1, PossibleAction.ADRENALINE_PICK, request);
        assertThrows(NullPointerException.class, action::validate);

        request = new MovePickRequest(p1.getUsername(), null, new PlayerPosition(1,0), null, getWeaponByName("Electroscythe"), null);
        action = new PickAction(p1, PossibleAction.ADRENALINE_PICK, request);
        assertFalse(action.validate());
    }


    WeaponCard getWeaponByName(String name) {
        WeaponCard weaponCard = null;

        for (Card card : weapons) {
            WeaponCard weapon = (WeaponCard) card;

            if (weapon.getName().equals(name)) {
                weaponCard = weapon;
                break;
            }
        }

        return weaponCard;
    }
}
