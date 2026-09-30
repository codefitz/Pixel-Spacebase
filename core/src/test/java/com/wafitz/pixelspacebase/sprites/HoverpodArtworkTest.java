package com.wafitz.pixelspacebase.sprites;

import java.io.DataInputStream;
import java.io.FileInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class HoverpodArtworkTest {
    @Test
    public void newArtworkKeepsTheExistingInventorySlotAndNativeAnimationGrid() throws Exception {
        assertEquals(163, ItemSpriteSheet.HOVERPOD);
        check("hoverpod.png", 64, 64);
        check("hoverpod_animations.png", 1008, 60);
        for (String hero : new String[]{"captain", "commander", "shapeshifter", "dm3000"}) {
            check(hero + ".png", 1024, 512);
        }
    }

    private void check(String file, int width, int height) throws Exception {
        try (DataInputStream png = new DataInputStream(new FileInputStream("src/main/assets/" + file))) {
            assertEquals(0x89504E470D0A1A0AL, png.readLong());
            assertEquals(13, png.readInt());
            assertEquals(0x49484452, png.readInt());
            assertEquals(width, png.readInt());
            assertEquals(height, png.readInt());
            assertEquals(8, png.readUnsignedByte());
            assertEquals(6, png.readUnsignedByte());
        }
    }
}
