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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.particles.ElmoParticle;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.scripts.FixScript;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.items.scripts.ScriptOfIdentify;
import com.wafitz.pixelspacebase.items.scripts.ScriptOfMagicMapping;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;

public class UnstableSpellbook extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_SPELLBOOK;

        levelCap = 10;

        charge = ((level() / 2) + 3);
        partialCharge = 0;
        chargeCap = ((level() / 2) + 3);

        defaultAction = AC_READ;
    }

    private static final String AC_READ = "READ";
    private static final String AC_ADD = "ADD";

    private final ArrayList<Class> scripts = new ArrayList<>();

    protected WndBag.Mode mode = WndBag.Mode.SCRIPT;

    public UnstableSpellbook() {
        super();

        Class<?>[] scriptClasses = Generator.Category.SCRIPT.classes;
        float[] probs = Generator.Category.SCRIPT.probs.clone(); //array of primitives, clone gives deep copy.
        int i = Random.chances(probs);

        while (i != -1) {
            scripts.add(scriptClasses[i]);
            probs[i] = 0;

            i = Random.chances(probs);
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && charge > 0 && !malfunctioning)
            actions.add(AC_READ);
        if (isEquipped(hero) && level() < levelCap && !malfunctioning)
            actions.add(AC_ADD);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_READ)) {

            if (hero.buff(Blindness.class) != null) GLog.w(Messages.get(this, "blinded"));
            else if (!isEquipped(hero)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            else if (charge == 0) GLog.i(Messages.get(this, "no_charge"));
            else if (malfunctioning) GLog.i(Messages.get(this, "malfunctioning"));
            else {
                charge--;

                Script script;
                do {
                    script = (Script) Generator.random(Generator.Category.SCRIPT);
                } while (script == null ||
                        //gotta reduce the rate on these scripts or that'll be all the item does.
                        ((script instanceof ScriptOfIdentify ||
                                script instanceof FixScript ||
                                script instanceof ScriptOfMagicMapping) && Random.Int(2) == 0));

                script.ownedByBook = true;
                script.execute(hero, AC_READ);
            }

        } else if (action.equals(AC_ADD)) {
            GameScene.selectItem(itemSelector, mode, Messages.get(this, "prompt"));
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new bookRecharge();
    }

    @Override
    public Item upgrade() {
        chargeCap = (((level() + 1) / 2) + 3);

        //for artifact transmutation.
        while (scripts.size() > (levelCap - 1 - level()))
            scripts.remove(0);

        return super.upgrade();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (malfunctioning && isEquipped(Dungeon.hero)) {
            desc += "\n\n" + Messages.get(this, "desc_malfunctioning");
        }

        if (level() < levelCap)
            if (scripts.size() > 0) {
                desc += "\n\n" + Messages.get(this, "desc_index");
                desc += "\n" + Messages.get(scripts.get(0), "name");
                if (scripts.size() > 1) desc += "\n" + Messages.get(scripts.get(1), "name");
            }

        return desc;
    }

    private static final String SCRIPTS = "scripts";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SCRIPTS, scripts.toArray(new Class[scripts.size()]));
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        scripts.clear();
        Collections.addAll(scripts, bundle.getClassArray(SCRIPTS));
        if (scripts.contains(null)) {
            //compatability with pre-0.3.4, just give them a maxed book.
            scripts.clear();
            level(levelCap);
            chargeCap = 8;
        }
    }

    public class bookRecharge extends ArtifactBuff {
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

    protected WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null && item instanceof Script && item.isIdentified()) {
                Hero hero = Dungeon.hero;
                for (int i = 0; (i <= 1 && i < scripts.size()); i++) {
                    if (scripts.get(i).equals(item.getClass())) {
                        hero.sprite.operate(hero.pos);
                        hero.busy();
                        hero.spend(2f);
                        Sample.INSTANCE.play(Assets.SND_BURNING);
                        hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);

                        scripts.remove(i);
                        item.detach(hero.belongings.backpack);

                        upgrade();
                        GLog.i(Messages.get(UnstableSpellbook.class, "infuse_script"));
                        return;
                    }
                }
                GLog.w(Messages.get(UnstableSpellbook.class, "buggy_script"));
            } else if (item instanceof Script && !item.isIdentified())
                GLog.w(Messages.get(UnstableSpellbook.class, "unknown_script"));
        }
    };
}
