package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.mobs.npcs.AlienResident;
import com.wafitz.pixelspacebase.actors.mobs.npcs.AlienTrader;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** A separately saved, breathable colony visit with no campaign drops or enemy respawns. */
public class AlienPlanetLevel extends Level {
    { color1 = 0x58356f; color2 = 0x27b7b4; }

    @Override public String tilesTex() { return Assets.ALIEN_COLONY_TILES; }
    @Override public String waterTex() { return Assets.WATER_ENGINEERING; }
    @Override protected void setupSize() { width = 28; height = 22; length = width * height; }

    @Override public void create() {
        // Do not call Level.create(): a detour must not consume the station's limited drops.
        setupSize();
        Random.seed(SpacebaseRun.seed ^ ((long) SpacebaseRun.hero.buff(
                com.wafitz.pixelspacebase.actors.buffs.YRescueJourney.class).journeyId * 7919));
        try {
            PathFinder.setMapSize(width, height);
            map = new int[length];
            visited = new boolean[length];
            mapped = new boolean[length];
            vacuum = new boolean[length];
            pressurized = new boolean[length];
            Arrays.fill(pressurized, true);
            mobs = new HashSet<>(); heaps = new SparseArray<>(); blobs = new HashMap<>();
            mines = new SparseArray<>(); vents = new SparseArray<>(); customTiles = new HashSet<>();
            feeling = Feeling.NONE;
            floorBreakerOn = true;
            litViewDistance = viewDistance;
            build();
            buildFlagMaps();
            cleanWalls();
            createMobs();
        } finally { Random.seed(); }
    }

    @Override protected boolean build() {
        Arrays.fill(map, Terrain.WALL);
        for (int y = 2; y < height - 2; y++) for (int x = 2; x < width - 2; x++) {
            map[x + y * width] = Random.Int(5) == 0 ? Terrain.EMPTY_DECO : Terrain.EMPTY;
        }
        // Isolated outcrops stay away from the clear east-west route and all NPCs.
        for (int x : new int[]{5, 11, 20}) for (int y : new int[]{4, 17}) {
            map[x + y * width] = map[x + 1 + y * width] = Terrain.WALL;
            map[x + (y + 1) * width] = map[x + 1 + (y + 1) * width] = Terrain.WALL;
        }
        entrance = 3 + (height / 2) * width;
        exit = entrance; // No station stairs: the beacon is decorative, and Y is the way home.
        map[entrance] = Terrain.EMPTY_SP;
        for (int x = 4; x <= 24; x++) map[x + (height / 2) * width] = Terrain.EMPTY;
        return true;
    }

    public int rescueCell() { return 24 + (height / 2) * width; }
    public int tileVisual(int cell, int terrain) {
        if (terrain == Terrain.WALL || terrain == Terrain.WALL_DECO) return 1;
        if (cell == entrance) return 3;
        return terrain == Terrain.EMPTY_DECO ? 2 : 0;
    }
    @Override public String tileName(int tile) {
        return Messages.get(this, tile == Terrain.WALL ? "rock_name" : "ground_name");
    }
    @Override public String tileDesc(int tile) {
        return Messages.get(this, tile == Terrain.WALL ? "rock_desc" : "ground_desc");
    }
    @Override protected void decorate() { }
    @Override protected void createItems() { }
    @Override protected void createMobs() {
        if (!mobs.isEmpty()) return;
        AlienTrader trader = new AlienTrader(); trader.pos = 13 + 8 * width; trader.seedStock(); mobs.add(trader);
        for (int cell : new int[]{8 + 13 * width, 18 + 14 * width}) {
            AlienResident resident = new AlienResident(); resident.pos = cell; mobs.add(resident);
        }
        YRescuer y = new YRescuer(); y.pos = rescueCell(); mobs.add(y);
    }
    @Override public Actor respawner() { return null; }
    @Override public int randomRespawnCell() { return entrance; }
}
