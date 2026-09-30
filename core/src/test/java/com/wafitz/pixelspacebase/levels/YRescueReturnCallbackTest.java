package com.wafitz.pixelspacebase.levels;

import java.io.DataInputStream;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/** Checks the compiled callback target without constructing an Android/OpenGL dialogue. */
public class YRescueReturnCallbackTest {
    @Test public void returnCallbackDestroysYNotTheAlreadyClosedWindow() throws Exception {
        String owner = "com/wafitz/pixelspacebase/actors/mobs/npcs/YRescuer";
        InputStream resource = getClass().getClassLoader().getResourceAsStream(owner + "$1.class");
        assertNotNull(resource);
        try (DataInputStream input = new DataInputStream(resource)) {
            assertEquals(0xCAFEBABE, input.readInt());
            input.readUnsignedShort(); // minor version
            input.readUnsignedShort(); // major version
            int count = input.readUnsignedShort();
            String[] text = new String[count];
            int[] tags = new int[count];
            int[] first = new int[count];
            int[] second = new int[count];
            for (int i = 1; i < count; i++) {
                int tag = tags[i] = input.readUnsignedByte();
                switch (tag) {
                    case 1: text[i] = input.readUTF(); break;
                    case 3: case 4: input.readInt(); break;
                    case 5: case 6: input.readLong(); i++; break;
                    case 7: case 8: case 16: case 19: case 20:
                        first[i] = input.readUnsignedShort(); break;
                    case 9: case 10: case 11: case 12: case 17: case 18:
                        first[i] = input.readUnsignedShort();
                        second[i] = input.readUnsignedShort(); break;
                    case 15: input.readUnsignedByte(); input.readUnsignedShort(); break;
                    default: fail("Unexpected constant-pool tag " + tag);
                }
            }
            boolean removesY = false;
            for (int i = 1; i < count; i++) {
                if (tags[i] != 10) continue;
                String method = text[first[second[i]]];
                if (!"destroy".equals(method)) continue;
                String receiver = text[first[first[i]]];
                assertEquals("Callback must not destroy its Window a second time", owner, receiver);
                removesY = true;
            }
            assertTrue("Return callback must remove Y from the saved deck", removesY);
        }
    }
}
