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
package com.wafitz.pixelspacebase.items.weapon;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.KindOfWeapon;
import com.wafitz.pixelspacebase.items.modules.AttackModule;
import com.wafitz.pixelspacebase.items.modules.TargetingModule;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Blazing;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Buggy;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Chilling;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Dazzling;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Eldritch;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Grim;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Lucky;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Projecting;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Shocking;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Stunning;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Vampiric;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Venomous;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Vorpal;
import com.wafitz.pixelspacebase.items.weapon.malfunctions.Annoying;
import com.wafitz.pixelspacebase.items.weapon.malfunctions.Backfiring;
import com.wafitz.pixelspacebase.items.weapon.malfunctions.Displacing;
import com.wafitz.pixelspacebase.items.weapon.malfunctions.Fragile;
import com.wafitz.pixelspacebase.items.weapon.malfunctions.LowEnergy;
import com.wafitz.pixelspacebase.items.weapon.malfunctions.Misfiring;
import com.wafitz.pixelspacebase.items.weapon.melee.MeleeWeapon;
import com.wafitz.pixelspacebase.items.weapon.missiles.MissileWeapon;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

abstract public class Weapon extends KindOfWeapon {

    private static final int HITS_TO_KNOW = 20;

    private static final String TXT_TO_STRING = "%s :%d";

    public float ACC = 1f;    // Accuracy modifier
    public float DLY = 1f;    // Speed modifier
    public int RCH = 1;    // Reach modifier (only applies to melee hits)

    public enum Convert {
        NONE(1.0f, 1.00f),
        LIGHT(0.7f, 0.67f),
        HEAVY(1.5f, 1.67f);

        private float damageFactor;
        private float delayFactor;

        Convert(float dmg, float dly) {
            damageFactor = dmg;
            delayFactor = dly;
        }

        public int damageFactor(int dmg) {
            return Math.round(dmg * damageFactor);
        }

        public float delayFactor(float dly) {
            return dly * delayFactor;
        }
    }

    public Convert convert = Convert.NONE;

    private int hitsToKnow = HITS_TO_KNOW;

    public Enhancement enhancement;

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        if (enhancement != null) {
            damage = enhancement.proc(this, attacker, defender, damage);
        }

        if (!levelKnown) {
            if (--hitsToKnow <= 0) {
                levelKnown = true;
                GLog.i(Messages.get(Weapon.class, "identify", name()));
                Badges.validateItemLevelAquired(this);
            }
        }

