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
package com.wafitz.pixelspacebase.items.scripts;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.ItemStatusHandler;
import com.wafitz.pixelspacebase.items.artifacts.UnstableSpellbook;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.HeroSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Script extends Item {

    private static final String AC_READ = "READ";

    static final float TIME_TO_READ = 1f;

    protected int initials;

    private static final Class<?>[] scripts = {
            ScriptOfIdentify.class,
            ScriptOfMagicMapping.class,
            ScriptOfRecharging.class,
            FixScript.class,
            ScriptOfTeleportation.class,
            ScriptOfUpgrade.class,
            ScriptOfRage.class,
            ScriptOfTerror.class,
            ScriptOfLullaby.class,
            ScriptOfMagicalInfusion.class,
            ScriptOfPsionicBlast.class,
            ScriptOfMirrorImage.class
    };

    private static final HashMap<String, Integer> runes = new HashMap<String, Integer>() {
        {
            put("KAUNAN", ItemSpriteSheet.KAUNAN_SCRIPT);
            put("SOWILO", ItemSpriteSheet.SOWILO_SCRIPT);
            put("LAGUZ", ItemSpriteSheet.LAGUZ_SCRIPT);
            put("YNGVI", ItemSpriteSheet.YNGVI_SCRIPT);
            put("GYFU", ItemSpriteSheet.GYFU_SCRIPT);
            put("RAIDO", ItemSpriteSheet.RAIDO_SCRIPT);
            put("ISAZ", ItemSpriteSheet.ISAZ_SCRIPT);
            put("MANNAZ", ItemSpriteSheet.MANNAZ_SCRIPT);
            put("NAUDIZ", ItemSpriteSheet.NAUDIZ_SCRIPT);
            put("BERKANAN", ItemSpriteSheet.BERKANAN_SCRIPT);
            put("ODAL", ItemSpriteSheet.ODAL_SCRIPT);
            put("TIWAZ", ItemSpriteSheet.TIWAZ_SCRIPT);
        }
    };

    private static ItemStatusHandler<Script> handler;

    private String rune;

    public boolean ownedByBook = false;

    {
        stackable = true;
        defaultAction = AC_READ;
    }

    @SuppressWarnings("unchecked")
    public static void initLabels() {
        handler = new ItemStatusHandler<>((Class<? extends Script>[]) scripts, runes);
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        handler.saveSelectively(bundle, items);
    }

    @SuppressWarnings("unchecked")
    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler<>((Class<? extends Script>[]) scripts, runes, bundle);
    }

    public Script() {
        super();
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        image = handler.image(this);
        rune = handler.label(this);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_READ);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_READ)) {

            if (hero.buff(Blindness.class) != null) {
                GLog.w(Messages.get(this, "blinded"));
            } else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null
                    && hero.buff(UnstableSpellbook.bookRecharge.class).isMalfunctioning()
                    && !(this instanceof FixScript)) {
                GLog.n(Messages.get(this, "malfunctioning"));
            } else {
                curUser = hero;
                curItem = detach(hero.belongings.backpack);
                doRead();
            }

        }
    }

    abstract protected void doRead();

    void readAnimation() {
        curUser.spend(TIME_TO_READ);
        curUser.busy();
        ((HeroSprite) curUser.sprite).read();
    }

    public boolean isKnown() {
        return handler.isKnown(this);
    }

    public void setKnown() {
        if (!isKnown() && !ownedByBook) {
            handler.know(this);
        }

        Badges.validateAllScriptsIdentified();
    }

    @Override
    public Item identify() {
        setKnown();
        return super.identify();
    }

    @Override
    public String name() {
        return isKnown() ? name : Messages.get(Script.class, rune);
    }

    @Override
    public String info() {
        return isKnown() ?
                desc() :
                Messages.get(this, "unknown_desc");
    }

    public Integer initials() {
        return isKnown() ? initials : null;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return isKnown();
    }

    public static HashSet<Class<? extends Script>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends Script>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler.known().size() == scripts.length;
    }

    @Override
    public int price() {
        return 30 * quantity;
    }
}
