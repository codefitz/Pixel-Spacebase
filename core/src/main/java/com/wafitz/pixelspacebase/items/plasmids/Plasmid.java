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
package com.wafitz.pixelspacebase.items.plasmids;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.SpacebaseRun;
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

public class Plasmid extends Item {

    private static final String AC_OPEN = "OPEN";
    private static final String AC_USE = "USE";

    private static final float TIME_TO_USE = 1f;
    private static final String SEALED = "sealed";

    protected Integer initials;

    private static final Class<?>[] plasmids = {
            HealingPlasmid.class,
            ExperiencePlasmid.class,
            ToxicGrenade.class,
            FireGrenade.class,
            MyoFiberPlasmid.class,
            ParalysisGrenade.class,
            GravLiftPlasmid.class,
            SecurityPlasmid.class,
            PolymerPlasmid.class,
            CloakPlasmid.class,
            TitanPlasmid.class,
            CryoGrenade.class
    };

    private static final HashMap<String, Integer> colors = new HashMap<String, Integer>() {
        {
            put("crimson", ItemSpriteSheet.CRIMSON_PLASMID);
            put("amber", ItemSpriteSheet.AMBER_PLASMID);
            put("golden", ItemSpriteSheet.GOLDEN_PLASMID);
            put("jade", ItemSpriteSheet.JADE_PLASMID);
            put("turquoise", ItemSpriteSheet.TURQUOISE_PLASMID);
            put("azure", ItemSpriteSheet.AZURE_PLASMID);
            put("indigo", ItemSpriteSheet.INDIGO_PLASMID);
            put("magenta", ItemSpriteSheet.MAGENTA_PLASMID);
            put("bistre", ItemSpriteSheet.BISTRE_PLASMID);
            put("charcoal", ItemSpriteSheet.CHARCOAL_PLASMID);
            put("silver", ItemSpriteSheet.SILVER_PLASMID);
            put("ivory", ItemSpriteSheet.IVORY_PLASMID);
        }
    };

    private static ItemStatusHandler<Plasmid> handler;

    private String color;

    private boolean sealed = true;

    public boolean ownedByFruit = false;

    {
        stackable = true;
        defaultAction = AC_USE;
    }

    @SuppressWarnings("unchecked")
    public static void initColors() {
        handler = new ItemStatusHandler<>((Class<? extends Plasmid>[]) plasmids, colors);
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        handler.saveSelectively(bundle, items);
    }

