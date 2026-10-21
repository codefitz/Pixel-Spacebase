package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.levels.DarkMazeLevel;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.RenderedText;
import java.util.ArrayList;

/** Local isometric wireframe: raised walls, floor grid and a centred hero position. */
public class HunterMazeOverlay extends Group {
    private static final int FLOOR = 0xFF286A78, WALL = 0xFF54DBC9, HERO = 0xFFFFDD70;
    private final DarkMazeLevel maze;
    private final float left, top;
    private final ArrayList<ColorBlock> lines = new ArrayList<>();
    private int used, lastPos = -1;
    private boolean wasActive;

    public HunterMazeOverlay(DarkMazeLevel level, float x, float y) {
        maze = level; left = x; top = y;
        ColorBlock backing = new ColorBlock(96, 70, 0xDD06141E);
        backing.x = x; backing.y = y; add(backing);
        RenderedText label = PixelScene.renderText(Messages.get(maze, "hunter_map"), 6);
        label.x = x + 4; label.y = y + 3; label.hardlight(0x72FFE7); add(label);
        visible = false;
    }

    @Override public void update() {
        super.update();
        visible = maze.hunterMappingActive();
        if (!visible) { wasActive = false; return; }
        int pos = SpacebaseRun.hero.pos;
        if (wasActive && pos == lastPos) return;
        wasActive = true; lastPos = pos;
        maze.scanFromHero();
        used = 0;
        int cx = pos % maze.width(), cy = pos / maze.width();
        // North and east stay in fixed orientations; geometry moves with the hero.
        for (int dy = -5; dy <= 5; dy++) for (int dx = -5; dx <= 5; dx++) {
            int x = cx + dx, y = cy + dy;
            if (x < 0 || x >= maze.width() || y < 0 || y >= maze.height()) continue;
            int cell = x + y * maze.width();
            if (!maze.hunterScanned[cell]) continue;
            float px = left + 48 + (dx - dy) * 4, py = top + 40 + (dx + dy) * 2;
            boolean wall = maze.map[cell] == Terrain.WALL;
            int color = cell == maze.exit ? HERO : wall ? WALL : FLOOR;
            diamond(px, py - (wall ? 6 : 0), color);
            if (wall) {
                line(px - 4, py - 6, px - 4, py, color);
                line(px + 4, py - 6, px + 4, py, color);
                line(px, py - 4, px, py + 2, color);
            }
        }
        diamond(left + 48, top + 39, HERO);
        for (int i = used; i < lines.size(); i++) lines.get(i).visible = false;
    }

    private void diamond(float x, float y, int color) {
        line(x, y - 2, x + 4, y, color); line(x + 4, y, x, y + 2, color);
        line(x, y + 2, x - 4, y, color); line(x - 4, y, x, y - 2, color);
    }

    private void line(float x1, float y1, float x2, float y2, int color) {
        ColorBlock line;
        if (used == lines.size()) { line = new ColorBlock(1, 1, 0xFFFFFFFF); lines.add(line); add(line); }
        else line = lines.get(used);
        used++;
        float dx = x2 - x1, dy = y2 - y1;
        line.size((float) Math.sqrt(dx * dx + dy * dy), 0.6f);
        line.x = x1; line.y = y1; line.angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        line.hardlight(color & 0xFFFFFF); line.visible = true;
    }
}
