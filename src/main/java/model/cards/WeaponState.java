package model.cards;

import exceptions.AdrenalinaException;
import model.player.Player;

public interface WeaponState {

    boolean charged(WeaponCard weapon);

    boolean rechargeable(WeaponCard weapon);

    int status();

    void use(Effect effect, Player playerDealer) throws AdrenalinaException;
}
