import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Restores DM3000's original mechanical base and installs its two generated suits. */
public class InstallDM3000ArmourArtwork {
    private static final Path ART = Path.of("art/dm3000-armour");
    private static final Path ASSETS = Path.of("core/src/main/assets");
    private static final Path BACKUPS = Path.of("docs/sprite-backups");
    private static final int FRAMES = 21;
    private static final int WIDTH = 12;
    private static final int HEIGHT = 15;
    private static final int SCALE = 4;
    // Half-logical-pixel clusters keep the small optical sensors readable.
    private static final int ART_SCALE = 2;

    public static void main(String[] args) throws Exception {
        BufferedImage hero = read(ASSETS.resolve("dm3000.png"));
        BufferedImage items = read(ASSETS.resolve("items.png"));
        BufferedImage beforeHero = copy(hero);
        BufferedImage beforeItems = copy(items);
        backup("dm3000.png");
        backup("items.png");

        // The old tier-two chassis becomes the naked look; tier one remains Data-inspired.
        BufferedImage original = read(BACKUPS.resolve("dm3000-before-dm3000-armour.png"));
        BufferedImage naked = original.getSubimage(0, 2 * HEIGHT * SCALE,
                FRAMES * WIDTH * SCALE, HEIGHT * SCALE);
        blit(naked, hero, 0, 0);
        ImageIO.write(naked, "png", ART.resolve("unarmoured-dm300-animations.png").toFile());
        for (int y = 0; y < naked.getHeight(); y++) {
            for (int x = 0; x < naked.getWidth(); x++) {
                if (hero.getRGB(x, y) != naked.getRGB(x, y)) {
                    throw new IllegalStateException("Unarmoured DM300 frame differs from original");
                }
            }
        }

        for (int suit = 0; suit < 2; suit++) {
            String name = suit == 0 ? "gold-astromech" : "borg-hunter";
            BufferedImage source = read(ART.resolve(name + "-source.png"));
            BufferedImage strip = pack(source);
            BufferedImage icon = icon(strip);
            ImageIO.write(strip, "png", ART.resolve(name + "-animations.png").toFile());
            ImageIO.write(icon, "png", ART.resolve(name + "-icon.png").toFile());
            blit(strip, hero, 0, (suit + 2) * HEIGHT * SCALE);
            // ItemSpriteSheet.ARMOR_DM3000_SPACESUIT / ARMOR_DM3000_HUNTER (169 / 170).
            blit(icon, items, (suit + 9) * 64, 640);
        }

        verifyPreserved(beforeHero, hero, true);
        verifyPreserved(beforeItems, items, false);
        ImageIO.write(hero, "png", ASSETS.resolve("dm3000.png").toFile());
        ImageIO.write(items, "png", ASSETS.resolve("items.png").toFile());
        preview(hero, items);
        System.out.println("Restored 21 original DM300 base frames; installed 42 suit frames and icons 169/170; unrelated pixels preserved.");
    }

