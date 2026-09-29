/*
 * Pixel Spacebase
 * Copyright (C) 2017 Wes Fitzpatrick
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.wafitz.pixelspacebase.actors.blobs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.BlobEmitter;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;

/** Persistent Command plasma hazard. Unlike gas, it remains exactly on the plasma tiles. */
public class Plasma extends Blob implements Hero.Doom {

    public static final int DAMAGE = 25;

    /** Installs the hazard across every plasma tile after a Command map is generated. */
    public static void seed(Level level) {
        if (level.blobs.get(Plasma.class) != null) return;
        Plasma plasma = null;
        for (int cell = 0; cell < level.length(); cell++) {
            if (level.isPlasmaCell(cell)) {
                if (plasma == null) {
                    plasma = new Plasma();
                    level.blobs.put(Plasma.class, plasma);
                }
                plasma.device(level, cell, 1);
            }
        }
        if (plasma != null) Actor.add(plasma);
    }

    @Override
    protected void evolve() {
        Level level = SpacebaseRun.level;
        for (int y = area.top - 1; y <= area.bottom; y++) {
            for (int x = area.left - 1; x <= area.right; x++) {
                int cell = x + y * level.width();
                if (!level.insideMap(cell)) continue;

                int value = cur[cell];
                if (value <= 0 || !level.isPlasmaCell(cell)) {
                    off[cell] = 0;
                    continue;
                }

                Char ch = Actor.findChar(cell);
                if (ch != null && !ch.flying) {
                    ch.damage(DAMAGE, this);
                }

                Heap heap = level.heaps.get(cell);
                if (heap != null) {
                    if (SpacebaseRun.visible != null && cell < SpacebaseRun.visible.length
                            && SpacebaseRun.visible[cell]) {
                        GLog.w(Messages.get(this, "item_burned"));
                    }
                    heap.burnAway();
                }

                off[cell] = value;
                volume += value;
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }

    @Override
    public void onDeath() {
        SpacebaseRun.fail(getClass());
        GLog.n(Messages.get(this, "ondeath"));
    }
}
