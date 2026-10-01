import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/** Fits generated artwork into one 16px item tile, preserving all other atlas pixels. */
public class InstallHardLightEmitterIcon {
    public static void main(String[] args) throws Exception {
        Path assets = Path.of("core/src/main/assets");
        BufferedImage generated = ImageIO.read(Path.of(args[0]).toFile());
        BufferedImage sheet = ImageIO.read(assets.resolve("items.png").toFile());
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        int left = generated.getWidth(), top = generated.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < generated.getHeight(); y++) {
            for (int x = 0; x < generated.getWidth(); x++) {
                if ((generated.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); right = Math.max(right, x);
                top = Math.min(top, y); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalArgumentException("Empty generated image");
        int size = Math.max(right - left + 1, bottom - top + 1);
        double startX = (left + right + 1 - size) / 2.0;
        double startY = (top + bottom + 1 - size) / 2.0;
        for (int y = 1; y < 15; y++) {
            for (int x = 1; x < 15; x++) {
                int sx = (int) (startX + (x - .5) * size / 14.0);
                int sy = (int) (startY + (y - .5) * size / 14.0);
                if (sx < 0 || sy < 0 || sx >= generated.getWidth() || sy >= generated.getHeight()) continue;
                int p = generated.getRGB(sx, sy);
                if ((p >>> 24) < 128) continue;
                p |= 0xFF000000;
                for (int dy = 0; dy < 4; dy++) {
                    for (int dx = 0; dx < 4; dx++) icon.setRGB(x * 4 + dx, y * 4 + dy, p);
                }
            }
        }
        // QUEST + 6: column 7, row 26 (one-based); retain the old serialized index.
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) sheet.setRGB(6 * 64 + x, 25 * 64 + y, icon.getRGB(x, y));
        }
        BufferedImage original = ImageIO.read(Path.of("docs/sprite-backups/items-before-hard-light-emitter.png").toFile());
        for (int y = 0; y < sheet.getHeight(); y++) {
            for (int x = 0; x < sheet.getWidth(); x++) {
                if (x >= 384 && x < 448 && y >= 1600 && y < 1664) continue;
                if (sheet.getRGB(x, y) != original.getRGB(x, y)) {
                    throw new IllegalStateException("Unrelated atlas pixel changed at " + x + "," + y);
                }
            }
        }
        ImageIO.write(icon, "png", assets.resolve("hard_light_emitter.png").toFile());
        ImageIO.write(sheet, "png", assets.resolve("items.png").toFile());
    }
}
