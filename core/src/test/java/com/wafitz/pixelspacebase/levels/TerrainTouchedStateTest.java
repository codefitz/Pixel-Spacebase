package com.wafitz.pixelspacebase.levels;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TerrainTouchedStateTest {

    @Test
    public void touchedPanelsRemainPassableWithoutActivePanelFlags() {
        assertNotEquals(Terrain.OFFVENT, Terrain.TRAMPLED_OFFVENT);
        assertNotEquals(Terrain.INACTIVE_VENT, Terrain.SPENT_MINE);
        assertEquals(Terrain.flags[Terrain.EMPTY], Terrain.flags[Terrain.TRAMPLED_OFFVENT]);
        assertEquals(Terrain.flags[Terrain.EMPTY], Terrain.flags[Terrain.SPENT_MINE]);
    }
}
