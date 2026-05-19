package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class LevelRandomRespawnCellTest {

    @After
    public void tearDown() {
        Actor.clear();
    }

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

    private static class TestLevel extends Level {

        private TestLevel(int levelLength) {
            width = levelLength;
            height = 1;
            length = levelLength;
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
