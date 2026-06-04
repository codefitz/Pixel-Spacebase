package com.wafitz.pixelspacebase.levels.features;

import com.wafitz.pixelspacebase.Statistics;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RescueCradleTest {

    @After
    public void tearDown() {
        Statistics.rescueCradleDepths = 0;
    }

    @Test
    public void allOpenedRequiresEveryRescueDepth() {
        Statistics.rescueCradleDepths = (1 << 1) | (1 << 6) | (1 << 11) | (1 << 16);

        assertFalse(RescueCradle.allOpened());
    }

    @Test
    public void allOpenedDetectsCompleteRescueRoute() {
        Statistics.rescueCradleDepths = (1 << 1) | (1 << 6) | (1 << 11) | (1 << 16) | (1 << 21);

        assertTrue(RescueCradle.allOpened());
    }
}
