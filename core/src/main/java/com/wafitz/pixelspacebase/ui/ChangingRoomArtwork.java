package com.wafitz.pixelspacebase.ui;

/** Pixel-aligned room tiles, drawn at the game's native 16-pixel tile resolution. */
public final class ChangingRoomArtwork {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 16;

    private ChangingRoomArtwork() {
    }

    public static int[] pixels() {
        int[] pixels = new int[WIDTH * HEIGHT];
        for (int tile = 0; tile < 4; tile++) {
            rect(pixels, tile, 0, 0, 16, 16, 0xFF33464E);
            rect(pixels, tile, 1, 1, 14, 14, 0xFF6B8085);
            rect(pixels, tile, 1, 1, 14, 1, 0xFF91A2A4);
            rect(pixels, tile, 1, 1, 1, 14, 0xFF91A2A4);
            rect(pixels, tile, 8, 1, 1, 14, 0xFF50666E);
            rect(pixels, tile, 1, 8, 14, 1, 0xFF50666E);
            for (int y = 3; y < 14; y += 4) {
                for (int x = 3; x < 14; x += 4) {
                    rect(pixels, tile, x, y, 1, 1, 0xFFABB8B4);
                    rect(pixels, tile, x + 1, y + 1, 1, 1, 0xFF485E65);
                }
            }
        }

        // Chrome shower riser, overhead nozzle, droplets and a recessed basin edge.
        rect(pixels, 1, 0, 0, 16, 16, 0xFF253A46);
        rect(pixels, 1, 1, 1, 14, 12, 0xFF435A67);
        rect(pixels, 1, 4, 2, 2, 10, 0xFF92ABB3);
        rect(pixels, 1, 4, 2, 7, 2, 0xFFCEE0DE);
        rect(pixels, 1, 9, 4, 4, 2, 0xFF9CB8BC);
        rect(pixels, 1, 8, 5, 6, 1, 0xFFD5E7E4);
        rect(pixels, 1, 3, 9, 4, 2, 0xFF9CB8BC);
        rect(pixels, 1, 3, 9, 1, 1, 0xFFF4AC69);
        rect(pixels, 1, 9, 7, 1, 2, 0xFF7DDCE5);
        rect(pixels, 1, 12, 8, 1, 2, 0xFF7DDCE5);
        rect(pixels, 1, 10, 11, 1, 1, 0xFF7DDCE5);
        rect(pixels, 1, 1, 13, 14, 2, 0xFFB5C7C8);
        rect(pixels, 1, 2, 15, 12, 1, 0xFF364F60);

        // Two lockers with narrow illuminated labels and recessed handles.
        rect(pixels, 2, 0, 0, 16, 16, 0xFF243943);
        rect(pixels, 2, 1, 1, 14, 14, 0xFFA7B9BC);
        rect(pixels, 2, 2, 2, 5, 12, 0xFF526A79);
        rect(pixels, 2, 9, 2, 5, 12, 0xFF526A79);
        rect(pixels, 2, 3, 3, 3, 1, 0xFF82D3D5);
        rect(pixels, 2, 10, 3, 3, 1, 0xFF82D3D5);
        rect(pixels, 2, 5, 7, 1, 3, 0xFFEDBD78);
        rect(pixels, 2, 12, 7, 1, 3, 0xFFEDBD78);
        rect(pixels, 2, 3, 12, 3, 1, 0xFF2B424E);
        rect(pixels, 2, 10, 12, 3, 1, 0xFF2B424E);

        // Walkable floor grille. Steam is emitted separately above the tile.
        rect(pixels, 3, 2, 2, 12, 12, 0xFFB1C2C4);
        rect(pixels, 3, 3, 3, 10, 10, 0xFF283B47);
        for (int y = 4; y < 12; y += 2) {
            rect(pixels, 3, 4, y, 8, 1, 0xFF718990);
        }
        rect(pixels, 3, 2, 2, 1, 1, 0xFFE0E9E3);
        rect(pixels, 3, 13, 13, 1, 1, 0xFF435C65);
        return pixels;
    }

    private static void rect(int[] pixels, int tile, int x, int y, int width, int height, int color) {
        for (int row = y; row < y + height; row++) {
            for (int column = x; column < x + width; column++) {
                pixels[tile * 16 + column + row * WIDTH] = color;
            }
        }
    }
}
