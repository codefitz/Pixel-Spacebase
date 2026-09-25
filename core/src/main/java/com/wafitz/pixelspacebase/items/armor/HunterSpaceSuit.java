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
package com.wafitz.pixelspacebase.items.armor;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.JetPack;
import com.wafitz.pixelspacebase.actors.buffs.LockedDown;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.StartScene;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class HunterSpaceSuit extends Armor {

    private static final String AC_JETPACK_ON = "JETPACK_ON";
    private static final String AC_JETPACK_OFF = "JETPACK_OFF";
    private static final String JETPACK_ON = "jetpackOn";
    private static final float TIME_TO_SWITCH = 1f;

    private boolean jetpackOn;

    {

        image = ItemSpriteSheet.ARMOR_HUNTER;

        if (StartScene.curClass == HeroClass.DM3000) {
            image = ItemSpriteSheet.ARMOR_DM3000_HUNTER;
        }

    }

    public HunterSpaceSuit() {
        super(3);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(JETPACK_ON, jetpackOn);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        jetpackOn = bundle.getBoolean(JETPACK_ON);
    }

    @Override
    public void reset() {
        super.reset();
        jetpackOn = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero)) actions.add(jetpackOn ? AC_JETPACK_OFF : AC_JETPACK_ON);
        return actions;
    }

    @Override
    public void execute(Hero hero) {
        if (isEquipped(hero))
            execute(hero, jetpackOn ? AC_JETPACK_OFF : AC_JETPACK_ON);
        else if (hero.heroClass == HeroClass.SHAPESHIFTER)
            super.execute(hero);
        else
            execute(hero, AC_EQUIP);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (isEquipped(hero) && (AC_JETPACK_ON.equals(action) || AC_JETPACK_OFF.equals(action))) {
            jetpackOn = AC_JETPACK_ON.equals(action);
            GLog.i(Messages.get(this, jetpackOn ? "jetpack_started" : "jetpack_stopped"));
            hero.spendAndNext(TIME_TO_SWITCH);
            updateQuickslot();
            updateFlight(hero, !jetpackOn);
        }
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        if (jetpackOn) {
            ch.flying = true;
            Buff.detach(ch, LockedDown.class);
            if (ch.sprite != null && ch.buff(JetPack.class) == null)
                ch.sprite.add(CharSprite.State.LEVITATING);
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (!super.doUnequip(hero, collect, single)) return false;
        jetpackOn = false;
        updateFlight(hero, true);
        return true;
    }

    @Override
    public void forceUnequip(Hero hero) {
        super.forceUnequip(hero);
        powerDown(hero);
    }

    public void powerDown(Hero hero) {
        jetpackOn = false;
        updateFlight(hero, true);
    }

    private void updateFlight(Hero hero, boolean checkLanding) {
        boolean timedJetPack = hero.buff(JetPack.class) != null;
        hero.flying = jetpackOn || timedJetPack;
        if (hero.sprite != null && !timedJetPack) {
            if (jetpackOn) hero.sprite.add(CharSprite.State.LEVITATING);
            else hero.sprite.remove(CharSprite.State.LEVITATING);
        }
        if (jetpackOn) Buff.detach(hero, LockedDown.class);
        if (checkLanding && !hero.flying && SpacebaseRun.level != null)
            SpacebaseRun.level.press(hero.pos, hero);
    }

    public static boolean jetpackEnabled(Char ch) {
        return SpacebaseRun.hero != null && ch == SpacebaseRun.hero
                && SpacebaseRun.hero.belongings.armor instanceof HunterSpaceSuit
                && ((HunterSpaceSuit) SpacebaseRun.hero.belongings.armor).jetpackOn;
    }

    public static boolean signatureScannerActive() {
        return SpacebaseRun.hero != null && SpacebaseRun.level != null
                && SpacebaseRun.hero.belongings.armor instanceof HunterSpaceSuit
                && !SpacebaseRun.level.floorBreakerOn;
    }

}
