package com.wafitz.pixelspacebase.sprites;

import org.junit.Test;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import static org.junit.Assert.assertEquals;

public class EnemySuitArtworkTest {
    @Test
    public void enemySuitsUseAnExactHeroFrameGridAndRgbaTransparency() throws Exception {
        for (String asset : new String[]{"outer_colony_psion.png", "ruptured_crew_suit.png"}) {
            try (DataInputStream png = new DataInputStream(
                    new FileInputStream(new File("src/main/assets", asset)))) {
                assertEquals(asset, 0x89504E470D0A1A0AL, png.readLong());
                assertEquals(13, png.readInt());
                assertEquals(0x49484452, png.readInt()); // IHDR
                assertEquals(asset, 21 * 12 * 4, png.readInt());
                assertEquals(asset, 15 * 4, png.readInt());
                assertEquals(8, png.readUnsignedByte());
                assertEquals(6, png.readUnsignedByte()); // RGBA, not opaque RGB
            }
        }
    }
}
