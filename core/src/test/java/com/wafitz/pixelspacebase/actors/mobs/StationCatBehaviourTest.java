package com.wafitz.pixelspacebase.actors.mobs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Domination;
import com.wafitz.pixelspacebase.actors.buffs.YRescueJourney;
import com.wafitz.pixelspacebase.actors.hero.Belongings;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.npcs.StationCat;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.utils.PathFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class StationCatBehaviourTest {
    private Level previousLevel;
    private Hero previousHero;
    private int previousDepth;
    private boolean[] previousPassable, previousAvoid, previousFov;
    private int[] previousNeighbours;
    private String previousIdentity;
    private Object previousCarried;
    private final HashMap<Field, Object> questState = new HashMap<>();
    private HashSet<Actor> actors;
    private HashSet<Actor> previousActors;
    private TestLevel level;
    private TestHero hero;

    @Before public void fixture() throws Exception {
        previousLevel = SpacebaseRun.level; previousHero = SpacebaseRun.hero; previousDepth = SpacebaseRun.depth;
        previousPassable = Level.passable; previousAvoid = Level.avoid; previousFov = Level.fieldOfView;
        previousNeighbours = PathFinder.NEIGHBOURS8;
        PathFinder.NEIGHBOURS8 = new int[]{-10, -9, -8, -1, 1, 8, 9, 10};
        previousIdentity = (String) field(SpacebaseRun.class, "rescueLevelIdentity").get(null);
        previousCarried = field(StationCat.class, "carriedCat").get(null);
        for (Field f : StationCat.Quest.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers())) {
                f.setAccessible(true); questState.put(f, f.get(null));
            }
        }
        actors = actorSet(); previousActors = new HashSet<>(actors);
        StationCat.Quest.reset(); SpacebaseRun.selectRescueLevel("");
        level = new TestLevel(); SpacebaseRun.level = level;
        hero = allocate(TestHero.class); hero.activeBuffs = new HashSet<>();
        hero.belongings = allocate(Belongings.class); hero.HP = hero.HT = 20; hero.pos = 20;
        SpacebaseRun.hero = hero; SpacebaseRun.depth = 10;
    }

    @After public void restore() throws Exception {
        SpacebaseRun.level = previousLevel; SpacebaseRun.hero = previousHero; SpacebaseRun.depth = previousDepth;
        Level.passable = previousPassable; Level.avoid = previousAvoid; Level.fieldOfView = previousFov;
        PathFinder.NEIGHBOURS8 = previousNeighbours;
        SpacebaseRun.selectRescueLevel(previousIdentity);
        for (Field f : questState.keySet()) f.set(null, questState.get(f));
        field(StationCat.class, "carriedCat").set(null, previousCarried);
        actors.clear(); actors.addAll(previousActors);
    }

    @Test public void followingCatPreemptsAnExistingHeroTargetEvenWhenHeroIsCloser() throws Exception {
        TestMob enemy = enemy(19); enemy.enemy = hero;
        TestCat cat = cat(22, true);
        for (int i = 0; i < 30; i++) assertSame(cat, enemy.chooseEnemy());
    }

    @Test public void waitingCatIsIgnoredAndAnOldCatTargetIsReleased() throws Exception {
        TestMob enemy = enemy(19);
        TestCat cat = cat(22, false); enemy.enemy = cat;
        assertFalse(enemy.seesEnemy(cat));
        for (int i = 0; i < 30; i++) assertSame(hero, enemy.chooseEnemy());
    }

    @Test public void invisibleOrUnseenCatDoesNotStealAVisibleTarget() throws Exception {
        TestMob enemy = enemy(19); enemy.enemy = hero;
        TestCat cat = cat(22, true);
        Level.fieldOfView[cat.pos] = false;
        assertSame(hero, enemy.chooseEnemy());
        Level.fieldOfView[cat.pos] = true; cat.invisible = 1;
        assertSame(hero, enemy.chooseEnemy());
    }

    @Test public void dominationKeepsItsOwnEnemyRules() throws Exception {
        TestMob controlled = enemy(19);
        controlled.activeBuffs.add(new Domination());
        TestMob foe = enemy(21);
        cat(22, true);
        assertSame(foe, controlled.chooseEnemy());
    }

    @Test public void bossesKillFollowingAndWaitingCatsInTheirRoom() throws Exception {
        TestMob boss = enemy(19); boss.properties().add(Char.Property.BOSS);
        for (boolean following : new boolean[]{true, false}) {
            TestCat cat = cat(22, following);
            StationCat.killInBossRoom(boss);
            assertEquals(1, cat.deaths);
        }
    }

    @Test public void doorsAndWallsKeepTheCatOutsideTheBossRoom() throws Exception {
        TestMob boss = enemy(29); boss.properties().add(Char.Property.BOSS);
        TestCat cat = cat(33, false);
        for (int y = 1; y < 6; y++) level.wall(4 + y * 9);
        for (int door : new int[]{Terrain.DOOR, Terrain.OPEN_DOOR, Terrain.LOCKED_DOOR}) {
            level.map[31] = door; Level.passable[31] = door != Terrain.LOCKED_DOOR;
            StationCat.killInBossRoom(boss);
            assertEquals(0, cat.deaths);
        }
        cat.pos = 30;
        StationCat.killInBossRoom(boss);
        assertEquals(1, cat.deaths);
    }

    @Test public void ordinaryEnemyDoesNotApplyTheBossRoomKillRule() throws Exception {
        TestMob enemy = enemy(19);
        TestCat cat = cat(22, false);
        StationCat.killInBossRoom(enemy);
        assertEquals(0, cat.deaths);
    }

    @Test public void prisonRetreatPreservesTheCatAndReturnsHerOnlyOnce() throws Exception {
        TestCat cat = cat(22, true); cat.HP = 3;
        assertTrue(StationCat.retreatFrom(level));
        assertSame(cat, quest("returningCat")); assertEquals(9, quest("returnDepth"));
        assertFalse(level.mobs.contains(cat)); assertFalse(cat.isActiveFollower()); assertEquals(3, cat.HP);
        assertFalse(StationCat.retreatFrom(level));

        level = new TestLevel(); level.exit = 40; SpacebaseRun.level = level; SpacebaseRun.depth = 9;
        // Skip Android's scheduler registry in this JVM fixture; emulator checks cover registration.
        actors.add(cat);
        StationCat.placeReturningOn(level);
        assertEquals(1, level.mobs.size()); assertTrue(level.mobs.contains(cat));
        assertEquals(3, cat.HP); assertFalse(cat.isActiveFollower());
        assertNull(quest("returningCat")); assertEquals(-1, quest("returnDepth"));
        StationCat.placeReturningOn(level);
        assertEquals(1, level.mobs.size());
        SpacebaseRun.depth = 1;
        assertFalse(StationCat.canSpawnOnFirstLevel());
    }

    @Test public void queuedReturnRemovesAStaleDeckCopyWithoutLosingTheCanonicalCat() throws Exception {
        TestCat original = cat(22, false);
        StationCat.retreatFrom(level);
        TestCat stale = cat(23, true);
        field(StationCat.Quest.class, "following").setBoolean(null, true); // Restoring a stale follower sets this.
        StationCat.placeReturningOn(level); // Still on deck 10, so only reconcile the stale copy.
        assertTrue(level.mobs.isEmpty()); assertFalse(stale.isActiveFollower());
        assertFalse(StationCat.isFollowing());
        assertSame(original, quest("returningCat")); assertEquals(9, quest("returnDepth"));
    }

    @Test public void aBlockedReturnStaysPendingAndCarrierStateCancelsIt() throws Exception {
        TestCat cat = cat(22, false); StationCat.retreatFrom(level);
        SpacebaseRun.depth = 9;
        for (int i = 0; i < level.length(); i++) level.wall(i);
        StationCat.placeReturningOn(level);
        assertSame(cat, quest("returningCat")); assertTrue(level.mobs.isEmpty());
        StationCat.Quest.markCarried();
        assertNull(quest("returningCat")); assertEquals(-1, quest("returnDepth"));
        assertTrue(StationCat.isCarried());
    }

    @Test public void aRescueArenaReturnsTheCatToTheJourneyOrigin() throws Exception {
        YRescueJourney journey = new YRescueJourney();
        journey.destination = YRescueJourney.Destination.BOSS;
        journey.destinationIdentity = "boss_1"; journey.sourceDepth = 7;
        hero.activeBuffs.add(journey); SpacebaseRun.selectRescueLevel("boss_1");
        cat(22, false); StationCat.retreatFrom(level);
        assertEquals(7, quest("returnDepth"));
    }

    private TestMob enemy(int pos) throws Exception {
        TestMob mob = allocate(TestMob.class); mob.activeBuffs = new HashSet<>();
        mob.initProperties(); mob.HP = mob.HT = 10; mob.pos = pos; mob.hostile = true;
        mob.HUNTING = mob.new Hunting(); mob.WANDERING = mob.new Wandering(); mob.state = mob.HUNTING;
        level.mobs.add(mob); return mob;
    }

    private TestCat cat(int pos, boolean following) throws Exception {
        TestCat cat = allocate(TestCat.class); cat.pos = pos; cat.HP = cat.HT = 4; cat.ally = following;
        field(StationCat.class, "following").setBoolean(cat, following);
        level.mobs.add(cat); return cat;
    }

    private static Object quest(String name) throws Exception { return field(StationCat.Quest.class, name).get(null); }
    private static Field field(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name); field.setAccessible(true); return field;
    }
    @SuppressWarnings("unchecked") private static HashSet<Actor> actorSet() throws Exception {
        return (HashSet<Actor>) field(Actor.class, "all").get(null);
    }
    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe"); Field field = unsafe.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return type.cast(unsafe.getMethod("allocateInstance", Class.class).invoke(field.get(null), type));
    }

    private static class TestMob extends Mob {
        HashSet<Buff> activeBuffs;
        void initProperties() { properties = new HashSet<>(); }
        @Override public <T extends Buff> T buff(Class<T> type) {
            for (Buff buff : activeBuffs) if (type.isInstance(buff)) return type.cast(buff);
            return null;
        }
    }
    private static class TestHero extends Hero {
        HashSet<Buff> activeBuffs;
        @Override public <T extends Buff> T buff(Class<T> type) {
            for (Buff buff : activeBuffs) if (type.isInstance(buff)) return type.cast(buff);
            return null;
        }
    }
    private static class TestCat extends StationCat {
        int deaths;
        @Override public void die(Object source) { deaths++; HP = 0; SpacebaseRun.level.mobs.remove(this); }
        @Override protected void onRemove() { }
    }
    private static class TestLevel extends Level {
        TestLevel() {
            width = 9; height = 7; length = width * height; map = new int[length]; mobs = new HashSet<>();
            Level.passable = new boolean[length]; Level.avoid = new boolean[length]; Level.fieldOfView = new boolean[length];
            for (int cell = 0; cell < length; cell++) {
                map[cell] = insideMap(cell) ? Terrain.EMPTY : Terrain.WALL;
                Level.passable[cell] = insideMap(cell); Level.fieldOfView[cell] = true;
            }
        }
        void wall(int cell) { map[cell] = Terrain.WALL; Level.passable[cell] = false; }
        @Override protected boolean build() { return true; }
        @Override protected void decorate() { }
        @Override protected void createMobs() { }
        @Override protected void createItems() { }
        @Override public int randomRespawnCell() { return -1; }
    }
}
