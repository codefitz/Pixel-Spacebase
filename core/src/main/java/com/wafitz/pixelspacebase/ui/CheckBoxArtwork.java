package com.wafitz.pixelspacebase.ui;

/** Native-size checkbox artwork independent of the shared icon atlas. */
public final class CheckBoxArtwork {
    public static final int SIZE = 12;

    private CheckBoxArtwork() { }

    public static int[] pixels(boolean checked) {
        int[] pixels = new int[SIZE * SIZE];
        for (int y = 1; y < SIZE - 1; y++) {
            for (int x = 1; x < SIZE - 1; x++) {
                boolean border = x == 1 || y == 1 || x == SIZE - 2 || y == SIZE - 2;
                pixels[y * SIZE + x] = border ? 0xFF3DAECA : 0xFF050F19;
            }
        }
        if (checked) {
            // Two-pixel-wide tick, kept entirely inside the cyan frame.
            for (int i = 0; i < 3; i++) {
                pixels[(5 + i) * SIZE + 3 + i] = 0xFFFFD84A;
                pixels[(6 + i) * SIZE + 3 + i] = 0xFFFFD84A;
            }
            for (int i = 0; i < 4; i++) {
                pixels[(7 - i) * SIZE + 5 + i] = 0xFFFFD84A;
                pixels[(8 - i) * SIZE + 5 + i] = 0xFFFFD84A;
            }
        }
        return pixels;
    }
}
