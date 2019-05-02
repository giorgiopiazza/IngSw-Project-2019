package exceptions.map;

import exceptions.AdrenalinaRuntimeException;
import model.cards.WeaponCard;

public class MissingWeaponOnSquareException extends AdrenalinaRuntimeException {
    public MissingWeaponOnSquareException(WeaponCard weaponCard) {
        super("The weapon: " + weaponCard.getName() + "is not on this square");
    }
}
