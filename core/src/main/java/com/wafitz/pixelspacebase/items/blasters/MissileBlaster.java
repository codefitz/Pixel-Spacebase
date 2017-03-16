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
package com.wafitz.pixelspacebase.items.blasters;

import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Recharging;
import com.wafitz.pixelspacebase.effects.SpellSprite;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Staff;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class MissileBlaster extends DamageBlaster {

    {
        image = ItemSpriteSheet.MISSILEBLASTER;
    }

    public int min(int lvl) {
        return 2 + lvl;
    }

    public int max(int lvl) {
        return 8 + 2 * lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {

        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {

            processSoulMark(ch, chargesPerCast());
            ch.damage(damageRoll(), this);

            ch.sprite.burst(0xFFFFFFFF, level() / 2 + 2);

        }
    }

    @Override
    public void onHit(DM3000Staff staff, Char attacker, Char defender, int damage) {
        Buff.prolong(attacker, Recharging.class, 1 + staff.level() / 2f);
        SpellSprite.show(attacker, SpellSprite.CHARGE);

    }

    protected int initialCharges() {
        return 3;
    }

}
