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
package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.Fire;
import com.wafitz.pixelspacebase.levels.HabitationRingLevel;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

public class HabitatSprinklers extends Buff {

    private static final String PENDING = "pending";

    private boolean pending;

    {
        // Run after fire, characters, and their burning buffs on the activation turn.
        actPriority = Integer.MAX_VALUE;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PENDING, pending);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        pending = bundle.getBoolean(PENDING);
    }

    @Override
    public boolean act() {
        if (!(SpacebaseRun.level instanceof HabitationRingLevel)
                || target != SpacebaseRun.hero || !target.isAlive()) {
            detach();
            return true;
        }

        if (pending) {
            if (firePresent() || heroIsBurning()) {
                extinguish();
                GLog.i(Messages.get(HabitationRingLevel.class, "sprinklers"));
            }
            pending = false;
        } else if (firePresent() || heroIsBurning()) {
            pending = true;
        }

        spend(TICK);
        return true;
    }

    private boolean firePresent() {
        Blob blob = SpacebaseRun.level.blobs.get(Fire.class);
        return blob != null && blob.volume > 0;
    }

    private boolean heroIsBurning() {
        return SpacebaseRun.hero.buff(Burning.class) != null;
    }

    private void extinguish() {
        Blob blob = SpacebaseRun.level.blobs.get(Fire.class);
        if (blob != null && blob.volume > 0) {
            blob.fullyClear();
        }

        for (Char ch : Actor.chars().toArray(new Char[0])) {
            Buff.detach(ch, Burning.class);
        }

        GameScene.updateMap();
    }
}
