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

import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Light;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.particles.FlameParticle;
import com.wafitz.pixelspacebase.items.weapon.melee.MeleeWeapon;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Torch extends MeleeWeapon {

    private static final String AC_LIGHT = "LIGHT";

    private static final float TIME_TO_LIGHT = 1;
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
        }
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
