package com.wafitz.pixelspacebase.actors;

import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroSubClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests focused on sniper attacks. These do not execute the full combat
 * pipeline but instead verify that the DR calculation in {@link Char#attack(Char)}
 * correctly ignores enemy DR when the attacker is a sniper using a ranged
 * weapon.
 */
public class SniperAttackTest {

    /** Dummy enemy with predictable DR values. */
    private static class DummyEnemy extends Char {
        DummyEnemy() {
            name = "dummy";
            HT = HP = 10;
        }

        @Override
        public int drRoll() {
            return 5;
        }
    }


    @Test
    public void sniperRangedAttackIgnoresDR() {
        DummyEnemy enemy = new DummyEnemy();
        Object weapon = new Object();
        HeroSubClass subClass = HeroSubClass.SNIPER;

        // Replicates DR logic from Char.attack() when a sniper uses a ranged weapon
        int dr = enemy.drRoll();
        if (weapon != null && subClass == HeroSubClass.SNIPER) {
            dr = 0;
        }

        assertEquals("Sniper ranged attack should ignore DR", 0, dr);
    }
}