    private static BufferedImage pack(BufferedImage source) {
        int[] rows = {0, rowGap(source, .27, .42), rowGap(source, .58, .73), source.getHeight()};
        BufferedImage[] cells = new BufferedImage[FRAMES];
        int[][] bounds = new int[FRAMES][];
        double standingHeight = 0;
        for (int frame = 0; frame < FRAMES; frame++) {
            int left = frame % 7 * source.getWidth() / 7;
            int right = (frame % 7 + 1) * source.getWidth() / 7;
            int row = frame / 7;
            cells[frame] = copy(source.getSubimage(left, rows[row], right - left, rows[row + 1] - rows[row]));
            // A generated energy pulse can spill into the next cell. Retain detached effects
            // only in the firing, flight and scanner poses that actually use them.
            if (frame != 14 && frame != 18 && frame != 19 && frame != 20) isolateCharacter(cells[frame]);
            bounds[frame] = bounds(cells[frame]);
            if (frame < 8) standingHeight += bounds[frame][3] - bounds[frame][1] + 1;
        }
        // A common scale retains body size through crouching, collapse and tool poses.
        double scale = 24.0 / (standingHeight / 8);
        BufferedImage strip = new BufferedImage(FRAMES * WIDTH * SCALE, HEIGHT * SCALE,
                BufferedImage.TYPE_INT_ARGB);
        for (int frame = 0; frame < FRAMES; frame++) {
            int[] box = bounds[frame];
            int width = box[2] - box[0] + 1;
            int height = box[3] - box[1] + 1;
            double fittedScale = Math.min(scale, Math.min(20.0 / width, 26.0 / height));
            double startX = (WIDTH * SCALE / ART_SCALE - width * fittedScale) / 2;
            double startY = (HEIGHT - 1) * SCALE / ART_SCALE - height * fittedScale;
            for (int y = 0; y < HEIGHT * SCALE / ART_SCALE; y++) {
                for (int x = 0; x < WIDTH * SCALE / ART_SCALE; x++) {
                    double sx = box[0] + (x + .5 - startX) / fittedScale;
                    double sy = box[1] + (y + .5 - startY) / fittedScale;
                    if (sx < box[0] || sx >= box[2] + 1 || sy < box[1] || sy >= box[3] + 1) continue;
                    int pixel = cells[frame].getRGB((int) sx, (int) sy);
                    if ((pixel >>> 24) >= 128) block(strip, frame * WIDTH * SCALE + x * ART_SCALE,
                            y * ART_SCALE, pixel | 0xFF000000);
                }
            }
            bounds(strip.getSubimage(frame * WIDTH * SCALE, 0, WIDTH * SCALE, HEIGHT * SCALE));
        }
        return strip;
    }

