import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Fits generated detector artwork to the item atlas without touching other slots. */
public class InstallHunterDetectorIcons {
    private static final String[] NAMES = {"bio", "item", "trap"};
    // Reserved slots 264–266: row 17, columns 9–11 in ItemSpriteSheet.
    private static final int FIRST_SLOT = 264;

    public static void main(String[] args) throws Exception {
        Path artwork = Path.of(args[0]);
        Path assets = Path.of("core/src/main/assets");
        Path atlasPath = assets.resolve("items.png");
        Path backup = Path.of("docs/sprite-backups/items-before-hunter-detectors.png");
        BufferedImage atlas = ImageIO.read(atlasPath.toFile());
        BufferedImage preview = new BufferedImage(768, 256, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < NAMES.length; i++) {
            BufferedImage source = ImageIO.read(artwork.resolve(NAMES[i] + "-source.png").toFile());
            BufferedImage icon = fit(source);
            int slot = FIRST_SLOT + i;
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 64; x++) {
                    int pixel = icon.getRGB(x, y);
                    atlas.setRGB(slot % 16 * 64 + x, slot / 16 * 64 + y, pixel);
                    for (int dy = 0; dy < 4; dy++) {
                        for (int dx = 0; dx < 4; dx++) {
                            preview.setRGB(i * 256 + x * 4 + dx, y * 4 + dy, pixel);
                        }
                    }
                }
            }
            ImageIO.write(icon, "png", assets.resolve(NAMES[i] + "_detector.png").toFile());
        }
        if (!Files.exists(backup)) Files.copy(atlasPath, backup);
        ImageIO.write(atlas, "png", atlasPath.toFile());
        ImageIO.write(preview, "png", artwork.resolve("preview.png").toFile());
    }

    private static BufferedImage fit(BufferedImage source) {
        int left = source.getWidth(), top = source.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x);
                top = Math.min(top, y);
                right = Math.max(right, x);
                bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalArgumentException("Empty detector artwork");
        double size = Math.max(right - left + 1, bottom - top + 1);
        double startX = (left + right + 1 - size) / 2;
        double startY = (top + bottom + 1 - size) / 2;
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        // Retain enough detail for the screen symbols inside the same 64px frame.
        for (int y = 2; y < 30; y++) {
            for (int x = 2; x < 30; x++) {
                int sx = (int) (startX + (x - 1.5) * size / 28);
                int sy = (int) (startY + (y - 1.5) * size / 28);
                if (sx < left || sx > right || sy < top || sy > bottom) continue;
                int pixel = source.getRGB(sx, sy);
                if ((pixel >>> 24) < 128) continue;
                pixel |= 0xFF000000;
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        icon.setRGB(x * 2 + dx, y * 2 + dy, pixel);
                    }
                }
            }
        }
        return icon;
    }
}
