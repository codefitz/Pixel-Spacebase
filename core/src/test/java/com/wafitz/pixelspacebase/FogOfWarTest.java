package com.wafitz.pixelspacebase;

import com.wafitz.pixelspacebase.levels.Terrain;

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
        boolean[] discoverable = new boolean[16];
        int[] map = new int[16];
        discoverable[1 + 1 * 4] = true;
        discoverable[2 + 1 * 4] = true;
        map[1 + 1 * 4] = Terrain.WALL;
        map[2 + 1 * 4] = Terrain.EMPTY;

        assertFalse(FogOfWar.touchesHullEdge(2, 1, 4, 4, discoverable, map));
    }
}
