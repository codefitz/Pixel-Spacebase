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
package com.wafitz.pixelspacebase.items.armor;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.scrolls.ScrollOfTeleportation;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.CellSelector;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class RogueArmor extends ClassArmor {

    {
        image = ItemSpriteSheet.ARMOR_ROGUE;
    }

    @Override
    public void doSpecial() {
        GameScene.selectCell(teleporter);
    }

    protected static CellSelector.Listener teleporter = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            if (target != null) {

                if (!Level.fieldOfView[target] ||
                        !(Level.passable[target] || Level.avoid[target]) ||
                        Actor.findChar(target) != null) {

                    GLog.w(Messages.get(RogueArmor.class, "fov"));
                    return;
                }

                curUser.HP -= (curUser.HP / 3);

                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[Dungeon.level.mobs.size()])) {
                    if (Level.fieldOfView[mob.pos]) {
                        Buff.prolong(mob, Blindness.class, 2);
                        if (mob.state == mob.HUNTING) mob.state = mob.WANDERING;
                        mob.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 4);
                    }
                }

                ScrollOfTeleportation.appear(curUser, target);
                CellEmitter.get(target).burst(Speck.factory(Speck.WOOL), 10);
                Sample.INSTANCE.play(Assets.SND_PUFF);
                Dungeon.level.press(target, curUser);
                Dungeon.observe();
                GameScene.updateFog();

                curUser.spendAndNext(Actor.TICK);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(RogueArmor.class, "prompt");
        }
    };
}