package com.wafitz.pixelspacebase.items.quest;

import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class DroneParts extends Item {

    public static final int IMAGE = ItemSpriteSheet.DEADDRONECONT;

    {
        image = IMAGE;
        stackable = true;
        unique = true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}
