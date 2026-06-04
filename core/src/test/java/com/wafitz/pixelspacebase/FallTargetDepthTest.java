package com.wafitz.pixelspacebase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FallTargetDepthTest {

    @Test
    public void fallReturnsToPreviousVisitedDepthWhenPossible() {
        assertEquals(2, SpacebaseRun.fallTargetDepth(3));
    }

    @Test
    public void fallFromFirstDepthUsesDeeperFallback() {
        assertEquals(2, SpacebaseRun.fallTargetDepth(1));
    }
}
