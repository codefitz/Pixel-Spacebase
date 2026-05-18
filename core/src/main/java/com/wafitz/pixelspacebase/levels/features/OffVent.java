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
package com.wafitz.pixelspacebase.levels.features;

import com.wafitz.pixelspacebase.Challenges;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Shielding;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroSubClass;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.particles.ElmoParticle;
import com.wafitz.pixelspacebase.items.MedigelDroplet;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.armor.enhancements.Camouflage;
import com.wafitz.pixelspacebase.items.equippablemodules.FrontierTechShield;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.AlienEgg;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Random;

public class OffVent {

    public static void trample(Level level, int pos, Char ch) {

        Level.set(pos, Terrain.INACTIVE_VENT);
        GameScene.updateMap(pos);

        if (ch instanceof Hero) {
            triggerPanelOutcome(level, pos, (Hero) ch);
        }

        int leaves = 4;


        if (ch instanceof Hero) {
            Hero hero = (Hero) ch;

            // Shielding
            if (hero.subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Shielding.class).level(ch.HT / 3);
                leaves += 4;
            }

            //Camouflage
            if (hero.belongings.armor != null && hero.belongings.armor.hasEnhancement(Camouflage.class)) {
                Buff.affect(hero, Camouflage.Camo.class).set(3 + hero.belongings.armor.level());
                leaves += 4;
            }
        }

        // wafitz.v4: No more leaves for lights/vents
        //CellEmitter.get(pos).burst(LeafParticle.LEVEL_SPECIFIC, leaves);
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 1);
        if (SpacebaseRun.visible[pos])
            SpacebaseRun.observe();
    }

    private static void triggerPanelOutcome(Level level, int pos, Hero hero) {
        if (!SpacebaseRun.isChallenged(Challenges.NO_HERBALISM)) {
            int naturalismLevel = 0;

            FrontierTechShield.Naturalism naturalism = hero.buff(FrontierTechShield.Naturalism.class);
            if (naturalism != null) {
                if (!naturalism.isMalfunctioning()) {
                    naturalismLevel = naturalism.itemLevel() + 1;
                    naturalism.charge();
                } else {
                    naturalismLevel = -1;
                }
            }

            if (naturalismLevel >= 0) {
                // Salvage, scales from 1/4 to 1/2.
                if (Random.Int(4 + Math.max(0, 3 - naturalismLevel)) == 0) {
                    Item device = Generator.random(Generator.Category.DEVICE);

                    if (device instanceof AlienEgg.Device) {
                        if (Random.Int(15) - SpacebaseRun.limitedDrops.alienTechDevice.count >= 0) {
                            level.drop(device, pos).sprite.drop();
                            SpacebaseRun.limitedDrops.alienTechDevice.count++;
                        }
                    } else
                        level.drop(device, pos).sprite.drop();

                    GLog.p(Messages.get(OffVent.class, "salvage"));
                }

                // Dew, scales from 1/6 to 1/3
                if (Random.Int(24 - naturalismLevel * 3) <= 3) {
                    level.drop(new MedigelDroplet(), pos).sprite.drop();
                }
            }
        }

        switch (Random.Int(8)) {
            case 0:
                Buff.affect(hero, Burning.class).reignite(hero);
                CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 4);
                GLog.w(Messages.get(OffVent.class, "burn"));
                break;
            case 1:
                hero.damage(Math.max(1, Random.IntRange(1, Math.max(2, SpacebaseRun.depth / 2 + 1))), LightningVent.LIGHTNING);
                CellEmitter.center(pos).burst(Speck.factory(Speck.LIGHT), 4);
                GLog.w(Messages.get(OffVent.class, "shock"));
                break;
            default:
        }
    }
}
