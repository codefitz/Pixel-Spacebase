package com.wafitz.pixelspacebase.sprites;

import java.io.DataInputStream;
import java.io.FileInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class HunterSuitArtworkTest {
    @Test
    public void newHunterArtKeepsTheHeroGridAndBothExistingItemSlots() throws Exception {
        assertEquals(162, ItemSpriteSheet.ARMOR_HUNTER);
        assertEquals(170, ItemSpriteSheet.ARMOR_DM3000_HUNTER);
        assertPng("hunter_space_suit.png", 64, 64);
        assertPng("hunter_space_suit_animations.png", 1008, 60);
        for (String hero : new String[]{"captain", "commander", "shapeshifter", "dm3000"}) {
            assertPng(hero + ".png", 1024, 512);
        }
    }

    private static void assertPng(String file, int width, int height) throws Exception {
        try (DataInputStream png = new DataInputStream(new FileInputStream("src/main/assets/" + file))) {
            assertEquals(file, 0x89504E470D0A1A0AL, png.readLong());
            assertEquals(13, png.readInt());
            assertEquals(0x49484452, png.readInt());
            assertEquals(file, width, png.readInt());
            assertEquals(file, height, png.readInt());
            assertEquals(8, png.readUnsignedByte());
            assertEquals(6, png.readUnsignedByte());
        }
    }
}
