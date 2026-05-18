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
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.particles.ElmoParticle;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.upgrades.RepairUpgrade;
import com.wafitz.pixelspacebase.items.upgrades.DiagnosticScanUpgrade;
import com.wafitz.pixelspacebase.items.upgrades.MappingUpgrade;
import com.wafitz.pixelspacebase.items.upgrades.Upgrade;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndContainer;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;

public class BuggyCompiler extends EquippableModule {

    {
        image = ItemSpriteSheet.BUGGY_COMPILER;

        levelCap = 10;

        charge = ((level() / 2) + 3);
        partialCharge = 0;
        chargeCap = ((level() / 2) + 3);

        defaultAction = AC_RUN;
    }

    private static final String AC_RUN = "RUN";
    private static final String AC_ADD = "ADD";

    private final ArrayList<Class> upgrades = new ArrayList<>();

    protected WndContainer.Mode mode = WndContainer.Mode.UPGRADE;

    public BuggyCompiler() {
        super();

        Class<?>[] upgradeClasses = Generator.Category.UPGRADE.classes;
        float[] probs = Generator.Category.UPGRADE.probs.clone(); //array of primitives, clone gives deep copy.
        int i = Random.chances(probs);

        while (i != -1) {
            upgrades.add(upgradeClasses[i]);
            probs[i] = 0;

            i = Random.chances(probs);
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && charge > 0 && !malfunctioning)
            actions.add(AC_RUN);
        if (isEquipped(hero) && level() < levelCap && !malfunctioning)
            actions.add(AC_ADD);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_RUN)) {

            if (hero.buff(Blindness.class) != null) GLog.w(Messages.get(this, "blinded"));
            else if (!isEquipped(hero)) GLog.i(Messages.get(EquippableModule.class, "need_to_equip"));
            else if (charge == 0) GLog.i(Messages.get(this, "no_charge"));
            else if (malfunctioning) GLog.i(Messages.get(this, "malfunctioning"));
            else {
                charge--;

                Upgrade upgrade;
                do {
                    upgrade = (Upgrade) Generator.random(Generator.Category.UPGRADE);
                } while (upgrade == null ||
                        //gotta reduce the rate on these upgrades or that'll be all the item does.
                        ((upgrade instanceof DiagnosticScanUpgrade ||
                                upgrade instanceof RepairUpgrade ||
                                upgrade instanceof MappingUpgrade) && Random.Int(2) == 0));

                upgrade.ownedByCompiler = true;
                upgrade.execute(hero, AC_RUN);
            }

        } else if (action.equals(AC_ADD)) {
            GameScene.selectItem(itemSelector, mode, Messages.get(this, "prompt"));
        }
    }

    @Override
    protected ModuleBuff passiveBuff() {
        return new compilerRecharge();
    }

    @Override
    public Item upgrade() {
        chargeCap = (((level() + 1) / 2) + 3);

        //for artifact transmutation.
        while (upgrades.size() > (levelCap - 1 - level()))
            upgrades.remove(0);

        return super.upgrade();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (malfunctioning && isEquipped(SpacebaseRun.hero)) {
            desc += "\n\n" + Messages.get(this, "desc_malfunctioning");
        }

        if (level() < levelCap)
            if (upgrades.size() > 0) {
                desc += "\n\n" + Messages.get(this, "desc_index");
                desc += "\n" + Messages.get(upgrades.get(0), "name");
                if (upgrades.size() > 1) desc += "\n" + Messages.get(upgrades.get(1), "name");
            }

        return desc;
    }

    private static final String UPGRADES = "upgrades";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(UPGRADES, upgrades.toArray(new Class[upgrades.size()]));
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        upgrades.clear();
        Collections.addAll(upgrades, bundle.getClassArray(UPGRADES));
        if (upgrades.contains(null)) {
            //compatability with pre-0.3.4, just give them a maxed compiler.
            upgrades.clear();
            level(levelCap);
            chargeCap = 8;
        }
    }

    public class compilerRecharge extends ModuleBuff {
        @Override
        public boolean act() {
            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !malfunctioning && (lock == null || lock.regenOn())) {
                partialCharge += 1 / (150f - (chargeCap - charge) * 15f);

                if (partialCharge >= 1) {
                    partialCharge--;
                    charge++;

                    if (charge == chargeCap) {
                        partialCharge = 0;
                    }
                }
            }

            updateQuickslot();

            spend(TICK);

            return true;
        }
    }

    protected WndContainer.Listener itemSelector = new WndContainer.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null && item instanceof Upgrade && item.isIdentified()) {
                Hero hero = SpacebaseRun.hero;
                for (int i = 0; (i <= 1 && i < upgrades.size()); i++) {
                    if (upgrades.get(i).equals(item.getClass())) {
                        hero.sprite.operate(hero.pos);
                        hero.busy();
                        hero.spend(2f);
                        Sample.INSTANCE.play(Assets.SND_BURNING);
                        hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);

                        upgrades.remove(i);
                        item.detach(hero.belongings.backpack);

                        upgrade();
                        GLog.i(Messages.get(BuggyCompiler.class, "merge_upgrade"));
                        return;
                    }
                }
                GLog.w(Messages.get(BuggyCompiler.class, "buggy_upgrade"));
            } else if (item instanceof Upgrade && !item.isIdentified())
                GLog.w(Messages.get(BuggyCompiler.class, "unknown_upgrade"));
        }
    };
}
