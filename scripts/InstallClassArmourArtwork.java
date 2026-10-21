import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Installs full-detail 64px class armour and archives the original tribute sprites. */
public class InstallClassArmourArtwork {
    private static final Path ART = Path.of("art/class-armour");
    private static final Path ASSETS = Path.of("core/src/main/assets");
    private static final Path BACKUPS = Path.of("docs/sprite-backups");
    private static final String[] HEROES = {"commander", "captain", "dm3000", "shapeshifter"};
    private static final String[] DESIGNS = {"bulwark", "boarding-rig", "reactor-chassis", "cthulhu"};
    private static final String[] TRIBUTES = {"darth-vader", "metroid", "robocop", "cthulhu"};
    private static final int[] SLOTS = {165, 168, 166, 167};
    private static final int FRAME = 64, COUNT = 21;

    public static void main(String[] args) throws Exception {
        Files.createDirectories(ART.resolve("legacy"));
        backup("items");
        BufferedImage originalItems = read(BACKUPS.resolve("items-before-class-armour.png"));
        BufferedImage items = read(ASSETS.resolve("items.png"));
        BufferedImage beforeItems = copy(items);
        BufferedImage[] strips = new BufferedImage[4];
        for (int hero = 0; hero < HEROES.length; hero++) {
            String name = HEROES[hero];
            backup(name);
            BufferedImage original = read(BACKUPS.resolve(name + "-before-class-armour.png"));
            BufferedImage legacy = original.getSubimage(0, 360, 1008, 60);
            ImageIO.write(legacy, "png", ART.resolve("legacy/" + TRIBUTES[hero] + "-" + name + ".png").toFile());
            int itemX = SLOTS[hero] % 16 * 64;
            int itemY = SLOTS[hero] / 16 * 64;
            ImageIO.write(originalItems.getSubimage(itemX, itemY, 64, 64), "png",
                    ART.resolve("legacy/" + TRIBUTES[hero] + "-icon.png").toFile());

            BufferedImage sheet = read(ASSETS.resolve(name + ".png"));
            BufferedImage before = copy(sheet);
            strips[hero] = hero < 3 ? pack(read(ART.resolve(DESIGNS[hero] + "-source.png")), hero)
                    : retainCthulhu(legacy);
            validate(strips[hero]);
            ImageIO.write(strips[hero], "png", ASSETS.resolve("class_armor_" + name + ".png").toFile());
            if (hero < 3) {
                // Compact copies serve any remaining consumers of the legacy hero atlas.
                for (int pose = 0; pose < COUNT; pose++) {
                    BufferedImage compact = resize(strips[hero].getSubimage(pose * FRAME, 0, FRAME, FRAME), 48, 60, 44, 56);
                    blit(compact, sheet, pose * 48, 360);
                }
                blit(strips[hero].getSubimage(0, 0, FRAME, FRAME), items, itemX, itemY);
            }
            verifyOutside(before, sheet, true);
            if (hero < 3) ImageIO.write(sheet, "png", ASSETS.resolve(name + ".png").toFile());
            posePreview(strips[hero], DESIGNS[hero]);
        }
        verifyOutside(beforeItems, items, false);
        ImageIO.write(items, "png", ASSETS.resolve("items.png").toFile());
        preview(strips);
        System.out.println("Installed four 21-pose 64x64 class-armour sheets; archived tribute art and icons; unrelated pixels preserved.");
    }

