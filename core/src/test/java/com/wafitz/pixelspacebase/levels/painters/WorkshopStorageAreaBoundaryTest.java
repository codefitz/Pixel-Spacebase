package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.items.Item;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class WorkshopStorageAreaBoundaryTest {

    @Test
    public void storageSurvivesWorkshopAreaBoundary() {
        int previousDepth = SpacebaseRun.depth;
        try {
            Workshop.resetStorage();
            SpacebaseRun.depth = 4;
            ArrayList<Item> stored = Workshop.storageForCurrentDepth();
            // A null placeholder avoids constructing Items, which requires Android preferences in JVM tests.
            stored.add(null);

            // Depth 6 follows the depth-5 boss and starts a new shop area; storage must not be reset.
            SpacebaseRun.depth = 6;
            assertSame(stored, Workshop.storageForCurrentDepth());
            assertEquals(1, Workshop.storageForCurrentDepth().size());
        } finally {
            Workshop.resetStorage();
            SpacebaseRun.depth = previousDepth;
        }
    }
}
