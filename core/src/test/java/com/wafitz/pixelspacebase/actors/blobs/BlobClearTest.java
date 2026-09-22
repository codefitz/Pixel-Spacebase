package com.wafitz.pixelspacebase.actors.blobs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlobClearTest {

    @Test
    public void clearRemovesCurrentAndPendingGasAtCell() {
        Blob blob = new Blob();
        blob.cur = new int[3];
        blob.off = new int[3];
        blob.cur[1] = 5;
        blob.off[1] = 3;
        blob.volume = 5;

        assertEquals(5, blob.clear(1));

        assertEquals(0, blob.cur[1]);
        assertEquals(0, blob.off[1]);
        assertEquals(0, blob.volume);
    }
}