    @SuppressWarnings("unchecked")
    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler<>((Class<? extends Plasmid>[]) plasmids, colors, bundle);
    }

    public Plasmid() {
        super();
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        image = handler.image(this);
        color = handler.label(this);
        updateDefaultAction();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        updateDefaultAction();
        ArrayList<String> actions = super.actions(hero);
        actions.add(isSealed() ? AC_OPEN : AC_USE);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {

        if (isSealed() && (action.equals(AC_OPEN) || action.equals(AC_USE))) {
            openStorage();
            drink(hero);
            return;
        }

        super.execute(hero, action);

        if (action.equals(AC_USE)) {

            if (isKnown() && (
                    this instanceof FireGrenade ||
                            this instanceof ToxicGrenade ||
                            this instanceof ParalysisGrenade)) {

                GameScene.show(
                        new WndOptions(Messages.get(Plasmid.class, "harmful"),
                                Messages.get(Plasmid.class, "sure_plug"),
                                Messages.get(Plasmid.class, "yes"), Messages.get(Plasmid.class, "no")) {
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

    public boolean isSealed() {
        return sealed && !isKnown();
    }

    private void updateDefaultAction() {
        defaultAction = isSealed() ? AC_OPEN : this instanceof FireGrenade ? AC_THROW : AC_USE;
    }

    @Override
    protected boolean shapeshifterThrowsByDefault(Hero hero) {
        return false;
    }

    private boolean revealStorage() {
        if (!isSealed()) {
            return false;
        }
        sealed = false;
        updateDefaultAction();
        updateQuickslot();
        return true;
    }

    private void openStorage() {
        if (revealStorage()) {
            GLog.i(Messages.get(Plasmid.class, "revealed", name()));
        }
    }

    @Override
    public boolean doPickUp(Hero hero) {
        return super.doPickUp(hero);
    }

    @Override
    public void doThrow(final Hero hero) {

        if (isKnown() && (
                this instanceof ExperiencePlasmid ||
                        this instanceof HealingPlasmid ||
                        this instanceof SecurityPlasmid ||
                        this instanceof MyoFiberPlasmid ||
                        this instanceof CloakPlasmid ||
                        this instanceof TitanPlasmid)) {

            GameScene.show(
                    new WndOptions(Messages.get(Plasmid.class, "beneficial"),
                            Messages.get(Plasmid.class, "sure_throw"),
                            Messages.get(Plasmid.class, "yes"), Messages.get(Plasmid.class, "no")) {
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                Plasmid.super.doThrow(hero);
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

        hero.preserveShapeshiftForNextSpend();
        hero.spend(TIME_TO_USE);
        hero.busy();
        apply(hero);

        Sample.INSTANCE.play(Assets.SND_DRINK);

        hero.sprite.operate(hero.pos);
    }

    @Override
    protected void onThrow(int cell) {
        if (SpacebaseRun.level.map[cell] == Terrain.WELL || Level.pit[cell]) {

            super.onThrow(cell);

        } else {

            SpacebaseRun.level.press(cell, null);
            shatter(cell);

        }
    }

    public void apply(Hero hero) {
        shatter(hero.pos);
    }

    public void shatter(int cell) {
        if (SpacebaseRun.visible[cell]) {
            GLog.i(Messages.get(Plasmid.class, "shatter"));
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

            Badges.validateAllPlasmidIdentified();
        }
    }

    @Override
    public Item identify() {

        sealed = false;
        updateDefaultAction();
        setKnown();
        return this;
    }

    @Override
    public String name() {
        if (isSealed()) {
            return Messages.get(Plasmid.class, "sealed_name");
        }
        return isKnown() ? super.name() : Messages.get(Plasmid.class, color);
    }

    @Override
    public String info() {
        if (isSealed()) {
            return Messages.get(Plasmid.class, "sealed_desc");
        }
        return isKnown() ? desc() : Messages.get(Plasmid.class, "unknown_desc");
    }

    public Integer initials() {
        return isKnown() ? initials : null;
    }

    @Override
    public int image() {
        if (isSealed()) {
            return ItemSpriteSheet.SEALED_STORAGE;
        } else if (isKnown()) {
            if (this instanceof FireGrenade) {
                return ItemSpriteSheet.FIRE_GRENADE_GENE_MOD;
            } else if (this instanceof ToxicGrenade) {
                return ItemSpriteSheet.TOXIC_GRENADE_GENE_MOD;
            } else if (this instanceof ParalysisGrenade) {
                return ItemSpriteSheet.PARALYSIS_GRENADE_GENE_MOD;
            }
        }
        return super.image();
    }

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && item instanceof Plasmid && isSealed() == ((Plasmid) item).isSealed();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SEALED, sealed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        sealed = !bundle.contains(SEALED) || bundle.getBoolean(SEALED);
        updateDefaultAction();
    }

    @Override
    public boolean isIdentified() {
        return isKnown();
    }

    @Override
    public boolean goesInOrdnanceKit() {
        return !isSealed() && (
                this instanceof FireGrenade
                        || this instanceof ToxicGrenade
                        || this instanceof ParalysisGrenade
                        || this instanceof CryoGrenade);
    }

    @Override
    public boolean goesInPlasmidKit() {
        return !isSealed() && !goesInOrdnanceKit();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public static HashSet<Class<? extends Plasmid>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends Plasmid>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler.known().size() == plasmids.length;
    }

    protected void splash(int cell) {
        final int color = ItemSprite.pick(image, 8, 10);
        Splash.at(cell, color, 5);

        Fire fire = (Fire) SpacebaseRun.level.blobs.get(Fire.class);
        if (fire != null)
            fire.clear(cell);

        Char ch = Actor.findChar(cell);
        if (ch != null)
            Buff.detach(ch, Burning.class);
    }

    @Override
    public int cost() {
        return 30 * quantity;
    }
}
