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
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.LockedDown;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.particles.EarthParticle;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.mines.WeakForcefield;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndContainer;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collections;

public class GnollTechShield extends Artifact {

    {
        image = ItemSpriteSheet.GNOLLTECH_SHIELD_1;

        levelCap = 3;

        charge = 0;

        defaultAction = AC_ROOT;
    }

    private static final String AC_APPLY = "APPLY";
    private static final String AC_ROOT = "ROOT";

    protected WndContainer.Mode mode = WndContainer.Mode.DEVICE;

    public ArrayList<Class> devices = new ArrayList<>();

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && level() < 3 && !malfunctioning)
            actions.add(AC_APPLY);
        if (isEquipped(hero) && charge > 0)
            actions.add(AC_ROOT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_APPLY)) {

            GameScene.selectItem(itemSelector, mode, Messages.get(this, "prompt"));

        } else if (action.equals(AC_ROOT) && level() > 0) {

            if (!isEquipped(hero)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            else if (charge == 0) GLog.i(Messages.get(this, "no_charge"));
            else {
                Buff.prolong(hero, LockedDown.class, 5);
                Buff.affect(hero, WeakForcefield.Armor.class).level(charge);
                CellEmitter.bottom(hero.pos).start(EarthParticle.FACTORY, 0.05f, 8);
                Camera.main.shake(1, 0.4f);
                charge = 0;
                updateQuickslot();
            }
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new Naturalism();
    }

    @Override
    public String desc() {
        String desc = Messages.get(this, "desc_" + (level() + 1));

        if (isEquipped(Dungeon.hero)) {
            desc += "\n\n";

            if (!malfunctioning)
                desc += Messages.get(this, "desc_hint");
            else
                desc += Messages.get(this, "desc_malfunctioning");

            if (level() > 0)
                desc += "\n\n" + Messages.get(this, "desc_ability");
        }

        if (!devices.isEmpty()) {
            desc += "\n\n" + Messages.get(this, "desc_devices", devices.size());
        }

        return desc;
    }

    @Override
    public Item upgrade() {
        if (level() < 0) image = ItemSpriteSheet.GNOLLTECH_SHIELD_1;
        else if (level() == 0) image = ItemSpriteSheet.GNOLLTECH_SHIELD_2;
        else if (level() == 1) image = ItemSpriteSheet.GNOLLTECH_SHIELD_3;
        else if (level() >= 2) image = ItemSpriteSheet.GNOLLTECH_SHIELD_4;
        name = Messages.get(this, "name_" + (level() + 1));
        return super.upgrade();
    }


    private static final String DEVICES = "devices";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEVICES, devices.toArray(new Class[devices.size()]));
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (level() > 0) name = Messages.get(this, "name_" + level());
        if (bundle.contains(DEVICES))
            Collections.addAll(devices, bundle.getClassArray(DEVICES));
        if (level() == 1) image = ItemSpriteSheet.GNOLLTECH_SHIELD_2;
        else if (level() == 2) image = ItemSpriteSheet.GNOLLTECH_SHIELD_3;
        else if (level() >= 3) image = ItemSpriteSheet.GNOLLTECH_SHIELD_4;
    }

    public class Naturalism extends ArtifactBuff {
        public void charge() {
            if (level() > 0 && charge < target.HT) {
                //gain 1+(1*level)% of the difference between current charge and max HP.
                charge += (Math.round((target.HT - charge) * (.01 + level() * 0.01)));
                updateQuickslot();
            }
        }
    }

    protected WndContainer.Listener itemSelector = new WndContainer.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null && item instanceof Mine.Device) {
                if (devices.contains(item.getClass())) {
                    GLog.w(Messages.get(GnollTechShield.class, "already_applied"));
                } else {
                    devices.add(item.getClass());

                    Hero hero = Dungeon.hero;
                    hero.sprite.operate(hero.pos);
                    Sample.INSTANCE.play(Assets.SND_PLANT);
                    hero.busy();
                    hero.spend(2f);
                    if (devices.size() >= 3 + (level() * 3)) {
                        devices.clear();
                        upgrade();
                        if (level() >= 1 && level() <= 3) {
                            GLog.p(Messages.get(GnollTechShield.class, "levelup"));
                        }

                    } else {
                        GLog.i(Messages.get(GnollTechShield.class, "absorb_device"));
                    }
                    item.detach(hero.belongings.backpack);
                }
            }
        }
    };

}
