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
package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.hero.Belongings;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.npcs.ImpMakerBot;
import com.wafitz.pixelspacebase.actors.mobs.npcs.MakerBot;
import com.wafitz.pixelspacebase.items.Bomb;
import com.wafitz.pixelspacebase.items.Clone;
import com.wafitz.pixelspacebase.items.DroneController;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfHealing;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.MerchantsBeacon;
import com.wafitz.pixelspacebase.items.Stylus;
import com.wafitz.pixelspacebase.items.Torch;
import com.wafitz.pixelspacebase.items.Weightstone;
import com.wafitz.pixelspacebase.items.armor.HoverPod;
import com.wafitz.pixelspacebase.items.armor.HunterSpaceSuit;
import com.wafitz.pixelspacebase.items.armor.Loader;
import com.wafitz.pixelspacebase.items.armor.SpaceSuit;
import com.wafitz.pixelspacebase.items.artifacts.TimekeepersHourglass;
import com.wafitz.pixelspacebase.items.bags.ExperimentalTechBandolier;
import com.wafitz.pixelspacebase.items.bags.ScriptHolder;
import com.wafitz.pixelspacebase.items.bags.SeedPouch;
import com.wafitz.pixelspacebase.items.bags.WandHolster;
import com.wafitz.pixelspacebase.items.food.OverpricedRation;
import com.wafitz.pixelspacebase.items.scripts.FixScript;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.items.scripts.ScriptOfIdentify;
import com.wafitz.pixelspacebase.items.scripts.ScriptOfMagicMapping;
import com.wafitz.pixelspacebase.items.wands.Wand;
import com.wafitz.pixelspacebase.items.weapon.melee.BattleAxe;
import com.wafitz.pixelspacebase.items.weapon.melee.Greatsword;
import com.wafitz.pixelspacebase.items.weapon.melee.HandAxe;
import com.wafitz.pixelspacebase.items.weapon.melee.Longsword;
import com.wafitz.pixelspacebase.items.weapon.melee.Mace;
import com.wafitz.pixelspacebase.items.weapon.melee.NewShortsword;
import com.wafitz.pixelspacebase.items.weapon.melee.Sword;
import com.wafitz.pixelspacebase.items.weapon.melee.WarHammer;
import com.wafitz.pixelspacebase.items.weapon.missiles.CurareDart;
import com.wafitz.pixelspacebase.items.weapon.missiles.IncendiaryDart;
import com.wafitz.pixelspacebase.items.weapon.missiles.Javelin;
import com.wafitz.pixelspacebase.items.weapon.missiles.Shuriken;
import com.wafitz.pixelspacebase.items.weapon.missiles.Tamahawk;
import com.wafitz.pixelspacebase.levels.LastWorkshopLevel;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.plants.Plant;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;

public class Workshop extends Painter {

    private static int pasWidth;
    private static int pasHeight;

    private static ArrayList<Item> itemsToSpawn;

    public static void paint(Level level, Room room) {

        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.EMPTY_SP);

        pasWidth = room.width() - 2;
        pasHeight = room.height() - 2;
        int per = pasWidth * 2 + pasHeight * 2;

        if (itemsToSpawn == null)
            generateItems();

        int pos = xy2p(room, room.entrance()) + (per - itemsToSpawn.size()) / 2;
        for (Item item : itemsToSpawn) {

            Point xy = p2xy(room, (pos + per) % per);
            int cell = xy.x + xy.y * level.width();

            if (level.heaps.get(cell) != null) {
                do {
                    cell = level.pointToCell(room.random());
                } while (level.heaps.get(cell) != null);
            }

            level.drop(item, cell).type = Heap.Type.FOR_SALE;

            pos++;
        }

        placeMakerBot(level, room);

        for (Room.Door door : room.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

        itemsToSpawn = null;
    }

