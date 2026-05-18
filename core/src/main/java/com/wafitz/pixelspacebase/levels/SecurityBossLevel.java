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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Bones;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.MaskedPrisoner;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YInterlude;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.KindOfWeapon;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.blasters.MissileBlaster;
import com.wafitz.pixelspacebase.items.keys.IronKey;
import com.wafitz.pixelspacebase.items.weapon.missiles.MissileWeapon;
import com.wafitz.pixelspacebase.levels.painters.MazePainter;
import com.wafitz.pixelspacebase.levels.vents.SpearVent;
import com.wafitz.pixelspacebase.levels.vents.Vent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.ui.CustomTileVisual;
import com.wafitz.pixelspacebase.ui.HealthIndicator;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SecurityBossLevel extends Level {

    private static final int ALL_Y_MAZE_INTERLUDES_FOUND = 0x7;
    private static final int TENGU_DAMAGE_CAP = 20;
    private static final int MIN_RANGED_USES = 3;

    {
        color1 = 0x6a723d;
        color2 = 0x88924c;
    }

    private enum State {
        START,
        FIGHT_START,
        MAZE,
        FIGHT_ARENA,
        WON
    }

    private State state;
    private MaskedPrisoner maskedPrisoner;
    private int yMazeFound;

    //keep track of that need to be removed as the level is changed. We dump 'em back into the level at the end.
    private ArrayList<Item> storedItems = new ArrayList<>();

    @Override
    public String tilesTex() {
        return Assets.TILES_SECURITY_BLOCK;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_SECURITY_BLOCK;
    }

    private static final String STATE = "state";
    private static final String MASKED_PRISONER = "masked_prisoner";
    private static final String Y_MAZE_FOUND = "yMazeFound";
    private static final String STORED_ITEMS = "storeditems";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STATE, state);
        bundle.put(MASKED_PRISONER, maskedPrisoner);
        bundle.put(Y_MAZE_FOUND, yMazeFound);
        bundle.put(STORED_ITEMS, storedItems);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        state = bundle.getEnum(STATE, State.class);
        yMazeFound = bundle.getInt(Y_MAZE_FOUND);

        //in some states the masked prisoner won't be in the world, in others he will be.
        if (state == State.START || state == State.MAZE) {
            maskedPrisoner = (MaskedPrisoner) bundle.get(MASKED_PRISONER);
        } else {
            for (Mob mob : mobs) {
                if (mob instanceof MaskedPrisoner) {
                    maskedPrisoner = (MaskedPrisoner) mob;
                    break;
                }
            }
        }

        for (Bundlable item : bundle.getCollection(STORED_ITEMS)) {
            storedItems.add((Item) item);
        }

        updateArenaPlanetVisuals(state == State.FIGHT_ARENA);
    }

    @Override
    protected boolean build() {

        map = MAP_START.clone();
        decorate();

        buildFlagMaps();
        cleanWalls();

        state = State.START;
        yMazeFound = 0;
        entrance = 5 + 2 * 32;
        exit = 0;

        resetVents();

        return true;
    }

    @Override
    protected void decorate() {
        //do nothing, all decorations are hard-coded.
    }

    @Override
    protected void createMobs() {
        maskedPrisoner = new MaskedPrisoner(); //We want to keep track of the masked prisoner independently of other mobs, he's not always in the level.
    }

    public Actor respawner() {
        return null;
    }

    @Override
    protected void createItems() {
        Item item = Bones.get();
        if (item != null) {
            drop(item, randomRespawnCell()).type = Heap.Type.REMAINS;
        }
        drop(new IronKey(10), randomSecurityCell());
    }

    private int randomSecurityCell() {
        int pos = 1 + 8 * 32; //initial position at top-left room

        //randomly assign a room.
        pos += Random.Int(4) * (4 * 32); //one of the 4 rows
        pos += Random.Int(2) * 6; // one of the 2 columns

        //and then a certain tile in that room.
        pos += Random.Int(3) + Random.Int(3) * 32;

        return pos;
    }

    @Override
    public void press(int cell, Char ch) {

        super.press(cell, ch);

        if (ch == SpacebaseRun.hero) {
            //hero enters masked prisoner's chamber
            if (state == State.START
                    && new Room().set(2, 25, 8, 32).inside(cellToPoint(cell))) {
                progress();
            }

            //hero finishes the maze
            else if (state == State.MAZE
                    && new Room().set(4, 0, 7, 4).inside(cellToPoint(cell))) {
                progress();
            }
        }
    }

    @Override
    public int randomRespawnCell() {
        return 5 + 2 * 32 + PathFinder.NEIGHBOURS8[Random.Int(8)]; //random cell adjacent to the entrance.
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(SecurityBlockLevel.class, "water_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.EMPTY_DECO:
                return Messages.get(SecurityBlockLevel.class, "empty_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(SecurityBlockLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    private void resetVents() {
        vents.clear();

        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.INACTIVE_VENT) {
                Vent t = new SpearVent().reveal();
                t.active = false;
                setVent(t, i);
                map[i] = Terrain.INACTIVE_VENT;
            }
        }
    }

    private void changeMap(int[] map) {
        this.map = map.clone();
        buildFlagMaps();
        cleanWalls();
        updateArenaPlanetVisuals(map == MAP_ARENA);

        exit = entrance = 0;
        for (int i = 0; i < length(); i++)
            if (map[i] == Terrain.ENTRANCE)
                entrance = i;
            else if (map[i] == Terrain.EXIT)
                exit = i;

        visited = mapped = new boolean[length()];
        for (Blob blob : blobs.values()) {
            blob.fullyClear();
        }
        addVisuals(); //this also resets existing visuals
        resetVents();


        GameScene.resetMap();
        if (PixelSpacebase.scene() instanceof GameScene) {
            ((GameScene) PixelSpacebase.scene()).resetCustomTiles();
        }
        SpacebaseRun.observe();
    }

    private void updateArenaPlanetVisuals(boolean arenaActive) {
        ArrayList<CustomTileVisual> toRemove = new ArrayList<>();
        for (CustomTileVisual visual : customTiles) {
            if (visual instanceof AlienPlanetSurface) {
                toRemove.add(visual);
            }
        }
        customTiles.removeAll(toRemove);

        if (!arenaActive) {
            return;
        }

        for (int i = 0; i < map.length; i++) {
            if (map[i] == Terrain.EMPTY) {
                AlienPlanetSurface surface = new AlienPlanetSurface();
                int x = i % width();
                int y = i / width();
                surface.pos(x, y);
                surface.variant(Math.abs((x * 31 + y * 17) % 8));
                customTiles.add(surface);
            }
        }
    }

    private void clearEntities(Room safeArea) {
        for (Heap heap : heaps.values()) {
            if (safeArea == null || !safeArea.inside(cellToPoint(heap.pos))) {
                for (Item item : heap.items)
                    storedItems.add(item);
                heap.destroy();
            }
        }
        for (Mob mob : SpacebaseRun.level.mobs.toArray(new Mob[SpacebaseRun.level.mobs.size()])) {
            if (mob != maskedPrisoner && (safeArea == null || !safeArea.inside(cellToPoint(mob.pos)))) {
                mob.destroy();
                if (mob.sprite != null)
                    mob.sprite.killAndErase();
            }
        }
        for (Mine mine : mines.values()) {
            if (safeArea == null || !safeArea.inside(cellToPoint(mine.pos))) {
                mines.remove(mine.pos);
            }
        }
    }

    public void progress() {
        switch (state) {
            //moving to the beginning of the fight
            case START:
                seal();
                set(5 + 25 * 32, Terrain.LOCKED_DOOR);
                GameScene.updateMap(5 + 25 * 32);

                maskedPrisoner.state = maskedPrisoner.HUNTING;
                maskedPrisoner.pos = 5 + 28 * 32; //in the middle of the fight room
                GameScene.add(maskedPrisoner);
                maskedPrisoner.notice();

                state = State.FIGHT_START;
                break;

            //halfway through, move to the maze
            case FIGHT_START:

                changeMap(MAP_MAZE);
                clearEntities((Room) new Room().set(0, 5, 8, 32)); //clear all but the entrance

                Actor.remove(maskedPrisoner);
                mobs.remove(maskedPrisoner);
                HealthIndicator.instance.target(null);
                maskedPrisoner.sprite.kill();

                Room maze = new Room();
                maze.set(10, 1, 31, 29);
                maze.connected.put(null, new Room.Door(10, 2));
                maze.connected.put(maze, new Room.Door(20, 29));
                MazePainter.paint(this, maze);
                buildFlagMaps();
                cleanWalls();
                GameScene.resetMap();
                summonMazeYInterludes(maze);

                GameScene.flash(0xFFFFFF);
                Sample.INSTANCE.play(Assets.SND_BLAST);

                state = State.MAZE;
                break;

            //maze beaten, moving to the arena
            case MAZE:
                balanceMaskedPrisonerArenaIfYFound();

                SpacebaseRun.hero.interrupt();
                SpacebaseRun.hero.pos += 9 + 3 * 32;
                SpacebaseRun.hero.sprite.interruptMotion();
                SpacebaseRun.hero.sprite.place(SpacebaseRun.hero.pos);

                changeMap(MAP_ARENA);
                clearEntities(null);

                maskedPrisoner.state = maskedPrisoner.HUNTING;
                do {
                    maskedPrisoner.pos = Random.Int(length());
                } while (solid[maskedPrisoner.pos] || distance(maskedPrisoner.pos, SpacebaseRun.hero.pos) < 8);
                GameScene.add(maskedPrisoner);
                maskedPrisoner.notice();

                state = State.FIGHT_ARENA;
                break;

            //arena ended, fight over.
            case FIGHT_ARENA:
                unseal();

                CustomTileVisual vis = new exitVisual();
                vis.pos(11, 8);
                customTiles.add(vis);
                ((GameScene) PixelSpacebase.scene()).addCustomTile(vis);

                SpacebaseRun.hero.interrupt();
                SpacebaseRun.hero.pos = 5 + 27 * 32;
                SpacebaseRun.hero.sprite.interruptMotion();
                SpacebaseRun.hero.sprite.place(SpacebaseRun.hero.pos);

                maskedPrisoner.pos = 5 + 28 * 32;
                maskedPrisoner.sprite.place(5 + 28 * 32);

                changeMap(MAP_END);
                clearEntities(null);

                maskedPrisoner.die(SpacebaseRun.hero);
                summonYInterlude();

                for (Item item : storedItems)
                    drop(item, randomSecurityCell());

                state = State.WON;
                break;
        }
    }

    public void recordMazeYFound(int appearance) {
        if (appearance >= 1 && appearance <= 3) {
            yMazeFound |= 1 << (appearance - 1);
        }
    }

    private void balanceMaskedPrisonerArenaIfYFound() {
        if ((yMazeFound & ALL_Y_MAZE_INTERLUDES_FOUND) != ALL_Y_MAZE_INTERLUDES_FOUND) {
            return;
        }

        if (bestRangedUses() < MIN_RANGED_USES) {
            MissileBlaster blaster = new MissileBlaster();
            blaster.identify();
            blaster.malfunctioning = blaster.malfunctioningKnown = false;
            blaster.curCharges = Math.max(blaster.curCharges, MIN_RANGED_USES);
            if (!blaster.collect(SpacebaseRun.hero.belongings.backpack)) {
                SpacebaseRun.hero.belongings.backpack.items.add(blaster);
            }
            GLog.p(Messages.get(YInterlude.class, "balance_gift", blaster.name()));
            return;
        }

        Item strongest = strongestUnequippedOffensiveItem();
        if (strongest != null && offensiveScore(strongest) > TENGU_DAMAGE_CAP) {
            Item removed = strongest.detachAll(SpacebaseRun.hero.belongings.backpack);
            if (removed != null) {
                storedItems.add(removed);
            }
            GLog.w(Messages.get(YInterlude.class, "balance_remove", strongest.name()));
        }
    }

    private int bestRangedUses() {
        int best = 0;
        for (Item item : SpacebaseRun.hero.belongings) {
            if (item instanceof Blaster) {
                Blaster blaster = (Blaster) item;
                if (!blaster.malfunctioning) {
                    best = Math.max(best, blaster.curCharges);
                }
            } else if (item instanceof MissileWeapon) {
                best = Math.max(best, item.quantity());
            }
        }
        return best;
    }

    private Item strongestUnequippedOffensiveItem() {
        Item strongest = null;
        int bestScore = 0;

        for (Item item : SpacebaseRun.hero.belongings.backpack) {
            int score = offensiveScore(item);
            if (score > bestScore) {
                bestScore = score;
                strongest = item;
            }
        }

        return strongest;
    }

    private int offensiveScore(Item item) {
        if (item instanceof KindOfWeapon) {
            return ((KindOfWeapon) item).max();
        } else if (item instanceof MissileWeapon) {
            return item.quantity() >= MIN_RANGED_USES ? ((MissileWeapon) item).max() + item.quantity() : 0;
        } else if (item instanceof Blaster) {
            Blaster blaster = (Blaster) item;
            return blaster.curCharges >= MIN_RANGED_USES ? 8 + blaster.level() * 2 + blaster.curCharges : 0;
        }
        return 0;
    }

    private void summonMazeYInterludes(Room maze) {
        int[] bands = new int[]{1, 2, 3};
        for (int i = 0; i < bands.length; i++) {
            int pos = randomMazeInterludeCell(maze, bands[i]);
            if (pos == -1) {
                continue;
            }

            YInterlude y = new YInterlude(-1, i + 1);
            y.pos = pos;
            GameScene.add(y);
            if (y.sprite != null) {
                y.sprite.emitter().burst(Speck.factory(Speck.WOOL), 10);
            }
        }
    }

    private int randomMazeInterludeCell(Room maze, int band) {
        int top = maze.top + 1 + (maze.height() - 2) * (band - 1) / 3;
        int bottom = maze.top + 1 + (maze.height() - 2) * band / 3;
        int fallback = -1;

        for (int tries = 0; tries < 80; tries++) {
            int x = Random.IntRange(maze.left + 1, maze.right - 1);
            int y = Random.IntRange(top, bottom);
            int cell = x + y * width();
            if (canHostMazeInterlude(cell)) {
                return cell;
            }
        }

        for (int y = top; y <= bottom; y++) {
            for (int x = maze.left + 1; x < maze.right; x++) {
                int cell = x + y * width();
                if (canHostMazeInterlude(cell)) {
                    fallback = cell;
                }
            }
        }
        return fallback;
    }

    private boolean canHostMazeInterlude(int cell) {
        return insideMap(cell)
                && passable[cell]
                && Actor.findChar(cell) == null
                && heaps.get(cell) == null
                && distance(cell, entrance) > 5
                && distance(cell, exit) > 5;
    }

    private void summonYInterlude() {
        int pos = -1;
        for (int offset : PathFinder.NEIGHBOURS8) {
            int cell = SpacebaseRun.hero.pos + offset;
            if (passable[cell] && Actor.findChar(cell) == null) {
                pos = cell;
                break;
            }
        }

        if (pos == -1) {
            return;
        }

        YInterlude y = new YInterlude(SpacebaseRun.hero.pos);
        y.pos = pos;
        GameScene.add(y);
        if (y.sprite != null) {
            y.sprite.emitter().burst(Speck.factory(Speck.WOOL), 15);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        SecurityBlockLevel.addPrisonVisuals(this, visuals);
        return visuals;
    }

    private static final int W = Terrain.WALL;
    private static final int D = Terrain.DOOR;
    private static final int L = Terrain.LOCKED_DOOR;
    private static final int e = Terrain.EMPTY;
    private static final int S = Terrain.SIGN;

    private static final int T = Terrain.INACTIVE_VENT;

    private static final int E = Terrain.ENTRANCE;
    private static final int X = Terrain.EXIT;

    private static final int M = Terrain.WALL_DECO;
    private static final int P = Terrain.PEDESTAL;

    //TODO if I ever need to store more static maps I should externalize them instead of hard-coding
    //Especially as I means I won't be limited to legal identifiers
    private static final int[] MAP_START =
            {W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, e, E, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, S, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, M, W, L, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W};

    private static final int[] MAP_MAZE =
            {W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, e, e, e, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    e, e, e, D, e, e, e, D, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, W, e, e, e, W, W, M, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, W, W, M, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, W, W, D, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, W, W, e, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, M, W, W, e, W, W, M, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, W, W, e, W, W, W, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, M, W, D, W, M, W, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, T, T, T, T, T, W, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, T, T, T, T, T, W, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, T, T, T, T, T, W, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W,
                    W, W, W, T, T, T, T, T, W, e, W, W, W, W, W, W, W, W, W, W, e, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W};

    private static final int[] MAP_ARENA =
            {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W,
                    W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W,
                    W, e, e, e, e, e, W, e, e, e, e, e, W, W, M, W, W, e, e, e, e, e, W, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, W, e, e, e, e, W, W, e, e, e, W, W, e, e, e, e, W, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, M, W, e, e, e, e, e, D, e, e, e, D, e, e, e, e, e, W, M, e, e, e, e, W, W, W, W,
                    W, e, e, W, W, W, W, e, e, e, e, W, W, e, e, e, W, W, e, e, e, e, W, W, W, W, e, e, W, W, W, W,
                    W, e, e, e, e, e, e, e, e, e, e, e, W, W, M, W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, e, e, e, W, W, e, e, e, e, e, e, e, W, W, e, e, e, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, e, W, e, e, e, W, W, W, W,
                    W, e, e, W, W, D, W, W, e, e, e, e, W, e, e, e, W, e, e, e, e, W, W, D, W, W, e, e, W, W, W, W,
                    W, e, W, W, e, e, e, W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, e, e, e, W, W, e, W, W, W, W,
                    W, e, W, M, e, e, e, M, W, e, e, e, e, e, M, e, e, e, e, e, W, M, e, e, e, M, W, e, W, W, W, W,
                    W, e, W, W, e, e, e, W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, e, e, e, W, W, e, W, W, W, W,
                    W, e, e, W, W, D, W, W, e, e, e, e, W, e, e, e, W, e, e, e, e, W, W, D, W, W, e, e, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, e, W, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, e, e, e, W, W, e, e, e, e, e, e, e, W, W, e, e, e, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, W, e, e, e, e, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, e, e, e, e, e, e, W, W, M, W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W,
                    W, e, e, W, W, W, W, e, e, e, e, W, W, e, e, e, W, W, e, e, e, e, W, W, W, W, e, e, W, W, W, W,
                    W, e, e, e, e, M, W, e, e, e, e, e, D, e, e, e, D, e, e, e, e, e, W, M, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, W, e, e, e, e, W, W, e, e, e, W, W, e, e, e, e, W, e, e, e, e, e, W, W, W, W,
                    W, e, e, e, e, e, W, e, e, e, e, e, W, W, M, W, W, e, e, e, e, e, W, e, e, e, e, e, W, W, W, W,
                    W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W,
                    W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W,
                    W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W};

    private static final int[] MAP_END =
            {W, W, W, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, e, E, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, S, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, D, e, D, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, M, e, W, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, D, e, D, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, X, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, D, e, D, e, e, e, W, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, e, W, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, D, e, D, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, e, e, e, W, e, W, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, M, W, W, e, W, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, M, W, D, W, M, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, P, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, T, T, T, T, T, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
                    W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W};


    private static class exitVisual extends CustomTileVisual {

        {
            name = "prison exit";

            tx = Assets.SECURITY_EXIT;
            txX = txY = 0;
            tileW = 12;
            tileH = 15;
        }

        //for compatibility with pre-0.4.3 saves
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos(11, 8);
        }

        @Override
        public String desc() {
            return super.desc();
        }
    }

    public static class AlienPlanetSurface extends CustomTileVisual {

        {
            name = Messages.get(this, "name");

            tx = Assets.ALIEN_PLANET_TILES;
            txX = 0;
            txY = 3;
        }

        void variant(int value) {
            ofsX = value;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }
    }
}
