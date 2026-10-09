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
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.SparseArray;

import java.util.HashMap;
import java.util.Map;

/** Shows scanner pings above fog without revealing map cells or changing trap state. */
public class HunterSensorMarkers extends Group {

    static final int BIO_SIGNAL = 0xFF4DFF79;
    static final int ITEM_SIGNAL = 0xFF408CFF;
    static final int TRAP_SIGNAL = 0xFFFF5A4E;

    private final Map<Integer, ItemSprite> itemMarkers = new HashMap<>();
    private final Map<Integer, TrapMarker> trapMarkers = new HashMap<>();

    @Override
    public void update() {
        super.update();

        setAllHidden(itemMarkers);
        setAllHidden(trapMarkers);

        Level level = SpacebaseRun.level;
        if (level == null || SpacebaseRun.visible == null) return;
        if (level instanceof com.wafitz.pixelspacebase.levels.DarkMazeLevel) return;

        boolean itemScannerActive = HunterItemScanner.active();
        boolean trapScannerActive = HunterTrapScanner.active();

        if (itemScannerActive) {
            for (int cell : level.heaps.keyArray()) {
                Heap heap = level.heaps.get(cell);
                if (heap != null && heap.type == Heap.Type.HEAP && !heap.isEmpty()
                        && shouldDisplayMarker(itemScannerActive, isVisible(cell))) {
                    showItemMarker(heap, cell);
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
                    showTrapMarker(vent, cell);
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

    private void setAllHidden(Map<Integer, ? extends com.watabou.noosa.Visual> markers) {
        for (com.watabou.noosa.Visual marker : markers.values()) {
            marker.visible = false;
        }
    }

    private void showItemMarker(Heap heap, int cell) {
        ItemSprite marker = itemMarkers.get(cell);
        if (marker == null) {
            marker = new ItemSprite();
            itemMarkers.put(cell, marker);
            add(marker);
        }
        // Use the same frame as the heap, including unidentified caches and visit art.
        // Solid blue retains its alpha silhouette without showing item colors or glows.
        marker.view(heap.image(), null);
        marker.color(ITEM_SIGNAL);
        marker.alpha(0.9f);
        marker.center(SpacebaseTilemap.tileCenterToWorld(cell));
        marker.visible = true;
    }

    private void showTrapMarker(Vent vent, int cell) {
        TrapMarker marker = trapMarkers.get(cell);
        if (marker == null) {
            marker = new TrapMarker();
            trapMarkers.put(cell, marker);
            add(marker);
        }
        marker.frame(marker.film.get(TerrainFeaturesTilemap.trapVisual(vent)));
        marker.color(TRAP_SIGNAL);
        marker.alpha(0.9f);
        marker.point(SpacebaseTilemap.tileToWorld(cell));
        marker.visible = true;
    }

    private static class TrapMarker extends Image {
        final TextureFilm film;

        TrapMarker() {
            super(TerrainFeaturesTilemap.tilesTexture());
            film = new TextureFilm(texture, TerrainFeaturesTilemap.SIZE, TerrainFeaturesTilemap.SIZE);
        }
    }
}
