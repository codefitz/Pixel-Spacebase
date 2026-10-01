package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.MaintenanceLevel;
import org.junit.Test;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

public class WorkshopStorageTransferTest {
    @Test
    public void generatedChestConsumesStorageAndCannotReceiveItTwice() throws Exception {
        Workshop.resetStorage();
        try {
            TestLevel level = new TestLevel();
            Heap chest = new Heap();
            chest.type = Heap.Type.WORKSHOP_STORAGE;
            level.heaps.put(12, chest);
            // Placeholder avoids Android-dependent item construction; TestLevel records delivery.
            Workshop.storageForCurrentDepth().add(null);
            Method place = Workshop.class.getDeclaredMethod("placeStorageChests",
                    com.wafitz.pixelspacebase.levels.Level.class, int[].class);
            place.setAccessible(true);
            place.invoke(null, level, new int[]{12});
            assertEquals(1, level.deliveries);
            assertTrue(Workshop.storageForCurrentDepth().isEmpty());
            Workshop.deliverStorageTo(level);
            assertEquals(1, level.deliveries);
        } finally {
            Workshop.resetStorage();
        }
    }

    private static class TestLevel extends MaintenanceLevel {
        int deliveries;
        TestLevel() {
            heaps = new com.watabou.utils.SparseArray<Heap>() {
                private Heap chest;
                @Override public void put(int key, Heap value) { chest = value; }
                @Override public Heap get(int key) { return chest; }
                @Override public int[] keyArray() { return new int[]{12}; }
            };
        }
        @Override public Heap drop(Item item, int cell) {
            deliveries++;
            return heaps.get(cell);
        }
    }
}
