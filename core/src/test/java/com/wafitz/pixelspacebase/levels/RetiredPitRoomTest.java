package com.wafitz.pixelspacebase.levels;

import com.watabou.utils.Bundle;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class RetiredPitRoomTest {
    @Test
    public void newRunsNeverOfferTheRetiredPair() {
        ArrayList<Room.Type> before = new ArrayList<>(Room.SPECIALS);
        try {
            Room.shuffleTypes();
            assertFalse(Room.SPECIALS.contains(Room.Type.WEAK_FLOOR));
            assertFalse(Room.SPECIALS.contains(Room.Type.PIT));
            assertTrue(Room.SPECIALS.contains(Room.Type.ARMORY));
            assertTrue(Room.SPECIALS.contains(Room.Type.VAULT));
        } finally {
            Room.SPECIALS = before;
        }
    }

    @Test
    public void oldSavedRoomPoolCannotReenableThePair() {
        ArrayList<Room.Type> before = new ArrayList<>(Room.SPECIALS);
        try {
            Room.restoreRoomsFromBundle(new Bundle() {
                @Override public boolean contains(String key) { return "rooms".equals(key); }
                @Override public String[] getStringArray(String key) {
                    return new String[]{"WEAK_FLOOR", "ARMORY", "PIT", "VAULT"};
                }
            });
            assertEquals(2, Room.SPECIALS.size());
            assertEquals(Room.Type.ARMORY, Room.SPECIALS.get(0));
            assertEquals(Room.Type.VAULT, Room.SPECIALS.get(1));
            // Existing map rooms must still deserialize with their old type names.
            assertEquals(Room.Type.PIT, Room.Type.valueOf("PIT"));
            assertEquals(Room.Type.WEAK_FLOOR, Room.Type.valueOf("WEAK_FLOOR"));
        } finally {
            Room.SPECIALS = before;
        }
    }
}
