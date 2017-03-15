/*
 * Pixel Dungeon, Copyright (C) 2012-2015  Oleg Dolya
 * Shattered Pixel Dungeon, Copyright (C) 2014-2016 Evan Debenham
 * Pixel Spacebase, Copyright (C) 2017 Wes Fitzpatrick
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
import com.wafitz.pixelspacebase.Challenges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.DungeonTilemap;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.Craft;
import com.wafitz.pixelspacebase.actors.blobs.WellWater;
import com.wafitz.pixelspacebase.actors.buffs.Awareness;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.buffs.MindVision;
import com.wafitz.pixelspacebase.actors.buffs.Shadows;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.actors.mobs.Bestiary;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.particles.FlowParticle;
import com.wafitz.pixelspacebase.effects.particles.WindParticle;
import com.wafitz.pixelspacebase.items.Dewdrop;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfHealing;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfMight;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfStrength;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.Stylus;
import com.wafitz.pixelspacebase.items.Torch;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.artifacts.AlchemistsToolkit;
import com.wafitz.pixelspacebase.items.artifacts.DriedRose;
import com.wafitz.pixelspacebase.items.artifacts.TimekeepersHourglass;
import com.wafitz.pixelspacebase.items.bags.ScriptHolder;
import com.wafitz.pixelspacebase.items.bags.SeedPouch;
import com.wafitz.pixelspacebase.items.food.Blandfruit;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.modules.TechModule;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.items.scripts.ScriptOfMagicalInfusion;
import com.wafitz.pixelspacebase.items.scripts.ScriptOfUpgrade;
import com.wafitz.pixelspacebase.levels.features.Chasm;
import com.wafitz.pixelspacebase.levels.features.Door;
import com.wafitz.pixelspacebase.levels.features.OffVent;
import com.wafitz.pixelspacebase.levels.painters.Painter;
import com.wafitz.pixelspacebase.levels.vents.Vent;
import com.wafitz.pixelspacebase.mechanics.ShadowCaster;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.plants.BlandfruitBush;
import com.wafitz.pixelspacebase.plants.Plant;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.ui.CustomTileVisual;
import com.wafitz.pixelspacebase.utils.BArray;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Level implements Bundlable {

    public enum Feeling {
        NONE,
        CHASM,
        WATER,
        LIGHTEDVENT,
        DARK
    }

    protected int width;
    protected int height;
    protected int length;

    private static final float TIME_TO_RESPAWN = 50;

    public int version;
    public int[] map;
    public boolean[] visited;
    public boolean[] mapped;

    public int viewDistance = Dungeon.isChallenged(Challenges.DARKNESS) ? 3 : 8;

    //FIXME should not be static!
    public static boolean[] fieldOfView;

    public static boolean[] passable;
    public static boolean[] losBlocking;
    public static boolean[] flamable;
    public static boolean[] secret;
    public static boolean[] solid;
    public static boolean[] avoid;
    public static boolean[] water;
    public static boolean[] pit;

    public static boolean[] discoverable;

    public Feeling feeling = Feeling.NONE;

    public int entrance;
    public int exit;

    //when a boss level has become locked.
    public boolean locked = false;

    public HashSet<Mob> mobs;
    public SparseArray<Heap> heaps;
    public HashMap<Class<? extends Blob>, Blob> blobs;
    public SparseArray<Plant> plants;
    public SparseArray<Vent> vents;
    public HashSet<CustomTileVisual> customTiles;

    ArrayList<Item> itemsToSpawn = new ArrayList<>();

    protected Group visuals;

    public int color1 = 0x004400;
    public int color2 = 0x88CC44;

    //FIXME this is sloppy. Should be able to keep track of this without static variables
    static boolean pitRoomNeeded = false;
    public static boolean weakFloorCreated = false;

    private static final String VERSION = "version";
    private static final String MAP = "map";
    private static final String VISITED = "visited";
    private static final String MAPPED = "mapped";
    private static final String ENTRANCE = "entrance";
    private static final String EXIT = "exit";
    private static final String LOCKED = "locked";
    private static final String HEAPS = "heaps";
    private static final String PLANTS = "plants";
    private static final String VENTS = "vents";
    private static final String CUSTOM_TILES = "customTiles";
    private static final String MOBS = "mobs";
    private static final String BLOBS = "blobs";
    private static final String FEELING = "feeling";

    public void create() {

        Random.seed(Dungeon.seedCurDepth());

        setupSize();
        PathFinder.setMapSize(width(), height());
        passable = new boolean[length()];
        losBlocking = new boolean[length()];
        flamable = new boolean[length()];
        secret = new boolean[length()];
        solid = new boolean[length()];
        avoid = new boolean[length()];
        water = new boolean[length()];
        pit = new boolean[length()];

        map = new int[length()];
        visited = new boolean[length()];
        Arrays.fill(visited, false);
        mapped = new boolean[length()];
        Arrays.fill(mapped, false);

        if (!(Dungeon.bossLevel() || Dungeon.depth == 21) /*final shop floor*/) {
            addItemToSpawn(Generator.random(Generator.Category.FOOD));

            int bonus = TechModule.getBonus(Dungeon.hero, TechModule.Wealth.class);

            if (Dungeon.posNeeded()) {
                if (Random.Float() > Math.pow(0.925, bonus))
                    addItemToSpawn(new ExperimentalTechOfMight());
                else
                    addItemToSpawn(new ExperimentalTechOfStrength());
                Dungeon.limitedDrops.strengthTech.count++;
            }
            if (Dungeon.souNeeded()) {
                if (Random.Float() > Math.pow(0.925, bonus))
                    addItemToSpawn(new ScriptOfMagicalInfusion());
                else
                    addItemToSpawn(new ScriptOfUpgrade());
                Dungeon.limitedDrops.upgradeScripts.count++;
            }
            if (Dungeon.asNeeded()) {
                if (Random.Float() > Math.pow(0.925, bonus))
                    addItemToSpawn(new Stylus());
                addItemToSpawn(new Stylus());
                Dungeon.limitedDrops.arcaneStyli.count++;
            }

            DriedRose rose = Dungeon.hero.belongings.getItem(DriedRose.class);
            if (rose != null && !rose.malfunctioning) {
                //this way if a rose is dropped later in the game, player still has a chance to max it out.
                int petalsNeeded = (int) Math.ceil((float) ((Dungeon.depth / 2) - rose.droppedPetals) / 3);

                for (int i = 1; i <= petalsNeeded; i++) {
                    //the player may miss a single petal and still max their rose.
                    if (rose.droppedPetals < 11) {
                        addItemToSpawn(new DriedRose.Petal());
                        rose.droppedPetals++;
                    }
                }
            }

            if (Dungeon.depth > 1) {
                switch (Random.Int(10)) {
                    case 0:
                        if (!Dungeon.bossLevel(Dungeon.depth + 1)) {
                            feeling = Feeling.CHASM;
                        }
                        break;
                    case 1:
                        feeling = Feeling.WATER;
                        break;
                    case 2:
                        feeling = Feeling.LIGHTEDVENT;
                        break;
                    case 3:
                        feeling = Feeling.DARK;
                        addItemToSpawn(new Torch());
                        viewDistance = (int) Math.ceil(viewDistance / 3f);
                        break;
                }
            }
        }

        boolean pitNeeded = Dungeon.depth > 1 && weakFloorCreated;

        do {
            Arrays.fill(map, feeling == Feeling.CHASM ? Terrain.CHASM : Terrain.WALL);

            pitRoomNeeded = pitNeeded;
            weakFloorCreated = false;

            mobs = new HashSet<>();
            heaps = new SparseArray<>();
            blobs = new HashMap<>();
            plants = new SparseArray<>();
            vents = new SparseArray<>();
            customTiles = new HashSet<>();

        } while (!build());
        decorate();

        buildFlagMaps();
        cleanWalls();

        createMobs();
        createItems();

        Random.seed();
    }

    // Randomised Level Sizing
    protected void setupSize() {
        if (width == 0 || height == 0) {
            width = Random.Int(16, 52);
            height = Random.Int(width <= 24 ? 30 : 16, width >= 44 ? 36 : 52);
            //width = height = 32;
            length = width * height;
        }
    }

    public void reset() {

        for (Mob mob : mobs.toArray(new Mob[0])) {
            if (!mob.reset()) {
                mobs.remove(mob);
            }
        }
        createMobs();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        version = bundle.getInt(VERSION);

        if (bundle.contains("width") && bundle.contains("height")) {
            width = bundle.getInt("width");
            height = bundle.getInt("height");
        } else
            width = height = 32; //default sizes
        length = width * height;
        PathFinder.setMapSize(width(), height());

        mobs = new HashSet<>();
        heaps = new SparseArray<>();
        blobs = new HashMap<>();
        plants = new SparseArray<>();
        vents = new SparseArray<>();
        customTiles = new HashSet<>();

        map = bundle.getIntArray(MAP);

        visited = bundle.getBooleanArray(VISITED);
        mapped = bundle.getBooleanArray(MAPPED);

        entrance = bundle.getInt(ENTRANCE);
        exit = bundle.getInt(EXIT);

        locked = bundle.getBoolean(LOCKED);

        weakFloorCreated = false;

        //for pre-0.3.0c saves
        /*if (version < 44) {
            map = Terrain.convertTrapsFrom43(map, vents);
        }

        //for pre-0.4.3 saves
        if (version < 130) {
            map = Terrain.convertTilesFrom129(map);
        }*/

        Collection<Bundlable> collection = bundle.getCollection(HEAPS);
        for (Bundlable h : collection) {
            Heap heap = (Heap) h;
            if (!heap.isEmpty())
                heaps.put(heap.pos, heap);
        }

        collection = bundle.getCollection(PLANTS);
        for (Bundlable p : collection) {
            Plant plant = (Plant) p;
            plants.put(plant.pos, plant);
        }

        collection = bundle.getCollection(VENTS);
        for (Bundlable p : collection) {
            Vent vent = (Vent) p;
            vents.put(vent.pos, vent);
        }

        collection = bundle.getCollection(CUSTOM_TILES);
        for (Bundlable p : collection) {
            CustomTileVisual vis = (CustomTileVisual) p;
            customTiles.add(vis);
        }

        collection = bundle.getCollection(MOBS);
        for (Bundlable m : collection) {
            Mob mob = (Mob) m;
            if (mob != null) {
                mobs.add(mob);
            }
        }

        collection = bundle.getCollection(BLOBS);
        for (Bundlable b : collection) {
            Blob blob = (Blob) b;
            blobs.put(blob.getClass(), blob);
        }

        feeling = bundle.getEnum(FEELING, Feeling.class);
        if (feeling == Feeling.DARK)
            viewDistance = (int) Math.ceil(viewDistance / 3f);

        buildFlagMaps();
        cleanWalls();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(VERSION, Game.versionCode);
        bundle.put("width", width);
        bundle.put("height", height);
        bundle.put(MAP, map);
        bundle.put(VISITED, visited);
        bundle.put(MAPPED, mapped);
        bundle.put(ENTRANCE, entrance);
        bundle.put(EXIT, exit);
        bundle.put(LOCKED, locked);
        bundle.put(HEAPS, heaps.values());
        bundle.put(PLANTS, plants.values());
        bundle.put(VENTS, vents.values());
        bundle.put(CUSTOM_TILES, customTiles);
        bundle.put(MOBS, mobs);
        bundle.put(BLOBS, blobs.values());
        bundle.put(FEELING, feeling);
    }

    public int tunnelTile() {
        return feeling == Feeling.CHASM ? Terrain.EMPTY_SP : Terrain.EMPTY;
    }

    public int width() {
        if (width == 0)
            setupSize();
        return width;
    }

    public int height() {
        if (height == 0)
            setupSize();
        return height;
    }

    public int length() {
        if (length == 0)
            setupSize();
        return length;
    }

    public String tilesTex() {
        return null;
    }

    public String waterTex() {
        return null;
    }

    abstract protected boolean build();

    abstract protected void decorate();

    abstract protected void createMobs();

    abstract protected void createItems();

    public void seal() {
        if (!locked) {
            locked = true;
            Buff.affect(Dungeon.hero, LockedFloor.class);
        }
    }

    public void unseal() {
        if (locked) {
            locked = false;
        }
    }

    public Group addVisuals() {
        if (visuals == null || visuals.parent == null) {
            visuals = new Group();
        } else {
            visuals.clear();
        }
        for (int i = 0; i < length(); i++) {
            if (pit[i]) {
                visuals.add(new WindParticle.Wind(i));
                if (i >= width() && water[i - width()]) {
                    visuals.add(new FlowParticle.Flow(i - width()));
                }
            }
        }
        return visuals;
    }

    public int nMobs() {
        return 0;
    }

    public Mob findMob(int pos) {
        for (Mob mob : mobs) {
            if (mob.pos == pos) {
                return mob;
            }
        }
        return null;
    }

    public Actor respawner() {
        return new Actor() {

            {
                actPriority = 1; //as if it were a buff.
            }

            @Override
            protected boolean act() {
                if (mobs.size() < nMobs()) {

                    Mob mob = Bestiary.mutable(Dungeon.depth);
                    if (mob != null) {
                        mob.state = mob.WANDERING;
                    }
                    mob.pos = randomRespawnCell();
                    if (Dungeon.hero.isAlive() && mob.pos != -1 && distance(Dungeon.hero.pos, mob.pos) >= 4) {
                        GameScene.add(mob);
                        if (Statistics.amuletObtained) {
                            mob.beckon(Dungeon.hero.pos);
                        }
                    }
                }
                spend(Dungeon.level.feeling == Feeling.DARK || Statistics.amuletObtained ? TIME_TO_RESPAWN / 2 : TIME_TO_RESPAWN);
                return true;
            }
        };
    }

    public int randomRespawnCell() {
        int cell;
        do {
            cell = Random.Int(length());
        } while (!passable[cell] || Dungeon.visible[cell] || Actor.findChar(cell) != null);
        return cell;
    }

    public int randomDestination() {
        int cell;
        do {
            cell = Random.Int(length());
        } while (!passable[cell]);
        return cell;
    }

    public void addItemToSpawn(Item item) {
        if (item != null) {
            itemsToSpawn.add(item);
        }
    }

    public Item findPrizeItem() {
        return findPrizeItem(null);
    }

    public Item findPrizeItem(Class<? extends Item> match) {
        if (itemsToSpawn.size() == 0)
            return null;

        if (match == null) {
            Item item = Random.element(itemsToSpawn);
            itemsToSpawn.remove(item);
            return item;
        }

        for (Item item : itemsToSpawn) {
            if (match.isInstance(item)) {
                itemsToSpawn.remove(item);
                return item;
            }
        }

        return null;
    }

    void buildFlagMaps() {

        fieldOfView = new boolean[length()];

        passable = new boolean[length()];
        losBlocking = new boolean[length()];
        flamable = new boolean[length()];
        secret = new boolean[length()];
        solid = new boolean[length()];
        avoid = new boolean[length()];
        water = new boolean[length()];
        pit = new boolean[length()];

        for (int i = 0; i < length(); i++) {
            int flags = Terrain.flags[map[i]];
            passable[i] = (flags & Terrain.PASSABLE) != 0;
            losBlocking[i] = (flags & Terrain.LOS_BLOCKING) != 0;
            flamable[i] = (flags & Terrain.FLAMABLE) != 0;
            secret[i] = (flags & Terrain.SECRET) != 0;
            solid[i] = (flags & Terrain.SOLID) != 0;
            avoid[i] = (flags & Terrain.AVOID) != 0;
            water[i] = (flags & Terrain.LIQUID) != 0;
            pit[i] = (flags & Terrain.PIT) != 0;
        }

        int lastRow = length() - width();
        for (int i = 0; i < width(); i++) {
            passable[i] = avoid[i] = false;
            passable[lastRow + i] = avoid[lastRow + i] = false;
        }
        for (int i = width(); i < lastRow; i += width()) {
            passable[i] = avoid[i] = false;
            passable[i + width() - 1] = avoid[i + width() - 1] = false;
        }
    }

    public void destroy(int pos) {

        if (!DungeonTilemap.waterStitcheable.contains(map[pos])) {
            for (int j = 0; j < PathFinder.NEIGHBOURS4.length; j++) {
                if (water[pos + PathFinder.NEIGHBOURS4[j]]) {
                    set(pos, Terrain.WATER);
                    return;
                }
            }
        }

        set(pos, Terrain.EMBERS);
    }

    void cleanWalls() {
        discoverable = new boolean[length()];

        for (int i = 0; i < length(); i++) {

            boolean d = false;

            for (int j = 0; j < PathFinder.NEIGHBOURS9.length; j++) {
                int n = i + PathFinder.NEIGHBOURS9[j];
                if (n >= 0 && n < length() && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
                    d = true;
                    break;
                }
            }

            if (d) {
                d = false;

                for (int j = 0; j < PathFinder.NEIGHBOURS9.length; j++) {
                    int n = i + PathFinder.NEIGHBOURS9[j];
                    if (n >= 0 && n < length() && !pit[n]) {
                        d = true;
                        break;
                    }
                }
            }

            discoverable[i] = d;
        }
    }

    public static void set(int cell, int terrain) {
        Painter.set(Dungeon.level, cell, terrain);

        if (terrain != Terrain.VENT && terrain != Terrain.HIDDEN_VENT && terrain != Terrain.INACTIVE_VENT) {
            Dungeon.level.vents.remove(cell);
        }

        int flags = Terrain.flags[terrain];
        passable[cell] = (flags & Terrain.PASSABLE) != 0;
        losBlocking[cell] = (flags & Terrain.LOS_BLOCKING) != 0;
        flamable[cell] = (flags & Terrain.FLAMABLE) != 0;
        secret[cell] = (flags & Terrain.SECRET) != 0;
        solid[cell] = (flags & Terrain.SOLID) != 0;
        avoid[cell] = (flags & Terrain.AVOID) != 0;
        pit[cell] = (flags & Terrain.PIT) != 0;
        water[cell] = (flags & Terrain.WATER) != 0;
    }

    public Heap drop(Item item, int cell) {

        //This messy if statement deals will items which should not drop in challenges primarily.
        if ((Dungeon.isChallenged(Challenges.NO_FOOD) && (item instanceof Food || item instanceof BlandfruitBush.Seed)) ||
                (Dungeon.isChallenged(Challenges.NO_ARMOR) && item instanceof Armor) ||
                (Dungeon.isChallenged(Challenges.NO_HEALING) && item instanceof ExperimentalTechOfHealing) ||
                (Dungeon.isChallenged(Challenges.NO_HERBALISM) && (item instanceof Plant.Seed || item instanceof Dewdrop || item instanceof SeedPouch)) ||
                (Dungeon.isChallenged(Challenges.NO_SCRIPTS) && ((item instanceof Script && !(item instanceof ScriptOfUpgrade || item instanceof ScriptOfMagicalInfusion)) || item instanceof ScriptHolder)) ||
                item == null) {

            //create a dummy heap, give it a dummy sprite, don't add it to the game, and return it.
            //effectively nullifies whatever the logic calling this wants to do, including dropping items.
            Heap heap = new Heap();
            ItemSprite sprite = heap.sprite = new ItemSprite();
            sprite.link(heap);
            return heap;

        }

        if ((map[cell] == Terrain.ALCHEMY) && (
                !(item instanceof Plant.Seed || item instanceof Blandfruit) ||
                        item instanceof BlandfruitBush.Seed ||
                        (item instanceof Blandfruit && (((Blandfruit) item).experimentalTechAttrib != null || heaps.get(cell) != null)) ||
                        Dungeon.hero.buff(AlchemistsToolkit.alchemy.class) != null && Dungeon.hero.buff(AlchemistsToolkit.alchemy.class).isMalfunctioning())) {
            int n;
            do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (map[n] != Terrain.EMPTY_SP);
            cell = n;
        }

        Heap heap = heaps.get(cell);
        if (heap == null) {

            heap = new Heap();
            heap.seen = Dungeon.visible[cell];
            heap.pos = cell;
            if (map[cell] == Terrain.CHASM || (Dungeon.level != null && pit[cell])) {
                Dungeon.dropToChasm(item);
                GameScene.discard(heap);
            } else {
                heaps.put(cell, heap);
                GameScene.add(heap);
            }

        } else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {

            int n;
            do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)];
            } while (!Level.passable[n] && !Level.avoid[n]);
            return drop(item, n);

        }
        heap.drop(item);

        if (Dungeon.level != null) {
            press(cell, null);
        }

        return heap;
    }

    public Plant plant(Plant.Seed seed, int pos) {

        Plant plant = plants.get(pos);
        if (plant != null) {
            plant.wither();
        }

        if (map[pos] == Terrain.OFFVENT ||
                map[pos] == Terrain.EMPTY ||
                map[pos] == Terrain.EMBERS ||
                map[pos] == Terrain.EMPTY_DECO) {
            map[pos] = Terrain.LIGHTEDVENT;
            flamable[pos] = true;
        }

        plant = seed.couch(pos);
        plants.put(pos, plant);

        GameScene.plantSeed(pos);

        return plant;
    }

    public void uproot(int pos) {
        plants.remove(pos);
        GameScene.updateMap(pos);
    }

    public Vent setVent(Vent vent, int pos) {
        Vent existingVent = vents.get(pos);
        if (existingVent != null) {
            vents.remove(pos);
        }
        vent.set(pos);
        vents.put(pos, vent);
        GameScene.updateMap(pos);
        return vent;
    }

    public void disarmVent(int pos) {
        // wafitz.v4: 'Reset' vent so it can be lighted
        set(pos, Terrain.OFFVENT);
        GameScene.updateMap(pos);
    }

    public void discover(int cell) {
        set(cell, Terrain.discover(map[cell]));
        Vent vent = vents.get(cell);
        if (vent != null)
            vent.reveal();
        GameScene.updateMap(cell);
    }

    public int pitCell() {
        return randomRespawnCell();
    }

    public void press(int cell, Char ch) {

        if (ch != null && pit[cell] && !ch.flying) {
            if (ch == Dungeon.hero) {
                Chasm.heroFall(cell);
            } else if (ch instanceof Mob) {
                Chasm.mobFall((Mob) ch);
            }
            return;
        }

        Vent vent = null;

        switch (map[cell]) {

            case Terrain.HIDDEN_VENT:
                GLog.i(Messages.get(Level.class, "hidden_plate"));
            case Terrain.VENT:
                vent = vents.get(cell);
                break;

            case Terrain.OFFVENT:
                OffVent.trample(this, cell, ch);
                break;

            case Terrain.WELL:
                WellWater.affectCell(cell);
                break;

            case Terrain.ALCHEMY:
                if (ch == null) {
                    Craft.transmute(cell);
                }
                break;

            case Terrain.DOOR:
                Door.enter(cell);
                break;
        }

        TimekeepersHourglass.timeFreeze timeFreeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);

        if (vent != null) {
            if (timeFreeze == null) {

                if (ch == Dungeon.hero)
                    Dungeon.hero.interrupt();

                vent.trigger();

            } else {

                Sample.INSTANCE.play(Assets.SND_TRAP);

                discover(cell);

                timeFreeze.setDelayedPress(cell);

            }
        }

        Plant plant = plants.get(cell);
        if (plant != null) {
            plant.trigger();
        }
    }

    public void mobPress(Mob mob) {

        int cell = mob.pos;

        if (pit[cell] && !mob.flying) {
            Chasm.mobFall(mob);
            return;
        }

        Vent vent = null;
        switch (map[cell]) {

            case Terrain.VENT:
                vent = vents.get(cell);
                break;

            case Terrain.DOOR:
                Door.enter(cell);
                break;
        }

        if (vent != null) {
            vent.trigger();
        }

        Plant plant = plants.get(cell);
        if (plant != null) {
            plant.trigger();
        }
    }

    public void updateFieldOfView(Char c, boolean[] fieldOfView) {

        int cx = c.pos % width();
        int cy = c.pos / width();

        boolean sighted = c.buff(Blindness.class) == null && c.buff(Shadows.class) == null
                && c.buff(TimekeepersHourglass.timeStasis.class) == null && c.isAlive();
        if (sighted) {
            ShadowCaster.castShadow(cx, cy, fieldOfView, c.viewDistance);
        } else {
            BArray.setFalse(fieldOfView);
        }

        int sense = 1;
        //Currently only the hero can get mind vision
        if (c.isAlive() && c == Dungeon.hero) {
            for (Buff b : c.buffs(MindVision.class)) {
                sense = Math.max(((MindVision) b).distance, sense);
            }
        }

        if (!sighted || sense > 1) {

            int ax = Math.max(0, cx - sense);
            int bx = Math.min(cx + sense, width() - 1);
            int ay = Math.max(0, cy - sense);
            int by = Math.min(cy + sense, height() - 1);

            int len = bx - ax + 1;
            int pos = ax + ay * width();
            for (int y = ay; y <= by; y++, pos += width()) {
                System.arraycopy(discoverable, pos, fieldOfView, pos, len);
            }
        }

        //Currently only the hero can get mind vision or awareness
        if (c.isAlive() && c == Dungeon.hero) {
            Dungeon.hero.mindVisionEnemies.clear();
            if (c.buff(MindVision.class) != null) {
                for (Mob mob : mobs) {
                    int p = mob.pos;

                    if (!fieldOfView[p]) {
                        Dungeon.hero.mindVisionEnemies.add(mob);
                    }
                    for (int i : PathFinder.NEIGHBOURS9)
                        fieldOfView[p + i] = true;

                }
            } else if (((Hero) c).heroClass == HeroClass.CAPTAIN) {
                for (Mob mob : mobs) {
                    int p = mob.pos;
                    if (distance(c.pos, p) == 2) {

                        if (!fieldOfView[p]) {
                            Dungeon.hero.mindVisionEnemies.add(mob);
                        }
                        for (int i : PathFinder.NEIGHBOURS9)
                            fieldOfView[p + i] = true;
                    }
                }
            }
            if (c.buff(Awareness.class) != null) {
                for (Heap heap : heaps.values()) {
                    int p = heap.pos;
                    for (int i : PathFinder.NEIGHBOURS9)
                        fieldOfView[p + i] = true;
                }
            }
        }

        if (c == Dungeon.hero) {
            for (Heap heap : heaps.values())
                if (!heap.seen && fieldOfView[heap.pos])
                    heap.seen = true;
        }

    }

    public int distance(int a, int b) {
        int ax = a % width();
        int ay = a / width();
        int bx = b % width();
        int by = b / width();
        return Math.max(Math.abs(ax - bx), Math.abs(ay - by));
    }

    public boolean adjacent(int a, int b) {
        return distance(a, b) == 1;
    }

    //returns true if the input is a valid tile within the level
    public boolean insideMap(int tile) {
        //top and bottom row and beyond
        return !((tile < width || tile >= length - width) ||
                //left and right column
                (tile % width == 0 || tile % width == width - 1));
    }

    Point cellToPoint(int cell) {
        return new Point(cell % width(), cell / width());
    }

    public int pointToCell(Point p) {
        return p.x + p.y * width();
    }

    public String tileName(int tile) {

        switch (tile) {
            case Terrain.CHASM:
                return Messages.get(Level.class, "chasm_name");
            case Terrain.EMPTY:
            case Terrain.EMPTY_SP:
            case Terrain.EMPTY_DECO:
            case Terrain.HIDDEN_VENT:
                return Messages.get(Level.class, "floor_name");
            case Terrain.LIGHTEDVENT:
                return Messages.get(Level.class, "lightedvent_name");
            case Terrain.WATER:
                return Messages.get(Level.class, "water_name");
            case Terrain.WALL:
            case Terrain.WALL_DECO:
            case Terrain.SECRET_DOOR:
                return Messages.get(Level.class, "wall_name");
            case Terrain.DOOR:
                return Messages.get(Level.class, "closed_door_name");
            case Terrain.OPEN_DOOR:
                return Messages.get(Level.class, "open_door_name");
            case Terrain.ENTRANCE:
                return Messages.get(Level.class, "entrace_name");
            case Terrain.EXIT:
                return Messages.get(Level.class, "exit_name");
            case Terrain.EMBERS:
                return Messages.get(Level.class, "embers_name");
            case Terrain.LOCKED_DOOR:
                return Messages.get(Level.class, "locked_door_name");
            case Terrain.PEDESTAL:
                return Messages.get(Level.class, "pedestal_name");
            case Terrain.BARRICADE:
                return Messages.get(Level.class, "barricade_name");
            case Terrain.OFFVENT:
                return Messages.get(Level.class, "off_vent_name");
            case Terrain.LOCKED_EXIT:
                return Messages.get(Level.class, "locked_exit_name");
            case Terrain.UNLOCKED_EXIT:
                return Messages.get(Level.class, "unlocked_exit_name");
            case Terrain.SIGN:
                return Messages.get(Level.class, "sign_name");
            case Terrain.WELL:
                return Messages.get(Level.class, "well_name");
            case Terrain.EMPTY_WELL:
                return Messages.get(Level.class, "empty_well_name");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(Level.class, "statue_name");
            case Terrain.INACTIVE_VENT:
                return Messages.get(Level.class, "inactive_vent_name");
            case Terrain.BOOKSHELF:
                return Messages.get(Level.class, "bookshelf_name");
            case Terrain.ALCHEMY:
                return Messages.get(Level.class, "alchemy_name");
            default:
                return Messages.get(Level.class, "default_name");
        }
    }

    public String tileDesc(int tile) {

        switch (tile) {
            case Terrain.CHASM:
                return Messages.get(Level.class, "chasm_desc");
            case Terrain.WATER:
                return Messages.get(Level.class, "water_desc");
            case Terrain.ENTRANCE:
                return Messages.get(Level.class, "entrance_desc");
            case Terrain.EXIT:
            case Terrain.UNLOCKED_EXIT:
                return Messages.get(Level.class, "exit_desc");
            case Terrain.EMBERS:
                return Messages.get(Level.class, "embers_desc");
            case Terrain.OFFVENT:
                return Messages.get(Level.class, "off_vent_desc");
            case Terrain.LOCKED_DOOR:
                return Messages.get(Level.class, "locked_door_desc");
            case Terrain.LOCKED_EXIT:
                return Messages.get(Level.class, "locked_exit_desc");
            case Terrain.BARRICADE:
                return Messages.get(Level.class, "barricade_desc");
            case Terrain.SIGN:
                return Messages.get(Level.class, "sign_desc");
            case Terrain.INACTIVE_VENT:
                return Messages.get(Level.class, "inactive_vent_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(Level.class, "statue_desc");
            case Terrain.ALCHEMY:
                return Messages.get(Level.class, "alchemy_desc");
            case Terrain.EMPTY_WELL:
                return Messages.get(Level.class, "empty_well_desc");
            default:
                return Messages.get(Level.class, "default_desc");
        }
    }
}