    private static BufferedImage pack(BufferedImage source, int hero) {
        int[] rows = {0, gap(source, .29, .40), gap(source, .61, .71), source.getHeight()};
        BufferedImage[] cells = new BufferedImage[COUNT];
        int[][] boxes = new int[COUNT][];
        // The Bulwark source omitted the eighth walk and added a scanner pose.
        int[] commander = {0,1,2,3,4,5,6,3,7,8,9,10,11,12,13,14,15,16,17,18,19};
        double standingHeight = 0;
        int maxWidth = 0, maxHeight = 0;
        for (int pose = 0; pose < COUNT; pose++) {
            int sourcePose = hero == 0 ? commander[pose] : pose;
            int left = sourcePose % 7 * source.getWidth() / 7;
            int right = (sourcePose % 7 + 1) * source.getWidth() / 7;
            int row = sourcePose / 7;
            cells[pose] = copy(source.getSubimage(left, rows[row], right - left, rows[row + 1] - rows[row]));
            clean(cells[pose], pose == 18);
            boxes[pose] = bounds(cells[pose]);
            int width = boxes[pose][2] - boxes[pose][0] + 1;
            int height = boxes[pose][3] - boxes[pose][1] + 1;
            maxWidth = Math.max(maxWidth, width);
            maxHeight = Math.max(maxHeight, height);
            if (pose < 8) standingHeight += height;
        }
        double scale = Math.min(58.0 / (standingHeight / 8), Math.min(60.0 / maxWidth, 60.0 / maxHeight));
        BufferedImage strip = new BufferedImage(COUNT * FRAME, FRAME, BufferedImage.TYPE_INT_ARGB);
        for (int pose = 0; pose < COUNT; pose++) {
            int[] box = boxes[pose];
            double anchor = (box[0] + box[2] + 1) / 2.0;
            if (pose < 8 || pose >= 13) {
                // Anchor the feet rather than the extended fist during attacks.
                long sum = 0, count = 0;
                int start = box[3] - (box[3] - box[1]) / 6;
                for (int y = start; y <= box[3]; y++) for (int x = box[0]; x <= box[2]; x++) {
                    if ((cells[pose].getRGB(x, y) >>> 24) >= 128) { sum += x; count++; }
                }
                if (count > 0) anchor = (double) sum / count;
            }
            double ox = 32 - anchor * scale;
            ox = Math.max(2 - box[0] * scale, Math.min(62 - (box[2] + 1) * scale, ox));
            double oy = 62 - (box[3] + 1) * scale;
            sample(cells[pose], strip, pose * FRAME, FRAME, FRAME, scale, ox, oy);
        }
        return strip;
    }

    private static BufferedImage retainCthulhu(BufferedImage original) {
        BufferedImage strip = new BufferedImage(COUNT * FRAME, FRAME, BufferedImage.TYPE_INT_ARGB);
        for (int pose = 0; pose < COUNT; pose++) {
            blit(original.getSubimage(pose * 48, 0, 48, 60), strip, pose * FRAME + 8, 2);
        }
        return strip;
    }

    private static BufferedImage resize(BufferedImage source, int width, int height, int maxWidth, int maxHeight) {
        int[] box = bounds(source);
        double scale = Math.min((double) maxWidth / (box[2] - box[0] + 1), (double) maxHeight / (box[3] - box[1] + 1));
        double ox = (width - (box[2] - box[0] + 1) * scale) / 2 - box[0] * scale;
        double oy = height - 2 - (box[3] + 1) * scale;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        sample(source, result, 0, width, height, scale, ox, oy);
        return result;
    }