    private static void isolateCharacter(BufferedImage image) {
        int width = image.getWidth(), height = image.getHeight();
        int[] components = new int[width * height];
        int[] queue = new int[components.length];
        int largest = 0, largestCount = 0, component = 0;
        for (int start = 0; start < components.length; start++) {
            if (components[start] != 0 || (image.getRGB(start % width, start / width) >>> 24) < 128) continue;
            component++;
            int count = 1, read = 0;
            queue[0] = start;
            components[start] = component;
            while (read < count) {
                int cell = queue[read++];
                int x = cell % width, y = cell / width;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = x + dx, ny = y + dy;
                        if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                        int next = ny * width + nx;
                        if (components[next] != 0 || (image.getRGB(nx, ny) >>> 24) < 128) continue;
                        components[next] = component;
                        queue[count++] = next;
                    }
                }
            }
            if (count > largestCount) {
                largest = component;
                largestCount = count;
            }
        }
        if (largestCount == 0) throw new IllegalArgumentException("Empty generated character");
        for (int cell = 0; cell < components.length; cell++) {
            if (components[cell] != largest) image.setRGB(cell % width, cell / width, 0);
        }
    }

    private static BufferedImage icon(BufferedImage strip) {
        // Use the same idle design in the inventory's 16x16 logical-pixel tile.
        BufferedImage idle = strip.getSubimage(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
        int[] box = bounds(idle);
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = icon.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int width = box[2] - box[0] + 1;
        int height = box[3] - box[1] + 1;
        graphics.drawImage(idle, (64 - width) / 2, (64 - height) / 2,
                (64 + width) / 2, (64 + height) / 2, box[0], box[1], box[2] + 1, box[3] + 1, null);
        graphics.dispose();
        return icon;
    }

    private static void preview(BufferedImage hero, BufferedImage items) throws Exception {
        BufferedImage preview = new BufferedImage(1152, 420, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = preview.createGraphics();
        graphics.setColor(new Color(20, 27, 39));
        graphics.fillRect(0, 0, preview.getWidth(), preview.getHeight());
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        graphics.setColor(new Color(236, 241, 250));
        graphics.drawString("DM3000 armour progression", 24, 29);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        graphics.drawString("Unarmoured / original DM300", 24, 60);
        graphics.drawString("Level 1 / Data (retained)", 312, 60);
        graphics.drawString("Level 2 / Gold astromech", 600, 60);
        graphics.drawString("Hunter / Borg", 888, 60);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        for (int suit = 0; suit < 4; suit++) {
            int tier = suit;
            int x = suit * 288 + 24;
            graphics.drawImage(hero, x + 48, 80, x + 192, 260,
                    0, tier * 60, 48, (tier + 1) * 60, null);
            if (suit >= 2) {
                int itemX = (suit + 7) * 64;
                graphics.drawImage(items, x + 80, 286, x + 144, 350,
                        itemX, 640, itemX + 64, 704, null);
            }
        }
        graphics.setColor(new Color(166, 183, 202));
        graphics.drawString("Live atlas idle poses and inventory icons; in-game review pending.", 24, 394);
        graphics.dispose();
        ImageIO.write(preview, "png", ART.resolve("preview.png").toFile());

        BufferedImage poses = new BufferedImage(7 * 96, 9 * 120, BufferedImage.TYPE_INT_RGB);
        graphics = poses.createGraphics();
        graphics.setColor(new Color(20, 27, 39));
        graphics.fillRect(0, 0, poses.getWidth(), poses.getHeight());
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int[] tiers = {0, 2, 3};
        for (int suit = 0; suit < tiers.length; suit++) {
            for (int frame = 0; frame < FRAMES; frame++) {
                int x = frame % 7 * 96;
                int y = (suit * 3 + frame / 7) * 120;
                graphics.drawImage(hero, x, y, x + 96, y + 120,
                        frame * 48, tiers[suit] * 60, (frame + 1) * 48, (tiers[suit] + 1) * 60, null);
            }
        }
        graphics.dispose();
        ImageIO.write(poses, "png", ART.resolve("animation-preview.png").toFile());
    }

    private static int rowGap(BufferedImage image, double from, double to) {
        int bestStart = -1, bestLength = 0, start = -1;
        for (int y = (int) (image.getHeight() * from); y < image.getHeight() * to; y++) {
            int count = 0;
            for (int x = 0; x < image.getWidth(); x++) if ((image.getRGB(x, y) >>> 24) >= 128) count++;
            if (count < image.getWidth() * .02) {
                if (start < 0) start = y;
                if (y - start + 1 > bestLength) {
                    bestStart = start;
                    bestLength = y - start + 1;
                }
            } else start = -1;
        }
        if (bestStart < 0) throw new IllegalArgumentException("No transparent row gap");
        return bestStart + bestLength / 2;
    }

    private static int[] bounds(BufferedImage image) {
        int left = image.getWidth(), top = image.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); top = Math.min(top, y);
                right = Math.max(right, x); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalArgumentException("Empty animation frame");
        return new int[]{left, top, right, bottom};
    }

    private static void block(BufferedImage image, int x, int y, int pixel) {
        for (int dy = 0; dy < ART_SCALE; dy++) for (int dx = 0; dx < ART_SCALE; dx++) image.setRGB(x + dx, y + dy, pixel);
    }

    private static void blit(BufferedImage source, BufferedImage target, int left, int top) {
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) target.setRGB(left + x, top + y, source.getRGB(x, y));
        }
    }

    private static void verifyPreserved(BufferedImage before, BufferedImage after, boolean hero) {
        if (before.getWidth() != after.getWidth() || before.getHeight() != after.getHeight()) {
            throw new IllegalStateException("Atlas dimensions changed");
        }
        for (int y = 0; y < before.getHeight(); y++) {
            for (int x = 0; x < before.getWidth(); x++) {
                boolean allowed = hero ? x < 1008 && (y < 60 || y >= 120 && y < 240)
                        : x >= 576 && x < 704 && y >= 640 && y < 704;
                if (!allowed && before.getRGB(x, y) != after.getRGB(x, y)) {
                    throw new IllegalStateException("Unrelated pixel changed at " + x + "," + y);
                }
            }
        }
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        blit(source, result, 0, 0);
        return result;
    }

    private static BufferedImage read(Path path) throws Exception {
        BufferedImage result = ImageIO.read(path.toFile());
        if (result == null) throw new IllegalArgumentException("Not a PNG: " + path);
        return result;
    }

    private static void backup(String name) throws Exception {
        Path path = BACKUPS.resolve(name.replace(".png", "-before-dm3000-armour.png"));
        if (!Files.exists(path)) Files.copy(ASSETS.resolve(name), path);
    }
}
