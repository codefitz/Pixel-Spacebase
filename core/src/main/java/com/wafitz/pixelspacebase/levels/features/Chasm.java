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
package com.wafitz.pixelspacebase.levels.features;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.buffs.Bleeding;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Cripple;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.items.artifacts.DriedRose;
import com.wafitz.pixelspacebase.items.artifacts.TimekeepersHourglass;
import com.wafitz.pixelspacebase.levels.RegularLevel;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.InterlevelScene;
import com.wafitz.pixelspacebase.sprites.MobSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Chasm {

    public static boolean jumpConfirmed = false;

    public static void heroJump(final Hero hero) {
        GameScene.show(
                new WndOptions(Messages.get(Chasm.class, "chasm"),
                        Messages.get(Chasm.class, "jump"),
                        Messages.get(Chasm.class, "yes"),
                        Messages.get(Chasm.class, "no")) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            jumpConfirmed = true;
                            hero.resume();
                        }
                    }
                }
        );
    }

    public static void heroFall(int pos) {

        jumpConfirmed = false;

        Sample.INSTANCE.play(Assets.SND_FALLING);

        Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        if (buff != null) buff.detach();

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
            if (mob instanceof DriedRose.GhostHero) mob.destroy();

        if (Dungeon.hero.isAlive()) {
            Dungeon.hero.interrupt();
            InterlevelScene.mode = InterlevelScene.Mode.FALL;
            if (Dungeon.level instanceof RegularLevel) {
                Room room = ((RegularLevel) Dungeon.level).room(pos);
                InterlevelScene.fallIntoPit = room != null && room.type == Room.Type.WEAK_FLOOR;
            } else {
                InterlevelScene.fallIntoPit = false;
            }
            Game.switchScene(InterlevelScene.class);
        } else {
            Dungeon.hero.sprite.visible = false;
        }
    }

    public static void heroLand() {

        Hero hero = Dungeon.hero;

        hero.sprite.burst(hero.sprite.blood(), 10);
        Camera.main.shake(4, 0.2f);

        Buff.prolong(hero, Cripple.class, Cripple.DURATION);
        Buff.affect(hero, Bleeding.class).set(hero.HT / 6);
        hero.damage(Random.NormalIntRange(hero.HP / 4, hero.HT / 4), new Hero.Doom() {
            @Override
            public void onDeath() {
                Badges.validateDeathFromFalling();

                Dungeon.fail(getClass());
                GLog.n(Messages.get(Chasm.class, "ondeath"));
            }
        });
    }

    public static void mobFall(Mob mob) {
        mob.die(null);

        ((MobSprite) mob.sprite).fall();
    }
}
