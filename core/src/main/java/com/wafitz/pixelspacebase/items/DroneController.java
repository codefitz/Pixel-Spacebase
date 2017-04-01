/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Drone;
import com.wafitz.pixelspacebase.effects.Pushing;
import com.wafitz.pixelspacebase.effects.Splash;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DroneController extends Item {

    private static final String AC_ACTIVATE = "ACTIVATE";

    {
        image = ItemSpriteSheet.DRONECONTROLLER;

        defaultAction = AC_THROW;
        usesTargeting = true;

        stackable = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ACTIVATE);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ACTIVATE)) {

            hero.sprite.zap(hero.pos);

            detach(hero.belongings.backpack);

            shatter(hero, hero.pos).collect();

            hero.next();

        }
    }

    @Override
    protected void onThrow(int cell) {
        if (Level.pit[cell]) {
            super.onThrow(cell);
        } else {
            Dungeon.level.drop(shatter(null, cell), cell);
        }
    }

    public Item shatter(Char owner, int pos) {

        if (Dungeon.visible[pos]) {
            Sample.INSTANCE.play(Assets.SND_SHATTER);
            Splash.at(pos, 0xffd500, 5);
        }

        int newPos = pos;
        if (Actor.findChar(pos) != null) {
            ArrayList<Integer> candidates = new ArrayList<>();
            boolean[] passable = Level.passable;

            for (int n : PathFinder.NEIGHBOURS4) {
                int c = pos + n;
                if (passable[c] && Actor.findChar(c) == null) {
                    candidates.add(c);
                }
            }

            newPos = candidates.size() > 0 ? Random.element(candidates) : -1;
        }

        if (newPos != -1) {
            Drone drone = new Drone();
            drone.spawn(Dungeon.depth);
            drone.setPotInfo(pos, owner);
            drone.HP = drone.HT;
            drone.pos = newPos;

            GameScene.add(drone);
            Actor.addDelayed(new Pushing(drone, pos, newPos), -1f);

            drone.sprite.alpha(0);
            drone.sprite.parent.add(new AlphaTweener(drone.sprite, 1, 0.15f));

            Sample.INSTANCE.play(Assets.SND_BEE);
            return new ActivatedDrone().setDrone(drone);
        } else {
            return this;
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int cost() {
        return 30 * quantity;
    }

    //The bee's broken 'home', all this item does is let its bee know where it is, and who owns it (if anyone).
    public static class ActivatedDrone extends Item {

        {
            image = ItemSpriteSheet.DEADDRONECONT;
            stackable = false;
        }

        private int myDrone;
        private int droneDepth;

        Item setDrone(Char drone) {
            myDrone = drone.id();
            droneDepth = Dungeon.depth;
            return this;
        }

        @Override
        public boolean doPickUp(Hero hero) {
            if (super.doPickUp(hero)) {
                setHolder(hero);
                return true;
            } else
                return false;
        }

        @Override
        public void doDrop(Hero hero) {
            super.doDrop(hero);
            updateDrone(hero.pos, null);
        }

        @Override
        protected void onThrow(int cell) {
            super.onThrow(cell);
            updateDrone(cell, null);
        }

        public void setHolder(Char holder) {
            updateDrone(holder.pos, holder);
        }

        public void goAway() {
            updateDrone(-1, null);
        }

        private void updateDrone(int cell, Char holder) {
            //important, as ids are not unique between depths.
            if (Dungeon.depth != droneDepth)
                return;

            Drone drone = (Drone) Actor.findById(myDrone);
            if (drone != null)
                drone.setPotInfo(cell, holder);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        private static final String MYDRONE = "mydrone";
        private static final String DRONEDEPTH = "dronedepth";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(MYDRONE, myDrone);
            bundle.put(DRONEDEPTH, droneDepth);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            myDrone = bundle.getInt(MYDRONE);
            droneDepth = bundle.getInt(DRONEDEPTH);
        }
    }
}
