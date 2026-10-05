package com.wafitz.pixelspacebase.sprites;

import android.util.SparseIntArray;
import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.levels.PixelDungeonLevel;

/** Visit-scoped presentation only: stored item images and actor classes remain Spacebase's. */
public final class PixelDungeonSkins {
    private PixelDungeonSkins() { }
    public static boolean active() { return SpacebaseRun.isRescueSideLevel() && SpacebaseRun.level instanceof PixelDungeonLevel; }
    public static String heroSheet(HeroClass hero) {
        if (!active()) return hero.spritesheet();
        switch (hero) {
            case COMMANDER: return Assets.PD_ART + "warrior.png";
            case DM3000: return Assets.PD_ART + "mage.png";
            case CAPTAIN: return Assets.PD_ART + "ranger.png";
            default: return Assets.PD_ART + "rogue.png";
        }
    }
    public static String itemSheet() { return active() ? Assets.PD_ITEMS : Assets.ITEMS; }
    private static final SparseIntArray ITEMS = new SparseIntArray();
    private static void map(int source, int target) { ITEMS.put(source, target); }
    static {
        map(ItemSpriteSheet.WEAPON_HOLDER, 5); map(ItemSpriteSheet.ARMOR_HOLDER, 6); map(ItemSpriteSheet.MODULE_HOLDER, 7);
        map(ItemSpriteSheet.PARTS, 14); map(ItemSpriteSheet.DEWDROP, 81);
        map(ItemSpriteSheet.REMAINS, 0); map(ItemSpriteSheet.DISCARDEDSUIT, 0);
        map(ItemSpriteSheet.CHEST, 11); map(ItemSpriteSheet.LOCKED_CHEST, 12); map(ItemSpriteSheet.CRYSTAL_CHEST, 105);
        map(ItemSpriteSheet.CLONE, 1); map(ItemSpriteSheet.ENHANCEMENTCHIP, 80); map(ItemSpriteSheet.WEIGHT, 123);
        map(ItemSpriteSheet.TORCH, 84); map(ItemSpriteSheet.BEACON, 85); map(ItemSpriteSheet.BOMB, 124);
        map(ItemSpriteSheet.IRON_KEYCARD, 9); map(ItemSpriteSheet.GOLDEN_KEYCARD, 10); map(ItemSpriteSheet.SECURITY_KEYCARD, 8);
        map(ItemSpriteSheet.MASTERY, 82); map(ItemSpriteSheet.ARMOR_KIT, 86); map(ItemSpriteSheet.ESCAPE_POD_OVERRIDE, 87);
        int[] weapons = {2, 17, 16, 5, 19, 3, 5, 5, 18, 101, 29, 17, 19, 5, 5, 5,
                20, 21, 20, 5, 19, 30, 5, 5, 21, 22, 18, 21, 21, 5, 5, 5, 21, 23, 3, 22, 5};
        for (int i = 0; i < weapons.length; i++) map(ItemSpriteSheet.SPANNER + i, weapons[i]);
        int[] missiles = {31, 106, 108, 15, 109, 110, 125};
        for (int i = 0; i < missiles.length; i++) map(ItemSpriteSheet.DART + i, missiles[i]);
        int[] armors = {24, 25, 26, 27, 28, 97, 98, 96, 99, 25, 26};
        for (int i = 0; i < armors.length; i++) map(ItemSpriteSheet.ARMOR_UNIFORM + i, armors[i]);
        int[] wands = {3, 48, 49, 50, 51, 52, 53, 54, 55, 68, 69, 70, 71};
        for (int i = 0; i < wands.length; i++) map(ItemSpriteSheet.MISSILEBLASTER + i, wands[i]);
        int[] rings = {34, 35, 37, 72, 38, 33, 39, 73, 36, 74, 75, 32};
        for (int i = 0; i < rings.length; i++) map(ItemSpriteSheet.MODULE_GARNET + i, rings[i]);
        int[] scrolls = {40, 41, 42, 43, 44, 45, 46, 47, 76, 77, 78, 79};
        for (int i = 0; i < scrolls.length; i++) map(ItemSpriteSheet.KAUNAN_UPGRADE + i, scrolls[i]);
        int[] potions = {57, 64, 60, 59, 56, 58, 66, 61, 65, 62, 67, 63};
        for (int i = 0; i < potions.length; i++) map(ItemSpriteSheet.CRIMSON_PLASMID + i, potions[i]);
        for (int i = 0; i < 3; i++) map(ItemSpriteSheet.FIRE_GRENADE + i, 124);
        for (int i = 0; i < 7; i++) map(ItemSpriteSheet.HUNTER_TRAPPER + i, 123);
        map(ItemSpriteSheet.HEALING_DEVICE, 120);
        int[] foods = {113, 114, 115, 116, 4, 4, 112, 112, 112};
        for (int i = 0; i < foods.length; i++) map(ItemSpriteSheet.MEAT + i, foods[i]);
        map(ItemSpriteSheet.SKULL, 103); map(ItemSpriteSheet.DUST, 121); map(ItemSpriteSheet.PICKAXE, 101);
        map(ItemSpriteSheet.SALVAGED_PARTS, 102); map(ItemSpriteSheet.TOKEN, 122);
        for (int i = 0; i < 6; i++) map(ItemSpriteSheet.AIRTANK + i, 83);
        map(ItemSpriteSheet.BLASTER_HOLSTER, 111);
    }
    public static int itemFrame(int spacebaseImage) {
        return active() ? ITEMS.get(spacebaseImage, 127) : spacebaseImage;
    }
}
