package com.wafitz.pixelspacebase.effects;

import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.ui.TeleporterPads;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;

/** Coloured signal wash and moving photon streaks for pad travel. */
public class TeleportTransition extends Group {
    public enum Stage { DEPARTURE, LOADING, ARRIVAL }
    private final Stage stage;
    private final Callback callback;
    private final ColorBlock background;
    private final ColorBlock[] streaks = new ColorBlock[18];
    private final Image[] rings = new Image[3];
    private final float screenWidth;
    private final float screenHeight;
    private float time;

    public TeleportTransition(Camera camera, int color, Stage stage, Callback callback) {
        this.camera = camera;
        this.stage = stage;
        this.callback = callback;
        screenWidth = camera.width;
        screenHeight = camera.height;
        background = new ColorBlock(screenWidth, screenHeight,
                color == TeleporterPads.ORANGE ? 0xFF492E16 : 0xFF123C50);
        add(background);
        for (int i = 0; i < rings.length; i++) {
            rings[i] = TeleporterPads.icon(color == TeleporterPads.ORANGE ? Terrain.ENTRANCE : Terrain.EXIT);
            rings[i].origin.set(rings[i].width() / 2, rings[i].height() / 2);
            rings[i].x = screenWidth / 2 - rings[i].width() / 2;
            rings[i].y = screenHeight / 2 - rings[i].height() / 2;
            add(rings[i]);
        }
        for (int i = 0; i < streaks.length; i++) {
            streaks[i] = new ColorBlock(i % 3 == 0 ? 2 : 1, 12 + i % 5 * 8, 0xFF000000 | color);
            streaks[i].x = (i + .5f) * screenWidth / streaks.length;
            add(streaks[i]);
        }
        updateSignal();
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;
        updateSignal();
        float duration = stage == Stage.DEPARTURE ? .3f : .45f;
        if (stage != Stage.LOADING && time >= duration) {
            // Leave the outgoing wash in place until the next scene is created.
            if (stage == Stage.ARRIVAL) killAndErase();
            active = false;
            if (callback != null) callback.call();
        }
    }

    private void updateSignal() {
        float opacity = stage == Stage.LOADING ? 1
                : stage == Stage.DEPARTURE ? Math.min(1, time / .3f)
                : Math.max(0, 1 - time / .45f);
        background.alpha(opacity);
        for (int i = 0; i < rings.length; i++) {
            float cycle = (time * .65f + i / 3f) % 1;
            rings[i].scale.set(1 + cycle * Math.max(screenWidth, screenHeight) / 16f);
            rings[i].alpha(opacity * (1 - cycle) * .2f);
        }
        for (int i = 0; i < streaks.length; i++) {
            float distance = screenHeight + streaks[i].height();
            streaks[i].y = ((time * (80 + i % 4 * 24) + i * 37) % distance) - streaks[i].height();
            streaks[i].alpha(opacity * (.2f + .15f * (i % 3)));
        }
    }
}
