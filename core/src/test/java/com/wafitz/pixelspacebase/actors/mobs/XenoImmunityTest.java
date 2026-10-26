package com.wafitz.pixelspacebase.actors.mobs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.LockedDown;
import com.wafitz.pixelspacebase.actors.buffs.XenoInfection;
import com.wafitz.pixelspacebase.actors.hero.Belongings;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.MaintenanceLevel;
import com.wafitz.pixelspacebase.mines.AlienEgg;
import com.wafitz.pixelspacebase.mines.QueenNestEgg;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;

import static org.junit.Assert.*;

public class XenoImmunityTest {
    @Test public void facehuggerCanBeConstructedByTheSaveLoader() throws Exception {
        assertTrue(Modifier.isPublic(Facehugger.class.getModifiers()));
        assertTrue(Modifier.isPublic(Facehugger.class.getConstructor().getModifiers()));
    }

    @Test public void protectedClassesRejectBothInfectionEntrypointsWithoutSideEffects() throws Exception {
        for (HeroClass heroClass : new HeroClass[]{HeroClass.DM3000, HeroClass.SHAPESHIFTER}) {
            TestHero hero = hero(heroClass);
            assertTrue(hero.immunities().contains(XenoInfection.class));
            assertFalse(XenoInfection.infect(hero));
            assertFalse(XenoInfection.infectStrong(hero));
            assertNull(hero.buff(XenoInfection.class));
            assertNull(hero.buff(LockedDown.class));
            assertEquals(20, hero.HP);
            assertTrue("Rejection must not consume the first-infection reaction", hero.markXenoInfectionReactionShown());
        }
    }

    @Test public void olderSavedInfectionsCannotAttachToProtectedClasses() throws Exception {
        for (HeroClass heroClass : new HeroClass[]{HeroClass.DM3000, HeroClass.SHAPESHIFTER}) {
            TestHero hero = hero(heroClass);
            XenoInfection infection = new XenoInfection();
            assertFalse(infection.attachTo(hero));
            assertNull(infection.target);
            assertTrue(hero.buffs().isEmpty());
        }
    }

    @Test public void attachedLegacyInfectionRetiresBeforeWarningOrBurst() throws Exception {
        for (HeroClass heroClass : new HeroClass[]{HeroClass.DM3000, HeroClass.SHAPESHIFTER}) {
            TestHero hero = hero(heroClass);
            XenoInfection infection = new XenoInfection();
            infection.target = hero;
            hero.activeBuffs.add(infection);
            Field turns = XenoInfection.class.getDeclaredField("turns");
            turns.setAccessible(true);
            turns.setInt(infection, 4);
            assertTrue(infection.act());
            assertNull(hero.buff(XenoInfection.class));
            assertEquals(20, hero.HP);
            assertEquals(4, turns.getInt(infection));
        }
    }

    @Test public void ordinaryAndQueenEggsStillDropLootForProtectedClasses() throws Exception {
        Level previousLevel = SpacebaseRun.level;
        Field field = Actor.class.getDeclaredField("chars");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") HashSet<Char> chars = (HashSet<Char>) field.get(null);
        try {
            for (HeroClass heroClass : new HeroClass[]{HeroClass.DM3000, HeroClass.SHAPESHIFTER}) {
                TestHero hero = hero(heroClass);
                hero.pos = 123456; // Keep the inventory-only actor separate from any existing fixture.
                chars.add(hero);
                try {
                    for (AlienEgg egg : new AlienEgg[]{allocate(TestEgg.class), allocate(TestQueenEgg.class)}) {
                        TestLevel level = allocate(TestLevel.class);
                        level.heap = allocate(Heap.class);
                        level.heap.sprite = allocate(TestItemSprite.class);
                        SpacebaseRun.level = level;
                        egg.pos = hero.pos;
                        egg.activate();
                        assertNotNull(level.dropped);
                        assertEquals(hero.pos, level.dropCell);
                        assertTrue(((TestItemSprite) level.heap.sprite).dropped);
                        assertTrue(hero.buffs().isEmpty());
                        assertEquals(20, hero.HP);
                    }
                } finally { chars.remove(hero); }
            }
        } finally { SpacebaseRun.level = previousLevel; }
    }

    @Test public void facehuggersKeepAttackDamageButCannotImplantOrConsumeThemselvesOnImmuneHeroes() throws Exception {
        for (HeroClass heroClass : new HeroClass[]{HeroClass.DM3000, HeroClass.SHAPESHIFTER}) {
            TestHero hero = hero(heroClass);
            TestFacehugger facehugger = allocate(TestFacehugger.class);
            for (int i = 0; i < 20; i++) assertEquals(25, facehugger.attackProc(hero, 25));
            assertFalse(facehugger.died);
            assertNull(facehugger.state);
            assertTrue(hero.buffs().isEmpty());
        }
    }

    @Test public void commanderAndCaptainRemainSusceptibleAndKeepAttackDamage() throws Exception {
        for (HeroClass heroClass : new HeroClass[]{HeroClass.COMMANDER, HeroClass.CAPTAIN}) {
            TestHero hero = hero(heroClass);
            assertFalse(hero.isImmuneToXenoInfection());
            assertFalse(hero.immunities().contains(XenoInfection.class));
            assertTrue(new XenoInfection().attachTo(hero));
            TestFacehugger facehugger = allocate(TestFacehugger.class);
            for (int i = 0; i < 20; i++) assertEquals(25, facehugger.attackProc(hero, 25));
            assertFalse(facehugger.died);
            assertNotNull(hero.buff(XenoInfection.class));
        }
    }

    private static TestHero hero(HeroClass heroClass) throws Exception {
        TestHero hero = allocate(TestHero.class);
        hero.heroClass = heroClass;
        hero.HP = hero.HT = 20;
        hero.belongings = allocate(Belongings.class);
        hero.activeBuffs = new HashSet<>();
        return hero;
    }

    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        Field field = unsafe.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return type.cast(unsafe.getMethod("allocateInstance", Class.class).invoke(field.get(null), type));
    }

    private static class TestHero extends Hero {
        HashSet<Buff> activeBuffs;
        @Override public HashSet<Buff> buffs() { return activeBuffs; }
        @Override public <T extends Buff> T buff(Class<T> type) {
            for (Buff buff : activeBuffs) if (type.isInstance(buff)) return type.cast(buff);
            return null;
        }
        @Override public void add(Buff buff) { activeBuffs.add(buff); }
        @Override public void remove(Buff buff) { activeBuffs.remove(buff); }
    }

    private static class TestFacehugger extends Facehugger {
        boolean died;
        @Override public void die(Object cause) { died = true; }
    }

    private static class TestEgg extends AlienEgg {
        @Override protected Item hatchLoot() { return loot(); }
    }

    private static class TestQueenEgg extends QueenNestEgg {
        @Override protected Item hatchLoot() { return loot(); }
    }

    private static Item loot() {
        try { return allocate(Item.class); }
        catch (Exception e) { throw new AssertionError(e); }
    }

    private static class TestLevel extends MaintenanceLevel {
        Heap heap;
        Item dropped;
        int dropCell;
        @Override public Heap drop(Item item, int cell) {
            dropped = item;
            dropCell = cell;
            return heap;
        }
    }

    private static class TestItemSprite extends ItemSprite {
        boolean dropped;
        @Override public void drop() { dropped = true; }
    }
}
