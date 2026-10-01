package com.wafitz.pixelspacebase.levels;

import org.junit.Test;
import java.util.HashSet;
import static org.junit.Assert.*;

public class SurvivorDistributionTest {
    @Test public void everyChapterSelectsOnlyRegularFloorsAndVariesBetweenRuns() {
        for (int chapter = 0; chapter < 5; chapter++) {
            int start = chapter == 4 ? 22 : chapter * 5 + 1;
            int count = chapter == 4 ? 3 : 4;
            HashSet<Integer> depths = new HashSet<>();
            for (long seed = 0; seed < 100; seed++) {
                int depth = RegularLevel.survivorDepth(seed, chapter);
                assertTrue(depth >= start && depth < start + count);
                assertEquals(depth, RegularLevel.survivorDepth(seed, chapter));
                depths.add(depth);
            }
            assertEquals(count, depths.size());
        }
    }
}
