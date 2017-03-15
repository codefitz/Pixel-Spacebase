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
package com.wafitz.pixelspacebase.items.ExperimentalTech;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Fire;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Splash;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.ItemStatusHandler;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ExperimentalTech extends Item {

    private static final String AC_DRINK = "DRINK";

    private static final float TIME_TO_DRINK = 1f;

    protected Integer initials;

    private static final Class<?>[] experimentaltech = {
            ExperimentalTechOfHealing.class,
            ExperimentalTechOfExperience.class,
            ExperimentalTechOfToxicGas.class,
            ExperimentalTechOfLiquidFlame.class,
            ExperimentalTechOfStrength.class,
            ExperimentalTechOfParalyticGas.class,
            ExperimentalTechOfLevitation.class,
            ExperimentalTechOfMindVision.class,
            ExperimentalTechOfPurity.class,
            ExperimentalTechOfInvisibility.class,
            ExperimentalTechOfMight.class,
            ExperimentalTechOfFrost.class
    };

    private static final HashMap<String, Integer> colors = new HashMap<String, Integer>() {
        {
            put("crimson", ItemSpriteSheet.CRIMSON_EXPERIMENTAL_TECH);
            put("amber", ItemSpriteSheet.AMBER_EXPERIMENTAL_TECH);
            put("golden", ItemSpriteSheet.GOLDEN_EXPERIMENTAL_TECH);
            put("jade", ItemSpriteSheet.JADE_EXPERIMENTAL_TECH);
            put("turquoise", ItemSpriteSheet.TURQUOISE_EXPERIMENTAL_TECH);
            put("azure", ItemSpriteSheet.AZURE_EXPERIMENTAL_TECH);
            put("indigo", ItemSpriteSheet.INDIGO_EXPERIMENTAL_TECH);
            put("magenta", ItemSpriteSheet.MAGENTA_EXPERIMENTAL_TECH);
            put("bistre", ItemSpriteSheet.BISTRE_EXPERIMENTAL_TECH);
            put("charcoal", ItemSpriteSheet.CHARCOAL_EXPERIMENTAL_TECH);
            put("silver", ItemSpriteSheet.SILVER_EXPERIMENTAL_TECH);
            put("ivory", ItemSpriteSheet.IVORY_EXPERIMENTAL_TECH);
        }
    };

    private static ItemStatusHandler<ExperimentalTech> handler;

    private String color;

    public boolean ownedByFruit = false;

    {
        stackable = true;
        defaultAction = AC_DRINK;
    }

    @SuppressWarnings("unchecked")
    public static void initColors() {
        handler = new ItemStatusHandler<>((Class<? extends ExperimentalTech>[]) experimentaltech, colors);
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        handler.saveSelectively(bundle, items);
    }

    @SuppressWarnings("unchecked")
    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler<>((Class<? extends ExperimentalTech>[]) experimentaltech, colors, bundle);
    }

    public ExperimentalTech() {
        super();
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        image = handler.image(this);
        color = handler.label(this);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_DRINK);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_DRINK)) {

            if (isKnown() && (
                    this instanceof ExperimentalTechOfLiquidFlame ||
                            this instanceof ExperimentalTechOfToxicGas ||
                            this instanceof ExperimentalTechOfParalyticGas)) {

                GameScene.show(
                        new WndOptions(Messages.get(ExperimentalTech.class, "harmful"),
                                Messages.get(ExperimentalTech.class, "sure_drink"),
                                Messages.get(ExperimentalTech.class, "yes"), Messages.get(ExperimentalTech.class, "no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    drink(hero);
                                }
                            }
                        }
                );

            } else {
                drink(hero);
            }

        }
    }

    @Override
    public void doThrow(final Hero hero) {

        if (isKnown() && (
                this instanceof ExperimentalTechOfExperience ||
                        this instanceof ExperimentalTechOfHealing ||
                        this instanceof ExperimentalTechOfMindVision ||
                        this instanceof ExperimentalTechOfStrength ||
                        this instanceof ExperimentalTechOfInvisibility ||
                        this instanceof ExperimentalTechOfMight)) {

            GameScene.show(
                    new WndOptions(Messages.get(ExperimentalTech.class, "beneficial"),
                            Messages.get(ExperimentalTech.class, "sure_throw"),
                            Messages.get(ExperimentalTech.class, "yes"), Messages.get(ExperimentalTech.class, "no")) {
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                ExperimentalTech.super.doThrow(hero);
                            }
                        }
                    }
            );

        } else {
            super.doThrow(hero);
        }
    }

    private void drink(Hero hero) {

        detach(hero.belongings.backpack);

        hero.spend(TIME_TO_DRINK);
        hero.busy();
        apply(hero);

        Sample.INSTANCE.play(Assets.SND_DRINK);

        hero.sprite.operate(hero.pos);
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.map[cell] == Terrain.WELL || Level.pit[cell]) {

            super.onThrow(cell);

        } else {

            Dungeon.level.press(cell, null);
            shatter(cell);

        }
    }

    public void apply(Hero hero) {
        shatter(hero.pos);
    }

    public void shatter(int cell) {
        if (Dungeon.visible[cell]) {
            GLog.i(Messages.get(ExperimentalTech.class, "shatter"));
            Sample.INSTANCE.play(Assets.SND_SHATTER);
            splash(cell);
        }
    }

    @Override
    public void cast(final Hero user, int dst) {
        super.cast(user, dst);
    }

    public boolean isKnown() {
        return handler.isKnown(this);
    }

    public void setKnown() {
        if (!ownedByFruit) {
            if (!isKnown()) {
                handler.know(this);
            }

            Badges.validateAllExperimentalTechIdentified();
        }
    }

    @Override
    public Item identify() {

        setKnown();
        return this;
    }

    @Override
    public String name() {
        return isKnown() ? super.name() : Messages.get(ExperimentalTech.class, color);
    }

    @Override
    public String info() {
        return isKnown() ?
                desc() :
                Messages.get(ExperimentalTech.class, "unknown_desc");
    }

    public Integer initials() {
        return isKnown() ? initials : null;
    }

    @Override
    public boolean isIdentified() {
        return isKnown();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public static HashSet<Class<? extends ExperimentalTech>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends ExperimentalTech>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler.known().size() == experimentaltech.length;
    }

    protected void splash(int cell) {
        final int color = ItemSprite.pick(image, 8, 10);
        Splash.at(cell, color, 5);

        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
        if (fire != null)
            fire.clear(cell);

        Char ch = Actor.findChar(cell);
        if (ch != null)
            Buff.detach(ch, Burning.class);
    }

    @Override
    public int price() {
        return 30 * quantity;
    }
}
