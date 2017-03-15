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
package com.wafitz.pixelspacebase.actors.blobs;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.Journal;
import com.wafitz.pixelspacebase.Journal.Feature;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.BlobEmitter;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.particles.ShaftParticle;
import com.wafitz.pixelspacebase.items.AirTank;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfHealing;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class MedicalTerminal extends WellWater {

    @Override
    protected boolean affectHero(Hero hero) {

        Sample.INSTANCE.play(Assets.SND_DRINK);

        ExperimentalTechOfHealing.heal(hero);
        hero.belongings.fixEquipped();
        hero.buff(Hunger.class).satisfy(Hunger.STARVING);

        CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3);

        Dungeon.hero.interrupt();

        GLog.p(Messages.get(this, "procced"));

        Journal.remove(Feature.WELL_OF_HEALTH);

        return true;
    }

    @Override
    protected Item affectItem(Item item) {
        if (item instanceof AirTank && !((AirTank) item).isFull()) {
            ((AirTank) item).fill();
            Journal.remove(Feature.WELL_OF_HEALTH);
            return item;
        }

        return null;
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(Speck.factory(Speck.HEALING), 0.5f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
