import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/** Installs generated Hunter art without touching other suit rows or item slots. */
public class InstallHunterSuitArtwork {
    private static final Path ASSETS = Path.of("core/src/main/assets");
    private static final Path BACKUPS = Path.of("docs/sprite-backups");

    public static void main(String[] args) throws Exception {
        BufferedImage generated = ImageIO.read(Path.of(args[0]).toFile());
        BufferedImage strip = new BufferedImage(1008, 60, BufferedImage.TYPE_INT_ARGB);
        int[] rows = {0, rowGap(generated, .25, .45), rowGap(generated, .55, .78), generated.getHeight()};
        for (int frame = 0; frame < 21; frame++) {
            int row = frame / 7;
            int cellLeft = frame % 7 * generated.getWidth() / 7;
            int cellRight = (frame % 7 + 1) * generated.getWidth() / 7;
            int left = cellRight, right = -1, top = rows[row + 1], bottom = -1;
            for (int sy = rows[row]; sy < rows[row + 1]; sy++) {
                for (int sx = cellLeft; sx < cellRight; sx++) {
                    if ((generated.getRGB(sx, sy) >>> 24) < 128) continue;
                    left = Math.min(left, sx); right = Math.max(right, sx);
                    top = Math.min(top, sy); bottom = Math.max(bottom, sy);
                }
            }
            if (right < left) throw new IllegalStateException("Empty Hunter frame " + frame);
            double scale = Math.min(10.0 / (right - left + 1), 13.0 / (bottom - top + 1));
            double originX = 6 - (right - left + 1) * scale / 2;
            double originY = 14 - (bottom - top + 1) * scale;
            int visible = 0;
            for (int y = 0; y < 15; y++) {
                for (int x = 0; x < 12; x++) {
                    int sx = (int) (left + (x + .5 - originX) / scale);
                    int sy = (int) (top + (y + .5 - originY) / scale);
                    if (sx < left || sx > right || sy < top || sy > bottom) continue;
                    int pixel = crisp(generated.getRGB(sx, sy));
                    if ((pixel >>> 24) != 0) visible++;
                    block(strip, frame * 48 + x * 4, y * 4, pixel);
                }
            }
            if (visible == 0) throw new IllegalStateException("Empty Hunter frame " + frame);
            if (frame < 8 || frame >= 13) clarifyVisor(strip, frame * 48, 12, 15);
        }
        ImageIO.write(strip, "png", ASSETS.resolve("hunter_space_suit_animations.png").toFile());
        for (String hero : new String[]{"captain", "commander", "shapeshifter", "dm3000"}) {
            BufferedImage original = ImageIO.read(BACKUPS.resolve(hero + "-before-hunter-reskin.png").toFile());
            BufferedImage atlas = ImageIO.read(ASSETS.resolve(hero + ".png").toFile());
            for (int y = 0; y < 60; y++) {
                for (int x = 0; x < 1008; x++) atlas.setRGB(x, 180 + y, strip.getRGB(x, y));
            }
            verifyPreserved(original, atlas, true);
            ImageIO.write(atlas, "png", ASSETS.resolve(hero + ".png").toFile());
        }

        BufferedImage sourceIcon = args.length > 1 ? ImageIO.read(Path.of(args[1]).toFile())
                : generated.getSubimage(0, 0, generated.getWidth() / 7, rows[1]);
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        int left = sourceIcon.getWidth(), right = -1, top = sourceIcon.getHeight(), bottom = -1;
        for (int y = 0; y < sourceIcon.getHeight(); y++) {
            for (int x = 0; x < sourceIcon.getWidth(); x++) {
                if ((sourceIcon.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); right = Math.max(right, x);
                top = Math.min(top, y); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalStateException("Empty Hunter icon");
        int size = Math.max(right - left + 1, bottom - top + 1);
        double originX = (left + right + 1 - size) / 2.0;
        double originY = (top + bottom + 1 - size) / 2.0;
        for (int y = 1; y < 15; y++) {
            for (int x = 1; x < 15; x++) {
                int sx = (int) (originX + (x - .5) * size / 14);
                int sy = (int) (originY + (y - .5) * size / 14);
                if (sx >= 0 && sy >= 0 && sx < sourceIcon.getWidth() && sy < sourceIcon.getHeight()) {
                    block(icon, x * 4, y * 4, crisp(sourceIcon.getRGB(sx, sy)));
                }
            }
        }
        clarifyVisor(icon, 0, 16, 16);
        ImageIO.write(icon, "png", ASSETS.resolve("hunter_space_suit.png").toFile());
        BufferedImage items = ImageIO.read(ASSETS.resolve("items.png").toFile());
        BufferedImage originalItems = ImageIO.read(BACKUPS.resolve("items-before-hunter-reskin.png").toFile());
        for (int column : new int[]{2, 10}) {
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 64; x++) items.setRGB(column * 64 + x, 640 + y, icon.getRGB(x, y));
            }
        }
        verifyPreserved(originalItems, items, false);
        ImageIO.write(items, "png", ASSETS.resolve("items.png").toFile());
        BufferedImage preview = new BufferedImage(512, 240, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 240; y++) {
            for (int x = 0; x < 192; x++) {
                preview.setRGB(x, y, strip.getRGB(x / 4, y / 4));
                preview.setRGB(192 + x, y, strip.getRGB(18 * 48 + x / 4, y / 4));
            }
        }
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) preview.setRGB(384 + x, 56 + y,
                    icon.getRGB(x / 2, y / 2));
        }
        ImageIO.write(preview, "png", Path.of("docs/hunter-suit-preview.png").toFile());
    }

    private static int crisp(int pixel) {
        return (pixel >>> 24) < 128 ? 0 : pixel | 0xFF000000;
    }

    private static int rowGap(BufferedImage image, double from, double to) {
        int bestStart = -1, bestLength = 0, runStart = -1;
        for (int y = (int) (image.getHeight() * from); y < image.getHeight() * to; y++) {
            int opaque = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) >>> 24) >= 128) opaque++;
            }
            if (opaque < image.getWidth() * .02) {
                if (runStart < 0) runStart = y;
                if (y - runStart + 1 > bestLength) {
                    bestStart = runStart; bestLength = y - runStart + 1;
                }
            } else runStart = -1;
        }
        return bestStart < 0 ? (int) (image.getHeight() * (from + to) / 2)
                : bestStart + bestLength / 2;
    }

    private static void block(BufferedImage image, int x, int y, int pixel) {
        for (int dy = 0; dy < 4; dy++) {
            for (int dx = 0; dx < 4; dx++) image.setRGB(x + dx, y + dy, pixel);
        }
    }

    /** Retains the generated helmet's T motif when thin strokes vanish at native size. */
    private static void clarifyVisor(BufferedImage image, int offset, int width, int height) {
        int top = height;
        for (int y = 0; y < height && top == height; y++) {
            for (int x = 0; x < width; x++) {
                if ((image.getRGB(offset + x * 4, y * 4) >>> 24) != 0) { top = y; break; }
            }
        }
        int left = width, right = -1;
        for (int y = top; y < Math.min(top + 3, height); y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(offset + x * 4, y * 4);
                int r = pixel >>> 16 & 255, g = pixel >>> 8 & 255, b = pixel & 255;
                if ((pixel >>> 24) != 0 && Math.abs(r - g) < 24 && Math.abs(g - b) < 24) {
                    left = Math.min(left, x); right = Math.max(right, x);
                }
            }
        }
        if (right - left < 3 || top + 6 >= height) return;
        int center = Math.min(right - 1, (left + right) / 2 + 1);
        for (int y = top + 2; y <= top + 5; y++) {
            for (int x = center - 2; x <= center + 2; x++) {
                if (x < 0 || x >= width || (image.getRGB(offset + x * 4, y * 4) >>> 24) == 0) continue;
                boolean visor = y == top + 3 && Math.abs(x - center) <= 1
                        || y > top + 3 && x == center;
                boolean rim = y == top + 2 && Math.abs(x - center) <= 1
                        || y == top + 3 && Math.abs(x - center) == 2
                        || y > top + 3 && Math.abs(x - center) == 1;
                if (visor || rim) block(image, offset + x * 4, y * 4,
                        visor ? 0xFF0B1015 : 0xFF989DA5);
            }
        }
    }

    private static void verifyPreserved(BufferedImage before, BufferedImage after, boolean hero) {
        if (before.getWidth() != after.getWidth() || before.getHeight() != after.getHeight()) {
            throw new IllegalStateException("Atlas dimensions changed");
        }
        for (int y = 0; y < after.getHeight(); y++) {
            for (int x = 0; x < after.getWidth(); x++) {
                boolean allowed = hero ? y >= 180 && y < 240 && x < 1008
                        : y >= 640 && y < 704 && ((x >= 128 && x < 192) || (x >= 640 && x < 704));
                if (!allowed && before.getRGB(x, y) != after.getRGB(x, y)) {
                    throw new IllegalStateException("Unrelated pixel changed at " + x + "," + y);
                }
            }
        }
    }
}