    private static void sample(BufferedImage source, BufferedImage target, int left, int width, int height,
                               double scale, double ox, double oy) {
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
            double sx = (x + .5 - ox) / scale, sy = (y + .5 - oy) / scale;
            if (sx < 0 || sx >= source.getWidth() || sy < 0 || sy >= source.getHeight()) continue;
            int pixel = source.getRGB((int) sx, (int) sy);
            if ((pixel >>> 24) >= 128) target.setRGB(left + x, y, pixel | 0xFF000000);
        }
    }

    private static void clean(BufferedImage image, boolean flight) {
        int width = image.getWidth(), height = image.getHeight();
        int[] labels = new int[width * height], queue = new int[width * height];
        int[] sizes = new int[labels.length + 1];
        int nextLabel = 0, largest = 0;
        for (int cell = 0; cell < labels.length; cell++) {
            if (labels[cell] != 0 || (image.getRGB(cell % width, cell / width) >>> 24) < 128) continue;
            int label = ++nextLabel, count = 1, read = 0;
            labels[cell] = label; queue[0] = cell;
            while (read < count) {
                int current = queue[read++], x = current % width, y = current / width;
                for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                    int nx = x + dx, ny = y + dy;
                    if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                    int neighbour = ny * width + nx;
                    if (labels[neighbour] != 0 || (image.getRGB(nx, ny) >>> 24) < 128) continue;
                    labels[neighbour] = label; queue[count++] = neighbour;
                }
            }
            sizes[label] = count;
            if (count > sizes[largest]) largest = label;
        }
        if (largest == 0) throw new IllegalStateException("Empty generated pose");
        for (int cell = 0; cell < labels.length; cell++) {
            int label = labels[cell];
            if (label != largest && (!flight || label == 0 || sizes[label] < sizes[largest] / 100)) {
                image.setRGB(cell % width, cell / width, 0);
            }
        }
    }

    private static int gap(BufferedImage source, double from, double to) {
        int bestStart = -1, bestLength = 0, start = -1;
        for (int y = (int) (source.getHeight() * from); y < source.getHeight() * to; y++) {
            int count = 0;
            for (int x = 0; x < source.getWidth(); x++) if ((source.getRGB(x, y) >>> 24) >= 128) count++;
            if (count < source.getWidth() * .02) {
                if (start < 0) start = y;
                if (y - start + 1 > bestLength) { bestStart = start; bestLength = y - start + 1; }
            } else start = -1;
        }
        if (bestStart < 0) throw new IllegalArgumentException("No transparent gap between source rows");
        return bestStart + bestLength / 2;
    }

    private static int[] bounds(BufferedImage image) {
        int left = image.getWidth(), top = image.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) {
            if ((image.getRGB(x, y) >>> 24) < 128) continue;
            left = Math.min(left, x); top = Math.min(top, y); right = Math.max(right, x); bottom = Math.max(bottom, y);
        }
        if (right < left) throw new IllegalStateException("Empty pose");
        return new int[]{left, top, right, bottom};
    }

    private static void validate(BufferedImage strip) {
        if (strip.getWidth() != COUNT * FRAME || strip.getHeight() != FRAME) throw new IllegalStateException("Wrong class armour grid");
        for (int pose = 0; pose < COUNT; pose++) {
            int[] box = bounds(strip.getSubimage(pose * FRAME, 0, FRAME, FRAME));
            if (box[0] < 1 || box[1] < 1 || box[2] >= 63 || box[3] >= 63) throw new IllegalStateException("Class armour clips frame " + pose);
        }
    }

    private static void verifyOutside(BufferedImage before, BufferedImage after, boolean hero) {
        if (before.getWidth() != after.getWidth() || before.getHeight() != after.getHeight()) throw new IllegalStateException("Atlas dimensions changed");
        for (int y = 0; y < before.getHeight(); y++) for (int x = 0; x < before.getWidth(); x++) {
            boolean allowed = hero ? x < 1008 && y >= 360 && y < 420
                    : y >= 640 && y < 704 && (x >= 320 && x < 448 || x >= 512 && x < 576);
            if (!allowed && before.getRGB(x, y) != after.getRGB(x, y)) throw new IllegalStateException("Unrelated pixel changed at " + x + "," + y);
        }
    }

    private static void preview(BufferedImage[] strips) throws Exception {
        BufferedImage preview = new BufferedImage(1024, 370, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = graphics(preview);
        graphics.setColor(new Color(236, 241, 250));
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        graphics.drawString("Class armour - 64 x 64 animation frames", 24, 30);
        String[] names = {"Commander / Bulwark", "Captain / Boarding Rig", "DM3000 / Reactor", "Shapeshifter / retained"};
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        for (int hero = 0; hero < strips.length; hero++) {
            graphics.drawString(names[hero], hero * 256 + 18, 66);
            graphics.drawImage(strips[hero].getSubimage(0, 0, FRAME, FRAME), hero * 256 + 32, 80, 192, 192, null);
            graphics.drawImage(strips[hero].getSubimage(0, 0, FRAME, FRAME), hero * 256 + 96, 284, null);
        }
        graphics.dispose();
        ImageIO.write(preview, "png", ART.resolve("preview.png").toFile());
    }

    private static void posePreview(BufferedImage strip, String name) throws Exception {
        BufferedImage preview = new BufferedImage(896, 384, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = graphics(preview);
        for (int pose = 0; pose < COUNT; pose++) graphics.drawImage(strip.getSubimage(pose * FRAME, 0, FRAME, FRAME),
                pose % 7 * 128, pose / 7 * 128, 128, 128, null);
        graphics.dispose();
        ImageIO.write(preview, "png", ART.resolve(name + "-poses.png").toFile());
    }

    private static Graphics2D graphics(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(20, 27, 39)); graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        return graphics;
    }

    private static void blit(BufferedImage source, BufferedImage target, int left, int top) {
        for (int y = 0; y < source.getHeight(); y++) for (int x = 0; x < source.getWidth(); x++) target.setRGB(left + x, top + y, source.getRGB(x, y));
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB); blit(source, result, 0, 0); return result;
    }

    private static BufferedImage read(Path path) throws Exception {
        BufferedImage image = ImageIO.read(path.toFile());
        if (image == null) throw new IllegalArgumentException("Not an image: " + path);
        return image;
    }

    private static void backup(String name) throws Exception {
        Path backup = BACKUPS.resolve(name + "-before-class-armour.png");
        if (!Files.exists(backup)) Files.copy(ASSETS.resolve(name + ".png"), backup);
    }
}
