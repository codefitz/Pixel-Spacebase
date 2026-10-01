package com.wafitz.pixelspacebase.ui;

import android.graphics.Bitmap;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.utils.Bundle;

/** Persisted room decoration with artwork cached through the game's texture cache. */
public class ChangingRoomTile extends CustomTileVisual {
    public static final int FLOOR = 0;
    public static final int SHOWER = 1;
    public static final int LOCKER = 2;
    public static final int VENT = 3;

    private static final String KIND = "changingRoomTileKind";
    private static Bitmap atlas;
    private int kind;

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int kind() {
        return kind;
    }

    private static synchronized Bitmap artwork() {
        if (atlas == null) {
            atlas = Bitmap.createBitmap(ChangingRoomArtwork.pixels(),
                    ChangingRoomArtwork.WIDTH, ChangingRoomArtwork.HEIGHT, Bitmap.Config.ARGB_8888);
        }
        return atlas;
    }

    @Override
    public CustomTileVisual create() {
        texture(artwork());
        frame(kind * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
        x = tileX * TILE_SIZE;
        y = tileY * TILE_SIZE;
        name = Messages.get(this, "name_" + suffix());
        return this;
    }

    @Override
    public void update() {
        super.update();
        int cell = tileX + tileY * SpacebaseRun.level.width();
        int terrain = SpacebaseRun.level.map[cell];
        // Keep exploded or otherwise altered terrain visible beneath decorations.
        visible = kind == FLOOR ? terrain == Terrain.EMPTY_SP
                : kind == VENT ? terrain == Terrain.INACTIVE_VENT
                : terrain == Terrain.WALL || terrain == Terrain.WALL_DECO;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc_" + suffix());
    }

    private String suffix() {
        switch (kind) {
            case SHOWER: return "shower";
            case LOCKER: return "locker";
            case VENT: return "vent";
            default: return "floor";
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(KIND, kind);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        kind = bundle.getInt(KIND);
    }
}
