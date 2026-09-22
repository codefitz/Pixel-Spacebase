package com.wafitz.pixelspacebase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FallTargetDepthTest {

    @Test
    public void fallReturnsToPreviousVisitedDepthWhenPossible() {
        assertEquals(2, SpacebaseRun.fallTargetDepth(3));
    }

    @Test
    public void fallFromFirstDepthUsesDeeperFallback() {
        assertEquals(2, SpacebaseRun.fallTargetDepth(1));
    }

    @Test
    public void pitRoomsArePlannedBeforeTheirHigherWeakFloor() {
        long originalSeed = SpacebaseRun.seed;
        try {
            long[] seeds = {0L, 1L, 0x1234ABCDL, Long.MAX_VALUE};
            for (long seed : seeds) {
                SpacebaseRun.seed = seed;
                for (int depth = 1; depth <= 25; depth++) {
                    assertEquals("pit plan at depth " + depth,
                            SpacebaseRun.hasWeakFloorAtDepth(depth + 1),
                            SpacebaseRun.needsPitRoomAtDepth(depth));
                }
            }
        } finally {
            SpacebaseRun.seed = originalSeed;
        }
    }

    @Test
    public void weakFloorsAreNotPlannedAboveLevelsWithoutPitRooms() {
        long originalSeed = SpacebaseRun.seed;
        try {
            SpacebaseRun.seed = 0L;
            assertFalse(SpacebaseRun.hasWeakFloorAtDepth(1));
            assertFalse(SpacebaseRun.hasWeakFloorAtDepth(5));
            assertFalse(SpacebaseRun.hasWeakFloorAtDepth(6));
            assertFalse(SpacebaseRun.hasWeakFloorAtDepth(21));
            assertFalse(SpacebaseRun.hasWeakFloorAtDepth(22));
        } finally {
            SpacebaseRun.seed = originalSeed;
        }
    }
}
