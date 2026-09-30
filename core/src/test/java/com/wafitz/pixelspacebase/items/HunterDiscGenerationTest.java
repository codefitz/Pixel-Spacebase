package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.items.weapon.missiles.HunterDisc;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HunterDiscGenerationTest {

    @Test
    public void hunterDiscHasAChanceToAppearInRandomWeaponDrops() {
        boolean availableInTierOne = false;
        // Reading wepTiers initializes Generator's static drop tables.
        Generator.Category tierOne = Generator.wepTiers[0];
        for (int i = 0; i < tierOne.classes.length; i++) {
            if (tierOne.classes[i] == HunterDisc.class && tierOne.probs[i] > 0) {
                availableInTierOne = true;
                break;
            }
        }

        assertTrue(availableInTierOne);
    }
}
