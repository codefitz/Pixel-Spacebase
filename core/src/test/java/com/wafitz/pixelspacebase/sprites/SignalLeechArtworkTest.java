package com.wafitz.pixelspacebase.sprites;

import java.io.DataInputStream;
import java.io.FileInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class SignalLeechArtworkTest {
    @Test public void sheetFitsElevenAnimationFramesAtFourTimesScale() throws Exception {
        try (DataInputStream png = new DataInputStream(new FileInputStream("src/main/assets/signal_leech.png"))) {
            assertEquals(0x89504E470D0A1A0AL, png.readLong());
            assertEquals(13, png.readInt());
            assertEquals(0x49484452, png.readInt());
            assertEquals(192, png.readInt());
            assertEquals(180, png.readInt());
            assertEquals(8, png.readUnsignedByte());
            assertEquals(6, png.readUnsignedByte());
        }
    }
}
