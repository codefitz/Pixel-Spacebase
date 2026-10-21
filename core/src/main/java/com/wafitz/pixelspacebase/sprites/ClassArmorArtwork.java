package com.wafitz.pixelspacebase.sprites;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;

/** Dedicated class-armour sheets: 21 square 64px poses at the game's 4x asset scale. */
public final class ClassArmorArtwork {
    public static final int FRAME_SIZE = 16;
    public static final int PIXEL_FRAME_SIZE = 64;
    public static final int FRAME_COUNT = 21;

    private ClassArmorArtwork() { }

    public static String sheet(HeroClass heroClass) {
        switch (heroClass) {
            case COMMANDER: return Assets.CLASS_ARMOR_COMMANDER;
            case CAPTAIN: return Assets.CLASS_ARMOR_CAPTAIN;
            case DM3000: return Assets.CLASS_ARMOR_DM3000;
            default: return Assets.CLASS_ARMOR_SHAPESHIFTER;
        }
    }
}
