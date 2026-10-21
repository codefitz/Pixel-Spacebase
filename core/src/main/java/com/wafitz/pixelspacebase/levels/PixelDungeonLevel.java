package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.mobs.Bestiary;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer;
import com.wafitz.pixelspacebase.actors.buffs.YRescueJourney;
import com.wafitz.pixelspacebase.items.Parts;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.plasmids.HealingPlasmid;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** A sewer first-floor homage, using Spacebase actors/items beneath the original PD artwork. */
public class PixelDungeonLevel extends Level {
    private int[][] rooms;
    { color1 = 0x48763c; color2 = 0x59994a; }
    @Override public String tilesTex() { return Assets.PD_TILES; }
    @Override public String waterTex() { return Assets.PD_WATER; }
    @Override protected void setupSize() { width = height = 32; length = width * height; }

    @Override public void create() {
        setupSize();
        Random.seed(SpacebaseRun.seed ^ ((long) SpacebaseRun.hero.buff(YRescueJourney.class).journeyId * 15485863));
        try {
            PathFinder.setMapSize(width, height);
            map = new int[length]; visited = new boolean[length]; mapped = new boolean[length];
            vacuum = new boolean[length]; pressurized = new boolean[length]; Arrays.fill(pressurized, true);
            mobs = new HashSet<>(); heaps = new SparseArray<>(); blobs = new HashMap<>();
            mines = new SparseArray<>(); vents = new SparseArray<>(); customTiles = new HashSet<>();
            feeling = Feeling.NONE; floorBreakerOn = true; litViewDistance = viewDistance = 8;
            // Do not generate campaign upgrades, consume bones, or advance the station's depth.
            build(); decorate(); buildFlagMaps(); cleanWalls(); createMobs(); createItems();
        } finally { Random.seed(); }
    }

    private int centre(int room) { return 5 + (room % 3) * 10 + (5 + (room / 3) * 10) * width; }
    @Override protected boolean build() {
        Arrays.fill(map, Terrain.WALL);
        rooms = new int[9][4];
        for (int i = 0; i < 9; i++) {
            int cx = centre(i) % width, cy = centre(i) / width;
            int w = Random.IntRange(5, 8), h = Random.IntRange(5, 8);
            int left = cx - w / 2, top = cy - h / 2;
            rooms[i] = new int[]{left, top, left + w - 1, top + h - 1};
            for (int y = top + 1; y < top + h - 1; y++) for (int x = left + 1; x < left + w - 1; x++)
                map[x + y * width] = Terrain.EMPTY;
        }
        // A spanning tree connects every room, with two extra corridors for recognisable loops.
        boolean[] linked = new boolean[9]; linked[0] = true;
        ArrayList<Integer> stack = new ArrayList<>(); stack.add(0);
        while (!stack.isEmpty()) {
            int room = stack.get(stack.size() - 1);
            ArrayList<Integer> choices = new ArrayList<>();
            for (int next : new int[]{room % 3 > 0 ? room - 1 : -1, room % 3 < 2 ? room + 1 : -1,
                    room > 2 ? room - 3 : -1, room < 6 ? room + 3 : -1}) {
                if (next >= 0 && !linked[next]) choices.add(next);
            }
            if (choices.isEmpty()) stack.remove(stack.size() - 1);
            else {
                int next = Random.element(choices); connect(room, next);
                linked[next] = true; stack.add(next);
            }
        }
        connect(0, 1); connect(7, 8);
        entrance = centre(0); exit = centre(8);
        map[entrance] = Terrain.ENTRANCE; map[exit] = Terrain.EXIT;
        return true;
    }

    private void connect(int from, int to) {
        int start = centre(from), end = centre(to), step = start / width == end / width
                ? (start < end ? 1 : -1) : (start < end ? width : -width);
        for (int cell = start; cell != end; cell += step) {
            if (map[cell] != Terrain.WALL) continue;
            int x = cell % width, y = cell / width;
            boolean threshold = false;
            for (int[] room : rooms) {
                if (((x == room[0] || x == room[2]) && y > room[1] && y < room[3])
                        || ((y == room[1] || y == room[3]) && x > room[0] && x < room[2])) threshold = true;
            }
            map[cell] = threshold ? Terrain.DOOR : Terrain.EMPTY;
        }
    }

