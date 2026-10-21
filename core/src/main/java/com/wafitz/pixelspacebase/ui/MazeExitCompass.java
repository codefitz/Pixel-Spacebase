package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.levels.DarkMazeLevel;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.watabou.noosa.Group;
import com.watabou.noosa.RenderedText;

/** Direction to the exit, without distances, corridors or a computed route. */
public class MazeExitCompass extends Group {
    private final DarkMazeLevel maze;
    private final RenderedText label;
    private int lastPos = -1;

    public MazeExitCompass(DarkMazeLevel level, float x, float y) {
        maze = level;
        label = PixelScene.renderText("", 6);
        label.x = x; label.y = y; label.hardlight(0x72FFE7); add(label);
    }

    @Override public void update() {
        super.update();
        int pos = SpacebaseRun.hero.pos;
        if (lastPos == pos) return;
        lastPos = pos;
        int dx = maze.exit % maze.width() - pos % maze.width();
        int dy = maze.exit / maze.width() - pos / maze.width();
        String direction = (dy < 0 ? "N" : dy > 0 ? "S" : "") + (dx < 0 ? "W" : dx > 0 ? "E" : "");
        label.text(Messages.get(maze, "exit_direction", direction));
    }
}
