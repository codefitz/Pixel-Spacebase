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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.DungeonTilemap;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Paralysis;
import com.wafitz.pixelspacebase.effects.Effects;
import com.wafitz.pixelspacebase.effects.MagicMissile;
import com.wafitz.pixelspacebase.effects.Pushing;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WaveBlaster extends DamageBlaster {

    {
        image = ItemSpriteSheet.WAVEBLASTER;

        collisionProperties = Ballistica.PROJECTILE;
    }

    public int min(int lvl) {
        return 1 + lvl;
    }

    public int max(int lvl) {
        return 5 + 3 * lvl;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        if (!validCell(bolt.collisionPos)) {
            return;
        }

        Sample.INSTANCE.play(Assets.SND_BLAST);
        BlastWave.blast(bolt.collisionPos);

        int damage = damageRoll();

        //presses all tiles in the AOE first
        for (int i : PathFinder.NEIGHBOURS9) {
            int cell = bolt.collisionPos + i;
            if (validCell(cell)) {
                Dungeon.level.press(cell, Actor.findChar(cell));
            }
        }

        //throws other chars around the center.
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = bolt.collisionPos + i;
            if (!validCell(cell)) {
                continue;
            }

            Char ch = Actor.findChar(cell);

            if (ch != null) {
                processSoulMark(ch, chargesPerCast());
                ch.damage(Math.round(damage * 0.667f), this);

                if (ch.isAlive()) {
                    int targetCell = ch.pos + i;
                    // Only create trajectory if target cell is valid to prevent crashes
                    if (validCell(targetCell)) {
                        Ballistica trajectory = new Ballistica(ch.pos, targetCell, Ballistica.MAGIC_BOLT);
                        int strength = Math.round(1.5f + level() / 2f);
                        throwChar(ch, trajectory, strength);
                    }
                }
            }
        }

        //throws the char at the center of the blast
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            processSoulMark(ch, chargesPerCast());
            ch.damage(damage, this);

            Integer nextCell = cellBeyondImpact(bolt);
            if (ch.isAlive() && nextCell != null) {
                Ballistica trajectory = new Ballistica(ch.pos, nextCell, Ballistica.MAGIC_BOLT);
                int strength = level() + 3;
                throwChar(ch, trajectory, strength);
            }
        }

        if (!curUser.isAlive()) {
            Dungeon.fail(getClass());
            GLog.n(Messages.get(this, "ondeath"));
        }
    }

    public static void throwChar(final Char ch, final Ballistica trajectory, int power) {
        if (trajectory == null || trajectory.path.isEmpty()) {
            return;
        }

        int dist = Math.min(trajectory.dist, power);

        if (ch.properties().contains(Char.Property.BOSS)) {
            dist = (dist + 1) / 2;
        }

        if (dist <= 0 || ch.properties().contains(Char.Property.IMMOVABLE) || ch.rooted) {
            return;
        }

        // Cap dist to path size - 1 to prevent IndexOutOfBoundsException
        dist = Math.min(dist, trajectory.path.size() - 1);

        // Find a valid landing cell by backing up from intended distance
        while (dist > 0) {
            int landingCell = trajectory.path.get(dist);
            if (validCell(landingCell) && Actor.findChar(landingCell) == null) {
                break;
            }
            dist--;
        }

        if (dist <= 0 || !validCell(trajectory.path.get(dist))) {
            return;
        }

        final int newPos = trajectory.path.get(dist);

        if (newPos == ch.pos) {
            return;
        }

        final int finalDist = dist;
        final int initialpos = ch.pos;

        Actor.addDelayed(new Pushing(ch, ch.pos, newPos, new Callback() {
            public void call() {
                if (initialpos != ch.pos) {
                    //something caused movement before pushing resolved, cancel to be safe.
                    ch.sprite.place(ch.pos);
                    return;
                }
                ch.pos = newPos;
                // Apply collision damage if character landed on blast center
                if (ch.pos == trajectory.collisionPos && finalDist > 0) {
                    ch.damage(Random.NormalIntRange(finalDist, 2 * finalDist), WaveBlaster.class);
                    Paralysis.prolong(ch, Paralysis.class, 1 + finalDist / 2f);
                }
                Dungeon.level.press(ch.pos, ch);
            }
        }), -1);
    }

    private static Integer cellBeyondImpact(Ballistica bolt) {
        if (bolt == null || bolt.path.isEmpty() || bolt.dist == null) {
            return null;
        }

        int dist = Math.min(bolt.dist, bolt.path.size() - 1);
        if (dist < 0 || !validCell(bolt.path.get(dist))) {
            return null;
        }

        if (bolt.path.size() > dist + 1 && validCell(bolt.path.get(dist + 1))) {
            return bolt.path.get(dist + 1);
        }

        if (dist <= 0) {
            return null;
        }

        int current = bolt.path.get(dist);
        int previous = bolt.path.get(dist - 1);
        int projected = current + (current - previous);
        return validCell(projected) ? projected : null;
    }

    private static boolean validCell(int cell) {
        return cell >= 0 && cell < Dungeon.level.length() && Dungeon.level.insideMap(cell);
    }

    @Override
    //behaves just like enhancement of Repulsion
    public void onHit(DM3000Launcher launcher, Char attacker, Char defender, int damage) {
        int level = Math.max(0, launcher.level());

        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        if (Random.Int(level + 4) >= 3) {
            int oppositeHero = defender.pos + (defender.pos - attacker.pos);
            Ballistica trajectory = new Ballistica(defender.pos, oppositeHero, Ballistica.MAGIC_BOLT);
            throwChar(defender, trajectory, 2);
        }
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.slowness(curUser.sprite.parent, bolt.sourcePos, bolt.collisionPos, callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    public void launcherFx(DM3000Launcher.launcherParticle particle) {
        particle.color(0x664422);
        particle.am = 0.6f;
        particle.setLifespan(3f);
        particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
        particle.setSize(1f, 2f);
        particle.radiateXY(2.5f);
    }

    public static class BlastWave extends Image {

        private static final float TIME_TO_FADE = 0.2f;

        private float time;

        public BlastWave() {
            super(Effects.get(Effects.Type.RIPPLE));
            origin.set(width / 2, height / 2);
        }

        public void reset(int pos) {
            revive();

            x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2;
            y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2;

            time = TIME_TO_FADE;
        }

        @Override
        public void update() {
            super.update();

            if ((time -= Game.elapsed) <= 0) {
                kill();
            } else {
                float p = time / TIME_TO_FADE;
                alpha(p);
                scale.y = scale.x = (1 - p) * 3;
            }
        }

        static void blast(int pos) {
            Group parent = Dungeon.hero.sprite.parent;
            if (parent == null) {
                return;
            }
            BlastWave b = (BlastWave) parent.recycle(BlastWave.class);
            if (b == null) {
                return;
            }
            parent.bringToFront(b);
            b.reset(pos);
        }

    }
}
