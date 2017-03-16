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
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Shielding;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroSubClass;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.particles.ElmoParticle;
import com.wafitz.pixelspacebase.items.Dewdrop;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.armor.glyphs.Camouflage;
import com.wafitz.pixelspacebase.items.artifacts.SandalsOfNature;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.triggers.BlandfruitBush;
import com.watabou.utils.Random;

public class OffVent {

    public static void trample(Level level, int pos, Char ch) {

        Level.set(pos, Terrain.LIGHTEDVENT);
        GameScene.updateMap(pos);

        if (!Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
            int naturalismLevel = 0;

            if (ch != null) {
                SandalsOfNature.Naturalism naturalism = ch.buff(SandalsOfNature.Naturalism.class);
                if (naturalism != null) {
                    if (!naturalism.isMalfunctioning()) {
                        naturalismLevel = naturalism.itemLevel() + 1;
                        naturalism.charge();
                    } else {
                        naturalismLevel = -1;
                    }
                }
            }

            if (naturalismLevel >= 0) {
                // Gadget, scales from 1/16 to 1/4
                if (Random.Int(16 - naturalismLevel * 3) == 0) {
                    Item gadget = Generator.random(Generator.Category.GADGET);

                    if (gadget instanceof BlandfruitBush.Gadget) {
                        if (Random.Int(15) - Dungeon.limitedDrops.blandfruitGadget.count >= 0) {
                            level.drop(gadget, pos).sprite.drop();
                            Dungeon.limitedDrops.blandfruitGadget.count++;
                        }
                    } else
                        level.drop(gadget, pos).sprite.drop();
                }

                // Dew, scales from 1/6 to 1/3
                if (Random.Int(24 - naturalismLevel * 3) <= 3) {
                    level.drop(new Dewdrop(), pos).sprite.drop();
                }
            }
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
            if (hero.belongings.armor != null && hero.belongings.armor.hasGlyph(Camouflage.class)) {
                Buff.affect(hero, Camouflage.Camo.class).set(3 + hero.belongings.armor.level());
                leaves += 4;
            }
        }

        // wafitz.v4: No more leaves for lights/vents
        //CellEmitter.get(pos).burst(LeafParticle.LEVEL_SPECIFIC, leaves);
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 1);
        if (Dungeon.visible[pos])
            Dungeon.observe();
    }
}
