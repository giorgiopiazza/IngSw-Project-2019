package model.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.PowerupParser;
import utility.WeaponParser;

public class ParseTest {
    private Deck weaponCards;
    private Deck powerupCards;

    @BeforeEach
    void before() {
        weaponCards = new Deck();
        powerupCards = new Deck();
    }

    @Test
    void parseCards() {
        weaponCards = WeaponParser.parseCards();
        powerupCards = PowerupParser.parseCards();
    }
}
