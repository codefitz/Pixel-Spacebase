package com.wafitz.pixelspacebase.levels;

import com.watabou.utils.Random;

import java.util.List;

/** Scatters isolated, walkable-space gaps across generated Command decks. */
final class CommandFloorHoles {

    private CommandFloorHoles() {
    }

    /**
     * Replaces eligible floor cells with open chasms. Call before level flag maps
     * are built; callers provide candidates that avoid important level features.
     */
    static int scatter(Level level, List<Integer> candidates, int target) {
        int placed = 0;
        while (placed < target && !candidates.isEmpty()) {
            int cell = candidates.remove(Random.Int(candidates.size()));
            if (!isEligibleFloor(level, cell) || touchesChasm(level, cell)) continue;

            level.map[cell] = Terrain.CHASM;
            placed++;
        }
        return placed;
    }

    private static boolean isEligibleFloor(Level level, int cell) {
        if (!level.insideMap(cell)) return false;
        int x = cell % level.width();
        int y = cell / level.width();
        if (x == 0 || y == 0 || x == level.width() - 1 || y == level.height() - 1) return false;

        int tile = level.map[cell];
        return tile == Terrain.EMPTY || tile == Terrain.EMPTY_DECO;
    }

    private static boolean touchesChasm(Level level, int cell) {
        int x = cell % level.width();
        int y = cell / level.width();
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int neighbor = cell + dx + dy * level.width();
                if (level.insideMap(neighbor)
                        && Math.abs(neighbor % level.width() - x) <= 1
                        && level.map[neighbor] == Terrain.CHASM) {
                    return true;
                }
            }
        }
        return false;
    }
}
