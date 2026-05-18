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
package com.wafitz.pixelspacebase.items.upgrades;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.ItemStatusHandler;
import com.wafitz.pixelspacebase.items.equippablemodules.BuggyCompiler;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.HeroSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Upgrade extends Item {

    private static final String AC_OPEN = "OPEN";
    private static final String AC_RUN = "RUN";

    static final float TIME_TO_READ = 1f;
    private static final String SEALED = "sealed";

    protected int initials;

    private static final Class<?>[] upgrades = {
            DiagnosticScanUpgrade.class,
            MappingUpgrade.class,
            RechargeUpgrade.class,
            RepairUpgrade.class,
            PhaseShiftUpgrade.class,
            UpgradePatch.class,
            EchoLocationUpgrade.class,
            PanicUpgrade.class,
            KnockoutUpgrade.class,
            EnhancementUpgrade.class,
            PsionicBlastUpgrade.class,
            WeakCloneUpgrade.class
    };

    private static final HashMap<String, Integer> runes = new HashMap<String, Integer>() {
        {
            put("KAUNAN", ItemSpriteSheet.KAUNAN_UPGRADE);
            put("SOWILO", ItemSpriteSheet.SOWILO_UPGRADE);
            put("LAGUZ", ItemSpriteSheet.LAGUZ_UPGRADE);
            put("YNGVI", ItemSpriteSheet.YNGVI_UPGRADE);
            put("GYFU", ItemSpriteSheet.GYFU_UPGRADE);
            put("RAIDO", ItemSpriteSheet.RAIDO_UPGRADE);
            put("ISAZ", ItemSpriteSheet.ISAZ_UPGRADE);
            put("MANNAZ", ItemSpriteSheet.MANNAZ_UPGRADE);
            put("NAUDIZ", ItemSpriteSheet.NAUDIZ_UPGRADE);
            put("BERKANAN", ItemSpriteSheet.BERKANAN_UPGRADE);
            put("ODAL", ItemSpriteSheet.ODAL_UPGRADE);
            put("TIWAZ", ItemSpriteSheet.TIWAZ_UPGRADE);
        }
    };

    private static ItemStatusHandler<Upgrade> handler;

    private String rune;

    private boolean sealed = true;

    public boolean ownedByBook = false;

    {
        stackable = true;
        defaultAction = AC_RUN;
    }

    @SuppressWarnings("unchecked")
    public static void initLabels() {
        handler = new ItemStatusHandler<>((Class<? extends Upgrade>[]) upgrades, runes);
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        handler.saveSelectively(bundle, items);
    }

    @SuppressWarnings("unchecked")
    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler<>((Class<? extends Upgrade>[]) upgrades, runes, bundle);
    }

    public Upgrade() {
        super();
        reset();
    }

    @Override
    public void reset() {
        super.reset();
        image = handler.image(this);
        rune = handler.label(this);
        updateDefaultAction();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        updateDefaultAction();
        ArrayList<String> actions = super.actions(hero);
        actions.add(isSealed() ? AC_OPEN : AC_RUN);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        if (isSealed() && (action.equals(AC_OPEN) || action.equals(AC_RUN))) {
            if (canRunUpgrade(hero)) {
                openStorage();
                consumeUpgrade(hero);
            }
            return;
        }

        super.execute(hero, action);

        if (action.equals(AC_RUN)) {
            runUpgrade(hero);

        }
    }

    private void runUpgrade(Hero hero) {
        if (canRunUpgrade(hero)) {
            consumeUpgrade(hero);
        }
    }

    private boolean canRunUpgrade(Hero hero) {
        if (hero.buff(Blindness.class) != null) {
            GLog.w(Messages.get(this, "blinded"));
            return false;
        } else if (hero.buff(BuggyCompiler.bookRecharge.class) != null
                && hero.buff(BuggyCompiler.bookRecharge.class).isMalfunctioning()
                && !(this instanceof RepairUpgrade)) {
            GLog.n(Messages.get(this, "malfunctioning"));
            return false;
        }
        return true;
    }

    private void consumeUpgrade(Hero hero) {
        curUser = hero;
        curItem = detach(hero.belongings.backpack);
        doRead();
    }

    abstract protected void doRead();

    public boolean isSealed() {
        return sealed && !isKnown();
    }

    private void updateDefaultAction() {
        defaultAction = isSealed() ? AC_OPEN : AC_RUN;
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
            GLog.i(Messages.get(Upgrade.class, "revealed", name()));
        }
    }

    @Override
    public boolean doPickUp(Hero hero) {
        return super.doPickUp(hero);
    }

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

        Badges.validateAllUpgradesIdentified();
    }

    @Override
    public Item identify() {
        sealed = false;
        updateDefaultAction();
        setKnown();
        return super.identify();
    }

    @Override
    public String name() {
        if (isSealed()) {
            return Messages.get(Upgrade.class, "sealed_name");
        }
        return isKnown() ? name : Messages.get(Upgrade.class, rune);
    }

    @Override
    public String info() {
        if (isSealed()) {
            return Messages.get(Upgrade.class, "sealed_desc");
        }
        return isKnown() ? desc() : Messages.get(this, "unknown_desc");
    }

    public Integer initials() {
        return isKnown() ? initials : null;
    }

    @Override
    public int image() {
        return isSealed() ? ItemSpriteSheet.SEALED_STORAGE : super.image();
    }

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && item instanceof Upgrade && isSealed() == ((Upgrade) item).isSealed();
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
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return isKnown();
    }

    @Override
    public boolean goesInOrdnanceKit() {
        return !isSealed() && (
                this instanceof PsionicBlastUpgrade
                        || this instanceof KnockoutUpgrade
                        || this instanceof PanicUpgrade
                        || this instanceof EchoLocationUpgrade);
    }

    @Override
    public boolean goesInUtilityKit() {
        return !isSealed() && !goesInOrdnanceKit();
    }

    public static HashSet<Class<? extends Upgrade>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends Upgrade>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler.known().size() == upgrades.length;
    }

    @Override
    public int cost() {
        return 30 * quantity;
    }
}
