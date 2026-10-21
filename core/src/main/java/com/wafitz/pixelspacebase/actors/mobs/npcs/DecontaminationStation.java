package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Fire;
import com.wafitz.pixelspacebase.actors.buffs.Acid;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.DecontaminationStationSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

/** A reusable safety fixture on Command decks, saved with the other level actors. */
public class DecontaminationStation extends NPC {
    {
        spriteClass = DecontaminationStationSprite.class;
        properties.add(Property.IMMOVABLE);
    }

    @Override public int defenseSkill(Char enemy) { return 1000; }
    @Override public void damage(int damage, Object source) { }
    @Override public void add(Buff buff) { }
    @Override public boolean reset() { return true; }

    @Override public boolean interact() {
        Hero hero = SpacebaseRun.hero;
        Level level = SpacebaseRun.level;
        if (!level.adjacent(hero.pos, pos)) return false;
        Buff.detach(hero, Acid.class);
        Buff.detach(hero, Burning.class);
        Fire fire = (Fire) level.blobs.get(Fire.class);
        if (fire != null) fire.clear(hero.pos);
        // Preserve stairs, doors, vents and other special terrain beneath the user.
        if (plainFloor(level.map[hero.pos])) {
            level.makeRinsePuddle(hero.pos);
            GameScene.updateMap(hero.pos);
        }
        Sample.INSTANCE.play(Assets.SND_WATER);
        GLog.i(Messages.get(this, "rinsed"));
        hero.spend(1f);
        hero.busy();
        hero.sprite.operate(pos);
        return false;
    }

    /** Also adds the fixture to older saved Command decks without resetting their maps. */
    public static void install(Level level) {
        if (!Assets.TILES_CONTAINMENT_DECK.equals(level.tilesTex())) return;
        for (Mob mob : level.mobs) if (mob instanceof DecontaminationStation) return;
        if (!level.insideMap(level.entrance)) return;
        int[] queue = new int[level.length()];
        boolean[] seen = new boolean[level.length()];
        queue[0] = level.entrance;
        seen[level.entrance] = true;
        int read = 0, count = 1, fallback = -1;
        while (read < count) {
            int cell = queue[read++];
            boolean candidate = plainFloor(level.map[cell]) && cell != level.entrance
                    && cell != level.exit && !level.isDoorlessRoomCell(cell)
                    && !level.isPlasmaCell(cell) && level.heaps.get(cell) == null
                    && level.mines.get(cell) == null && level.vents.get(cell) == null
                    && (SpacebaseRun.hero == null || cell != SpacebaseRun.hero.pos);
            for (Mob mob : level.mobs) if (mob.pos == cell) candidate = false;
            int neighbours = 0;
            boolean wall = false;
            for (int next : neighbours(level, cell)) {
                if (next < 0) continue;
                // Installation runs immediately after this level's flag maps are built.
                if (Level.passable[next] && !Level.pit[next]) {
                    neighbours++;
                    if (!seen[next]) { seen[next] = true; queue[count++] = next; }
                }
                if (level.map[next] == Terrain.WALL || level.map[next] == Terrain.WALL_DECO) wall = true;
            }
            // Leave narrow passages clear so the fixture cannot block the route.
            if (candidate && neighbours >= 3) {
                if (fallback < 0) fallback = cell;
                if (wall) { place(level, cell); return; }
            }
        }
        if (fallback >= 0) place(level, fallback);
    }

    private static int[] neighbours(Level level, int cell) {
        int x = cell % level.width(), y = cell / level.width();
        return new int[]{x > 0 ? cell - 1 : -1, x + 1 < level.width() ? cell + 1 : -1,
                y > 0 ? cell - level.width() : -1, y + 1 < level.height() ? cell + level.width() : -1};
    }

    private static void place(Level level, int cell) {
        DecontaminationStation station = new DecontaminationStation();
        station.pos = cell;
        level.mobs.add(station);
    }

    private static boolean plainFloor(int terrain) {
        return terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO || terrain == Terrain.EMPTY_SP;
    }
}
