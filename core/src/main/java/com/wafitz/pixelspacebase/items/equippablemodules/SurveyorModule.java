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
package com.wafitz.pixelspacebase.items.equippablemodules;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.buffs.Awareness;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.ui.BuffIndicator;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class SurveyorModule extends EquippableModule {

    {
        image = ItemSpriteSheet.SURVEYOR_MODULE;

        exp = 0;
        levelCap = 10;

        charge = 0;
        partialCharge = 0;
        chargeCap = 100;

        defaultAction = AC_SURVEY;
    }

    private static final String AC_SURVEY = "SURVEY";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && charge == chargeCap && !malfunctioning)
            actions.add(AC_SURVEY);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_SURVEY)) {

            if (!isEquipped(hero)) GLog.i(Messages.get(EquippableModule.class, "need_to_equip"));
            else if (charge != chargeCap) GLog.i(Messages.get(this, "no_charge"));
            else {
                hero.sprite.operate(hero.pos);
                hero.busy();
                Sample.INSTANCE.play(Assets.SND_BEACON);
                charge = 0;
                for (int i = 0; i < SpacebaseRun.level.length(); i++) {

                    int terr = SpacebaseRun.level.map[i];
                    if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                        GameScene.updateMap(i);

                        if (SpacebaseRun.visible[i]) {
                            GameScene.discoverTile(i, terr);
                        }
                    }
                }

                GLog.p(Messages.get(this, "scry"));

                updateQuickslot();

                Buff.affect(hero, Awareness.class, Awareness.DURATION);
                SpacebaseRun.observe();
            }
        }
    }

    @Override
    protected ModuleBuff passiveBuff() {
        return new Survey();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(SpacebaseRun.hero)) {
            if (!malfunctioning) {
                desc += "\n\n" + Messages.get(this, "desc_worn");

            } else {
                desc += "\n\n" + Messages.get(this, "desc_malfunctioning");
            }
        }

        return desc;
    }

    public class Survey extends ModuleBuff {
        private int warn = 0;

        @Override
        public boolean act() {
            spend(TICK);

            boolean smthFound = false;

            int distance = 3;

            int cx = target.pos % SpacebaseRun.level.width();
            int cy = target.pos / SpacebaseRun.level.width();
            int ax = cx - distance;
            if (ax < 0) {
                ax = 0;
            }
            int bx = cx + distance;
            if (bx >= SpacebaseRun.level.width()) {
                bx = SpacebaseRun.level.width() - 1;
            }
            int ay = cy - distance;
            if (ay < 0) {
                ay = 0;
            }
            int by = cy + distance;
            if (by >= SpacebaseRun.level.height()) {
                by = SpacebaseRun.level.height() - 1;
            }

            for (int y = ay; y <= by; y++) {
                for (int x = ax, p = ax + y * SpacebaseRun.level.width(); x <= bx; x++, p++) {

                    if (SpacebaseRun.visible[p] && Level.secret[p] && SpacebaseRun.level.map[p] != Terrain.SECRET_DOOR)
                        smthFound = true;
                }
            }

            if (smthFound && !malfunctioning) {
                if (warn == 0) {
                    GLog.w(Messages.get(this, "uneasy"));
                    if (target instanceof Hero) {
                        ((Hero) target).interrupt();
                    }
                }
                warn = 3;
            } else {
                if (warn > 0) {
                    warn--;
                }
            }
            BuffIndicator.refreshHero();

            //fully charges in 2500 turns at lvl=0, scaling to 1000 turns at lvl = 10.
            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !malfunctioning && (lock == null || lock.regenOn())) {
                partialCharge += 0.04 + (level() * 0.006);

                if (partialCharge > 1 && charge < chargeCap) {
                    partialCharge--;
                    charge++;
                } else if (charge >= chargeCap) {
                    partialCharge = 0;
                    GLog.p(Messages.get(this, "full_charge"));
                }
            }

            return true;
        }

        public void charge() {
            charge = Math.min(charge + (2 + (level() / 3)), chargeCap);
            exp++;
            if (exp >= 4 && level() < levelCap) {
                upgrade();
                GLog.p(Messages.get(this, "levelup"));
                exp -= 4;
            }
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }

        @Override
        public int icon() {
            if (warn == 0)
                return BuffIndicator.NONE;
            else
                return BuffIndicator.SURVEY;
        }
    }
}
