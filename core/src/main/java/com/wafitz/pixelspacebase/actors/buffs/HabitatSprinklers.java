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

import com.wafitz.pixelspacebase.actors.Char;

/** Retained only so saved sprinkler buffs can be read and discarded. */
@Deprecated
public class HabitatSprinklers extends Buff {

    @Override
    public boolean attachTo(Char target) {
        return false;
    }

    @Override
    public boolean act() {
        if (target != null) detach();
        return true;
    }
}
