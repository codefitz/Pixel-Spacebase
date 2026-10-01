package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.actors.mobs.npcs.Leonard;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.items.weapon.melee.BrightSaber;
import com.wafitz.pixelspacebase.items.weapon.melee.DarkSaber;
import com.wafitz.pixelspacebase.items.weapon.melee.EclipseSaber;
import org.junit.Test;
import static org.junit.Assert.*;

public class EclipseSaberTest {
    @Test public void acceptsEitherSelectionOrderAndKeepsStrongerWeaponProperties() {
        BrightSaber bright = new BrightSaber();
        DarkSaber dark = new DarkSaber();
        bright.identify();
        dark.identify();
        dark.level(4);
        dark.convert = Weapon.Convert.LIGHT;
        assertNull(Leonard.verify(bright, dark));
        assertNull(Leonard.verify(dark, bright));
        for (EclipseSaber result : new EclipseSaber[]{
                EclipseSaber.combine(bright, dark), EclipseSaber.combine(dark, bright)}) {
            assertEquals(5, result.level());
            assertEquals(5, result.tier);
            assertEquals(Weapon.Convert.LIGHT, result.convert);
            assertTrue(result.isIdentified());
            assertTrue(result.max(5) > bright.max(5));
            assertTrue(result.max(5) > dark.max(5));
        }
        assertEquals(4, dark.level());
        assertEquals(0, bright.level());
    }

    @Test public void recipeDoesNotTreatFusedWeaponAsAnotherDarkSaber() {
        assertFalse(EclipseSaber.canCombine(new EclipseSaber(), new BrightSaber()));
        assertFalse(EclipseSaber.canCombine(new BrightSaber(), new BrightSaber()));
    }
}
