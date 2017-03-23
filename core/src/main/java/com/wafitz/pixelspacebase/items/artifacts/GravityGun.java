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
package com.wafitz.pixelspacebase.items.artifacts;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Cripple;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Chains;
import com.wafitz.pixelspacebase.effects.Pushing;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.CellSelector;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.ui.QuickSlotButton;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GravityGun extends Artifact {

    private static final String AC_SHOOT = "SHOOT";

    {
        image = ItemSpriteSheet.GRAVITY_GUN;

        levelCap = 5;
        exp = 0;

        charge = 5;

        defaultAction = AC_SHOOT;
        usesTargeting = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && charge > 0 && !malfunctioning)
            actions.add(AC_SHOOT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {

            curUser = hero;

            if (!isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip"));
                QuickSlotButton.cancel();

            } else if (charge < 1) {
                GLog.i(Messages.get(this, "no_charge"));
                QuickSlotButton.cancel();

            } else if (malfunctioning) {
                GLog.w(Messages.get(this, "malfunctioning"));
                QuickSlotButton.cancel();

            } else {
                GameScene.selectCell(caster);
            }

        }
    }

    private CellSelector.Listener caster = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            if (target != null && (Dungeon.level.visited[target] || Dungeon.level.mapped[target])) {

                //ballistica does not go through walls on pre-rework boss arenas
                int missileProperties = (Dungeon.depth == 10 || Dungeon.depth == 15 || Dungeon.depth == 20 || Dungeon.depth == 25) ?
                        Ballistica.PROJECTILE : Ballistica.STOP_CHARS | Ballistica.STOP_TARGET;

                final Ballistica chain = new Ballistica(curUser.pos, target, missileProperties);

                //determine if we're grabbing an enemy, pulling to a location, or doing nothing.
                if (Actor.findChar(chain.collisionPos) != null) {
                    int newPos = -1;
                    for (int i : chain.subPath(1, chain.dist)) {
                        if (!Level.solid[i] && Actor.findChar(i) == null) {
                            newPos = i;
                            break;
                        }
                    }
                    if (newPos == -1) {
                        GLog.w(Messages.get(GravityGun.class, "does_nothing"));
                    } else {
                        final int newMobPos = newPos;
                        final Char affected = Actor.findChar(chain.collisionPos);
                        int chargeUse = Dungeon.level.distance(affected.pos, newMobPos);
                        if (chargeUse > charge) {
                            GLog.w(Messages.get(GravityGun.class, "no_charge"));
                            return;
                        } else if (affected.properties().contains(Char.Property.IMMOVABLE)) {
                            GLog.w(Messages.get(GravityGun.class, "cant_pull"));
                            return;
                        } else {
                            charge -= chargeUse;
                            updateQuickslot();
                        }
                        curUser.busy();
                        curUser.sprite.parent.add(new Chains(curUser.pos, affected.pos, new Callback() {
                            public void call() {
                                Actor.add(new Pushing(affected, affected.pos, newMobPos, new Callback() {
                                    public void call() {
                                        Dungeon.level.press(newMobPos, affected);
                                    }
                                }));
                                affected.pos = newMobPos;
                                Dungeon.observe();
                                GameScene.updateFog();
                                curUser.spendAndNext(1f);
                            }
                        }));
                    }

                } else if (Level.solid[chain.path.get(chain.dist)]
                        || (chain.dist > 0 && Level.solid[chain.path.get(chain.dist - 1)])
                        || (chain.path.size() > chain.dist + 1 && Level.solid[chain.path.get(chain.dist + 1)])
                        //if the player is trying to grapple the edge of the map, let them.
                        || (chain.path.size() == chain.dist + 1)) {
                    int newPos = -1;
                    for (int i : chain.subPath(1, chain.dist)) {
                        if (!Level.solid[i] && Actor.findChar(i) == null) newPos = i;
                    }
                    if (newPos == -1) {
                        GLog.w(Messages.get(GravityGun.class, "does_nothing"));
                    } else {
                        final int newHeroPos = newPos;
                        int chargeUse = Dungeon.level.distance(curUser.pos, newHeroPos);
                        if (chargeUse > charge) {
                            GLog.w(Messages.get(GravityGun.class, "no_charge"));
                            return;
                        } else {
                            charge -= chargeUse;
                            updateQuickslot();
                        }
                        curUser.busy();
                        curUser.sprite.parent.add(new Chains(curUser.pos, target, new Callback() {
                            public void call() {
                                Actor.add(new Pushing(curUser, curUser.pos, newHeroPos, new Callback() {
                                    public void call() {
                                        Dungeon.level.press(newHeroPos, curUser);
                                    }
                                }));
                                curUser.spendAndNext(1f);
                                curUser.pos = newHeroPos;
                                Dungeon.observe();
                                GameScene.updateFog();
                            }
                        }));
                    }

                } else {
                    GLog.i(Messages.get(GravityGun.class, "nothing_to_grab"));
                }

            }

        }

        @Override
        public String prompt() {
            return Messages.get(GravityGun.class, "prompt");
        }
    };

    @Override
    protected ArtifactBuff passiveBuff() {
        return new gravityRecharge();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            desc += "\n\n";
            if (malfunctioning)
                desc += Messages.get(this, "desc_malfunctioning");
            else
                desc += Messages.get(this, "desc_equipped");
        }
        return desc;
    }

    public class gravityRecharge extends ArtifactBuff {

        @Override
        public boolean act() {
            int chargeTarget = 5 + (level() * 2);
            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeTarget && !malfunctioning && (lock == null || lock.regenOn())) {
                partialCharge += 1 / (40f - (chargeTarget - charge) * 2f);
            } else if (malfunctioning && Random.Int(100) == 0) {
                Buff.prolong(target, Cripple.class, 10f);
            }

            if (partialCharge >= 1) {
                partialCharge--;
                charge++;
            }

            updateQuickslot();

            spend(TICK);

            return true;
        }

        public void gainExp(float levelPortion) {
            if (malfunctioning) return;

            exp += Math.round(levelPortion * 100);

            //past the soft charge cap, gaining  charge from leveling is slowed.
            if (charge > 5 + (level() * 2)) {
                levelPortion *= (5 + ((float) level() * 2)) / charge;
            }
            partialCharge += levelPortion * 10f;

            if (exp > 100 + level() * 50 && level() < levelCap) {
                exp -= 100 + level() * 50;
                GLog.p(Messages.get(this, "levelup"));
                upgrade();
            }

        }
    }
}
