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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.wafitz.pixelspacebase.windows;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.levels.vents.Vent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.ui.TerrainFeaturesTilemap;

public class WndInfoVent extends WndTitledMessage {

    public WndInfoVent(Vent vent) {

        super(TerrainFeaturesTilemap.tile(vent.pos, Dungeon.level.map[vent.pos]),
                vent.name,
                (!vent.active ? Messages.get(WndInfoVent.class, "inactive") + "\n\n" : "") + vent.desc());

    }

}
