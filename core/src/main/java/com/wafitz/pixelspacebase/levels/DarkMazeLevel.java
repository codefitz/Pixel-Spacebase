package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.buffs.Camoflaged;
import com.wafitz.pixelspacebase.actors.buffs.Light;
import com.wafitz.pixelspacebase.actors.buffs.YRescueJourney;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.equippablemodules.TimeFolder;
import com.wafitz.pixelspacebase.mechanics.ShadowCaster;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** An unlit, separately saved maze. Only torch sight and the Hunter hologram reveal geometry. */
public class DarkMazeLevel extends Level {
    public static final int SCAN_RADIUS = 4;
    public boolean[] hunterScanned;
    public volatile boolean exitReached;

    @Override public String tilesTex() { return Assets.TILES_MAINTENANCE; }
    @Override public String waterTex() { return Assets.WATER_MAINTENANCE; }
    @Override protected void setupSize() { width = 25; height = 19; length = width * height; }

    @Override public void create() {
        // Avoid limited station drops, bones, and campaign progression.
        setupSize();
        Random.seed(SpacebaseRun.seed ^ ((long) SpacebaseRun.hero.buff(YRescueJourney.class).journeyId * 104729));
        try {
            PathFinder.setMapSize(width, height);
            map = new int[length]; visited = new boolean[length]; mapped = new boolean[length];
            hunterScanned = new boolean[length]; vacuum = new boolean[length]; pressurized = new boolean[length];
            Arrays.fill(pressurized, true);
            mobs = new HashSet<>(); heaps = new SparseArray<>(); blobs = new HashMap<>();
            mines = new SparseArray<>(); vents = new SparseArray<>(); customTiles = new HashSet<>();
            feeling = Feeling.NONE; floorBreakerOn = false; viewDistance = litViewDistance = 0;
            build(); buildFlagMaps(); cleanWalls();
        } finally { Random.seed(); }
    }

    @Override protected boolean build() {
        Arrays.fill(map, Terrain.WALL);
        entrance = 1 + width;
        map[entrance] = Terrain.EMPTY;
        ArrayList<Integer> stack = new ArrayList<>(); stack.add(entrance);
        while (!stack.isEmpty()) {
            int cell = stack.get(stack.size() - 1), x = cell % width, y = cell / width;
            ArrayList<Integer> choices = new ArrayList<>();
            for (int next : new int[]{x > 2 ? cell - 2 : -1, x < width - 3 ? cell + 2 : -1,
                    y > 2 ? cell - 2 * width : -1, y < height - 3 ? cell + 2 * width : -1}) {
                if (next >= 0 && map[next] == Terrain.WALL) choices.add(next);
            }
            if (choices.isEmpty()) stack.remove(stack.size() - 1);
            else {
                int next = Random.element(choices);
                map[(cell + next) / 2] = map[next] = Terrain.EMPTY;
                stack.add(next);
            }
        }
        // The last cell of a breadth-first traversal is a reachable, distant dead end.
        boolean[] seen = new boolean[length]; seen[entrance] = true;
        ArrayList<Integer> queue = new ArrayList<>(); queue.add(entrance);
        for (int i = 0; i < queue.size(); i++) {
            int cell = queue.get(i); exit = cell;
            for (int next : new int[]{cell - 1, cell + 1, cell - width, cell + width}) {
                if (next >= 0 && next < length && !seen[next] && map[next] == Terrain.EMPTY) {
                    seen[next] = true; queue.add(next);
                }
            }
        }
        map[exit] = Terrain.EXIT;
        return true;
    }

    public boolean canStep(int from, int to) {
        return to >= 0 && to < length && Math.abs(from % width - to % width)
                + Math.abs(from / width - to / width) == 1;
    }

    public boolean hunterMappingActive() {
        Armor armor = SpacebaseRun.hero == null ? null : SpacebaseRun.hero.belongings.armor;
        return armor != null && armor.hasHunterTracking();
    }

    public void scanFromHero() {
        if (!hunterMappingActive()) return;
        if (hunterScanned == null || hunterScanned.length != length) hunterScanned = new boolean[length];
        int cx = SpacebaseRun.hero.pos % width, cy = SpacebaseRun.hero.pos / width;
        for (int y = Math.max(0, cy - SCAN_RADIUS); y <= Math.min(height - 1, cy + SCAN_RADIUS); y++) {
            for (int x = Math.max(0, cx - SCAN_RADIUS); x <= Math.min(width - 1, cx + SCAN_RADIUS); x++) {
                hunterScanned[x + y * width] = true;
            }
        }
    }

    @Override public void updateFieldOfView(Char ch, boolean[] sight) {
        Arrays.fill(sight, false);
        // Deliberately bypass room lighting, innate robot vision, awareness and mind vision.
        if (ch.buff(Light.class) != null && ch.buff(Blindness.class) == null
                && ch.buff(Camoflaged.class) == null && ch.buff(TimeFolder.timeStasis.class) == null && ch.isAlive()) {
            ShadowCaster.castShadow(ch.pos % width, ch.pos / width, sight, Light.DISTANCE);
        }
        if (ch == SpacebaseRun.hero) {
            SpacebaseRun.hero.mindVisionEnemies.clear();
            Arrays.fill(visited, false); Arrays.fill(mapped, false);
            System.arraycopy(sight, 0, visited, 0, length);
            scanFromHero();
        }
    }

    public void checkExit(int cell) { if (cell == exit) exitReached = true; }
    @Override public void press(int cell, Char ch) {
        super.press(cell, ch);
        if (ch == SpacebaseRun.hero) checkExit(cell);
    }
    @Override public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("hunterScanned", hunterScanned); bundle.put("mazeExitReached", exitReached);
    }
    @Override public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        hunterScanned = bundle.contains("hunterScanned") ? bundle.getBooleanArray("hunterScanned") : new boolean[length];
        exitReached = bundle.getBoolean("mazeExitReached");
        floorBreakerOn = false; viewDistance = litViewDistance = 0;
        Arrays.fill(visited, false); Arrays.fill(mapped, false);
    }
    @Override public String tileName(int tile) {
        return Messages.get(this, tile == Terrain.EXIT ? "exit_name" : tile == Terrain.WALL ? "wall_name" : "floor_name");
    }
    @Override public String tileDesc(int tile) {
        return Messages.get(this, tile == Terrain.EXIT ? "exit_desc" : "terrain_desc");
    }
    @Override protected void decorate() { }
    @Override protected void createItems() { }
    @Override protected void createMobs() { }
    @Override public Actor respawner() { return null; }
    @Override public int randomRespawnCell() { return entrance; }
}
