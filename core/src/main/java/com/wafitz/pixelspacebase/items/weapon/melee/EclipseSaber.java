package com.wafitz.pixelspacebase.items.weapon.melee;

import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

/** Reforged light and dark blades, retaining the dark blade's surprise strikes. */
public class EclipseSaber extends DarkSaber {
    {
        tier = 5;
        image = ItemSpriteSheet.BRIGHT_SABER;
    }

    @Override public int max(int level) {
        return 30 + level * 7;
    }

    @Override public ItemSprite.Glowing glowing() {
        ItemSprite.Glowing enhancementGlow = super.glowing();
        return enhancementGlow != null ? enhancementGlow : new ItemSprite.Glowing(0x9966FF);
    }

    public static boolean canCombine(Item first, Item second) {
        return first != null && second != null
                && ((first.getClass() == BrightSaber.class && second.getClass() == DarkSaber.class)
                || (first.getClass() == DarkSaber.class && second.getClass() == BrightSaber.class));
    }

    public static EclipseSaber combine(MeleeWeapon first, MeleeWeapon second) {
        if (!canCombine(first, second)) throw new IllegalArgumentException("Requires bright and dark sabers");
        MeleeWeapon stronger = second.level() > first.level() ? second : first;
        EclipseSaber result = new EclipseSaber();
        result.level(stronger.level() + 1);
        result.enhancement = stronger.enhancement;
        result.convert = stronger.convert;
        result.identify();
        return result;
    }
}
