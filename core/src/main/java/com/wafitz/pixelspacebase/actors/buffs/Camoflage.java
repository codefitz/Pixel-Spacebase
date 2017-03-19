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
package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.items.artifacts.StealthModule;
import com.wafitz.pixelspacebase.items.artifacts.TimeFolder;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.ui.BuffIndicator;

public class Camoflage extends FlavourBuff {

    public static final float DURATION = 20f;

    {
        type = buffType.POSITIVE;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.invisible++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        if (target.invisible > 0)
            target.invisible--;
        super.detach();
    }

    @Override
    public int icon() {
        return BuffIndicator.INVISIBLE;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.INVISIBLE);
        else if (target.invisible == 0) target.sprite.remove(CharSprite.State.INVISIBLE);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    public static void dispel() {
        Camoflage buff = Dungeon.hero.buff(Camoflage.class);
        if (buff != null) {
            buff.detach();
        }
        StealthModule.stealthModule cloakBuff = Dungeon.hero.buff(StealthModule.stealthModule.class);
        if (cloakBuff != null) {
            cloakBuff.dispel();
        }
        //this isn't a form of invisibilty, but it is meant to dispel at the same time as it.
        TimeFolder.timeFreeze timeFreeze = Dungeon.hero.buff(TimeFolder.timeFreeze.class);
        if (timeFreeze != null) {
            timeFreeze.detach();
        }
    }
}
