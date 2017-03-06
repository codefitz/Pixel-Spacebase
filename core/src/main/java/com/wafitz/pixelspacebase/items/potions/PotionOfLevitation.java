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
package com.wafitz.pixelspacebase.items.potions;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.ConfusionGas;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Levitation;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfLevitation extends Potion {

    {
        initials = 4;
    }

    @Override
    public void shatter(int cell) {

        if (Dungeon.visible[cell]) {
            setKnown();

            splash(cell);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        GameScene.add(Blob.seed(cell, 1000, ConfusionGas.class));
    }

    @Override
    public void apply(Hero hero) {
        setKnown();
        Buff.affect(hero, Levitation.class, Levitation.DURATION);
        GLog.i(Messages.get(this, "float"));
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
