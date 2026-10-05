package com.wafitz.pixelspacebase.items.upgrades;

import com.watabou.utils.PathFinder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GrenadeBlastAreaTest {

    private static final int WIDTH = 11;
    private boolean[] blocked;

    @Before
    public void prepareDeck() {
        PathFinder.setMapSize(WIDTH, WIDTH);
        blocked = new boolean[WIDTH * WIDTH];
        for (int i = 0; i < WIDTH; i++) {
            blocked[cell(i, 0)] = true;
            blocked[cell(i, WIDTH - 1)] = true;
            blocked[cell(0, i)] = true;
            blocked[cell(WIDTH - 1, i)] = true;
        }
    }

    @Test
    public void blastIncludesSelfTargetAndNearbyHeroButStopsAtThreeTiles() {
        boolean[] affected = GrenadeUpgrade.blastArea(cell(5, 5), blocked);
        assertTrue(affected[cell(5, 5)]);
        assertTrue(affected[cell(8, 5)]);
        assertTrue(affected[cell(8, 8)]);
        assertFalse(affected[cell(9, 5)]);
        assertFalse(affected[cell(9, 9)]);
    }

    @Test
    public void wallProtectsCharactersOnTheOtherSide() {
        for (int y = 1; y < WIDTH - 1; y++) blocked[cell(5, y)] = true;
        boolean[] affected = GrenadeUpgrade.blastArea(cell(4, 5), blocked);
        assertTrue(affected[cell(3, 5)]);
        assertFalse(affected[cell(5, 5)]);
        assertFalse(affected[cell(6, 5)]);
    }

    @Test
    public void blastSnapshotSurvivesOtherPathfindingDuringDamage() {
        boolean[] firstBlast = GrenadeUpgrade.blastArea(cell(2, 2), blocked);
        GrenadeUpgrade.blastArea(cell(8, 8), blocked);
        assertTrue(firstBlast[cell(2, 2)]);
        assertFalse(firstBlast[cell(8, 8)]);
    }

    private static int cell(int x, int y) {
        return x + y * WIDTH;
    }
}
