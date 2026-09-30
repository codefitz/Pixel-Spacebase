package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.SpacebaseRun;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlasmaFloorTest {

    @Test
    public void stabilisedPlasmaBecomesPassableWeakPlatingThatExplosionsRupture() {
        Level previousLevel = SpacebaseRun.level;
        boolean[] previousPassable = Level.passable;
        boolean[] previousLosBlocking = Level.losBlocking;
        boolean[] previousFlamable = Level.flamable;
        boolean[] previousSecret = Level.secret;
        boolean[] previousSolid = Level.solid;
        boolean[] previousAvoid = Level.avoid;
        boolean[] previousWater = Level.water;
        boolean[] previousPit = Level.pit;

        try {
            TestPlasmaLevel level = new TestPlasmaLevel(5, 5);
            int cell = 2 + 2 * level.width();
            level.map[cell] = Terrain.WATER;
            SpacebaseRun.level = level;
            Level.passable = new boolean[level.length()];
            Level.losBlocking = new boolean[level.length()];
            Level.flamable = new boolean[level.length()];
            Level.secret = new boolean[level.length()];
            Level.solid = new boolean[level.length()];
            Level.avoid = new boolean[level.length()];
            Level.water = new boolean[level.length()];
            Level.pit = new boolean[level.length()];

            assertTrue(level.stabilisePlasma(cell));
            assertEquals(Terrain.STABILIZED_PLASMA, level.map[cell]);
            assertTrue(Level.passable[cell]);
            assertFalse(Level.water[cell]);
            assertFalse(level.isPlasmaCell(cell));

            assertTrue(level.rupturePlasmaFloor(cell));
            assertEquals(Terrain.CHASM, level.map[cell]);
            assertFalse(Level.passable[cell]);
        } finally {
            SpacebaseRun.level = previousLevel;
            Level.passable = previousPassable;
            Level.losBlocking = previousLosBlocking;
            Level.flamable = previousFlamable;
            Level.secret = previousSecret;
            Level.solid = previousSolid;
            Level.avoid = previousAvoid;
            Level.water = previousWater;
            Level.pit = previousPit;
        }
    }

    private static class TestPlasmaLevel extends Level {

        private TestPlasmaLevel(int levelWidth, int levelHeight) {
            width = levelWidth;
            height = levelHeight;
            length = levelWidth * levelHeight;
            map = new int[length];
        }

        @Override
        public boolean isPlasmaCell(int cell) {
            return insideMap(cell) && map[cell] == Terrain.WATER;
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
