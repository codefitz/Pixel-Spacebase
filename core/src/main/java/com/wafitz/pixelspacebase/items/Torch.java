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
package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Light;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.particles.FlameParticle;
import com.wafitz.pixelspacebase.items.weapon.melee.MeleeWeapon;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Torch extends MeleeWeapon {

    private static final String AC_LIGHT = "LIGHT";
    private static final String AC_EQUIP_MODULE = "EQUIP_MODULE";

    private static final float TIME_TO_LIGHT = 1;
    private static final float TIME_TO_EQUIP_MODULE = 1f;
    private static final int MAX_CHARGE = 3;
    private static final String CHARGE = "charge";

    private int charge = MAX_CHARGE;

    {
        image = ItemSpriteSheet.TORCH;
        tier = 1;
        defaultAction = AC_LIGHT;
        levelKnown = true;
        malfunctioningKnown = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (!isEquipped(hero)) {
            actions.add(AC_EQUIP_MODULE);
        }
        actions.add(AC_LIGHT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_LIGHT)) {
            if (!isEquipped(hero)) {
                GLog.w(Messages.get(this, "need_equip"));
                return;
            }
            if (hero.buff(Light.class) != null) {
                GLog.i(Messages.get(this, "already_lit"));
                return;
            }
            if (charge <= 0) {
                GLog.w(Messages.get(this, "no_charge"));
                return;
            }

            charge--;
            updateQuickslot();

            hero.spend(TIME_TO_LIGHT);
            hero.busy();

            hero.sprite.operate(hero.pos);
            Buff.affect(hero, Light.class, Light.DURATION);

            Emitter emitter = hero.sprite.centerEmitter();
            emitter.start(FlameParticle.FACTORY, 0.2f, 3);

            GLog.p(Messages.get(this, "light_msg"));
        } else if (action.equals(AC_EQUIP_MODULE)) {
            doEquipModule(hero);
        }
    }

    private void doEquipModule(final Hero hero) {
        if (hero.belongings.misc1 != null && hero.belongings.misc2 != null) {
            final EquipableItem m1 = hero.belongings.misc1;
            final EquipableItem m2 = hero.belongings.misc2;

            GameScene.show(
                    new WndOptions(Messages.get(KindofMisc.class, "unequip_title"),
                            Messages.get(KindofMisc.class, "unequip_message"),
                            Messages.titleCase(m1.toString()),
                            Messages.titleCase(m2.toString())) {

                        @Override
                        protected void onSelect(int index) {
                            EquipableItem equipped = (index == 0 ? m1 : m2);
                            if (equipped.doUnequip(hero, true, false)) {
                                doEquipModule(hero);
                            }
                        }
                    });
            return;
        }

        detachAll(hero.belongings.backpack);

        if (hero.belongings.misc1 == null) {
            hero.belongings.misc1 = this;
        } else {
            hero.belongings.misc2 = this;
        }

        activate(hero);
        updateQuickslot();

        malfunctioningKnown = true;
        if (malfunctioning) {
            equipMalfunctioning(hero);
            GLog.n(Messages.get(KindOfWeapon.class, "malfunctioning"));
        }

        hero.spendAndNext(TIME_TO_EQUIP_MODULE);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (hero.belongings.misc1 == this || hero.belongings.misc2 == this) {
            if (malfunctioning) {
                GLog.w(Messages.get(EquipableItem.class, "unequip_malfunctioning"));
                return false;
            }

            if (single) {
                hero.spendAndNext(time2equip(hero));
            } else {
                hero.spend(time2equip(hero));
            }

            if (hero.belongings.misc1 == this) {
                hero.belongings.misc1 = null;
            } else {
                hero.belongings.misc2 = null;
            }

            if (!collect || !collect(hero.belongings.backpack)) {
                onDetach();
                SpacebaseRun.quickslot.clearItem(this);
                updateQuickslot();
                if (collect) {
                    SpacebaseRun.level.drop(this, hero.pos);
                }
            }

            return true;
        }

        return super.doUnequip(hero, collect, single);
    }

    @Override
    public boolean isEquipped(Hero hero) {
        return super.isEquipped(hero) || hero.belongings.misc1 == this || hero.belongings.misc2 == this;
    }

    public boolean canRecharge() {
        return charge < MAX_CHARGE;
    }

    public void recharge() {
        charge = MAX_CHARGE;
        updateQuickslot();
    }

    @Override
    public int min(int lvl) {
        return 1 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 6 + lvl * 2;
    }

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        return 8 - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public String status() {
        return charge + "/" + MAX_CHARGE;
    }

    @Override
    public String info() {
        return super.info() + "\n\n" + Messages.get(this, "charge", charge, MAX_CHARGE);
    }

    @Override
    public int cost() {
        return 30;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGE, charge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        charge = bundle.contains(CHARGE) ? bundle.getInt(CHARGE) : MAX_CHARGE;
    }
}
