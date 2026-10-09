package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.MaintenanceLevel;
import com.wafitz.pixelspacebase.levels.MaintenanceBossLevel;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Shared transparent pad overlays; terrain and saved floor connections stay intact. */
public class TeleporterPads extends Group {
    public static final int ORANGE = 0xFF9D36;
    public static final int BLUE = 0x38BEFF;

    private final Level level;
    private final HashMap<Integer, Pad> pads = new HashMap<>();

    public TeleporterPads(Level level) {
        this.level = level;
        refresh();
    }

    public static boolean enabled(Level level) {
        return level != null && !(level instanceof MaintenanceLevel)
                && !(level instanceof MaintenanceBossLevel);
    }

    public static boolean isPad(Level level, int terrain) {
        return enabled(level) && (terrain == Terrain.ENTRANCE || terrain == Terrain.EXIT
                || terrain == Terrain.LOCKED_EXIT || terrain == Terrain.UNLOCKED_EXIT);
    }

    public static int color(int terrain) {
        return terrain == Terrain.ENTRANCE ? ORANGE : BLUE;
    }

    public static Image icon(int terrain) {
        Image image = new Image(Assets.TELEPORT_PADS);
        TextureFilm film = new TextureFilm(image.texture, SpacebaseTilemap.SIZE, SpacebaseTilemap.SIZE);
        image.frame(film.get(terrain == Terrain.ENTRANCE ? 0 : 1));
        if (terrain == Terrain.LOCKED_EXIT) image.alpha(.35f);
        return image;
    }

    /** Also catches boss pads that appear or are restored during an encounter. */
    public synchronized void refresh() {
        Iterator<Map.Entry<Integer, Pad>> iterator = pads.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Pad> entry = iterator.next();
            if (!isPad(level, level.map[entry.getKey()])) {
                entry.getValue().killAndErase();
                iterator.remove();
            }
        }
        if (!enabled(level)) return;
        for (int cell = 0; cell < level.length(); cell++) {
            if (isPad(level, level.map[cell]) && !pads.containsKey(cell)) {
                Pad pad = new Pad(cell);
                pads.put(cell, pad);
                add(pad);
            }
        }
    }

    private class Pad extends Image {
        private final int cell;
        private int terrain = Integer.MIN_VALUE;
        private final TextureFilm film;
        private float time;

        Pad(int cell) {
            super(Assets.TELEPORT_PADS);
            this.cell = cell;
            film = new TextureFilm(texture, SpacebaseTilemap.SIZE, SpacebaseTilemap.SIZE);
            x = cell % level.width() * SpacebaseTilemap.SIZE;
            y = cell / level.width() * SpacebaseTilemap.SIZE;
            updatePad();
        }

        @Override
        public void update() {
            super.update();
            time += Game.elapsed;
            updatePad();
        }

        private void updatePad() {
            if (terrain != level.map[cell]) {
                terrain = level.map[cell];
                frame(film.get(terrain == Terrain.ENTRANCE ? 0 : 1));
            }
            visible = isPad(level, terrain) && Level.discoverable[cell];
            alpha(terrain == Terrain.LOCKED_EXIT ? .35f : .88f + .12f * (float) Math.sin(time * 2.5f));
        }
    }
}
