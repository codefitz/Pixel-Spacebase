package com.wafitz.pixelspacebase.levels;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandFloorHolesTest {

    @Test
    public void scattersSeparatedHolesAcrossEligibleFloorCells() {
        TestLevel level = new TestLevel(12, 12);
        ArrayList<Integer> candidates = new ArrayList<>(Arrays.asList(
                cell(level, 2, 2), cell(level, 5, 2), cell(level, 8, 2),
                cell(level, 2, 6), cell(level, 5, 6)));

        assertEquals(5, CommandFloorHoles.scatter(level, candidates, 5));

        int holes = 0;
        for (int y = 0; y < level.height(); y++) {
            for (int x = 0; x < level.width(); x++) {
                int cell = cell(level, x, y);
                if (level.map[cell] != Terrain.CHASM) continue;
                holes++;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dx == 0 && dy == 0) continue;
                        assertTrue(level.map[cell(level, x + dx, y + dy)] != Terrain.CHASM);
                    }
                }
            }
        }
        assertEquals(5, holes);
    }

    @Test
    public void doesNotConvertPlasmaOrCreateAdjacentOrEdgeHoles() {
        TestLevel level = new TestLevel(8, 8);
        int water = cell(level, 5, 3);
        level.map[water] = Terrain.WATER;
        ArrayList<Integer> candidates = new ArrayList<>(Arrays.asList(
                cell(level, 3, 3), cell(level, 4, 3), water, cell(level, 0, 0)));

        assertEquals(1, CommandFloorHoles.scatter(level, candidates, 4));
        assertEquals(Terrain.WATER, level.map[water]);
        assertEquals(1, countHoles(level));
    }

    private static int cell(TestLevel level, int x, int y) {
        return x + y * level.width();
    }

    private static int countHoles(TestLevel level) {
        int count = 0;
        for (int tile : level.map) {
            if (tile == Terrain.CHASM) count++;
        }
        return count;
    }

    private static class TestLevel extends Level {

        private TestLevel(int levelWidth, int levelHeight) {
            width = levelWidth;
            height = levelHeight;
            length = levelWidth * levelHeight;
            map = new int[length];
            Arrays.fill(map, Terrain.EMPTY);
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
