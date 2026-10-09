package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.levels.DarkMazeLevel;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.watabou.noosa.Group;
import com.watabou.noosa.RenderedText;

/** Movement and wall feedback for maze navigation. */
public class MazeNavigationFeedback extends Group {
    private final DarkMazeLevel maze;
    private final RenderedText feedback;
    private final float center, top;
    private int lastRevision = -1;

    public MazeNavigationFeedback(DarkMazeLevel maze, float center, float top) {
        this.maze = maze;
        this.center = center;
        this.top = top;
        feedback = PixelScene.renderText(Messages.get(maze, "move_hint"), 6);
        feedback.hardlight(0x72FFE7);
        add(feedback);
        layoutFeedback();
    }

    @Override public void update() {
        super.update();
        if (lastRevision != maze.navigationRevision) {
            lastRevision = maze.navigationRevision;
            feedback.text(maze.navigationMessage == null
                    ? Messages.get(maze, "move_hint") : maze.navigationMessage);
            layoutFeedback();
        }
    }

    private void layoutFeedback() {
        feedback.x = center - feedback.width() / 2;
        feedback.y = top;
        PixelScene.align(feedback);
    }
}
