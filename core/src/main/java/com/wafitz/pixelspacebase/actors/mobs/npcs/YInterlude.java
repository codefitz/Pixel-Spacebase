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
package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.levels.SecurityBossLevel;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.YSprite;
import com.wafitz.pixelspacebase.windows.WndQuest;
import com.watabou.utils.Bundle;

public class YInterlude extends NPC {

    private static final String START_HERO_POS = "start_hero_pos";
    private static final String MAZE_APPEARANCE = "maze_appearance";

    private int startHeroPos = -1;
    private int mazeAppearance = 0;

    {
        spriteClass = YSprite.class;
        properties.add(Property.IMMOVABLE);
    }

    public YInterlude() {
    }

    public YInterlude(int startHeroPos) {
        this.startHeroPos = startHeroPos;
    }

    public YInterlude(int startHeroPos, int mazeAppearance) {
        this.startHeroPos = startHeroPos;
        this.mazeAppearance = mazeAppearance;
    }

    @Override
    protected boolean act() {
        if (mazeAppearance > 0) {
            throwItem();
            spend(TICK);
            return true;
        }

        if (startHeroPos == -1) {
            startHeroPos = SpacebaseRun.hero.pos;
        }

        if (SpacebaseRun.hero.pos != startHeroPos) {
            yell(Messages.get(Arp.class, "masked_prisoner_cameo"));
            destroy();
            sprite.emitter().burst(Speck.factory(Speck.WOOL), 15);
            sprite.killAndErase();
        } else {
            throwItem();
            spend(TICK);
        }

        return true;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return 1000;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public void add(Buff buff) {
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact() {
        if (mazeAppearance > 0) {
            sprite.turnTo(pos, SpacebaseRun.hero.pos);
            if (SpacebaseRun.level instanceof SecurityBossLevel) {
                ((SecurityBossLevel) SpacebaseRun.level).recordMazeYFound(mazeAppearance);
            }
            GameScene.show(new WndQuest(this, Messages.get(this, "maze_" + mazeAppearance)));
            destroy();
            sprite.emitter().burst(Speck.factory(Speck.WOOL), 15);
            sprite.killAndErase();
        }
        return false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(START_HERO_POS, startHeroPos);
        bundle.put(MAZE_APPEARANCE, mazeAppearance);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        startHeroPos = bundle.getInt(START_HERO_POS);
        mazeAppearance = bundle.getInt(MAZE_APPEARANCE);
    }
}
