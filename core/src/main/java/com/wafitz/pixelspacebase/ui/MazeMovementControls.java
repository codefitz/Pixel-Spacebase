package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.levels.DarkMazeLevel;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.watabou.noosa.Group;
import com.watabou.noosa.RenderedText;

/** Explicit single-step controls, without indicating which directions are open. */
public class MazeMovementControls extends Group {
    private final DarkMazeLevel maze;
    private final RedButton[] buttons = new RedButton[4];
    private final RenderedText feedback;
    private final float center, top;
    private final boolean compact;
    private int lastRevision = -1;

    public MazeMovementControls(DarkMazeLevel maze, float center, float top, boolean compact) {
        this.maze = maze;
        this.center = center;
        this.top = top;
        this.compact = compact;
        if (compact) {
            buttons[0] = button("N", 0, -1, center - 25, top);
            buttons[1] = button("W", -1, 0, center - 51, top);
            buttons[2] = button("E", 1, 0, center + 27, top);
            buttons[3] = button("S", 0, 1, center + 1, top);
        } else {
            buttons[0] = button("N", 0, -1, center - 12, top);
            buttons[1] = button("W", -1, 0, center - 38, top + 24);
            buttons[2] = button("E", 1, 0, center + 14, top + 24);
            buttons[3] = button("S", 0, 1, center - 12, top + 48);
        }
        feedback = PixelScene.renderText(Messages.get(maze, "move_hint"), 6);
        feedback.hardlight(0x72FFE7);
        add(feedback);
        layoutFeedback();
    }

    private RedButton button(String label, final int dx, final int dy, float x, float y) {
        RedButton button = new RedButton(label, 8) {
            @Override protected void onClick() { GameScene.stepInMaze(maze, dx, dy); }
        };
        button.setRect(x, y, 24, 22);
        button.textColor(0x72FFE7);
        add(button);
        return button;
    }

    @Override public void update() {
        super.update();
        boolean ready = GameScene.mazeMovementReady(maze);
        for (RedButton button : buttons) button.enable(ready);
        if (lastRevision != maze.navigationRevision) {
            lastRevision = maze.navigationRevision;
            feedback.text(maze.navigationMessage == null
                    ? Messages.get(maze, "move_hint") : maze.navigationMessage);
            layoutFeedback();
        }
    }

    private void layoutFeedback() {
        feedback.x = center - feedback.width() / 2;
        feedback.y = top + (compact ? 26 : 74);
        PixelScene.align(feedback);
    }
}
