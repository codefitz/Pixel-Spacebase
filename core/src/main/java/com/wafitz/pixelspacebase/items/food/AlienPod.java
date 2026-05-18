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
package com.wafitz.pixelspacebase.items.food;

import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.FlameOn;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.buffs.Lockdown;
import com.wafitz.pixelspacebase.actors.buffs.Toxic;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.plasmids.CryoGrenade;
import com.wafitz.pixelspacebase.items.plasmids.ExperiencePlasmid;
import com.wafitz.pixelspacebase.items.plasmids.GravLiftPlasmid;
import com.wafitz.pixelspacebase.items.plasmids.Plasmid;
import com.wafitz.pixelspacebase.items.plasmids.FireGrenade;
import com.wafitz.pixelspacebase.items.plasmids.HealingPlasmid;
import com.wafitz.pixelspacebase.items.plasmids.CloakPlasmid;
import com.wafitz.pixelspacebase.items.plasmids.ParalysisGrenade;
import com.wafitz.pixelspacebase.items.plasmids.PolymerPlasmid;
import com.wafitz.pixelspacebase.items.plasmids.SecurityPlasmid;
import com.wafitz.pixelspacebase.items.plasmids.MyoFiberPlasmid;
import com.wafitz.pixelspacebase.items.plasmids.ToxicGrenade;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

public class AlienPod extends Food {

    public Plasmid plasmidAttrib = null;
    private ItemSprite.Glowing plasmidGlow = null;

    {
        stackable = true;
        image = ItemSpriteSheet.ALIENPOD;

        //only applies when blandfruit is cooked
        energy = Hunger.STARVING;
        hornValue = 6;

        bones = true;
    }

    @Override
    public boolean isSimilar(Item item) {
        if (item instanceof AlienPod) {
            if (plasmidAttrib == null) {
                if (((AlienPod) item).plasmidAttrib == null)
                    return true;
            } else if (((AlienPod) item).plasmidAttrib != null) {
                if (((AlienPod) item).plasmidAttrib.getClass() == plasmidAttrib.getClass())
                    return true;
            }
        }
        return false;
    }

    @Override
    public void execute(Hero hero, String action) {

        if (action.equals(AC_USE) && plasmidAttrib == null) {

            GLog.w(Messages.get(this, "raw"));
            return;

        }

        super.execute(hero, action);

        if (action.equals(AC_USE) && plasmidAttrib != null) {

            if (plasmidAttrib instanceof CryoGrenade) {
                GLog.i(Messages.get(this, "ice_msg"));
                FrozenCarpaccio.effect(hero);
            } else if (plasmidAttrib instanceof FireGrenade) {
                GLog.i(Messages.get(this, "fire_msg"));
                Buff.affect(hero, FlameOn.class).set(FlameOn.DURATION);
            } else if (plasmidAttrib instanceof ToxicGrenade) {
                GLog.i(Messages.get(this, "toxic_msg"));
                Buff.affect(hero, Toxic.class).set(Toxic.DURATION);
            } else if (plasmidAttrib instanceof ParalysisGrenade) {
                GLog.i(Messages.get(this, "para_msg"));
                Buff.affect(hero, Lockdown.class, Lockdown.DURATION);
            } else {
                plasmidAttrib.apply(hero);
            }

        }
    }

    @Override
    public String desc() {
        if (plasmidAttrib == null) return super.desc();
        else return Messages.get(this, "desc_made");
    }

    @Override
    public int cost() {
        return 20 * quantity;
    }

    public Item make(Mine.Device device) {

        try {
            return convertPlasmid((Plasmid) device.craftingClass.newInstance());
        } catch (Exception e) {
            PixelSpacebase.reportException(e);
            return null;
        }

    }

    private Item convertPlasmid(Plasmid plasmid) {

        plasmidAttrib = plasmid;
        plasmidAttrib.ownedByFruit = true;

        image = ItemSpriteSheet.PLASMID_KIT;
        plasmidAttrib.image = ItemSpriteSheet.PLASMID_KIT;

        if (plasmidAttrib instanceof HealingPlasmid) {
            name = Messages.get(this, "suntech");
            plasmidGlow = new ItemSprite.Glowing(0x2EE62E);
        } else if (plasmidAttrib instanceof MyoFiberPlasmid) {
            name = Messages.get(this, "rottech");
            plasmidGlow = new ItemSprite.Glowing(0xCC0022);
        } else if (plasmidAttrib instanceof ParalysisGrenade) {
            name = Messages.get(this, "earthtech");
            plasmidGlow = new ItemSprite.Glowing(0x67583D);
        } else if (plasmidAttrib instanceof CloakPlasmid) {
            name = Messages.get(this, "blindtech");
            plasmidGlow = new ItemSprite.Glowing(0xE5D273);
        } else if (plasmidAttrib instanceof FireGrenade) {
            name = Messages.get(this, "firetech");
            plasmidGlow = new ItemSprite.Glowing(0xFF7F00);
        } else if (plasmidAttrib instanceof CryoGrenade) {
            name = Messages.get(this, "icetech");
            plasmidGlow = new ItemSprite.Glowing(0x66B3FF);
        } else if (plasmidAttrib instanceof SecurityPlasmid) {
            name = Messages.get(this, "fadetech");
            plasmidGlow = new ItemSprite.Glowing(0xB8E6CF);
        } else if (plasmidAttrib instanceof ToxicGrenade) {
            name = Messages.get(this, "sorrowtech");
            plasmidGlow = new ItemSprite.Glowing(0xA15CE5);
        } else if (plasmidAttrib instanceof GravLiftPlasmid) {
            name = Messages.get(this, "stormtech");
            plasmidGlow = new ItemSprite.Glowing(0x1C3A57);
        } else if (plasmidAttrib instanceof PolymerPlasmid) {
            name = Messages.get(this, "dreamtech");
            plasmidGlow = new ItemSprite.Glowing(0x8E2975);
        } else if (plasmidAttrib instanceof ExperiencePlasmid) {
            name = Messages.get(this, "startech");
            plasmidGlow = new ItemSprite.Glowing(0xA79400);
        }

        return this;
    }

    private static final String PLASMID_ATTRIB = "plasmidattrib";

    @Override
    public void cast(final Hero user, int dst) {
        if (plasmidAttrib instanceof FireGrenade ||
                plasmidAttrib instanceof ToxicGrenade ||
                plasmidAttrib instanceof ParalysisGrenade ||
                plasmidAttrib instanceof CryoGrenade ||
                plasmidAttrib instanceof GravLiftPlasmid ||
                plasmidAttrib instanceof PolymerPlasmid) {
            plasmidAttrib.cast(user, dst);
            detach(user.belongings.backpack);
        } else {
            super.cast(user, dst);
        }

    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PLASMID_ATTRIB, plasmidAttrib);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(PLASMID_ATTRIB)) {
            convertPlasmid((Plasmid) bundle.get(PLASMID_ATTRIB));
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return plasmidGlow;
    }

}
