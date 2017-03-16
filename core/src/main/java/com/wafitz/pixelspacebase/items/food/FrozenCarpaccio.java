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
package com.wafitz.pixelspacebase.items.food;

import com.wafitz.pixelspacebase.actors.buffs.Bleeding;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Camoflage;
import com.wafitz.pixelspacebase.actors.buffs.Cripple;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.buffs.Knockout;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.actors.buffs.Shielding;
import com.wafitz.pixelspacebase.actors.buffs.TimeSink;
import com.wafitz.pixelspacebase.actors.buffs.Vertigo;
import com.wafitz.pixelspacebase.actors.buffs.Weakness;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Random;

public class FrozenCarpaccio extends Food {

    {
        image = ItemSpriteSheet.CARPACCIO;
        energy = Hunger.STARVING - Hunger.HUNGRY;
        hornValue = 1;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_EAT)) {
            effect(hero);
        }
    }

    public int price() {
        return 10 * quantity;
    }

    public static void effect(Hero hero) {
        switch (Random.Int(5)) {
            case 0:
                GLog.i(Messages.get(FrozenCarpaccio.class, "invis"));
                Buff.affect(hero, Camoflage.class, Camoflage.DURATION);
                break;
            case 1:
                GLog.i(Messages.get(FrozenCarpaccio.class, "hard"));
                Buff.affect(hero, Shielding.class).level(hero.HT / 4);
                break;
            case 2:
                GLog.i(Messages.get(FrozenCarpaccio.class, "refresh"));
                Buff.detach(hero, Poison.class);
                Buff.detach(hero, Cripple.class);
                Buff.detach(hero, Weakness.class);
                Buff.detach(hero, Bleeding.class);
                Buff.detach(hero, Knockout.class);
                Buff.detach(hero, TimeSink.class);
                Buff.detach(hero, Vertigo.class);
                break;
            case 3:
                GLog.i(Messages.get(FrozenCarpaccio.class, "better"));
                if (hero.HP < hero.HT) {
                    hero.HP = Math.min(hero.HP + hero.HT / 4, hero.HT);
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                }
                break;
        }
    }

    public static Food cook(MysteryMeat ingredient) {
        FrozenCarpaccio result = new FrozenCarpaccio();
        result.quantity = ingredient.quantity();
        return result;
    }
}
