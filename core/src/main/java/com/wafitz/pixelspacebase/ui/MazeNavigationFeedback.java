package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.levels.DarkMazeLevel;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.watabou.noosa.Group;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;

/** Movement and wall feedback for maze navigation. */
public class MazeNavigationFeedback extends Group {
    private static final int MARKER_SIZE = 8;
    private static final int MARKER_GAP = 4;
    private final DarkMazeLevel maze;
    private final RenderedText feedback;
    private final float center, top;
    private int lastRevision = -1;
    private final ColorBlock[] movementPulse = new ColorBlock[4];
    private int lastPos = -1;
    private float pulse;

    public MazeNavigationFeedback(DarkMazeLevel maze, float center, float top) {
        this.maze = maze;
        this.center = center;
        this.top = top;
        for (int i = 0; i < movementPulse.length; i++) {
            movementPulse[i] = new ColorBlock(i < 2 ? MARKER_SIZE : 1,
                    i < 2 ? 1 : MARKER_SIZE, 0xFFFFDD70);
            add(movementPulse[i]);
        }
        feedback = PixelScene.renderText(Messages.get(maze, "move_hint"), 6);
        feedback.hardlight(0x72FFE7);
        add(feedback);
        layoutFeedback();
    }

    @Override public void update() {
        super.update();
        int cell = SpacebaseRun.hero.pos;
        if (lastPos != cell) { lastPos = cell; pulse = .35f; }
        pulse = Math.max(0, pulse - Game.elapsed);
        for (ColorBlock edge : movementPulse) {
            edge.alpha(.55f + .45f * pulse / .35f);
            edge.visible = SpacebaseRun.hero.isAlive();
        }
        if (lastRevision != maze.navigationRevision) {
            lastRevision = maze.navigationRevision;
            feedback.text(maze.navigationMessage == null
                    ? Messages.get(maze, "move_hint") : maze.navigationMessage);
            layoutFeedback();
        }
    }

    private void layoutFeedback() {
        float left = center - (MARKER_SIZE + MARKER_GAP + feedback.width()) / 2;
        feedback.x = left + MARKER_SIZE + MARKER_GAP;
        feedback.y = top;
        PixelScene.align(feedback);
        float markerTop = top + (feedback.height() - MARKER_SIZE) / 2;
        for (int i = 0; i < movementPulse.length; i++) {
            movementPulse[i].x = left + (i == 3 ? MARKER_SIZE - 1 : 0);
            movementPulse[i].y = markerTop + (i == 1 ? MARKER_SIZE - 1 : 0);
            PixelScene.align(movementPulse[i]);
        }
    }
}
