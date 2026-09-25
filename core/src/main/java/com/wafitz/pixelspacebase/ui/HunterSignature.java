package com.wafitz.pixelspacebase.ui;

import android.graphics.RectF;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.items.armor.HunterSpaceSuit;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.watabou.noosa.Image;

/** A character silhouette drawn above fog without changing the hero's sight. */
public class HunterSignature extends Image {

    private static final int SIGNATURE_RED = 0xFF3030;

    private final Mob mob;
    private final CharSprite source;

    public HunterSignature(Mob mob) {
        super(mob.sprite);
        this.mob = mob;
        source = mob.sprite;
        color(SIGNATURE_RED);
        visible = false;
    }

    @Override
    public void update() {
        super.update();

        if (!mob.isAlive() || !source.alive) {
            killAndErase();
            return;
        }

        visible = HunterSpaceSuit.signatureScannerActive() && !SpacebaseRun.visible[mob.pos];
        if (!visible) return;

        RectF sourceFrame = source.frame();
        if (texture != source.texture || flipHorizontal != source.flipHorizontal
                || flipVertical != source.flipVertical
                || frame.left != sourceFrame.left || frame.top != sourceFrame.top
                || frame.right != sourceFrame.right || frame.bottom != sourceFrame.bottom) {
            flipHorizontal = source.flipHorizontal;
            flipVertical = source.flipVertical;
            copy(source);
        }

        x = source.x;
        y = source.y;
        scale.set(source.scale);
        origin.set(source.origin);
        angle = source.angle;
    }
}
