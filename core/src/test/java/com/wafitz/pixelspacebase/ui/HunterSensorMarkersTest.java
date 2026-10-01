package com.wafitz.pixelspacebase.ui;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class HunterSensorMarkersTest {

    @Test
    public void scannerTexturesAreOpaqueWithBlueItemsAndRedTraps() {
        assertEquals(255, HunterSensorMarkers.ITEM_SIGNAL >>> 24);
        assertEquals(255, HunterSensorMarkers.TRAP_SIGNAL >>> 24);
        int itemRed = (HunterSensorMarkers.ITEM_SIGNAL >>> 16) & 255;
        int itemGreen = (HunterSensorMarkers.ITEM_SIGNAL >>> 8) & 255;
        int itemBlue = HunterSensorMarkers.ITEM_SIGNAL & 255;
        assertTrue(itemBlue > itemRed && itemBlue > itemGreen);
        int trapRed = (HunterSensorMarkers.TRAP_SIGNAL >>> 16) & 255;
        int trapBlue = HunterSensorMarkers.TRAP_SIGNAL & 255;
        assertTrue(trapRed > trapBlue);
    }

    @Test
    public void onlyActiveModulesMarkTargetsBeyondNormalVision() {
        assertTrue(HunterSensorMarkers.shouldDisplayMarker(true, false));
        assertFalse(HunterSensorMarkers.shouldDisplayMarker(false, false));
        assertFalse(HunterSensorMarkers.shouldDisplayMarker(true, true));
    }
}
