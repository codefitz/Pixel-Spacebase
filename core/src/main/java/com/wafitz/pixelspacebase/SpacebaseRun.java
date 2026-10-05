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
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.StrandedRoomRescue;
import com.wafitz.pixelspacebase.actors.buffs.YRescueJourney;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Y;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Quartermaster;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Leonard;
import com.wafitz.pixelspacebase.actors.mobs.npcs.StationCat;
import com.wafitz.pixelspacebase.items.Clone;
import com.wafitz.pixelspacebase.items.plasmids.Plasmid;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.upgrades.Upgrade;
import com.wafitz.pixelspacebase.levels.EngineeringBossLevel;
import com.wafitz.pixelspacebase.levels.EngineeringLevel;
import com.wafitz.pixelspacebase.levels.HolodeckBossLevel;
import com.wafitz.pixelspacebase.levels.HabitationRingLevel;
import com.wafitz.pixelspacebase.levels.DeadEndLevel;
import com.wafitz.pixelspacebase.levels.DeepContainmentCoreLevel;
import com.wafitz.pixelspacebase.levels.DeepContainmentDeckLevel;
import com.wafitz.pixelspacebase.levels.LastLevel;
import com.wafitz.pixelspacebase.levels.LastWorkshopLevel;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.MaintenanceBossLevel;
import com.wafitz.pixelspacebase.levels.MaintenanceLevel;
import com.wafitz.pixelspacebase.levels.SecurityBossLevel;
import com.wafitz.pixelspacebase.levels.SecurityBlockLevel;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.painters.Workshop;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.StartScene;
import com.wafitz.pixelspacebase.ui.QuickSlotButton;
import com.wafitz.pixelspacebase.utils.BArray;
import com.wafitz.pixelspacebase.utils.SpacebaseSeed;
import com.wafitz.pixelspacebase.windows.WndResurrect;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class SpacebaseRun {

    public static int transmutation;    // depth number for a well of transmutation

    //enum of items which have limited spawns, records how many have spawned
    //could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
    //TODO: this is fairly brittle when it comes to bundling, should look into a more flexible solution.
    public enum limitedDrops {
        //limited world drops
        strengthTech,
        upgradeDrops,
        arcaneStyli,

        //all unlimited health potion sources (except guards, which are at the bottom.
        replicatorSwarmHP,
        siphonDroneHP,
        warlockHP,
        scorpioHP,
        makingHP,
        //blandfruit, which can technically be an unlimited health potion source
        alienTechDevice,

        //doesn't use Generator, so we have to enforce one armband drop here
        armband,

        //containers
        airTank,
        ordnanceKit,
        utilityKit,
        plasmidKit,
        blasterHolster,

        guardHP,

        //append-only: limited drop ordinals are persisted in saves
        allGearUpgrade;

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

    /** Visibility is transient, so a newly loaded floor starts with a correctly sized empty field. */
    static void resetVisibilityForLevel(Level level) {
        visible = new boolean[level.length()];
    }

    public static SparseArray<ArrayList<Item>> droppedItems;
    public static SparseArray<ArrayList<Heap>> droppedHeaps;

    public static int version;

    public static long seed;

    public static void init() {

        Workshop.resetStorage();

        version = Game.versionCode;
        challenges = PixelSpacebase.challenges();

        seed = SpacebaseSeed.randomSeed();

        Actor.clear();
        Actor.resetNextID();

        Random.seed(seed);

        Upgrade.initLabels();
        Plasmid.initColors();
        Module.initGems();

        transmutation = Random.IntRange(6, 14);

        Room.shuffleTypes();

        Random.seed();

        Statistics.reset();
        Journal.reset();

        quickslot.reset();
        QuickSlotButton.reset();

        depth = 0;
        rescueLevelIdentity = "";
        restoredRescueLevel = null;
        parts = 0;

        droppedItems = new SparseArray<>();
        droppedHeaps = new SparseArray<>();

        for (limitedDrops a : limitedDrops.values())
            a.count = 0;

        chapters = new HashSet<>();

        Hologram.Quest.reset();
        Quartermaster.Quest.reset();
        Leonard.Quest.reset();
        Y.Quest.reset();
        StationCat.Quest.reset();

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
        return newLevel(nextDepth(depth));
    }

    private static Level newLevel(int targetDepth) {

        SpacebaseRun.level = null;
        Actor.clear();

        depth = targetDepth;
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
                level = new MaintenanceLevel();
                break;
            case 5:
                level = new MaintenanceBossLevel();
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                level = new SecurityBlockLevel();
                break;
            case 10:
                level = new SecurityBossLevel();
                break;
            case 11:
            case 12:
            case 13:
            case 14:
                level = new EngineeringLevel();
                break;
            case 15:
                level = new EngineeringBossLevel();
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                level = new HabitationRingLevel();
                break;
            case 20:
                level = new HolodeckBossLevel();
                break;
            case 21:
                level = new LastWorkshopLevel();
                break;
            case 22:
            case 23:
            case 24:
                level = new DeepContainmentDeckLevel();
                break;
            case 25:
                level = new DeepContainmentCoreLevel();
                break;
            case 26:
                level = new LastLevel();
                break;
            default:
                level = new DeadEndLevel();
                Statistics.deepestFloor--;
        }

        resetVisibilityForLevel(level);
        level.create();

        Statistics.qualifiedForNoKilling = !bossLevel();

        return level;
    }

    /** Empty for station decks; future side journeys use a separate save namespace. */
    private static String rescueLevelIdentity = "";
    private static Level restoredRescueLevel;

    public static void selectRescueLevel(String identity) {
        if (identity == null || (!identity.isEmpty() && !identity.matches("(alien|maze|dungeon|boss)_[1-9][0-9]*"))) {
            throw new IllegalArgumentException("Invalid rescue level identity");
        }
        rescueLevelIdentity = identity;
    }

    public static boolean isRescueSideLevel() { return !rescueLevelIdentity.isEmpty(); }

    public static boolean matchesRescueLevel(String identity) {
        return isRescueSideLevel() && rescueLevelIdentity.equals(identity);
    }

    /** Next undefeated arena ahead of the origin; never replace an encounter already underway. */
    public static int nextRescueBoss(int origin) {
        for (int candidate : new int[]{5, 10, 15, 20, 25}) {
            if (candidate <= origin) continue;
            if (candidate == 20 && Y.Quest.isHolodeckPoweredDown()) continue;
            String file = Messages.format(depthFile(hero.heroClass), candidate);
            if (!Game.instance.getFileStreamPath(file).exists()) return candidate;
            try (InputStream input = Game.instance.openFileInput(file)) {
                Bundle arena = Bundle.read(input).getBundle(LEVEL);
                if (arena.getBoolean("rescueBossDefeated") || arena.getBoolean("droppped")) continue;
                if (candidate == 10) {
                    String state = arena.getString("state");
                    if ("WON".equals(state)) continue;
                    return "START".equals(state) ? candidate : -1;
                }
                if (candidate == 5) {
                    boolean bossPresent = false;
                    for (Bundle mob : arena.getBundleArray("mobs")) {
                        String type = mob.getString("__className");
                        if (type != null && (type.endsWith(".XenoQueen") || type.endsWith(".FeralShapeshifter"))) {
                            bossPresent = true;
                            if (mob.getInt("HP") < mob.getInt("HT")) return -1;
                        }
                    }
                    if (!bossPresent) continue;
                }
                return arena.getBoolean("locked") || arena.getBoolean("entered") ? -1 : candidate;
            } catch (IOException error) {
                PixelSpacebase.reportException(error);
                return -1;
            }
        }
        return -1;
    }

    /** Idempotent commit of a resolved detour to its real campaign deck. */
    public static void saveRescueBossArena(int bossDepth, Level arena) throws IOException {
        if (!bossLevel(bossDepth)) throw new IOException("Invalid boss arena");
        Bundle saved = new Bundle(); saved.put(LEVEL, arena);
        writeBundleAtomically(Messages.format(depthFile(hero.heroClass), bossDepth), saved);
    }

    public static Level loadOrCreateRescueLevel(String identity) throws IOException {
        selectRescueLevel(identity);
        restoredRescueLevel = null;
        if (Game.instance.getFileStreamPath(currentLevelFile(hero.heroClass)).exists()) {
            return loadLevel(hero.heroClass);
        }
        if (identity.startsWith("boss_")) {
            Actor.clear();
            level = null;
            String file = Messages.format(depthFile(hero.heroClass), depth);
            if (Game.instance.getFileStreamPath(file).exists()) {
                try (InputStream input = Game.instance.openFileInput(file)) {
                    Level arena = (Level) Bundle.read(input).get(LEVEL);
                    resetVisibilityForLevel(arena);
                    return arena;
                }
            }
            Level arena;
            switch (depth) {
                case 5: arena = new MaintenanceBossLevel(); break;
                case 10: arena = new SecurityBossLevel(); break;
                case 15: arena = new EngineeringBossLevel(); break;
                case 20: arena = new HolodeckBossLevel(); break;
                case 25: arena = new DeepContainmentCoreLevel(); break;
                default: throw new IOException("Invalid boss destination");
            }
            resetVisibilityForLevel(arena);
            arena.create();
            return arena;
        }
        if (!identity.startsWith("alien_")) throw new IOException("Rescue destination is not available");
        Actor.clear();
        level = null;
        Level planet = new com.wafitz.pixelspacebase.levels.AlienPlanetLevel();
        resetVisibilityForLevel(planet);
        planet.create();
        return planet;
    }

    private static String currentLevelFile(HeroClass cl) {
        return rescueLevelIdentity.isEmpty() ? Messages.format(depthFile(cl), depth)
                : gameFile(cl) + ".rescue." + rescueLevelIdentity;
    }

    /** Persists a prepared arrival without writing it over the campaign's current deck. */
    public static void saveJourneyCheckpoint() throws IOException {
        saveGame(gameFile(hero.heroClass));
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
        return !isRescueSideLevel() && !bossLevel();
    }

    public static boolean bossLevel() {
        return bossLevel(depth);
    }

    public static boolean bossLevel(int depth) {
        return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25;
    }

    public static boolean canVisitDepth(int targetDepth) {
        return targetDepth != 21 || Y.Quest.hasSecretWorkshopAccess();
    }

    public static int nextDepth(int currentDepth) {
        return currentDepth == 20 && !canVisitDepth(21) ? 22 : currentDepth + 1;
    }

    public static int previousDepth(int currentDepth) {
        return currentDepth == 22 && !canVisitDepth(21) ? 20 : currentDepth - 1;
    }

    @SuppressWarnings("deprecation")
    public static void switchLevel(final Level level, int pos) {

        SpacebaseRun.level = level;
        Workshop.reconcileStorageChest(level);
        Actor.init();

        if (Y.Quest.isHolodeckPoweredDown()) {
            Y.Quest.discardProjections(level);
        }

        PathFinder.setMapSize(level.width(), level.height());
        resetVisibilityForLevel(level);

        Actor respawner = level.respawner();
        if (respawner != null) {
            Actor.add(level.respawner());
        }

        hero.pos = pos != -1 ? pos : level.exit;
        YRescueJourney journey = hero.buff(YRescueJourney.class);
        if (journey != null) journey.arrived(depth);
        YRescuer.placeOn(level);
        if (level.isDoorlessRoomCell(hero.pos)) {
            Buff.affect(hero, StrandedRoomRescue.class);
        } else {
            Buff.detach(hero, StrandedRoomRescue.class);
        }
        StationCat.placeFollowerOn(level);

        hero.viewDistance = heroViewDistance();

        observe();
        try {
            saveAll();
            if (journey != null && journey.phase == YRescueJourney.Phase.COMPLETE) journey.detach();
        } catch (IOException e) {
            PixelSpacebase.reportException(e);
            /*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
            But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
        }
    }

    public static void dropToChasm(Item item) {
        int depth = fallTargetDepth();
        ArrayList<Item> dropped = SpacebaseRun.droppedItems.get(depth);
        if (dropped == null) {
            SpacebaseRun.droppedItems.put(depth, dropped = new ArrayList<>());
        }
        dropped.add(item);
    }

    public static int fallTargetDepth() {
        return fallTargetDepth(depth);
    }

    static int fallTargetDepth(int currentDepth) {
        return currentDepth > 1 ? previousDepth(currentDepth) : currentDepth + 1;
    }

    public static void dropHeapToDepth(Heap heap, int depth) {
        ArrayList<Heap> dropped = SpacebaseRun.droppedHeaps.get(depth);
        if (dropped == null) {
            SpacebaseRun.droppedHeaps.put(depth, dropped = new ArrayList<>());
        }
        dropped.add(heap);
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
        int souLeftThisSet = 3 - (limitedDrops.upgradeDrops.count - (depth / 5) * 3);
        if (souLeftThisSet <= 0) return false;

        int floorThisSet = (depth % 5);
        //chance is floors left / upgrades left
        return Random.Int(5 - floorThisSet) < souLeftThisSet;
    }

    public static boolean asNeeded() {
        //1 AS each floor set
        int asLeftThisSet = 1 - (limitedDrops.arcaneStyli.count - (depth / 5));
        if (asLeftThisSet <= 0) return false;

        int floorThisSet = (depth % 5);
        //chance is floors left / upgrades left
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
    private static final String DROPPED_HEAPS = "droppedHeaps%d";
    private static final String LEVEL = "level";
    private static final String TEMP_SAVE_SUFFIX = ".tmp";
    private static final String BACKUP_SAVE_SUFFIX = ".bak";
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

    private static void writeBundleAtomically(String fileName, Bundle bundle) throws IOException {
        String tempFileName = fileName + TEMP_SAVE_SUFFIX;
        String backupFileName = fileName + BACKUP_SAVE_SUFFIX;

        boolean writeSucceeded;
        try (OutputStream output = Game.instance.openFileOutput(tempFileName, Game.MODE_PRIVATE)) {
            writeSucceeded = Bundle.write(bundle, output);
        }

        if (!writeSucceeded) {
            deleteTempSaveFile(tempFileName);
            throw new IOException("Failed to write save file: " + fileName);
        }

        File tempFile = Game.instance.getFileStreamPath(tempFileName);
        File targetFile = Game.instance.getFileStreamPath(fileName);
        File backupFile = Game.instance.getFileStreamPath(backupFileName);

        if (backupFile.exists() && !backupFile.delete()) {
            deleteTempSaveFile(tempFileName);
            throw new IOException("Failed to clear backup save file: " + backupFileName);
        }

        boolean hadExistingSave = targetFile.exists();
        if (hadExistingSave && !targetFile.renameTo(backupFile)) {
            deleteTempSaveFile(tempFileName);
            throw new IOException("Failed to back up existing save file: " + fileName);
        }

        if (!tempFile.renameTo(targetFile)) {
            if (hadExistingSave && !backupFile.renameTo(targetFile)) {
                PixelSpacebase.reportException(new IOException("Failed to restore backup save file: " + fileName));
            }
            deleteTempSaveFile(tempFileName);
            throw new IOException("Failed to replace save file: " + fileName);
        }

        if (hadExistingSave && backupFile.exists() && !backupFile.delete()) {
            PixelSpacebase.reportException(new IOException("Failed to delete backup save file: " + backupFileName));
        }
    }

    private static void deleteTempSaveFile(String tempFileName) {
        if (!Game.instance.deleteFile(tempFileName)) {
            PixelSpacebase.reportException(new IOException("Failed to delete temp save file: " + tempFileName));
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
            bundle.put("rescueLevelIdentity", rescueLevelIdentity);
            // The hero and current rescue deck are committed in the same atomic game file.
            // This snapshot also repairs a partially written companion level file on reload.
            if (hero.buff(YRescueJourney.class) != null && level != null) {
                bundle.put("rescueCurrentLevel", level);
            }

            for (int d : droppedItems.keyArray()) {
                bundle.put(Messages.format(DROPPED, d), droppedItems.get(d));
            }
            for (int d : droppedHeaps.keyArray()) {
                bundle.put(Messages.format(DROPPED_HEAPS, d), droppedHeaps.get(d));
            }

            quickslot.storePlaceholders(bundle);

            bundle.put(WT, transmutation);

            int[] dropValues = new int[limitedDrops.values().length];
            for (limitedDrops value : limitedDrops.values())
                dropValues[value.ordinal()] = value.count;
            bundle.put(LIMDROPS, dropValues);

            int count = 0;
            int[] ids = new int[chapters.size()];
            for (Integer id : chapters) {
                ids[count++] = id;
            }
            bundle.put(CHAPTERS, ids);

            Bundle quests = new Bundle();
            Hologram.Quest.storeInBundle(quests);
            Quartermaster.Quest.storeInBundle(quests);
            Leonard.Quest.storeInBundle(quests);
            Y.Quest.storeInBundle(quests);
            StationCat.Quest.storeInBundle(quests);
            bundle.put(QUESTS, quests);

            Room.storeRoomsInBundle(bundle);

            Statistics.storeInBundle(bundle);
            Journal.storeInBundle(bundle);
            Generator.storeInBundle(bundle);

            Upgrade.save(bundle);
            Plasmid.save(bundle);
            Module.save(bundle);
            Workshop.storeInBundle(bundle);

            Actor.storeNextID(bundle);

            Bundle badges = new Bundle();
            Badges.saveLocal(badges);
            bundle.put(BADGES, badges);

            writeBundleAtomically(fileName, bundle);

        } catch (IOException e) {
            GamesInProgress.setUnknown(hero.heroClass);
            PixelSpacebase.reportException(e);
            throw e;
        }
    }

    private static void saveLevel() throws IOException {
        Bundle bundle = new Bundle();
        bundle.put(LEVEL, level);

        writeBundleAtomically(currentLevelFile(hero.heroClass), bundle);
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

        seed = bundle.contains(SEED) ? bundle.getLong(SEED) : SpacebaseSeed.randomSeed();

        Generator.reset();

        Actor.restoreNextID(bundle);

        quickslot.reset();
        QuickSlotButton.reset();

        SpacebaseRun.challenges = bundle.getInt(CHALLENGES);

        SpacebaseRun.level = null;
        SpacebaseRun.depth = -1;
        rescueLevelIdentity = bundle.contains("rescueLevelIdentity") ? bundle.getString("rescueLevelIdentity") : "";
        selectRescueLevel(rescueLevelIdentity);
        restoredRescueLevel = null;

        Upgrade.restore(bundle);
        Plasmid.restore(bundle);
        Module.restore(bundle);

        quickslot.restorePlaceholders(bundle);

        if (fullLoad) {
            Workshop.restoreFromBundle(bundle);

            transmutation = bundle.getInt(WT);

            int[] dropValues = bundle.getIntArray(LIMDROPS);
            for (limitedDrops value : limitedDrops.values())
                value.count = value.ordinal() < dropValues.length ?
                        dropValues[value.ordinal()] : 0;

            chapters = new HashSet<>();
            int[] ids = bundle.getIntArray(CHAPTERS);
            if (ids != null) {
                for (int id : ids) {
                    chapters.add(id);
                }
            }

            Bundle quests = bundle.getBundle(QUESTS);
            if (!quests.isNull()) {
                Hologram.Quest.restoreFromBundle(quests);
                Quartermaster.Quest.restoreFromBundle(quests);
                Leonard.Quest.restoreFromBundle(quests);
                Y.Quest.restoreFromBundle(quests);
                StationCat.Quest.restoreFromBundle(quests);
            } else {
                Hologram.Quest.reset();
                Quartermaster.Quest.reset();
                Leonard.Quest.reset();
                Y.Quest.reset();
                StationCat.Quest.reset();
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
        YRescueJourney journey = hero.buff(YRescueJourney.class);
        if (journey != null && journey.rescueDepth > 0) {
            Statistics.yRescueDepartures = Math.max(1, Math.max(Statistics.yRescueDepartures, journey.journeyId));
        }
        if (fullLoad && !isRescueSideLevel()) {
            Y.Quest.reconcileHolodeckState(hero, depth, Statistics.deepestFloor);
        }
        Journal.restoreFromBundle(bundle);
        Generator.restoreFromBundle(bundle);
        restoredRescueLevel = bundle.contains("rescueCurrentLevel") ? (Level) bundle.get("rescueCurrentLevel") : null;

        droppedItems = new SparseArray<>();
        droppedHeaps = new SparseArray<>();
        for (int i = 1; i <= Statistics.deepestFloor + 1; i++) {
            ArrayList<Item> dropped = new ArrayList<>();
            if (bundle.contains(Messages.format(DROPPED, i)))
                for (Bundlable b : bundle.getCollection(Messages.format(DROPPED, i))) {
                    dropped.add((Item) b);
                }
            if (!dropped.isEmpty()) {
                droppedItems.put(i, dropped);
            }

            ArrayList<Heap> heaps = new ArrayList<>();
            if (bundle.contains(Messages.format(DROPPED_HEAPS, i)))
                for (Bundlable b : bundle.getCollection(Messages.format(DROPPED_HEAPS, i))) {
                    heaps.add((Heap) b);
                }
            if (!heaps.isEmpty()) {
                droppedHeaps.put(i, heaps);
            }
        }
    }

    public static Level loadLevel(HeroClass cl) throws IOException {

        SpacebaseRun.level = null;
        Actor.clear();

        if (restoredRescueLevel != null) {
            Level restored = restoredRescueLevel;
            restoredRescueLevel = null;
            resetVisibilityForLevel(restored);
            return restored;
        }
        InputStream input = Game.instance.openFileInput(currentLevelFile(cl));
        Bundle bundle = Bundle.read(input);
        input.close();

        Level level = (Level) bundle.get("level");
        resetVisibilityForLevel(level);
        return level;
    }

    /** Rescue travel can create gaps below deepestFloor; never overwrite an existing deck. */
    public static Level loadOrCreateLevel(int targetDepth) throws IOException {
        rescueLevelIdentity = "";
        restoredRescueLevel = null;
        depth = targetDepth;
        if (Game.instance.getFileStreamPath(Messages.format(depthFile(hero.heroClass), depth)).exists()) {
            return loadLevel(hero.heroClass);
        }
        return newLevel(targetDepth);
    }

    public static void deleteGame(HeroClass cl, boolean deleteLevels) {

        Game.instance.deleteFile(gameFile(cl));

        if (deleteLevels) {
            File[] saves = Game.instance.getFileStreamPath(gameFile(cl)).getParentFile().listFiles();
            if (saves != null) for (File save : saves) {
                if (save.getName().startsWith(gameFile(cl) + ".rescue.")) Game.instance.deleteFile(save.getName());
            }
            for (int depth = 1; depth <= 26; depth++) {
                Game.instance.deleteFile(Messages.format(depthFile(cl), depth));
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
        if (Hero.devTestInvulnerable()) {
            if (hero != null) {
                hero.restoreDevTestHealth();
            }
            return;
        }
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

    public static int heroViewDistance() {
        int distance = level.viewDistance;
        if (hero != null && hero.heroClass == HeroClass.DM3000) {
            distance = Math.max(distance, Light.DISTANCE);
        }
        if (hero != null && hero.buff(Light.class) != null) {
            distance = Math.max(distance, Light.DISTANCE);
        }
        return distance;
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
        if (passable == null || passable.length != SpacebaseRun.level.length())
            passable = new boolean[SpacebaseRun.level.length()];
        else
            BArray.setFalse(passable);
    }

    public static PathFinder.Path findPath(Char ch, int from, int to, boolean[] pass, boolean[] visible) {

        setupPassable();
        if (ch.flying || ch.buff(Paranoid.class) != null) {
            BArray.or(pass, Level.avoid, passable);
        } else {
            System.arraycopy(pass, 0, passable, 0, SpacebaseRun.level.length());
        }

        for (Char c : Actor.chars()) {
            if (visible[c.pos]) {
                passable[c.pos] = false;
            }
        }

        return PathFinder.find(from, to, passable);

    }

    public static int findStep(Char ch, int from, int to, boolean[] pass, boolean[] visible) {

        if (level.adjacent(from, to)) {
            return Actor.findChar(to) == null && (pass[to] || Level.avoid[to]) ? to : -1;
        }

        setupPassable();
        if (ch.flying || ch.buff(Paranoid.class) != null) {
            BArray.or(pass, Level.avoid, passable);
        } else {
            System.arraycopy(pass, 0, passable, 0, SpacebaseRun.level.length());
        }

        for (Char c : Actor.chars()) {
            if (visible[c.pos]) {
                passable[c.pos] = false;
            }
        }

        return PathFinder.getStep(from, to, passable);

    }

    public static int flee(Char ch, int cur, int from, boolean[] pass, boolean[] visible) {

        setupPassable();
        if (ch.flying) {
            BArray.or(pass, Level.avoid, passable);
        } else {
            System.arraycopy(pass, 0, passable, 0, SpacebaseRun.level.length());
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
