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
package com.wafitz.pixelspacebase.levels;

public class Terrain {

    public static final int CHASM = 0;
    public static final int EMPTY = 1;
    public static final int LIGHTEDVENT = 2;
    public static final int EMPTY_WELL = 3;
    public static final int WALL = 4;
    public static final int DOOR = 5;
    public static final int OPEN_DOOR = 6;
    public static final int ENTRANCE = 7;
    public static final int EXIT = 8;
    public static final int EMBERS = 9;
    public static final int LOCKED_DOOR = 10;
    public static final int PEDESTAL = 11;
    public static final int WALL_DECO = 12;
    public static final int BARRICADE = 13;
    public static final int EMPTY_SP = 14;
    public static final int OFFVENT = 15;

    public static final int SECRET_DOOR = 16;
    public static final int HIDDEN_VENT = 17;
    public static final int VENT = 18;
    public static final int INACTIVE_VENT = 19;

    public static final int EMPTY_DECO = 20;
    public static final int LOCKED_EXIT = 21;
    public static final int UNLOCKED_EXIT = 22;
    public static final int SIGN = 23;
    public static final int WELL = 24;
    public static final int STATUE = 25;
    public static final int STATUE_SP = 26;
    public static final int BOOKSHELF = 27;
    public static final int CRAFTING = 28;

    // wafitz.v4 Changed to default, doesn't affect game tiles though...
    public static final int WATER = 63;

    static final int PASSABLE = 0x01;
    static final int LOS_BLOCKING = 0x02;
    static final int FLAMABLE = 0x04;
    public static final int SECRET = 0x08;
    static final int SOLID = 0x10;
    static final int AVOID = 0x20;
    static final int LIQUID = 0x40;
    static final int PIT = 0x80;

    public static final int[] flags = new int[256];

    static {
        flags[CHASM] = AVOID | PIT;
        flags[EMPTY] = PASSABLE;
        flags[LIGHTEDVENT] = PASSABLE | FLAMABLE;
        flags[EMPTY_WELL] = PASSABLE;
        flags[WATER] = PASSABLE | LIQUID;
        flags[WALL] = LOS_BLOCKING | SOLID;
        flags[DOOR] = PASSABLE | LOS_BLOCKING | FLAMABLE | SOLID;
        flags[OPEN_DOOR] = PASSABLE | FLAMABLE;
        flags[ENTRANCE] = PASSABLE/* | SOLID*/;
        flags[EXIT] = PASSABLE;
        flags[EMBERS] = PASSABLE;
        flags[LOCKED_DOOR] = LOS_BLOCKING | SOLID;
        flags[PEDESTAL] = PASSABLE;
        flags[WALL_DECO] = flags[WALL];
        flags[BARRICADE] = FLAMABLE | SOLID | LOS_BLOCKING;
        flags[EMPTY_SP] = flags[EMPTY];
        flags[OFFVENT] = PASSABLE | LOS_BLOCKING | FLAMABLE;

        flags[SECRET_DOOR] = flags[WALL] | SECRET;
        flags[HIDDEN_VENT] = flags[EMPTY] | SECRET;
        flags[VENT] = AVOID;
        flags[INACTIVE_VENT] = flags[EMPTY];

        flags[EMPTY_DECO] = flags[EMPTY];
        flags[LOCKED_EXIT] = SOLID;
        flags[UNLOCKED_EXIT] = PASSABLE;
        flags[SIGN] = PASSABLE | FLAMABLE;
        flags[WELL] = AVOID;
        flags[STATUE] = SOLID;
        flags[STATUE_SP] = flags[STATUE];
        flags[BOOKSHELF] = flags[BARRICADE];
        flags[CRAFTING] = PASSABLE;

    }

    public static int discover(int terr) {
        switch (terr) {
            case SECRET_DOOR:
                return DOOR;
            case HIDDEN_VENT:
                return VENT;
            default:
                return terr;
        }
    }

    // wafitz.v4 No longer needed as Pixel Spacebase starts above v0.4.3
    /*//converts terrain values from pre versioncode 120 (0.4.3) saves
    //TODO: remove when no longer supporting saves from 0.4.2b and under
    public static int[] convertTilesFrom129(int[] map) {
        for (int i = 0; i < map.length; i++) {

            int c = map[i];

            if (c >= 29) {
                if (c <= 32) {
                    c = 0; //chasm tiles
                } else {
                    c = 29; //water tiles
                }
            }

            map[i] = c;

        }
        return map;
    }

    // wafitz.v4 No longer needed as Pixel Spacebase starts above v0.3.0
    //converts terrain values from pre versioncode 44 (0.3.0c) saves
    //TODO: remove when no longer supporting saves from 0.3.0b and under
    public static int[] convertTrapsFrom43(int[] map, SparseArray<Vent> vents) {
        for (int i = 0; i < map.length; i++) {

            int c = map[i];

            //non-trap tiles getting their values shifted around
            if (c >= 24 && c <= 26) {
                c -= 4; //24-26 becomes 20-22
            } else if (c == 29) {
                c = 23; //29 becomes 23
            } else if (c >= 34 && c <= 36) {
                c -= 10; //34-36 becomes 24-26
            } else if (c >= 41 && c <= 46) {
                c -= 14; //41-46 becomes 27-32
            }

            //trap tiles, must be converted to general trap tiles and specific vents instantiated
            else if (c >= 17 && c <= 40) {
                //this is going to be messy...
                Vent trap = null;
                switch (c) {
                    case 17:
                        trap = new ToxicVent().reveal();
                        break;
                    case 18:
                        trap = new ToxicVent().hide();
                        break;

                    case 19:
                        trap = new FireVent().reveal();
                        break;
                    case 20:
                        trap = new FireVent().hide();
                        break;

                    case 21:
                        trap = new ParalyticVent().reveal();
                        break;
                    case 22:
                        trap = new ParalyticVent().hide();
                        break;

                    case 23:
                        c = INACTIVE_VENT;
                        trap = null;
                        break;

                    case 27:
                        trap = new PoisonVent().reveal();
                        break;
                    case 28:
                        trap = new PoisonVent().hide();
                        break;

                    case 30:
                        trap = new AlarmVent().reveal();
                        break;
                    case 31:
                        trap = new AlarmVent().hide();
                        break;

                    case 32:
                        trap = new LightningVent().reveal();
                        break;
                    case 33:
                        trap = new LightningVent().hide();
                        break;

                    case 37:
                        trap = new GrippingVent().reveal();
                        break;
                    case 38:
                        trap = new GrippingVent().hide();
                        break;

                    case 39:
                        trap = new SummoningVent().reveal();
                        break;
                    case 40:
                        trap = new SummoningVent().hide();
                        break;
                }
                if (trap != null) {
                    trap.set(i);
                    vents.put(trap.pos, trap);
                    if (trap.visible)
                        c = VENT;
                    else
                        c = HIDDEN_VENT;
                }
            }

            map[i] = c;
        }
        return map;
    }*/


}