    @Override protected void decorate() {
        for (int cell = width + 1; cell < length - width - 1; cell++) {
            if (map[cell] != Terrain.EMPTY || cell == rescueCell()) continue;
            int roll = Random.Int(12);
            if (roll == 0) map[cell] = Terrain.WATER;
            else if (roll < 4) map[cell] = Terrain.LIGHTEDVENT; // Original grass appearance, normal passable terrain.
            else if (roll == 4) map[cell] = Terrain.EMPTY_DECO;
        }
        for (int cell = width + 1; cell < length - width - 1; cell++) {
            if (map[cell] == Terrain.WALL && map[cell + width] == Terrain.WATER && Random.Int(2) == 0)
                map[cell] = Terrain.WALL_DECO;
        }
        map[rescueCell()] = Terrain.EMPTY;
    }

    public int rescueCell() { return exit - 1; }
    private int freeCell() {
        ArrayList<Integer> candidates = new ArrayList<>();
        for (int cell = 0; cell < length; cell++) {
            if (!passable[cell] || cell == entrance || cell == exit || cell == rescueCell()
                    || distance(cell, entrance) < 5 || map[cell] == Terrain.DOOR || heaps.get(cell) != null) continue;
            boolean occupied = false;
            for (Mob mob : mobs) if (mob.pos == cell) { occupied = true; break; }
            if (!occupied) candidates.add(cell);
        }
        return candidates.isEmpty() ? -1 : Random.element(candidates);
    }
    @Override protected void createMobs() {
        if (!mobs.isEmpty()) return;
        for (int i = 0; i < 6; i++) {
            Mob mob = Bestiary.rescueDungeonMob(i); // First-deck combat; reskinned as rats/gnolls.
            int cell = freeCell();
            if (mob != null && cell >= 0) { mob.pos = cell; mobs.add(mob); }
        }
        YRescuer y = new YRescuer(); y.pos = rescueCell(); mobs.add(y);
    }
    @Override protected void createItems() {
        for (com.wafitz.pixelspacebase.items.Item item : new com.wafitz.pixelspacebase.items.Item[]{
                new Food(), new HealingPlasmid(), new Parts(Random.IntRange(15, 40))}) {
            int cell = freeCell(); if (cell >= 0) drop(item, cell);
        }
    }
    @Override public Actor respawner() { return null; }
    @Override public int randomRespawnCell() {
        int cell = freeCell(); return cell >= 0 ? cell : entrance;
    }

    public int tileVisual(int cell, int terrain) {
        if (terrain == Terrain.WATER) {
            int visual = 48;
            int[] offsets = {-width, 1, width, -1};
            for (int i = 0; i < offsets.length; i++) {
                int next = cell + offsets[i];
                if (next < 0 || next >= length || map[next] == Terrain.WATER || solid[next]) visual += 1 << i;
            }
            return visual;
        }
        switch (terrain) {
            case Terrain.LIGHTEDVENT: return 2;
            case Terrain.OFFVENT: return 15;
            case Terrain.EMPTY_DECO: return 24;
            case Terrain.LOCKED_EXIT: return 25;
            case Terrain.UNLOCKED_EXIT: return 26;
            case Terrain.SIGN: return 29;
            case Terrain.WELL: case Terrain.HEALING_TANK: return 34;
            case Terrain.STATUE: return 35;
            case Terrain.STATUE_SP: return 36;
            case Terrain.BOOKSHELF: return 41;
            case Terrain.CRAFTING: return 42;
            case Terrain.SECRET_DOOR: return 4;
            case Terrain.HIDDEN_VENT: return 1;
            case Terrain.VENT: return 17;
            case Terrain.INACTIVE_VENT: case Terrain.SPENT_MINE: return 23;
            case Terrain.TRAMPLED_OFFVENT: return 2;
            case Terrain.STABILIZED_PLASMA: return 9;
            case Terrain.BREAKER: return 11;
            default: return terrain >= 0 && terrain <= 14 ? terrain : 1;
        }
    }
    @Override public String tileName(int terrain) {
        String key = terrain == Terrain.WALL || terrain == Terrain.WALL_DECO ? "wall_name"
                : terrain == Terrain.WATER ? "water_name" : terrain == Terrain.LIGHTEDVENT ? "grass_name"
                : terrain == Terrain.ENTRANCE ? "entrance_name" : terrain == Terrain.EXIT ? "exit_name" : "floor_name";
        return Messages.get(this, key);
    }
    @Override public String tileDesc(int terrain) { return Messages.get(this, "terrain_desc"); }
}
