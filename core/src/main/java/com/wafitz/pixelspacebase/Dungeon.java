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
package com.wafitz.pixelspacebase;

import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Awareness;
import com.wafitz.pixelspacebase.actors.buffs.IntruderAlert;
import com.wafitz.pixelspacebase.actors.buffs.Light;
import com.wafitz.pixelspacebase.actors.buffs.Paranoid;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Blacksmith;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Imp;
import com.wafitz.pixelspacebase.items.Clone;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.levels.CavesBossLevel;
import com.wafitz.pixelspacebase.levels.CavesLevel;
import com.wafitz.pixelspacebase.levels.CityBossLevel;
import com.wafitz.pixelspacebase.levels.CityLevel;
import com.wafitz.pixelspacebase.levels.DeadEndLevel;
import com.wafitz.pixelspacebase.levels.HallsBossLevel;
import com.wafitz.pixelspacebase.levels.HallsLevel;
import com.wafitz.pixelspacebase.levels.LastLevel;
import com.wafitz.pixelspacebase.levels.LastWorkshopLevel;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.OperationsBossLevel;
import com.wafitz.pixelspacebase.levels.OperationsLevel;
import com.wafitz.pixelspacebase.levels.PrisonBossLevel;
import com.wafitz.pixelspacebase.levels.PrisonLevel;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.StartScene;
import com.wafitz.pixelspacebase.ui.QuickSlotButton;
import com.wafitz.pixelspacebase.utils.BArray;
import com.wafitz.pixelspacebase.utils.DungeonSeed;
import com.wafitz.pixelspacebase.windows.WndResurrect;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class Dungeon {

    public static int transmutation;    // depth number for a well of transmutation

    //enum of items which have limited spawns, records how many have spawned
    //could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
    //TODO: this is fairly brittle when it comes to bundling, should look into a more flexible solution.
    public enum limitedDrops {
        //limited world drops
        strengthTech,
        upgradeScripts,
        arcaneStyli,

        //all unlimited health potion sources (except guards, which are at the bottom.
        swarmHP,
        batHP,
        warlockHP,
        scorpioHP,
        makingHP,
        //blandfruit, which can technically be an unlimited health potion source
        blandfruitGadget,

        //doesn't use Generator, so we have to enforce one armband drop here
        armband,

        //containers
        airTank,
        gadgetBag,
        scriptBag,
        experimentalTechBag,
        blasterHolster,

        guardHP;

        public int count = 0;

        //for items which can only be dropped once, should directly access count otherwise.
        public boolean dropped() {
            return count != 0;
        }

        public void drop() {
            count = 1;
        }
    }

    public static int challenges;

    public static Hero hero;
    public static Level level;

    public static QuickSlot quickslot = new QuickSlot();

    public static int depth;
    public static int parts;

    public static HashSet<Integer> chapters;

    // Hero's field of view
    public static boolean[] visible;

    public static SparseArray<ArrayList<Item>> droppedItems;

    public static int version;

    public static long seed;

    public static void init() {

        version = Game.versionCode;
        challenges = PixelSpacebase.challenges();

        seed = DungeonSeed.randomSeed();

        Actor.clear();
        Actor.resetNextID();

        Random.seed(seed);

        Script.initLabels();
        ExperimentalTech.initColors();
        Module.initGems();

        transmutation = Random.IntRange(6, 14);

        Room.shuffleTypes();

        Random.seed();

        Statistics.reset();
        Journal.reset();

        quickslot.reset();
        QuickSlotButton.reset();

        depth = 0;
        parts = 0;

        droppedItems = new SparseArray<>();

        for (limitedDrops a : limitedDrops.values())
            a.count = 0;

        chapters = new HashSet<>();

        Hologram.Quest.reset();
        Gunsmith.Quest.reset();
        Blacksmith.Quest.reset();
        Imp.Quest.reset();

        Generator.initArtifacts();
        hero = new Hero();
        hero.live();

        Badges.reset();

        StartScene.curClass.initHero(hero);
    }

    public static boolean isChallenged(int mask) {
        return (challenges & mask) != 0;
    }

    public static Level newLevel() {

        Dungeon.level = null;
        Actor.clear();

        depth++;
        if (depth > Statistics.deepestFloor) {
            Statistics.deepestFloor = depth;

            Statistics.completedWithNoKilling = Statistics.qualifiedForNoKilling;
        }

        Level level;
        switch (depth) {
            case 1:
            case 2:
            case 3:
            case 4:
                level = new OperationsLevel();
                break;
            case 5:
                level = new OperationsBossLevel();
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                level = new PrisonLevel();
                break;
            case 10:
                level = new PrisonBossLevel();
                break;
            case 11:
            case 12:
            case 13:
            case 14:
                level = new CavesLevel();
                break;
            case 15:
                level = new CavesBossLevel();
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                level = new CityLevel();
                break;
            case 20:
                level = new CityBossLevel();
                break;
            case 21:
                level = new LastWorkshopLevel();
                break;
            case 22:
            case 23:
            case 24:
                level = new HallsLevel();
                break;
            case 25:
                level = new HallsBossLevel();
                break;
            case 26:
                level = new LastLevel();
                break;
            default:
                level = new DeadEndLevel();
                Statistics.deepestFloor--;
        }

        visible = new boolean[level.length()];
        level.create();

        Statistics.qualifiedForNoKilling = !bossLevel();

        return level;
    }

    public static void resetLevel() {

        Actor.clear();

        level.reset();
        switchLevel(level, level.entrance);
    }

    public static long seedCurDepth() {
        return seedForDepth(depth);
    }

    private static long seedForDepth(int depth) {
        Random.seed(seed);
        for (int i = 0; i < depth; i++)
            Random.Long(); //we don't care about these values, just need to go through them
        long result = Random.Long();
        Random.seed();
        return result;
    }

    // wafitz.v1 - You get a shop, you get a shop, every level get's a shop!
    public static boolean workshopOnLevel() {
        return true;
    }

    public static boolean bossLevel() {
        return bossLevel(depth);
    }

    public static boolean bossLevel(int depth) {
        return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25;
    }

    @SuppressWarnings("deprecation")
    public static void switchLevel(final Level level, int pos) {

        Dungeon.level = level;
        Actor.init();

        PathFinder.setMapSize(level.width(), level.height());
        visible = new boolean[level.length()];

        Actor respawner = level.respawner();
        if (respawner != null) {
            Actor.add(level.respawner());
        }

        hero.pos = pos != -1 ? pos : level.exit;

        Light light = hero.buff(Light.class);
        hero.viewDistance = light == null ? level.viewDistance : Math.max(Light.DISTANCE, level.viewDistance);

        observe();
        try {
            saveAll();
        } catch (IOException e) {
            PixelSpacebase.reportException(e);
            /*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
            But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
        }
    }

    public static void dropToChasm(Item item) {
        int depth = Dungeon.depth + 1;
        ArrayList<Item> dropped = Dungeon.droppedItems.get(depth);
        if (dropped == null) {
            Dungeon.droppedItems.put(depth, dropped = new ArrayList<>());
        }
        dropped.add(item);
    }

    public static boolean posNeeded() {
        //2 POS each floor set
        int posLeftThisSet = 2 - (limitedDrops.strengthTech.count - (depth / 5) * 2);
        if (posLeftThisSet <= 0) return false;

        int floorThisSet = (depth % 5);

        //pos drops every two floors, (numbers 1-2, and 3-4) with a 50% chance for the earlier one each time.
        int targetPOSLeft = 2 - floorThisSet / 2;
        if (floorThisSet % 2 == 1 && Random.Int(2) == 0) targetPOSLeft--;

        return targetPOSLeft < posLeftThisSet;

    }

    public static boolean souNeeded() {
        //3 SOU each floor set
        int souLeftThisSet = 3 - (limitedDrops.upgradeScripts.count - (depth / 5) * 3);
        if (souLeftThisSet <= 0) return false;

        int floorThisSet = (depth % 5);
        //chance is floors left / scripts left
        return Random.Int(5 - floorThisSet) < souLeftThisSet;
    }

    public static boolean asNeeded() {
        //1 AS each floor set
        int asLeftThisSet = 1 - (limitedDrops.arcaneStyli.count - (depth / 5));
        if (asLeftThisSet <= 0) return false;

        int floorThisSet = (depth % 5);
        //chance is floors left / scripts left
        return Random.Int(5 - floorThisSet) < asLeftThisSet;
    }

    private static final String RG_GAME_FILE = "game.dat";
    private static final String RG_DEPTH_FILE = "depth%d.dat";

    private static final String WR_GAME_FILE = "commander.dat";
    private static final String WR_DEPTH_FILE = "commander%d.dat";

    private static final String MG_GAME_FILE = "dm3000.dat";
    private static final String MG_DEPTH_FILE = "dm3000%d.dat";

    private static final String RN_GAME_FILE = "ranger.dat";
    private static final String RN_DEPTH_FILE = "ranger%d.dat";

    private static final String VERSION = "version";
    private static final String SEED = "seed";
    private static final String CHALLENGES = "challenges";
    private static final String HERO = "hero";
    private static final String PARTS = "parts";
    private static final String DEPTH = "depth";
    private static final String DROPPED = "dropped%d";
    private static final String LEVEL = "level";
    private static final String LIMDROPS = "limiteddrops";
    private static final String DV = "airTank";
    private static final String WT = "transmutation";
    private static final String CHAPTERS = "chapters";
    private static final String QUESTS = "quests";
    private static final String BADGES = "badges";

    static String gameFile(HeroClass cl) {
        switch (cl) {
            case COMMANDER:
                return WR_GAME_FILE;
            case DM3000:
                return MG_GAME_FILE;
            case CAPTAIN:
                return RN_GAME_FILE;
            default:
                return RG_GAME_FILE;
        }
    }

    private static String depthFile(HeroClass cl) {
        switch (cl) {
            case COMMANDER:
                return WR_DEPTH_FILE;
            case DM3000:
                return MG_DEPTH_FILE;
            case CAPTAIN:
                return RN_DEPTH_FILE;
            default:
                return RG_DEPTH_FILE;
        }
    }

    private static void saveGame(String fileName) throws IOException {
        try {
            Bundle bundle = new Bundle();

            version = Game.versionCode;
            bundle.put(VERSION, version);
            bundle.put(SEED, seed);
            bundle.put(CHALLENGES, challenges);
            bundle.put(HERO, hero);
            bundle.put(PARTS, parts);
            bundle.put(DEPTH, depth);

            for (int d : droppedItems.keyArray()) {
                bundle.put(Messages.format(DROPPED, d), droppedItems.get(d));
            }

            quickslot.storePlaceholders(bundle);

            bundle.put(WT, transmutation);

            int[] dropValues = new int[limitedDrops.values().length];
            for (limitedDrops value : limitedDrops.values())
                dropValues[value.ordinal()] = value.count;
            bundle.put(LIMDROPS, dropValues);

            int count = 0;
            int ids[] = new int[chapters.size()];
            for (Integer id : chapters) {
                ids[count++] = id;
            }
            bundle.put(CHAPTERS, ids);

            Bundle quests = new Bundle();
            Hologram.Quest.storeInBundle(quests);
            Gunsmith.Quest.storeInBundle(quests);
            Blacksmith.Quest.storeInBundle(quests);
            Imp.Quest.storeInBundle(quests);
            bundle.put(QUESTS, quests);

            Room.storeRoomsInBundle(bundle);

            Statistics.storeInBundle(bundle);
            Journal.storeInBundle(bundle);
            Generator.storeInBundle(bundle);

            Script.save(bundle);
            ExperimentalTech.save(bundle);
            Module.save(bundle);

            Actor.storeNextID(bundle);

            Bundle badges = new Bundle();
            Badges.saveLocal(badges);
            bundle.put(BADGES, badges);

            OutputStream output = Game.instance.openFileOutput(fileName, Game.MODE_PRIVATE);
            Bundle.write(bundle, output);
            output.close();

        } catch (IOException e) {
            GamesInProgress.setUnknown(hero.heroClass);
            PixelSpacebase.reportException(e);
        }
    }

    private static void saveLevel() throws IOException {
        Bundle bundle = new Bundle();
        bundle.put(LEVEL, level);

        OutputStream output = Game.instance.openFileOutput(
                Messages.format(depthFile(hero.heroClass), depth), Game.MODE_PRIVATE);
        Bundle.write(bundle, output);
        output.close();
    }

    public static void saveAll() throws IOException {
        if (hero.isAlive()) {

            Actor.fixTime();
            saveGame(gameFile(hero.heroClass));
            saveLevel();

            GamesInProgress.set(hero.heroClass, depth, hero.lvl, challenges != 0);

        } else if (WndResurrect.instance != null) {

            WndResurrect.instance.hide();
            Hero.reallyDie(WndResurrect.causeOfDeath);

        }
    }

    public static void loadGame(HeroClass cl) throws IOException {
        loadGame(gameFile(cl), true);
    }

    public static void loadGame(String fileName) throws IOException {
        loadGame(fileName, false);
    }

    public static void loadGame(String fileName, boolean fullLoad) throws IOException {

        Bundle bundle = gameBundle(fileName);

        version = bundle.getInt(VERSION);

        seed = bundle.contains(SEED) ? bundle.getLong(SEED) : DungeonSeed.randomSeed();

        Generator.reset();

        Actor.restoreNextID(bundle);

        quickslot.reset();
        QuickSlotButton.reset();

        Dungeon.challenges = bundle.getInt(CHALLENGES);

        Dungeon.level = null;
        Dungeon.depth = -1;

        Script.restore(bundle);
        ExperimentalTech.restore(bundle);
        Module.restore(bundle);

        quickslot.restorePlaceholders(bundle);

        if (fullLoad) {
            transmutation = bundle.getInt(WT);

            int[] dropValues = bundle.getIntArray(LIMDROPS);
            for (limitedDrops value : limitedDrops.values())
                value.count = value.ordinal() < dropValues.length ?
                        dropValues[value.ordinal()] : 0;

            chapters = new HashSet<>();
            int ids[] = bundle.getIntArray(CHAPTERS);
            if (ids != null) {
                for (int id : ids) {
                    chapters.add(id);
                }
            }

            Bundle quests = bundle.getBundle(QUESTS);
            if (!quests.isNull()) {
                Hologram.Quest.restoreFromBundle(quests);
                Gunsmith.Quest.restoreFromBundle(quests);
                Blacksmith.Quest.restoreFromBundle(quests);
                Imp.Quest.restoreFromBundle(quests);
            } else {
                Hologram.Quest.reset();
                Gunsmith.Quest.reset();
                Blacksmith.Quest.reset();
                Imp.Quest.reset();
            }

            Room.restoreRoomsFromBundle(bundle);
        }

        Bundle badges = bundle.getBundle(BADGES);
        if (!badges.isNull()) {
            Badges.loadLocal(badges);
        } else {
            Badges.reset();
        }

        hero = null;
        hero = (Hero) bundle.get(HERO);

        parts = bundle.getInt(PARTS);
        depth = bundle.getInt(DEPTH);

        Statistics.restoreFromBundle(bundle);
        Journal.restoreFromBundle(bundle);
        Generator.restoreFromBundle(bundle);

        droppedItems = new SparseArray<>();
        for (int i = 2; i <= Statistics.deepestFloor + 1; i++) {
            ArrayList<Item> dropped = new ArrayList<>();
            if (bundle.contains(Messages.format(DROPPED, i)))
                for (Bundlable b : bundle.getCollection(Messages.format(DROPPED, i))) {
                    dropped.add((Item) b);
                }
            if (!dropped.isEmpty()) {
                droppedItems.put(i, dropped);
            }
        }
    }

    public static Level loadLevel(HeroClass cl) throws IOException {

        Dungeon.level = null;
        Actor.clear();

        InputStream input = Game.instance.openFileInput(Messages.format(depthFile(cl), depth));
        Bundle bundle = Bundle.read(input);
        input.close();

        return (Level) bundle.get("level");
    }

    public static void deleteGame(HeroClass cl, boolean deleteLevels) {

        Game.instance.deleteFile(gameFile(cl));

        if (deleteLevels) {
            int depth = 1;
            while (Game.instance.deleteFile(Messages.format(depthFile(cl), depth))) {
                depth++;
            }
        }

        GamesInProgress.delete(cl);
    }

    static Bundle gameBundle(String fileName) throws IOException {

        InputStream input = Game.instance.openFileInput(fileName);
        Bundle bundle = Bundle.read(input);
        input.close();

        return bundle;
    }

    static void preview(GamesInProgress.Info info, Bundle bundle) {
        info.depth = bundle.getInt(DEPTH);
        info.challenges = (bundle.getInt(CHALLENGES) != 0);
        if (info.depth == -1) {
            info.depth = bundle.getInt("maxDepth");    // FIXME
        }
        Hero.preview(info, bundle.getBundle(HERO));
    }

    public static void fail(Class cause) {
        if (hero.belongings.getItem(Clone.class) == null) {
            Rankings.INSTANCE.submit(false, cause);
        }
    }

    public static void win(Class cause) {

        hero.belongings.identify();

        if (challenges != 0) {
            Badges.validateChampion();
        }

        Rankings.INSTANCE.submit(true, cause);
    }

    public static void observe() {
        observe(hero.viewDistance + 1);
    }

    public static void observe(int dist) {

        if (level == null) {
            return;
        }

        level.updateFieldOfView(hero, visible);

        int cx = hero.pos % level.width();
        int cy = hero.pos / level.width();

        int ax = Math.max(0, cx - dist);
        int bx = Math.min(cx + dist, level.width() - 1);
        int ay = Math.max(0, cy - dist);
        int by = Math.min(cy + dist, level.height() - 1);

        int len = bx - ax + 1;
        int pos = ax + ay * level.width();
        for (int y = ay; y <= by; y++, pos += level.width()) {
            BArray.or(level.visited, visible, pos, len, level.visited);
        }

        if (hero.buff(IntruderAlert.class) != null || hero.buff(Awareness.class) != null)
            GameScene.updateFog();
        else
            GameScene.updateFog(ax, ay, len, by - ay);

        GameScene.afterObserve();
    }

    //we store this to avoid having to re-allocate the array with each pathfind
    private static boolean[] passable;

    private static void setupPassable() {
        if (passable == null || passable.length != Dungeon.level.length())
            passable = new boolean[Dungeon.level.length()];
        else
            BArray.setFalse(passable);
    }

    public static PathFinder.Path findPath(Char ch, int from, int to, boolean pass[], boolean[] visible) {

        setupPassable();
        if (ch.flying || ch.buff(Paranoid.class) != null) {
            BArray.or(pass, Level.avoid, passable);
        } else {
            System.arraycopy(pass, 0, passable, 0, Dungeon.level.length());
        }

        for (Char c : Actor.chars()) {
            if (visible[c.pos]) {
                passable[c.pos] = false;
            }
        }

        return PathFinder.find(from, to, passable);

    }

    public static int findStep(Char ch, int from, int to, boolean pass[], boolean[] visible) {

        if (level.adjacent(from, to)) {
            return Actor.findChar(to) == null && (pass[to] || Level.avoid[to]) ? to : -1;
        }

        setupPassable();
        if (ch.flying || ch.buff(Paranoid.class) != null) {
            BArray.or(pass, Level.avoid, passable);
        } else {
            System.arraycopy(pass, 0, passable, 0, Dungeon.level.length());
        }

        for (Char c : Actor.chars()) {
            if (visible[c.pos]) {
                passable[c.pos] = false;
            }
        }

        return PathFinder.getStep(from, to, passable);

    }

    public static int flee(Char ch, int cur, int from, boolean pass[], boolean[] visible) {

        setupPassable();
        if (ch.flying) {
            BArray.or(pass, Level.avoid, passable);
        } else {
            System.arraycopy(pass, 0, passable, 0, Dungeon.level.length());
        }

        for (Char c : Actor.chars()) {
            if (visible[c.pos]) {
                passable[c.pos] = false;
            }
        }
        passable[cur] = true;

        return PathFinder.getStepBack(cur, from, passable);

    }

}
