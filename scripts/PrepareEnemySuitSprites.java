import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/** Fits generated enemy variants to the original hero's exact pixel grid and alpha. */
public class PrepareEnemySuitSprites {
    public static void main(String[] args) throws Exception {
        Path assets = Path.of("core/src/main/assets");
        BufferedImage source = ImageIO.read(assets.resolve("captain.png").toFile());
        BufferedImage generated = ImageIO.read(Path.of(args[0]).toFile());
        for (int row : new int[]{2, 3}) {
            BufferedImage output = new BufferedImage(1008, 60, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < 15; y++) {
                for (int x = 0; x < 252; x++) {
                    int original = source.getRGB(x * 4 + 2, row * 60 + y * 4 + 2);
                    int alpha = original >>> 24;
                    if (alpha == 0) continue;
                    int gx = (int) ((x * 4 + 2.0) * generated.getWidth() / source.getWidth());
                    int gy = (int) ((row * 60 + y * 4 + 2.0) * generated.getHeight() / source.getHeight());
                    int color = generated.getRGB(gx, gy) & 0xFFFFFF;
                    if (row == 2) {
                        // Preserve white suit plating; empty the gold visor/emblem region
                        // in every pose, including the detached helmet death frames.
                        color = original & 0xFFFFFF;
                        int left = (x / 12) * 12;
                        int minX = 12, maxX = -1, minY = 15, maxY = -1;
                        for (int sy = 0; sy < 15; sy++) {
                            for (int sx = 0; sx < 12; sx++) {
                                int p = source.getRGB((left + sx) * 4 + 2, row * 60 + sy * 4 + 2);
                                int r = (p >>> 16) & 255, g = (p >>> 8) & 255, b = p & 255;
                                if ((p >>> 24) != 0 && r > 110 && g > 90 && b < g * .72) {
                                    minX = Math.min(minX, sx); maxX = Math.max(maxX, sx);
                                    minY = Math.min(minY, sy); maxY = Math.max(maxY, sy);
                                }
                            }
                        }
                        if (x % 12 >= minX && x % 12 <= maxX && y >= minY && y <= maxY) {
                            color = y == minY ? 0x252A35 : 0x10131C;
                        }
                    }
                    int pixel = (alpha << 24) | color;
                    for (int dy = 0; dy < 4; dy++) {
                        for (int dx = 0; dx < 4; dx++) output.setRGB(x * 4 + dx, y * 4 + dy, pixel);
                    }
                }
            }
            String name = row == 2 ? "ruptured_crew_suit.png" : "outer_colony_psion.png";
            for (int frame = 0; frame <= 15; frame++) {
                int visible = 0, transparent = 0;
                for (int y = 0; y < 60; y++) {
                    for (int x = frame * 48; x < (frame + 1) * 48; x++) {
                        if ((output.getRGB(x, y) >>> 24) == 0) transparent++;
                        else visible++;
                    }
                }
                if (visible == 0 || transparent == 0) {
                    throw new IllegalStateException(name + " has invalid animation frame " + frame);
                }
            }
            ImageIO.write(output, "png", assets.resolve(name).toFile());
        }
    }
}
