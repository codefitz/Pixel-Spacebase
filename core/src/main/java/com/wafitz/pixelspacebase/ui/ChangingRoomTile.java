package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.levels.HabitationRingLevel;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.noosa.TextureFilm;

/** Persisted room decoration using editable frames from the Habitat tile sheet. */
public class ChangingRoomTile extends CustomTileVisual {
    public static final int FLOOR = 0;
    public static final int SHOWER = 1;
    public static final int LOCKER = 2;
    public static final int VENT = 3;

    private static final String KIND = "changingRoomTileKind";
    private int kind;

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int kind() {
        return kind;
    }

    private int artworkFrame() {
        switch (kind) {
            case SHOWER: return 67;
            case LOCKER: return 68;
            case VENT: return 69;
            default: return HabitationRingLevel.CHANGING_ROOM_FLOOR_VISUAL;
        }
    }

    @Override
    public CustomTileVisual create() {
        texture(Assets.TILES_HABITATION_RING);
        frame(new TextureFilm(texture, TILE_SIZE, TILE_SIZE).get(artworkFrame()));
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
        // Old saves may contain floor overlays; let the deck floor show through.
        visible = kind == FLOOR ? false
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
