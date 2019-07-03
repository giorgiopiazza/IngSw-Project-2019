package exceptions.map;

import exceptions.AdrenalinaRuntimeException;
import model.cards.WeaponCard;

public class MissingWeaponOnSquareException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -5702471656082650156L;

    public MissingWeaponOnSquareException(WeaponCard weaponCard) {
        super("The weapon: " + weaponCard.getName() + "is not on this square");
    }
}
