package com.wafitz.pixelspacebase;

import com.wafitz.pixelspacebase.levels.Level;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SpacebaseRunVisibilityTest {

    @Test
    public void destinationLevelVisibilityIsResetToItsSizeBeforeRespawnSelection() {
        TestLevel destination = new TestLevel(50, 40);
        int onlyPassableCell = 1872;

        SpacebaseRun.visible = new boolean[1134];
        Arrays.fill(SpacebaseRun.visible, true);
        Level.passable = new boolean[destination.length()];
        Level.passable[onlyPassableCell] = true;

        SpacebaseRun.resetVisibilityForLevel(destination);

        assertEquals(destination.length(), SpacebaseRun.visible.length);
        assertFalse(SpacebaseRun.visible[onlyPassableCell]);
        assertEquals(onlyPassableCell, destination.randomRespawnCell());
    }

    private static class TestLevel extends Level {

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
