package model.cards;

import exceptions.AdrenalinaException;
import model.cards.effects.Effect;
import model.player.Player;

public interface WeaponState {

    boolean charged(WeaponCard weapon);

    boolean rechargeable(WeaponCard weapon);

    int status();

    void use(Effect effect, FiringAction firingAction, Player playerDealer) throws AdrenalinaException;
}
