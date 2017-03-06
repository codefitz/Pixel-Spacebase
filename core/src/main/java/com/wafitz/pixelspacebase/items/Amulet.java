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
package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.scenes.AmuletScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.io.IOException;
import java.util.ArrayList;

public class Amulet extends Item {

    private static final String AC_END = "END";

    {
        image = ItemSpriteSheet.AMULET;

        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_END);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_END)) {
            showAmuletScene(false);
        }
    }

    @Override
    public boolean doPickUp(Hero hero) {
        if (super.doPickUp(hero)) {

            if (!Statistics.amuletObtained) {
                Statistics.amuletObtained = true;
                Badges.validateVictory();
                hero.spend(-TIME_TO_PICK_UP);

                //add a delayed actor here so pickup behaviour can fully process.
                Actor.addDelayed(new Actor() {
                    @Override
                    protected boolean act() {
                        Actor.remove(this);
                        showAmuletScene(true);
                        return false;
                    }
                }, -5);
            }

            return true;
        } else {
            return false;
        }
    }

    private void showAmuletScene(boolean showText) {
        try {
            Dungeon.saveAll();
            AmuletScene.noText = !showText;
            Game.switchScene(AmuletScene.class);
        } catch (IOException e) {
            PixelSpacebase.reportException(e);
        }
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

}