    private static void generateItems() {

        itemsToSpawn = new ArrayList<>();

        switch (Dungeon.depth) {
            case 6:
                itemsToSpawn.add((Random.Int(2) == 0 ? new NewShortsword().identify() : new HandAxe()).identify());
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new IncendiaryDart().quantity(Random.NormalIntRange(2, 4)) :
                        new CurareDart().quantity(Random.NormalIntRange(1, 3)));
                itemsToSpawn.add(new SpaceSuit().identify());
                break;

            case 11:
                itemsToSpawn.add((Random.Int(2) == 0 ? new Sword().identify() : new Mace()).identify());
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new CurareDart().quantity(Random.NormalIntRange(2, 5)) :
                        new Shuriken().quantity(Random.NormalIntRange(3, 6)));
                itemsToSpawn.add(new HunterSpaceSuit().identify());
                break;

            case 16:
                itemsToSpawn.add((Random.Int(2) == 0 ? new Longsword().identify() : new BattleAxe()).identify());
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new Shuriken().quantity(Random.NormalIntRange(4, 7)) :
                        new Javelin().quantity(Random.NormalIntRange(3, 6)));
                itemsToSpawn.add(new HoverPod().identify());
                break;

            case 21:
                itemsToSpawn.add(Random.Int(2) == 0 ? new Greatsword().identify() : new WarHammer().identify());
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new Javelin().quantity(Random.NormalIntRange(4, 7)) :
                        new Tamahawk().quantity(Random.NormalIntRange(4, 7)));
                itemsToSpawn.add(new Loader().identify());
                itemsToSpawn.add(new Torch());
                itemsToSpawn.add(new Torch());
                break;
        }

        itemsToSpawn.add(new MerchantsBeacon());


        ChooseBag(Dungeon.hero.belongings);


        itemsToSpawn.add(new ExperimentalTechOfHealing());
        for (int i = 0; i < 3; i++)
            itemsToSpawn.add(Generator.random(Generator.Category.EXPERIMENTALTECH));

        itemsToSpawn.add(new ScriptOfIdentify());
        itemsToSpawn.add(new FixScript());
        itemsToSpawn.add(new ScriptOfMagicMapping());
        itemsToSpawn.add(Generator.random(Generator.Category.SCRIPT));

        for (int i = 0; i < 2; i++)
            itemsToSpawn.add(Random.Int(2) == 0 ?
                    Generator.random(Generator.Category.EXPERIMENTALTECH) :
                    Generator.random(Generator.Category.SCRIPT));


        itemsToSpawn.add(new OverpricedRation());
        itemsToSpawn.add(new OverpricedRation());

        itemsToSpawn.add(new Bomb().random());
        switch (Random.Int(5)) {
            case 1:
                itemsToSpawn.add(new Bomb());
                break;
            case 2:
                itemsToSpawn.add(new Bomb().random());
                break;
            case 3:
            case 4:
                itemsToSpawn.add(new DroneController());
                break;
        }


        if (Dungeon.depth == 6) {
            itemsToSpawn.add(new Clone());
            itemsToSpawn.add(new Weightstone());
        } else {
            itemsToSpawn.add(Random.Int(2) == 0 ? new Clone() : new Weightstone());
        }


        TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
        if (hourglass != null) {
            int bags = 0;
            //creates the given float percent of the remaining bags to be dropped.
            //this way players who get the hourglass late can still max it, usually.
            switch (Dungeon.depth) {
                case 6:
                    bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.20f);
                    break;
                case 11:
                    bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.25f);
                    break;
                case 16:
                    bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.50f);
                    break;
                case 21:
                    bags = (int) Math.ceil((5 - hourglass.sandBags) * 0.80f);
                    break;
            }

            for (int i = 1; i <= bags; i++) {
                itemsToSpawn.add(new TimekeepersHourglass.sandBag());
                hourglass.sandBags++;
            }
        }

        Item rare;
        switch (Random.Int(10)) {
            case 0:
                rare = Generator.random(Generator.Category.WAND);
                rare.level(0);
                break;
            case 1:
                rare = Generator.random(Generator.Category.MODULE);
                rare.level(1);
                break;
            case 2:
                rare = Generator.random(Generator.Category.ARTIFACT).identify();
                break;
            default:
                rare = new Stylus();
        }
        rare.malfunctioning = rare.malfunctioningKnown = false;
        itemsToSpawn.add(rare);

        //this is a hard limit, level gen allows for at most an 8x5 room, can't fit more than 39 items + 1 shopkeeper.
        if (itemsToSpawn.size() > 39)
            throw new RuntimeException("Workshop attempted to carry more than 39 items!");

        Collections.shuffle(itemsToSpawn);
    }

    private static void ChooseBag(Belongings pack) {

        int seeds = 0, scripts = 0, experimentaltech = 0, wands = 0;

        //count up items in the main bag, for bags which haven't yet been dropped.
        for (Item item : pack.backpack.items) {
            if (!Dungeon.limitedDrops.seedBag.dropped() && item instanceof Plant.Seed)
                seeds++;
            else if (!Dungeon.limitedDrops.scriptBag.dropped() && item instanceof Script)
                scripts++;
            else if (!Dungeon.limitedDrops.experimentalTechBag.dropped() && item instanceof ExperimentalTech)
                experimentaltech++;
            else if (!Dungeon.limitedDrops.wandBag.dropped() && item instanceof Wand)
                wands++;
        }

        //then pick whichever valid bag has the most items available to put into it.
        //note that the order here gives a perference if counts are otherwise equal
        if (seeds >= scripts && seeds >= experimentaltech && seeds >= wands && !Dungeon.limitedDrops.seedBag.dropped()) {
            Dungeon.limitedDrops.seedBag.drop();
            itemsToSpawn.add(new SeedPouch());

        } else if (scripts >= experimentaltech && scripts >= wands && !Dungeon.limitedDrops.scriptBag.dropped()) {
            Dungeon.limitedDrops.scriptBag.drop();
            itemsToSpawn.add(new ScriptHolder());

        } else if (experimentaltech >= wands && !Dungeon.limitedDrops.experimentalTechBag.dropped()) {
            Dungeon.limitedDrops.experimentalTechBag.drop();
            itemsToSpawn.add(new ExperimentalTechBandolier());

        } else if (!Dungeon.limitedDrops.wandBag.dropped()) {
            Dungeon.limitedDrops.wandBag.drop();
            itemsToSpawn.add(new WandHolster());
        }
    }

    public static int spaceNeeded() {
        if (itemsToSpawn == null)
            generateItems();

        //plus one for the shopkeeper
        return itemsToSpawn.size() + 1;
    }

    private static void placeMakerBot(Level level, Room room) {

        int pos;
        do {
            pos = level.pointToCell(room.random());
        } while (level.heaps.get(pos) != null);

        Mob makerbot = level instanceof LastWorkshopLevel ? new ImpMakerBot() : new MakerBot();
        makerbot.pos = pos;
        level.mobs.add(makerbot);

        if (level instanceof LastWorkshopLevel) {
            for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
                int p = makerbot.pos + PathFinder.NEIGHBOURS9[i];
                if (level.map[p] == Terrain.EMPTY_SP) {
                    level.map[p] = Terrain.WATER;
                }
            }
        }
    }

    private static int xy2p(Room room, Point xy) {
        if (xy.y == room.top) {

            return (xy.x - room.left - 1);

        } else if (xy.x == room.right) {

            return (xy.y - room.top - 1) + pasWidth;

        } else if (xy.y == room.bottom) {

            return (room.right - xy.x - 1) + pasWidth + pasHeight;

        } else {

            if (xy.y == room.top + 1) {
                return 0;
            } else {
                return (room.bottom - xy.y - 1) + pasWidth * 2 + pasHeight;
            }

        }
    }

    private static Point p2xy(Room room, int p) {
        if (p < pasWidth) {

            return new Point(room.left + 1 + p, room.top + 1);

        } else if (p < pasWidth + pasHeight) {

            return new Point(room.right - 1, room.top + 1 + (p - pasWidth));

        } else if (p < pasWidth * 2 + pasHeight) {

            return new Point(room.right - 1 - (p - (pasWidth + pasHeight)), room.bottom - 1);

        } else {

            return new Point(room.left + 1, room.bottom - 1 - (p - (pasWidth * 2 + pasHeight)));

        }
    }
}
