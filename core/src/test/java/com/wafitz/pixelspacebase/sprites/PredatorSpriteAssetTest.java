package com.wafitz.pixelspacebase.sprites;

import com.wafitz.pixelspacebase.Assets;
import java.io.DataInputStream;
import java.io.FileInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class PredatorSpriteAssetTest {
    @Test
    public void predatorHasAnExactTransparentFourByThreeAnimationGrid() throws Exception {
        assertNotEquals(Assets.SCORPIO, Assets.PREDATOR);
        try (DataInputStream png = new DataInputStream(new FileInputStream(
                "src/main/assets/" + Assets.PREDATOR))) {
            assertEquals(0x89504E470D0A1A0AL, png.readLong());
            assertEquals(13, png.readInt());
            assertEquals(0x49484452, png.readInt());
            assertEquals(4 * 18 * 4, png.readInt());
            assertEquals(3 * 17 * 4, png.readInt());
            assertEquals(8, png.readUnsignedByte());
            assertEquals(6, png.readUnsignedByte());
        }
    }
}
