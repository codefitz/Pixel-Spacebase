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
package com.wafitz.pixelspacebase.items.weapon.missiles;

import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.Fire;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class IncendiaryDart extends MissileWeapon {

    {
        image = ItemSpriteSheet.INCENDIARY_DART;
    }

    @Override
    public int min(int lvl) {
        return 1;
    }

    @Override
    public int max(int lvl) {
        return 2;
    }

    @Override
    public int STRReq(int lvl) {
        return 12;
    }

    public IncendiaryDart() {
        this(1);
    }

    private IncendiaryDart(int number) {
        super();
        quantity = number;
    }

    @Override
    protected void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);
        if ((enemy == null || enemy == curUser) && Level.flamable[cell])
            GameScene.add(Blob.gadget(cell, 4, Fire.class));
        else
            super.onThrow(cell);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.affect(defender, Burning.class).reignite(defender);
        return super.proc(attacker, defender, damage);
    }

    @Override
    public Item random() {
        quantity = Random.Int(3, 6);
        return this;
    }

    @Override
    public int price() {
        return 5 * quantity;
    }
}
