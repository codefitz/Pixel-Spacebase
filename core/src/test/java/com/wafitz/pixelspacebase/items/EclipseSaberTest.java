package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.actors.mobs.npcs.Leonard;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.items.weapon.melee.BrightSaber;
import com.wafitz.pixelspacebase.items.weapon.melee.DarkSaber;
import com.wafitz.pixelspacebase.items.weapon.melee.EclipseSaber;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import android.content.SharedPreferences;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import static org.junit.Assert.*;

public class EclipseSaberTest {
    private static Field preferencesField;
    private static Object preferences;
    private static Object originalPreferences;

    @BeforeClass public static void providePreferencesWithoutAnAndroidActivity() throws Exception {
        Class<?> type = Class.forName("com.wafitz.pixelspacebase.Preferences");
        preferences = type.getEnumConstants()[0];
        preferencesField = type.getDeclaredField("prefs");
        preferencesField.setAccessible(true);
        originalPreferences = preferencesField.get(preferences);
        SharedPreferences fixture = (SharedPreferences) Proxy.newProxyInstance(
                SharedPreferences.class.getClassLoader(), new Class<?>[]{SharedPreferences.class},
                (proxy, method, arguments) -> {
                    if ("getString".equals(method.getName())) {
                        return "language".equals(arguments[0]) ? "en" : arguments[1];
                    }
                    throw new AssertionError("Unexpected preference call: " + method.getName());
                });
        preferencesField.set(preferences, fixture);
    }

    @AfterClass public static void restorePreferences() throws Exception {
        if (preferencesField != null) preferencesField.set(preferences, originalPreferences);
    }

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
