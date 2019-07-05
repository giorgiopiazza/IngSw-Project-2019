package model.cards.effects;

import model.player.AmmoQuantity;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

public class GeneralEffectTest {
    private ExtraEffectDecorator extraEffectDecorator1;
    private ExtraEffectDecorator extraEffectDecoratorDummy1;

    private ExtraEffectDecorator extraEffectDecorator2;
    private ExtraEffectDecorator extraEffectDecoratorDummy2;

    private ExtraEffectDecorator extraEffectDecorator3;
    private ExtraEffectDecorator extraEffectDecoratorDummy3;

    private ExtraEffectDecorator extraEffectDecorator4;
    private ExtraEffectDecorator extraEffectDecoratorDummy4;


    @BeforeEach
    void before() {
        WeaponBaseEffect weaponBaseEffect = new WeaponBaseEffect(mock(AmmoQuantity.class), null, null, "Description");

        extraEffectDecorator1 = new ExtraMoveDecorator(weaponBaseEffect, null);
        extraEffectDecoratorDummy1 = new ExtraMoveDecorator(weaponBaseEffect, null);

        extraEffectDecorator2 = new ExtraDamageDecorator(weaponBaseEffect, null, null);
        extraEffectDecoratorDummy2 = new ExtraDamageDecorator(weaponBaseEffect, null, null);

        extraEffectDecorator3 = new ExtraMarkDecorator(weaponBaseEffect, null, null);
        extraEffectDecoratorDummy3 = new ExtraMarkDecorator(weaponBaseEffect, null, null);

        extraEffectDecorator4 = new ExtraDamageNoMarkDecorator(weaponBaseEffect, null);
        extraEffectDecoratorDummy4 = new ExtraDamageNoMarkDecorator(weaponBaseEffect, null);
    }

    @Test
    void defaultMethods() {
        boolean isEqual = true;

        if(!(extraEffectDecorator1.equals(extraEffectDecoratorDummy1) && extraEffectDecorator2.equals(extraEffectDecoratorDummy2) &&
                extraEffectDecorator3.equals(extraEffectDecoratorDummy3) && extraEffectDecorator4.equals(extraEffectDecoratorDummy4))) {
            isEqual = false;
        }

        assertTrue(isEqual);
    }
}
