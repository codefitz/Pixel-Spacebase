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
package com.wafitz.pixelspacebase.items.modules;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.ItemStatusHandler;
import com.wafitz.pixelspacebase.items.KindofMisc;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class Module extends KindofMisc {

    private static final int TICKS_TO_KNOW = 200;

    protected Buff buff;

    private static final Class<?>[] modules = {
            AccuracyModule.class,
            EvasionModule.class,
            ElementsModule.class,
            ForceModule.class,
            FurorModule.class,
            SpeedModule.class,
            ScienceModule.class,
            PowerModule.class,
            TargetingModule.class,
            SteelModule.class,
            TechModule.class,
    };

    private static final HashMap<String, Integer> gems = new HashMap<String, Integer>() {
        {
            put("garnet", ItemSpriteSheet.MODULE_GARNET);
            put("ruby", ItemSpriteSheet.MODULE_RUBY);
            put("topaz", ItemSpriteSheet.MODULE_TOPAZ);
            put("emerald", ItemSpriteSheet.MODULE_EMERALD);
            put("onyx", ItemSpriteSheet.MODULE_ONYX);
            put("opal", ItemSpriteSheet.MODULE_OPAL);
            put("tourmaline", ItemSpriteSheet.MODULE_TOURMALINE);
            put("sapphire", ItemSpriteSheet.MODULE_SAPPHIRE);
            put("amethyst", ItemSpriteSheet.MODULE_AMETHYST);
            put("quartz", ItemSpriteSheet.MODULE_QUARTZ);
            put("agate", ItemSpriteSheet.MODULE_AGATE);
            put("diamond", ItemSpriteSheet.MODULE_DIAMOND);
        }
    };

    private static ItemStatusHandler<Module> handler;

    private String gem;

    private int ticksToKnow = TICKS_TO_KNOW;

    @SuppressWarnings("unchecked")
    public static void initGems() {
        handler = new ItemStatusHandler<>((Class<? extends Module>[]) modules, gems);
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        handler.saveSelectively(bundle, items);
    }

    @SuppressWarnings("unchecked")
    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler<>((Class<? extends Module>[]) modules, gems, bundle);
    }

    public Module() {
        super();
        reset();
    }

    public void reset() {
        super.reset();
        image = handler.image(this);
        gem = handler.label(this);
    }

    public void activate(Char ch) {
        buff = buff();
        buff.attachTo(ch);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {

            hero.remove(buff);
            buff = null;

            return true;

        } else {

            return false;

        }
    }

    public boolean isKnown() {
        return handler.isKnown(this);
    }

    protected void setKnown() {
        if (!isKnown()) {
            handler.know(this);
        }

        Badges.validateAllModulesIdentified();
    }

    @Override
    public String name() {
        return isKnown() ? super.name() : Messages.get(Module.class, gem);
    }

    @Override
    public String info() {

        String desc = isKnown() ? desc() : Messages.get(this, "unknown_desc");

        if (malfunctioning && isEquipped(Dungeon.hero)) {

            desc += "\n\n" + Messages.get(Module.class, "malfunctioning_worn");

        } else if (malfunctioning && malfunctioningKnown) {

            desc += "\n\n" + Messages.get(Module.class, "malfunction_known");

        }

        return desc;
    }

    @Override
    public boolean isIdentified() {
        return super.isIdentified() && isKnown();
    }

    @Override
    public Item identify() {
        setKnown();
        return super.identify();
    }

    @Override
    public Item random() {
        int n = 1;
        if (Random.Int(3) == 0) {
            n++;
            if (Random.Int(5) == 0) {
                n++;
            }
        }

        if (Random.Float() < 0.3f) {
            level(-n);
            malfunctioning = true;
        } else
            level(n);

        return this;
    }

    public static boolean allKnown() {
        return handler.known().size() == modules.length - 2;
    }

    @Override
    public int price() {
        int price = 75;
        if (malfunctioning && malfunctioningKnown) {
            price /= 2;
        }
        if (levelKnown) {
            if (level() > 0) {
                price *= (level() + 1);
            } else if (level() < 0) {
                price /= (1 - level());
            }
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    protected ModuleBuff buff() {
        return null;
    }

    private static final String UNFAMILIRIARITY = "unfamiliarity";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(UNFAMILIRIARITY, ticksToKnow);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if ((ticksToKnow = bundle.getInt(UNFAMILIRIARITY)) == 0) {
            ticksToKnow = TICKS_TO_KNOW;
        }
    }

    public static int getBonus(Char target, Class<? extends ModuleBuff> type) {
        int bonus = 0;
        for (ModuleBuff buff : target.buffs(type)) {
            bonus += buff.level();
        }
        return bonus;
    }

    public class ModuleBuff extends Buff {

        @Override
        public boolean attachTo(Char target) {

            if (target instanceof Hero && ((Hero) target).heroClass == HeroClass.SHAPESHIFTER && !isKnown()) {
                setKnown();
                GLog.i(Messages.get(Module.class, "known", name()));
                Badges.validateItemLevelAquired(Module.this);
            }

            return super.attachTo(target);
        }

        @Override
        public boolean act() {

            if (!isIdentified() && --ticksToKnow <= 0) {
                identify();
                GLog.w(Messages.get(Module.class, "identify", Module.this.toString()));
                Badges.validateItemLevelAquired(Module.this);
            }

            spend(TICK);

            return true;
        }

        public int level() {
            return Module.this.level();
        }

    }
}
