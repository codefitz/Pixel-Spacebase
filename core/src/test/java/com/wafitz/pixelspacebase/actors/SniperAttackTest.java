package com.wafitz.pixelspacebase.actors;

import com.wafitz.pixelspacebase.actors.hero.HeroSubClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests focused on sniper DR behavior in production combat code.
 */
public class SniperAttackTest {

    @Test
    public void sniperRangedAttackIgnoresDR() {
        int dr = Char.adjustedDamageReductionForAttack(5, true, HeroSubClass.SNIPER);

        assertEquals("Sniper ranged attack should ignore DR", 0, dr);
    }

    @Test
    public void nonSniperRangedAttackKeepsDR() {
        int dr = Char.adjustedDamageReductionForAttack(5, true, HeroSubClass.BERSERKER);

        assertEquals("Non-sniper ranged attack should keep DR", 5, dr);
    }

    @Test
    public void sniperWithoutRangedWeaponKeepsDR() {
        int dr = Char.adjustedDamageReductionForAttack(5, false, HeroSubClass.SNIPER);

        assertEquals("Sniper without ranged weapon should keep DR", 5, dr);
    }
}
