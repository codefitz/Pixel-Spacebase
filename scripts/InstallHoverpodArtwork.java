import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/** Fits generated hoverpod art into existing tier-four frames without runtime changes. */
public class InstallHoverpodArtwork {
    private static final Path ASSETS = Path.of("core/src/main/assets");
    private static final Path BACKUPS = Path.of("docs/sprite-backups");

    public static void main(String[] args) throws Exception {
        BufferedImage source = ImageIO.read(Path.of(args[0]).toFile());
        int[] rows = {0, gap(source, .25, .45), gap(source, .55, .78), source.getHeight()};
        BufferedImage strip = new BufferedImage(1008, 60, BufferedImage.TYPE_INT_ARGB);
        for (int frame = 0; frame < 21; frame++) {
            int row = frame / 7;
            int cellLeft = frame % 7 * source.getWidth() / 7;
            int cellRight = (frame % 7 + 1) * source.getWidth() / 7;
            int left = cellRight, right = -1, top = rows[row + 1], bottom = -1;
            for (int y = rows[row]; y < rows[row + 1]; y++) {
                for (int x = cellLeft; x < cellRight; x++) {
                    if ((source.getRGB(x, y) >>> 24) < 128) continue;
                    left = Math.min(left, x); right = Math.max(right, x);
                    top = Math.min(top, y); bottom = Math.max(bottom, y);
                }
            }
            if (right < left) throw new IllegalStateException("Empty hoverpod pose " + frame);
            double scale = Math.min(10.0 / (right - left + 1), 12.0 / (bottom - top + 1));
            double ox = 6 - (right - left + 1) * scale / 2;
            double oy = 14 - (bottom - top + 1) * scale;
            for (int y = 0; y < 15; y++) {
                for (int x = 0; x < 12; x++) {
                    int sx = (int) (left + (x + .5 - ox) / scale);
                    int sy = (int) (top + (y + .5 - oy) / scale);
                    if (sx < left || sx > right || sy < top || sy > bottom) continue;
                    int pixel = source.getRGB(sx, sy);
                    if ((pixel >>> 24) < 128) continue;
                    block(strip, frame * 48 + x * 4, y * 4, pixel | 0xFF000000);
                }
            }
        }
        for (int frame = 0; frame < 21; frame++) {
            int visible = 0, transparent = 0;
            for (int y = 0; y < 60; y++) {
                for (int x = frame * 48; x < (frame + 1) * 48; x++) {
                    if ((strip.getRGB(x, y) >>> 24) == 0) transparent++;
                    else visible++;
                }
            }
            if (visible == 0 || transparent == 0) {
                throw new IllegalStateException("Invalid packed hoverpod pose " + frame);
            }
        }
        ImageIO.write(strip, "png", ASSETS.resolve("hoverpod_animations.png").toFile());
        BufferedImage preview = new BufferedImage(384, 120, BufferedImage.TYPE_INT_ARGB);
        int heroIndex = 0;
        int cockpitY = Integer.parseInt(args[1]);
        for (String hero : new String[]{"captain", "commander", "shapeshifter", "dm3000"}) {
            BufferedImage before = ImageIO.read(BACKUPS.resolve(hero + "-before-hoverpod-reskin.png").toFile());
            BufferedImage atlas = ImageIO.read(ASSETS.resolve(hero + ".png").toFile());
            for (int y = 0; y < 60; y++) {
                for (int x = 0; x < 1008; x++) atlas.setRGB(x, 240 + y, strip.getRGB(x, y));
            }
            for (int frame = 0; frame < 21; frame++) {
                if (frame >= 8 && frame <= 12) continue; // Wreckage has no pilot.
                for (int y = 0; y < 4; y++) {
                    for (int x = 0; x < 4; x++) {
                        int pixel = before.getRGB((2 + x * 7 / 4) * 4 + 2, (y * 6 / 4) * 4 + 2);
                        if ((pixel >>> 24) < 128) continue;
                        block(atlas, frame * 48 + (4 + x) * 4, 240 + (cockpitY + y) * 4, pixel);
                    }
                }
            }
            verify(before, atlas, true);
            ImageIO.write(atlas, "png", ASSETS.resolve(hero + ".png").toFile());
            for (int y = 0; y < 120; y++) {
                for (int x = 0; x < 96; x++) preview.setRGB(heroIndex * 96 + x, y,
                        atlas.getRGB(x / 2, 240 + y / 2));
            }
            heroIndex++;
        }
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 60; y++) {
            for (int x = 0; x < 48; x++) icon.setRGB(8 + x, y, strip.getRGB(x, y));
        }
        ImageIO.write(icon, "png", ASSETS.resolve("hoverpod.png").toFile());
        BufferedImage items = ImageIO.read(ASSETS.resolve("items.png").toFile());
        BufferedImage oldItems = ImageIO.read(BACKUPS.resolve("items-before-hoverpod-reskin.png").toFile());
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) items.setRGB(192 + x, 640 + y, icon.getRGB(x, y));
        }
        verify(oldItems, items, false);
        ImageIO.write(items, "png", ASSETS.resolve("items.png").toFile());
        ImageIO.write(preview, "png", Path.of("docs/hoverpod-preview.png").toFile());
    }

    private static void block(BufferedImage image, int x, int y, int pixel) {
        for (int dy = 0; dy < 4; dy++) {
            for (int dx = 0; dx < 4; dx++) image.setRGB(x + dx, y + dy, pixel);
        }
    }

    private static int gap(BufferedImage image, double from, double to) {
        int start = -1, length = 0, run = -1;
        for (int y = (int) (image.getHeight() * from); y < image.getHeight() * to; y++) {
            int opaque = 0;
            for (int x = 0; x < image.getWidth(); x++) if ((image.getRGB(x, y) >>> 24) >= 128) opaque++;
            if (opaque < image.getWidth() * .02) {
                if (run < 0) run = y;
                if (y - run + 1 > length) { start = run; length = y - run + 1; }
            } else run = -1;
        }
        return start < 0 ? (int) (image.getHeight() * (from + to) / 2) : start + length / 2;
    }

    private static void verify(BufferedImage before, BufferedImage after, boolean hero) {
        if (before.getWidth() != after.getWidth() || before.getHeight() != after.getHeight()) {
            throw new IllegalStateException("Atlas dimensions changed");
        }
        for (int y = 0; y < after.getHeight(); y++) {
            for (int x = 0; x < after.getWidth(); x++) {
                boolean allowed = hero ? x < 1008 && y >= 240 && y < 300
                        : x >= 192 && x < 256 && y >= 640 && y < 704;
                if (!allowed && before.getRGB(x, y) != after.getRGB(x, y)) {
                    throw new IllegalStateException("Unrelated pixel changed at " + x + "," + y);
                }
            }
        }
    }
}
