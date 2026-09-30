package com.wafitz.pixelspacebase.ui;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HunterSensorMarkersTest {

    @Test
    public void onlyActiveModulesMarkTargetsBeyondNormalVision() {
        assertTrue(HunterSensorMarkers.shouldDisplayMarker(true, false));
        assertFalse(HunterSensorMarkers.shouldDisplayMarker(false, false));
        assertFalse(HunterSensorMarkers.shouldDisplayMarker(true, true));
    }
}