        return damage;
    }

    private static final String UNFAMILIRIARITY = "unfamiliarity";
    private static final String ENHANCEMENT = "enhancement";
    private static final String CONVERT = "convert";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(UNFAMILIRIARITY, hitsToKnow);
        bundle.put(ENHANCEMENT, enhancement);
        bundle.put(CONVERT, convert);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if ((hitsToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
            hitsToKnow = HITS_TO_KNOW;
        }
        enhancement = (Enhancement) bundle.get(ENHANCEMENT);
        convert = bundle.getEnum(CONVERT, Convert.class);
    }

    @Override
    public float accuracyFactor(Hero hero) {

        int encumbrance = STRReq() - hero.STR();

        if (hasEnhance(Misfiring.class))
            encumbrance = Math.max(3, encumbrance + 3);

        float ACC = this.ACC;

        if (this instanceof MissileWeapon) {
            int bonus = TargetingModule.getBonus(hero, TargetingModule.Aim.class);
            ACC *= (float) (Math.pow(1.2, bonus));
        }

        return encumbrance > 0 ? (float) (ACC / Math.pow(1.5, encumbrance)) : ACC;
    }

    @Override
    public float speedFactor(Hero hero) {

        int encumrance = STRReq() - hero.STR();
        if (this instanceof MissileWeapon && hero.heroClass == HeroClass.CAPTAIN) {
            encumrance -= 2;
        }

        float DLY = convert.delayFactor(this.DLY);

        int bonus = AttackModule.getBonus(hero, AttackModule.Furor.class);

        DLY = (float) (0.2 + (DLY - 0.2) * Math.pow(0.85, bonus));

        return
                (encumrance > 0 ? (float) (DLY * Math.pow(1.2, encumrance)) : DLY);
    }

    @Override
    public int reachFactor(Hero hero) {
        return hasEnhance(Projecting.class) ? RCH + 1 : RCH;
    }

    @Override
    public int damageRoll(Hero hero) {

        int damage = super.damageRoll(hero);

        if (this instanceof MeleeWeapon || (this instanceof MissileWeapon && hero.heroClass == HeroClass.CAPTAIN)) {
            int exStr = hero.STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange(0, exStr);
            }
        }

        return convert.damageFactor(damage);
    }

    public int STRReq() {
        return STRReq(level());
    }

    public abstract int STRReq(int lvl);

    public Item upgrade(boolean enhance) {

        if (enhance && (enhancement == null || enhancement.malfunction())) {
            enhance(Enhancement.random());
        } else if (!enhance && Random.Float() > Math.pow(0.9, level())) {
            enhance(null);
        }

        return super.upgrade();
    }

    @Override
    public String name() {
        return enhancement != null && (malfunctioningKnown || !enhancement.malfunction()) ? enhancement.name(super.name()) : super.name();
    }

    @Override
    public Item random() {
        float roll = Random.Float();
        if (roll < 0.3f) {
            //30% chance to be level 0 and malfunctioning
            enhance(Enhancement.randomMalfunction());
            malfunctioning = true;
            return this;
        } else if (roll < 0.75f) {
            //45% chance to be level 0
        } else if (roll < 0.95f) {
            //15% chance to be +1
            upgrade(1);
        } else {
            //5% chance to be +2
            upgrade(2);
        }

        //if not malfunctioning, 10% chance to be enchanted (7% overall)
        if (Random.Int(10) == 0)
            enhance();

        return this;
    }

    public Weapon enhance(Enhancement ench) {
        enhancement = ench;
        return this;
    }

    public Weapon enhance() {

        Class<? extends Enhancement> oldEnhancement = enhancement != null ? enhancement.getClass() : null;
        Enhancement ench = Enhancement.random();
        while (ench.getClass() == oldEnhancement) {
            ench = Enhancement.random();
        }

        return enhance(ench);
    }

    public boolean hasEnhance(Class<? extends Enhancement> type) {
        return enhancement != null && enhancement.getClass() == type;
    }

    public boolean hasGoodEnhance() {
        return enhancement != null && !enhancement.malfunction();
    }

    public boolean hasMalfunctionEnhance() {
        return enhancement != null && enhancement.malfunction();
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return enhancement != null && (malfunctioningKnown || !enhancement.malfunction()) ? enhancement.glowing() : null;
    }

    public static abstract class Enhancement implements Bundlable {

        private static final Class<?>[] enhances = new Class<?>[]{
                Blazing.class, Venomous.class, Vorpal.class, Shocking.class,
                Chilling.class, Eldritch.class, Lucky.class, Projecting.class, Buggy.class, Dazzling.class,
                Grim.class, Stunning.class, Vampiric.class,};
        private static final float[] chances = new float[]{
                10, 10, 10, 10,
                5, 5, 5, 5, 5, 5,
                2, 2, 2};

        private static final Class<?>[] malfunctions = new Class<?>[]{
                Annoying.class, Displacing.class, LowEnergy.class, Fragile.class, Backfiring.class, Misfiring.class
        };

        public abstract int proc(Weapon weapon, Char attacker, Char defender, int damage);

        public String name() {
            if (!malfunction())
                return name(Messages.get(this, "enhance"));
            else
                return name(Messages.get(Item.class, "malfunction"));
        }

        public String name(String weaponName) {
            return Messages.get(this, "name", weaponName);
        }

        public String desc() {
            return Messages.get(this, "desc", name());
        }

        public boolean malfunction() {
            return false;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
        }

        @Override
        public void storeInBundle(Bundle bundle) {
        }

        public abstract ItemSprite.Glowing glowing();

        @SuppressWarnings("unchecked")
        public static Enhancement random() {
            try {
                return ((Class<Enhancement>) enhances[Random.chances(chances)]).newInstance();
            } catch (Exception e) {
                PixelSpacebase.reportException(e);
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        public static Enhancement randomMalfunction() {
            try {
                return ((Class<Enhancement>) Random.oneOf(malfunctions)).newInstance();
            } catch (Exception e) {
                PixelSpacebase.reportException(e);
                return null;
            }
        }

    }
}
