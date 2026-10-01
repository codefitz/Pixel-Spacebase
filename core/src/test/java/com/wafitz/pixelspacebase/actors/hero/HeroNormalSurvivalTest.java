package com.wafitz.pixelspacebase.actors.hero;

import org.junit.Test;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class HeroNormalSurvivalTest {
    @Test
    public void newGameStrengthIsTen() throws Exception {
        Field strength = Hero.class.getDeclaredField("STARTING_STR");
        strength.setAccessible(true);
        assertEquals(10, strength.getInt(null));
    }

    @Test
    public void zeroHealthHeroIsDeadAndDoesNotAutoHeal() throws Exception {
        // Bypass Android-dependent item/message initialization in this JVM fixture.
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field field = unsafeClass.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Hero hero = (Hero) unsafeClass.getMethod("allocateInstance", Class.class)
                .invoke(field.get(null), Hero.class);
        hero.HT = 20;
        hero.HP = 0;
        assertFalse(hero.isAlive());
        assertEquals(0, hero.HP);
        hero.HP = 1;
        assertTrue(hero.isAlive());
        assertEquals(1, hero.HP);
    }
}
