package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.actors.mobs.npcs.Leonard;
import com.wafitz.pixelspacebase.items.quest.DroneParts;
import com.wafitz.pixelspacebase.items.quest.ScrewDriver;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChiefEngineerQuestItemTest {

    @Test
    public void questUsesEngineeringToolAndDroneComponents() {
        assertEquals(ItemSpriteSheet.WRENCH, ScrewDriver.IMAGE);
        assertEquals(ItemSpriteSheet.DEADDRONECONT, DroneParts.IMAGE);
        assertEquals(4, Leonard.Quest.DRONE_PARTS_REQUIRED);
    }
}
