package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import java.io.DataInputStream;
import java.io.FileInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class HardLightEmitterIconTest {
    @Test
    public void newIconKeepsTheExistingQuestItemSlot() {
        assertEquals(ItemSpriteSheet.TOKEN, ItemSpriteSheet.HARD_LIGHT_EMITTER);
        assertEquals(406, ItemSpriteSheet.HARD_LIGHT_EMITTER);
    }

    @Test
    public void projectorPreviewFitsOneTransparentFourTimesScaledItemTile() throws Exception {
        try (DataInputStream png = new DataInputStream(new FileInputStream(
                "src/main/assets/hard_light_emitter.png"))) {
            assertEquals(0x89504E470D0A1A0AL, png.readLong());
            assertEquals(13, png.readInt());
            assertEquals(0x49484452, png.readInt());
            assertEquals(64, png.readInt());
            assertEquals(64, png.readInt());
            assertEquals(8, png.readUnsignedByte());
            assertEquals(6, png.readUnsignedByte());
        }
    }
}
