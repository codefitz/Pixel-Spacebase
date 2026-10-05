package com.wafitz.pixelspacebase;

import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.levels.Level;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FogOfWarTest {

    @Test
    public void unusedMapAreaDoesNotReceiveFog() {
        boolean[] discoverable = new boolean[16];
        discoverable[1 + 1 * 4] = true;

        assertFalse(FogOfWar.touchesDiscoverableCell(4, 4, 4, 4, discoverable));
        assertFalse(FogOfWar.touchesDiscoverableCell(3, 3, 4, 4, discoverable));
    }

    @Test
    public void fogFollowsTheStationFootprintIncludingItsOuterEdge() {
        boolean[] discoverable = new boolean[16];
        discoverable[1 + 1 * 4] = true;

        assertTrue(FogOfWar.touchesDiscoverableCell(1, 1, 4, 4, discoverable));
        assertTrue(FogOfWar.touchesDiscoverableCell(2, 2, 4, 4, discoverable));
    }

    @Test
    public void outerWallIsRecognisedAsTheVisibleHullEdge() {
        boolean[] discoverable = new boolean[16];
        int[] map = new int[16];
        discoverable[1 + 1 * 4] = true;
        map[1 + 1 * 4] = Terrain.WALL;

        assertTrue(FogOfWar.touchesHullEdge(1, 1, 4, 4, discoverable, map));
    }

    @Test
    public void interiorFloorDoesNotBecomePartOfTheHullOutline() {
        boolean[] discoverable = new boolean[49];
        int[] map = new int[49];
        for (int y = 1; y <= 5; y++) {
            for (int x = 1; x <= 5; x++) discoverable[x + y * 7] = true;
        }
        assertFalse(FogOfWar.touchesHullEdge(3, 3, 7, 7, discoverable, map));
    }

    @Test
    public void hullInnerCornersRetainFogWhenTouchingFloor() {
        boolean[] discoverable = new boolean[25];
        int[] map = new int[25];
        discoverable[6] = discoverable[7] = discoverable[11] = discoverable[12] = true;
        map[6] = Terrain.WALL;
        map[7] = map[11] = map[12] = Terrain.EMPTY;
        assertFalse(FogOfWar.touchesHullEdge(2, 2, 5, 5, discoverable, map));
    }

    @Test
    public void openSpaceNeverCountsAsHull() {
        boolean[] footprint = new boolean[25];
        footprint[12] = true;
        assertFalse(Level.isHullCell(11, 5, 5, footprint));
        assertTrue(Level.isHullCell(12, 5, 5, footprint));
    }
}
