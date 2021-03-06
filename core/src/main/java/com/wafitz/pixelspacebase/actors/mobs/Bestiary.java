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
package com.wafitz.pixelspacebase.actors.mobs;

import com.wafitz.pixelspacebase.PixelSpacebase;
import com.watabou.utils.Random;

public class Bestiary {

    public static Mob mob(int depth) {
        @SuppressWarnings("unchecked")
        Class<? extends Mob> cl = (Class<? extends Mob>) mobClass(depth);
        try {
            return cl.newInstance();
        } catch (Exception e) {
            PixelSpacebase.reportException(e);
            return null;
        }
    }

    public static Mob mutable(int depth) {
        @SuppressWarnings("unchecked")
        Class<? extends Mob> cl = (Class<? extends Mob>) mobClass(depth);

        if (Random.Int(30) == 0) {
            if (cl == Xenomorph.class) {
                cl = Albino.class;
            } else if (cl == Thief.class) {
                cl = Bandit.class;
            } else if (cl == Brute.class) {
                cl = Shielded.class;
            } else if (cl == Monk.class) {
                cl = Senior.class;
            } else if (cl == Scorpio.class) {
                cl = Acidic.class;
            }
        }

        try {
            return cl.newInstance();
        } catch (Exception e) {
            PixelSpacebase.reportException(e);
            return null;
        }
    }

    private static Class<?> mobClass(int depth) {

        float[] chances;
        Class<?>[] classes;

        switch (depth) {
            case 1:
                chances = new float[]{4};
                classes = new Class<?>[]{Xenomorph.class};
                break;
            case 2:
                chances = new float[]{4, 0.01f, 4};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class};
                break;
            case 3:
                chances = new float[]{3, 0.01f, 4, 4, 0.1f};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class, Crab.class, Squiddard.class};
                break;
            case 4:
                chances = new float[]{3, 0.02f, 3, 4, 0.2f};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class, Crab.class, Squiddard.class};
                break;

            case 5:
                chances = new float[]{1};
                classes = new Class<?>[]{FeralShapeshifter.class};
                break;

            case 6:
                chances = new float[]{2, 0.02f, 3, 3, 1, 4, 4, 4};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class, Crab.class, Squiddard.class, Skeleton.class, Thief.class, Shaman.class};
                break;
            case 7:
                chances = new float[]{2, 0.03f, 2, 3, 2, 4, 3, 4, 4};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class, Crab.class, Squiddard.class, Skeleton.class, Thief.class, Shaman.class, Guard.class};
                break;
            case 8:
                chances = new float[]{1, 0.03f, 2, 2, 3, 3, 2, 3, 3, 0.1f};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class, Crab.class, Squiddard.class, Skeleton.class, Thief.class, Shaman.class, Guard.class, Bat.class};
                break;
            case 9:
                chances = new float[]{1, 0.04f, 1, 2, 4, 3, 1, 3, 2, 0.2f, 0.1f};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class, Crab.class, Squiddard.class, Skeleton.class, Thief.class, Shaman.class, Guard.class, Bat.class, Brute.class};
                break;

            case 10:
                chances = new float[]{1};
                classes = new Class<?>[]{Tengu.class};
                break;

            case 11:
                chances = new float[]{0.2f, 0.04f, 1, 1, 4, 2, 2, 1, 1, 1};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class, Crab.class, Squiddard.class, Skeleton.class, Shaman.class, Guard.class, Bat.class, Brute.class};
                break;
            case 12:
                chances = new float[]{0.1f, 0.05f, 0.2f, 0.2f, 3, 2, 2, 0.2f, 2, 2, 4};
                classes = new Class<?>[]{Xenomorph.class, Eye.class, Gnoll.class, Crab.class, Squiddard.class, Skeleton.class, Shaman.class, Guard.class, Bat.class, Brute.class, Spinner.class};
                break;
            case 13:
                chances = new float[]{0.05f, 0.1f, 0.1f, 3, 1, 1, 0.1f, 3, 3, 3, 0.1f};
                classes = new Class<?>[]{Eye.class, Gnoll.class, Crab.class, Squiddard.class, Skeleton.class, Shaman.class, Guard.class, Bat.class, Brute.class, Spinner.class, Elemental.class};
                break;
            case 14:
                chances = new float[]{0.06f, 2, 1, 1, 4, 4, 2, 0.2f, 0.1f};
                classes = new Class<?>[]{Eye.class, Squiddard.class, Skeleton.class, Shaman.class, Bat.class, Brute.class, Spinner.class, Elemental.class, Monk.class};
                break;

            case 15:
                chances = new float[]{1};
                classes = new Class<?>[]{DM300.class};
                break;

            case 16:
                chances = new float[]{0.06f, 2, 0.2f, 0.2f, 4, 4, 1, 1, 1, 1};
                classes = new Class<?>[]{Eye.class, Squiddard.class, Skeleton.class, Shaman.class, Bat.class, Brute.class, Spinner.class, Elemental.class, Monk.class, Warlock.class,};
                break;
            case 17:
                chances = new float[]{0.07f, 1, 0.1f, 0.1f, 3, 3, 2, 2, 2};
                classes = new Class<?>[]{Eye.class, Squiddard.class, Skeleton.class, Shaman.class, Bat.class, Brute.class, Elemental.class, Monk.class, Warlock.class,};
                break;
            case 18:
                chances = new float[]{0.07f, 0.2f, 2, 2, 3, 3, 3, 4};
                classes = new Class<?>[]{Eye.class, Squiddard.class, Bat.class, Brute.class, Elemental.class, Monk.class, Warlock.class, Golem.class};
                break;
            case 19:
                chances = new float[]{0.08f, 0.1f, 1, 1, 4, 4, 4, 3, 0.1f};
                classes = new Class<?>[]{Eye.class, Squiddard.class, Bat.class, Brute.class, Elemental.class, Monk.class, Warlock.class, Golem.class, Succubus.class};
                break;

            case 20:
                chances = new float[]{1};
                classes = new Class<?>[]{King.class};
                break;

            case 21:
                chances = new float[]{0.09f, 0.2f, 0.2f, 0.2f, 2, 0.2f};
                classes = new Class<?>[]{Eye.class, Brute.class, Elemental.class, Monk.class, Golem.class, Succubus.class};
                break;
            case 22:
                chances = new float[]{0.1f, 0.1f, 0.1f, 0.1f, 1, 1};
                classes = new Class<?>[]{Eye.class, Brute.class, Elemental.class, Monk.class, Golem.class, Succubus.class};
                break;
            case 23:
                chances = new float[]{0.2f, 0.2f, 2, 1};
                classes = new Class<?>[]{Eye.class, Golem.class, Succubus.class, Scorpio.class};
                break;
            case 24:
                chances = new float[]{1, 3, 2};
                classes = new Class<?>[]{Eye.class, Succubus.class, Scorpio.class};
                break;

            case 25:
                chances = new float[]{1};
                classes = new Class<?>[]{Yog.class};
                break;

            default:
                chances = new float[]{1};
                classes = new Class<?>[]{Eye.class};
        }

        return classes[Random.chances(chances)];
    }
}
