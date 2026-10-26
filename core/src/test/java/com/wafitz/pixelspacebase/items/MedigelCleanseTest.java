package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.blobs.Fire;
import com.wafitz.pixelspacebase.actors.buffs.Acid;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.hero.Belongings;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.levels.HabitationRingLevel;
import com.wafitz.pixelspacebase.levels.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class MedigelCleanseTest {
    private Level previousLevel;
    private TestHero hero;
    private RecordingMedigel gel;
    private TestFire fire;

    @Before public void fixture() throws Exception {
        previousLevel = SpacebaseRun.level;
        SpacebaseRun.level = allocate(HabitationRingLevel.class);
        SpacebaseRun.level.blobs = new HashMap<>();
        fire = new TestFire();
        fire.cur = new int[]{0, 4, 6};
        fire.pending(new int[]{0, 2, 3});
        fire.volume = 10;
        SpacebaseRun.level.blobs.put(Fire.class, fire);
        hero = allocate(TestHero.class);
        hero.activeBuffs = new HashSet<>();
        hero.belongings = allocate(Belongings.class);
        hero.heroClass = HeroClass.DM3000;
        hero.pos = 1;
        hero.HP = 7;
        hero.HT = 20;
        gel = allocate(RecordingMedigel.class);
        volume(3);
    }

    @After public void restoreLevel() { SpacebaseRun.level = previousLevel; }

    @Test public void acidAloneIsRemovedForOneChargeWithoutHealing() {
        affect(new Acid());
        assertTrue(gel.cleanse(hero));
        assertNull(hero.buff(Acid.class));
        assertEquals(2, volume());
        assertEquals(7, hero.HP);
    }

    @Test public void fireAndAcidTogetherCostOneChargeAndOnlyClearTheHerosCell() {
        affect(new Acid());
        affect(new TestBurning());
        assertTrue(gel.cleanse(hero));
        assertNull(hero.buff(Acid.class));
        assertNull(hero.buff(Burning.class));
        assertEquals(2, volume());
        assertEquals(7, hero.HP);
        assertEquals(0, fire.cur[hero.pos]);
        assertEquals(0, fire.pendingAt(hero.pos));
        assertEquals(6, fire.cur[2]);
        assertEquals(3, fire.pendingAt(2));
        assertEquals(6, fire.volume);
    }

    @Test public void otherClassesKeepTheirExistingFireOnlyEffect() {
        for (HeroClass heroClass : HeroClass.values()) {
            if (heroClass == HeroClass.DM3000) continue;
            hero.heroClass = heroClass;
            hero.activeBuffs.clear();
            affect(new Acid());
            assertFalse(gel.cleanse(hero));
            assertNotNull(hero.buff(Acid.class));
            assertEquals(3, volume());
            affect(new TestBurning());
            assertTrue(gel.cleanse(hero));
            assertNull(hero.buff(Burning.class));
            assertNotNull(hero.buff(Acid.class));
            assertEquals(2, volume());
            assertEquals(7, hero.HP);
            volume(3);
        }
    }

    @Test public void cleanChassisAndEmptyContainerLeaveEverythingUntouched() {
        assertFalse(gel.cleanse(hero));
        assertEquals(3, volume());
        assertEquals(4, fire.cur[hero.pos]);
        affect(new Acid());
        affect(new TestBurning());
        volume(0);
        assertFalse(gel.cleanse(hero));
        assertNotNull(hero.buff(Acid.class));
        assertNotNull(hero.buff(Burning.class));
        assertEquals(0, volume());
        assertEquals(4, fire.cur[hero.pos]);
        assertEquals(7, hero.HP);
    }

    @Test public void dm3000MenuAndQuickslotUseCleanseEvenWithAnOldDefaultAction() {
        assertTrue(gel.actions(hero).contains("CLEANSE"));
        assertFalse(gel.actions(hero).contains("DRINK"));
        assertFalse(gel.actions(hero).contains("EXTINGUISH"));
        gel.defaultAction = "DRINK";
        gel.executeQuickslot(hero);
        assertEquals("CLEANSE", gel.action);
    }

    @Test public void otherClassesKeepHealAndExtinguishActions() {
        for (HeroClass heroClass : HeroClass.values()) {
            if (heroClass == HeroClass.DM3000) continue;
            hero.heroClass = heroClass;
            assertTrue(gel.actions(hero).contains("DRINK"));
            assertTrue(gel.actions(hero).contains("EXTINGUISH"));
            assertFalse(gel.actions(hero).contains("CLEANSE"));
        }
    }

    private void affect(Buff buff) {
        buff.target = hero;
        hero.activeBuffs.add(buff);
    }

    private int volume() {
        try {
            Field field = MedigelContainer.class.getDeclaredField("volume");
            field.setAccessible(true);
            return field.getInt(gel);
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }

    private void volume(int volume) {
        try {
            Field field = MedigelContainer.class.getDeclaredField("volume");
            field.setAccessible(true);
            field.setInt(gel, volume);
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }

    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        Field field = unsafe.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return type.cast(unsafe.getMethod("allocateInstance", Class.class).invoke(field.get(null), type));
    }

    private static class TestHero extends Hero {
        HashSet<Buff> activeBuffs;
        @Override public <T extends Buff> T buff(Class<T> type) {
            for (Buff buff : activeBuffs) if (type.isInstance(buff)) return type.cast(buff);
            return null;
        }
        @Override public void remove(Buff buff) { activeBuffs.remove(buff); }
    }

    private static class TestBurning extends Burning {
        @Override public void fx(boolean on) { }
    }

    private static class TestFire extends Fire {
        void pending(int[] values) { off = values; }
        int pendingAt(int cell) { return off[cell]; }
    }

    private static class RecordingMedigel extends MedigelContainer {
        String action;
        @Override public void execute(Hero hero, String action) { this.action = action; }
    }
}
