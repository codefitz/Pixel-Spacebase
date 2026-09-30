package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.mobs.SignalSiren;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import static org.junit.Assert.*;

public class SignalSirenTeleportTest {
    @Test public void includesPlasmaAndChasmButNotWallsBordersOrCurrentCell() {
        boolean[] previousPassable = Level.passable;
        boolean[] previousPit = Level.pit;
        try {
            TestLevel level = new TestLevel();
            Level.passable = new boolean[level.length()];
            Level.pit = new boolean[level.length()];
            Room room = new Room();
            room.set(1, 1, 6, 6);
            int hero = 2 + 2 * 9;
            int floor = 3 + 2 * 9;
            int chasm = 4 + 2 * 9;
            int plasma = 5 + 2 * 9;
            level.map[hero] = level.map[floor] = Terrain.EMPTY;
            Level.passable[hero] = Level.passable[floor] = true;
            level.map[chasm] = Terrain.CHASM;
            Level.pit[chasm] = true;
            level.map[plasma] = Terrain.WATER;
            // Even a pool not marked passable remains eligible on Command decks.
            Level.passable[1 + 3 * 9] = true; // Room border must never be selected.
            Level.passable[7 + 3 * 9] = true; // Another room must never be selected.
            ArrayList<Integer> candidates = SignalSiren.roomTeleportCells(level, room, hero);
            assertEquals(3, candidates.size());
            assertTrue(candidates.contains(floor));
            assertTrue(candidates.contains(chasm));
            assertTrue(candidates.contains(plasma));
        } finally { Level.passable = previousPassable; Level.pit = previousPit; }
    }

    @Test public void occupiedCellIsExcluded() throws Exception {
        boolean[] previousPassable = Level.passable;
        boolean[] previousPit = Level.pit;
        Field field = Actor.class.getDeclaredField("chars");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") HashSet<Char> chars = (HashSet<Char>) field.get(null);
        Char occupant = new Char() { };
        occupant.pos = 21;
        try {
            TestLevel level = new TestLevel();
            Level.passable = new boolean[level.length()];
            Level.pit = new boolean[level.length()];
            Level.passable[21] = true;
            Room room = new Room();
            room.set(1, 1, 6, 6);
            chars.add(occupant); // Position fixture without Android's actor-ID registry.
            assertTrue(SignalSiren.roomTeleportCells(level, room, 20).isEmpty());
        } finally {
            chars.remove(occupant);
            Level.passable = previousPassable; Level.pit = previousPit;
        }
    }

    @Test public void noRoomMeansNoTeleport() {
        assertTrue(SignalSiren.roomTeleportCells(new TestLevel(), null, 20).isEmpty());
    }

    private static class TestLevel extends DeepContainmentDeckLevel {
        TestLevel() {
            width = height = 9;
            length = width * height;
            map = new int[length];
            java.util.Arrays.fill(map, Terrain.WALL);
        }
    }
}
