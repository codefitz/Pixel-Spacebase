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
package com.wafitz.pixelspacebase.items.blasters;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Lightning;
import com.wafitz.pixelspacebase.effects.particles.SparkParticle;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Shocking;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.BArray;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShockBlaster extends DamageBlaster {

    {
        image = ItemSpriteSheet.LIGHTNINGBLASTER;
    }

    private ArrayList<Char> affected = new ArrayList<>();

    private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

    public int min(int lvl) {
        return 5 + lvl;
    }

    public int max(int lvl) {
        return 10 + 5 * lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {

        //lightning deals less damage per-target, the more targets that are hit.
        float multipler = 0.4f + (0.6f / affected.size());
        //if the main target is in water, all affected take full damage
        if (Level.water[bolt.collisionPos]) multipler = 1f;

        int min = 5 + level();
        int max = 10 + 5 * level();

        for (Char ch : affected) {
            processSoulMark(ch, chargesPerCast());
            ch.damage(Math.round(damageRoll() * multipler), LightningVent.LIGHTNING);

            if (ch == Dungeon.hero) Camera.main.shake(2, 0.3f);
            ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
            ch.sprite.flash();
        }

        if (!curUser.isAlive()) {
            Dungeon.fail(getClass());
            GLog.n(Messages.get(this, "ondeath"));
        }
    }

    @Override
    public void onHit(DM3000Launcher launcher, Char attacker, Char defender, int damage) {
        //acts like shocking enhancement
        new Shocking().proc(launcher, attacker, defender, damage);
    }

    private void arc(Char ch) {

        affected.add(ch);

        int dist;
        if (Level.water[ch.pos] && !ch.flying)
            dist = 2;
        else
            dist = 1;

        PathFinder.buildDistanceMap(ch.pos, BArray.not(Level.solid, null), dist);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char n = Actor.findChar(i);
                if (n == Dungeon.hero && PathFinder.distance[i] > 1)
                    //the hero is only zapped if they are adjacent
                    continue;
                else if (n != null && !affected.contains(n)) {
                    arcs.add(new Lightning.Arc(ch.pos, n.pos));
                    arc(n);
                }
            }
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {

        affected.clear();
        arcs.clear();
        arcs.add(new Lightning.Arc(bolt.sourcePos, bolt.collisionPos));

        int cell = bolt.collisionPos;

        Char ch = Actor.findChar(cell);
        if (ch != null) {
            arc(ch);
        } else {
            CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3);
        }

        //don't want to wait for the effect before processing damage.
        curUser.sprite.parent.add(new Lightning(arcs, null));
        callback.call();
    }

    @Override
    public void launcherFx(DM3000Launcher.launcherParticle particle) {
        particle.color(0xFFFFFF);
        particle.am = 0.6f;
        particle.setLifespan(0.6f);
        particle.acc.set(0, +10);
        particle.speed.polar(-Random.Float(3.1415926f), 6f);
        particle.setSize(0f, 1.5f);
        particle.sizeJitter = 1f;
        particle.shuffleXY(1f);
        float dst = Random.Float(1f);
        particle.x -= dst;
        particle.y += dst;
    }

}
