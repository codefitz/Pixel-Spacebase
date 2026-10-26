package com.wafitz.pixelspacebase.items.weapon.melee;

import com.wafitz.pixelspacebase.actors.hero.Belongings;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.blasters.MissileBlaster;
import com.wafitz.pixelspacebase.items.containers.Container;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DM3000MeltTest {
    private TestHero hero;
    private DM3000Launcher launcher;
    private TestWeapon weapon;

    @Before public void inventory() throws Exception {
        // Inventory-only fixtures avoid the Android activity and graphics constructors.
        hero = allocate(TestHero.class);
        hero.belongings = allocate(Belongings.class);
        hero.belongings.backpack = allocate(Container.class);
        hero.belongings.backpack.items = new ArrayList<>();
        launcher = allocate(DM3000Launcher.class);
        hero.belongings.weapon = launcher;
        weapon = allocate(TestWeapon.class);
        weapon.quantity(1);
        weapon.levelKnown = weapon.malfunctioningKnown = true;
        hero.belongings.backpack.items.add(weapon);
    }

    @Test public void ordinaryIdentifiedWorkingMeleeWeaponRemainsEligible() {
        assertTrue(DM3000Launcher.canMelt(hero, weapon));
        assertTrue(hero.belongings.backpack.contains(weapon));
        assertEquals(1, weapon.quantity());
        assertEquals(0f, hero.spent, 0f);
    }

    @Test public void blasterAndLauncherAreProtectedEvenWithoutUniqueFlag() throws Exception {
        MissileBlaster blaster = allocate(MissileBlaster.class);
        hero.belongings.backpack.items.add(blaster);
        hero.belongings.backpack.items.add(launcher);
        launcher.unique = false; // Type protection also covers older/restored item flags.
        assertFalse(DM3000Launcher.canMelt(hero, blaster));
        assertFalse(DM3000Launcher.canMelt(hero, launcher));
        assertTrue(hero.belongings.backpack.contains(blaster));
        assertTrue(hero.belongings.backpack.contains(launcher));
        assertSame(launcher, hero.belongings.weapon);
        assertEquals(0f, hero.spent, 0f);
    }

    @Test public void equippedAndUniqueMeleeWeaponsAreProtected() {
        hero.belongings.weapon = weapon;
        assertFalse(DM3000Launcher.canMelt(hero, weapon));
        hero.belongings.weapon = launcher;
        weapon.unique = true;
        assertFalse(DM3000Launcher.canMelt(hero, weapon));
    }

    @Test public void unidentifiedAndMalfunctioningWeaponsAreProtected() {
        weapon.levelKnown = false;
        assertFalse(DM3000Launcher.canMelt(hero, weapon));
        weapon.levelKnown = true;
        weapon.malfunctioningKnown = false;
        assertFalse(DM3000Launcher.canMelt(hero, weapon));
        weapon.malfunctioningKnown = true;
        weapon.malfunctioning = true;
        assertFalse(DM3000Launcher.canMelt(hero, weapon));
    }

    @Test public void cancellingSelectionConsumesNoItemOrTurn() {
        assertFalse(DM3000Launcher.canMelt(hero, null));
        launcher.meltSelector(hero).onSelect(null);
        assertEquals(1, hero.belongings.backpack.items.size());
        assertSame(weapon, hero.belongings.backpack.items.get(0));
        assertSame(launcher, hero.belongings.weapon);
        assertEquals(1, weapon.quantity());
        assertEquals(0f, hero.spent, 0f);
    }

    @Test public void staleSelectionConsumesNoItemOrTurn() {
        hero.belongings.backpack.items.remove(weapon);
        launcher.meltSelector(hero).onSelect(weapon);
        assertTrue(hero.belongings.backpack.items.isEmpty());
        assertSame(launcher, hero.belongings.weapon);
        assertEquals(1, weapon.quantity());
        assertEquals(0f, hero.spent, 0f);
    }

    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        Field field = unsafe.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return type.cast(unsafe.getMethod("allocateInstance", Class.class).invoke(field.get(null), type));
    }

    private static class TestHero extends Hero {
        float spent;
        @Override public void spendAndNext(float time) { spent += time; }
    }

    private static class TestWeapon extends MeleeWeapon { }
}
