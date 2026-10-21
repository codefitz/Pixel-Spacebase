package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.watabou.utils.PathFinder;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class BossFallLandingTest {

    @Test
    public void everyBossFloorUsesSealedRoomForChasmLandings() throws Exception {
        int previousDepth = SpacebaseRun.depth;
        try {
            TestLevel level = new TestLevel();
            for (int depth : new int[]{5, 10, 15, 20, 25}) {
                SpacebaseRun.depth = depth;
                assertEquals(32, level.fallLandingCell(true));
                assertEquals(32, level.fallLandingCell(false));
            }
        } finally {
            SpacebaseRun.depth = previousDepth;
        }
    }

    @Test
    public void bossRescueApproachUsesDestinationMapRatherThanPreviousMapOffsets() throws Exception {
        int previousDepth = SpacebaseRun.depth;
        try {
            SpacebaseRun.depth = 10;
            TestLevel level = new TestLevel();
            PathFinder.setMapSize(32, 32);
            Level.passable[level.entrance] = false;
            level.map[level.entrance] = Terrain.WALL;
            Level.passable[level.entrance + level.width()] = true;
            level.map[level.entrance + level.width()] = Terrain.EMPTY;
            // A chamber already present in an older boss save is closer to the
            // entrance, but must not become the fallback landing spot.
            Field center = Level.class.getDeclaredField("doorlessRoomCenter");
            center.setAccessible(true);
            center.setInt(level, level.entrance - 1);
            Field radius = Level.class.getDeclaredField("doorlessRoomRadius");
            radius.setAccessible(true);
            radius.setInt(level, 0);
            Level.passable[level.entrance - 1] = true;
            level.map[level.entrance - 1] = Terrain.EMPTY;
            assertEquals(level.entrance + level.width(), level.rescueBossLandingCell());
        } finally {
            SpacebaseRun.depth = previousDepth;
        }
    }

    @Test
    public void ordinaryFloorsKeepForcedSealedRoomLandings() throws Exception {
        int previousDepth = SpacebaseRun.depth;
        try {
            SpacebaseRun.depth = 4;
            assertEquals(32, new TestLevel().fallLandingCell(true));
        } finally {
            SpacebaseRun.depth = previousDepth;
        }
    }

    private static class TestLevel extends Level {
        TestLevel() throws Exception {
            width = height = 7;
            length = width * height;
            map = new int[length];
            Arrays.fill(map, Terrain.WALL);
            mobs = new HashSet<>();
            passable = new boolean[length];
            entrance = 16;
            map[entrance] = Terrain.ENTRANCE;
            passable[entrance] = true;
            map[32] = Terrain.EMPTY;
            passable[32] = true;
            Field center = Level.class.getDeclaredField("doorlessRoomCenter");
            center.setAccessible(true);
            center.setInt(this, 32);
            Field radius = Level.class.getDeclaredField("doorlessRoomRadius");
            radius.setAccessible(true);
            radius.setInt(this, 0);
        }

        @Override public int randomRespawnCell() {
            throw new AssertionError("Fall placement must not use boss respawn offsets");
        }
        @Override protected boolean build() { return true; }
        @Override protected void decorate() { }
        @Override protected void createMobs() { }
        @Override protected void createItems() { }
    }
}
