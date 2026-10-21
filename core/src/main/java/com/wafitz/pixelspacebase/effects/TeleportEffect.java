package com.wafitz.pixelspacebase.effects;

import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.ui.TeleporterPads;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;

/** A short beam and rising scan band, independent of combat turns. */
public class TeleportEffect extends Group {
    private static final float DURATION = .45f;
    private final Hero hero;
    private final boolean departing;
    private final Callback callback;
    private final Image ring;
    private final ColorBlock[] shafts = new ColorBlock[5];
    private final ColorBlock scan;
    private final float originalAlpha;
    private float time;

    public TeleportEffect(Hero hero, int color, boolean departing, Callback callback) {
        this.hero = hero;
        this.departing = departing;
        this.callback = callback;
        originalAlpha = hero.sprite.am;
        ring = TeleporterPads.icon(color == TeleporterPads.ORANGE ? Terrain.ENTRANCE : Terrain.EXIT);
        add(ring);
        for (int i = 0; i < shafts.length; i++) {
            shafts[i] = new ColorBlock(.6f, 20, 0xFF000000 | color);
            add(shafts[i]);
        }
        scan = new ColorBlock(14, 1, 0xFF000000 | color);
        add(scan);
        updateBeam(0);
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;
        float progress = Math.min(1, time / DURATION);
        updateBeam(progress);
        if (time >= DURATION) {
            if (!departing && hero.invisible == 0) hero.sprite.alpha(originalAlpha);
            killAndErase();
            if (callback != null) callback.call();
        }
    }

    private void updateBeam(float progress) {
        float center = hero.sprite.x + hero.sprite.width() / 2;
        float top = hero.sprite.y - 2;
        float height = Math.max(18, hero.sprite.height() + 4);
        float strength = (float) Math.sin(Math.PI * progress);
        ring.x = center - SpacebaseTilemap.SIZE / 2f;
        ring.y = hero.sprite.y + hero.sprite.height() - SpacebaseTilemap.SIZE * .7f;
        ring.alpha(.7f * strength);
        for (int i = 0; i < shafts.length; i++) {
            shafts[i].x = center - 6 + i * 3;
            shafts[i].y = top;
            shafts[i].size(.6f, height);
            shafts[i].alpha((.25f + .15f * (i % 2)) * strength);
        }
        scan.x = center - 7;
        scan.y = top + height * (departing ? 1 - progress : progress);
        scan.alpha(strength);
        if (hero.invisible == 0) hero.sprite.alpha(originalAlpha * (departing ? 1 - progress : progress));
    }
}
