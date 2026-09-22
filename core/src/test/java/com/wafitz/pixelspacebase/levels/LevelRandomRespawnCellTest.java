package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.SpacebaseRun;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LevelRandomRespawnCellTest {

    @Test
    public void randomRespawnCellReturnsMinusOneWhenNoCellIsValid() {
        TestLevel level = new TestLevel(6);

        Level.passable = new boolean[level.length()];
        SpacebaseRun.visible = new boolean[level.length()];
        Arrays.fill(SpacebaseRun.visible, true);

        assertEquals(-1, level.randomRespawnCell());
    }

    @Test
    public void randomRespawnCellFindsTheOnlyValidCell() {
        TestLevel level = new TestLevel(40);

        Level.passable = new boolean[level.length()];
        SpacebaseRun.visible = new boolean[level.length()];
        Arrays.fill(SpacebaseRun.visible, true);

        Level.passable[17] = true;
        SpacebaseRun.visible[17] = false;

        assertEquals(17, level.randomRespawnCell());
    }

    @Test
    public void exposedBridgeCellIsVacuumUnlessPressurized() {
        TestLevel level = new TestLevel(5, 5);
        Arrays.fill(level.map, Terrain.EMPTY);

        int bridge = 2 + 2 * level.width();
        level.map[bridge] = Terrain.EMPTY_SP;
        level.map[bridge - 1] = Terrain.CHASM;
        level.map[bridge + 1] = Terrain.CHASM;

        assertTrue(level.isVacuum(bridge));

        level = new TestLevel(5, 5);
        Arrays.fill(level.map, Terrain.EMPTY);
        level.map[bridge] = Terrain.EMPTY_SP;
        level.map[bridge - 1] = Terrain.CHASM;
        level.map[bridge + 1] = Terrain.CHASM;
        level.setPressurized(bridge);

        assertFalse(level.isVacuum(bridge));
    }

    private static class TestLevel extends Level {

        private TestLevel(int levelLength) {
            this(levelLength, 1);
        }

        private TestLevel(int levelWidth, int levelHeight) {
            width = levelWidth;
            height = levelHeight;
            length = levelWidth * levelHeight;
            map = new int[length];
            vacuum = new boolean[length];
            pressurized = new boolean[length];
        }

        @Override
        protected boolean build() {
            return true;
        }

        @Override
        protected void decorate() {
        }

        @Override
        protected void createMobs() {
        }

        @Override
        protected void createItems() {
        }
    }
}
