/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.equippablemodules.HunterItemScanner;
import com.wafitz.pixelspacebase.items.equippablemodules.HunterTrapScanner;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.vents.Vent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.utils.SparseArray;

import java.util.HashMap;
import java.util.Map;

/** Shows scanner pings above fog without revealing map cells or changing trap state. */
public class HunterSensorMarkers extends Group {

    private static final int ITEM_SIGNAL = 0x40E8FF;
    private static final int TRAP_SIGNAL = 0xFF5A4E;
    private static final float MARKER_SIZE = 6f;

    private final Map<Integer, ColorBlock> itemMarkers = new HashMap<>();
    private final Map<Integer, ColorBlock> trapMarkers = new HashMap<>();

    @Override
    public void update() {
        super.update();

        setAllHidden(itemMarkers);
        setAllHidden(trapMarkers);

        Level level = SpacebaseRun.level;
        if (level == null || SpacebaseRun.visible == null) return;

        boolean itemScannerActive = HunterItemScanner.active();
        boolean trapScannerActive = HunterTrapScanner.active();

        if (itemScannerActive) {
            for (int cell : level.heaps.keyArray()) {
                Heap heap = level.heaps.get(cell);
                if (heap != null && heap.type == Heap.Type.HEAP && !heap.isEmpty()
                        && shouldDisplayMarker(itemScannerActive, isVisible(cell))) {
                    showMarker(itemMarkers, cell, ITEM_SIGNAL);
                }
            }
        }

        if (trapScannerActive) {
            SparseArray<Vent> vents = level.vents;
            if (vents == null) return;
            for (int cell : vents.keyArray()) {
                Vent vent = vents.get(cell);
                if (vent != null && vent.active
                        && shouldDisplayMarker(trapScannerActive, isVisible(cell))) {
                    showMarker(trapMarkers, cell, TRAP_SIGNAL);
                }
            }
        }
    }

    static boolean shouldDisplayMarker(boolean moduleActive, boolean targetVisible) {
        return moduleActive && !targetVisible;
    }

    private boolean isVisible(int cell) {
        return cell >= 0 && cell < SpacebaseRun.visible.length && SpacebaseRun.visible[cell];
    }

    private void setAllHidden(Map<Integer, ColorBlock> markers) {
        for (ColorBlock marker : markers.values()) {
            marker.visible = false;
        }
    }

    private void showMarker(Map<Integer, ColorBlock> markers, int cell, int color) {
        ColorBlock marker = markers.get(cell);
        if (marker == null) {
            marker = new ColorBlock(MARKER_SIZE, MARKER_SIZE, color);
            marker.alpha(0.9f);
            markers.put(cell, marker);
            add(marker);
        }
        marker.center(SpacebaseTilemap.tileCenterToWorld(cell));
        marker.visible = true;
    }
}
