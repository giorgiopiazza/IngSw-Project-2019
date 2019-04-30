package exceptions;

import exceptions.cards.*;
import exceptions.command.InvalidCommandException;
import exceptions.file.JsonFileNotFoundException;
import exceptions.utility.InvalidPropertiesException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionsTest {

    @Test
    void cardsTest() {
        assertThrows(DamageDistributionException.class, () -> {throw new DamageDistributionException();});
        assertThrows(MarkDistributionException.class, () -> { throw new MarkDistributionException(); });
        assertThrows(PositionDistributionException.class, () -> { throw new PositionDistributionException(); });
        assertThrows(WeaponAlreadyChargedException.class, () -> { throw new WeaponAlreadyChargedException(); });
        assertThrows(WeaponAlreadyChargedException.class, () -> { throw new WeaponAlreadyChargedException("nome"); });
        assertThrows(WeaponNotChargedException.class, () -> { throw new WeaponNotChargedException(); });
        assertThrows(WeaponNotChargedException.class, () -> { throw new WeaponNotChargedException("nome"); });
    }

    @Test
    void commandTest() {
        assertThrows(InvalidCommandException.class, () -> { throw new InvalidCommandException(); });
    }

    @Test
    void fileTest() {
        assertThrows(JsonFileNotFoundException.class, () -> { throw new JsonFileNotFoundException(); });
        assertThrows(JsonFileNotFoundException.class, () -> { throw new JsonFileNotFoundException("message"); });
    }

    @Test
    void utilityTest() {
        assertThrows(InvalidPropertiesException.class, () -> { throw new InvalidPropertiesException(); });
    }

}
