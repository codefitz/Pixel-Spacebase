import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Installs larger Hoverpod frames, with each hero visible behind the canopy glass. */
public class InstallScreenedHoverpodArtwork {
    private static final Path ASSETS = Path.of("core/src/main/assets");
    private static final Path BACKUPS = Path.of("docs/sprite-backups");
    private static final int SIZE = 20;
    private static final int PIXEL_SCALE = 4;
    private static final int FRAME_SIZE = SIZE * PIXEL_SCALE;

    public static void main(String[] args) throws Exception {
        BufferedImage source = ImageIO.read(Path.of(args[0]).toFile());
        int cockpitX = Integer.parseInt(args[1]);
        int cockpitY = Integer.parseInt(args[2]);
        BufferedImage[] poses = new BufferedImage[21];
        for (int frame = 0; frame < poses.length; frame++) poses[frame] = fitPose(source, frame);
        BufferedImage emptyStrip = strip(poses);
        backup("hoverpod_animations.png");
        ImageIO.write(emptyStrip, "png", ASSETS.resolve("hoverpod_animations.png").toFile());
        BufferedImage preview = new BufferedImage(FRAME_SIZE * 4 * 2, FRAME_SIZE * 2,
                BufferedImage.TYPE_INT_ARGB);
        int heroIndex = 0;
        for (String hero : new String[]{"captain", "commander", "shapeshifter", "dm3000"}) {
            Path atlasPath = ASSETS.resolve(hero + ".png");
            BufferedImage atlas = ImageIO.read(atlasPath.toFile());
            backup(hero + ".png");
            BufferedImage[] occupied = new BufferedImage[21];
            for (int frame = 0; frame < occupied.length; frame++) {
                occupied[frame] = copy(poses[frame]);
                if (frame < 8 || frame > 12) insertPilot(occupied[frame], atlas, cockpitX, cockpitY);
                // Retain a compact copy in the old armor row for inventory portraits.
                BufferedImage portrait = resize(occupied[frame], 12, 15, 10, 12);
                for (int y = 0; y < 15; y++) {
                    for (int x = 0; x < 12; x++) block(atlas, frame * 48 + x * 4, 240 + y * 4,
                            portrait.getRGB(x, y));
                }
            }
            ImageIO.write(strip(occupied), "png", ASSETS.resolve("hoverpod_" + hero + ".png").toFile());
            ImageIO.write(atlas, "png", atlasPath.toFile());
            for (int y = 0; y < FRAME_SIZE * 2; y++) {
                for (int x = 0; x < FRAME_SIZE * 2; x++) {
                    preview.setRGB(heroIndex * FRAME_SIZE * 2 + x, y,
                            occupied[0].getRGB(x / 8, y / 8));
                }
            }
            heroIndex++;
        }
        BufferedImage logicalIcon = resize(poses[0], 16, 16, 14, 14);
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        backup("hoverpod.png");
        backup("items.png");
        BufferedImage items = ImageIO.read(ASSETS.resolve("items.png").toFile());
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int pixel = logicalIcon.getRGB(x, y);
                block(icon, x * 4, y * 4, pixel);
                block(items, 192 + x * 4, 640 + y * 4, pixel);
            }
        }
        ImageIO.write(icon, "png", ASSETS.resolve("hoverpod.png").toFile());
        ImageIO.write(items, "png", ASSETS.resolve("items.png").toFile());
        ImageIO.write(preview, "png", Path.of("docs/hoverpod-preview.png").toFile());
    }

    private static BufferedImage fitPose(BufferedImage source, int frame) {
        int left = frame % 7 * source.getWidth() / 7;
        int right = (frame % 7 + 1) * source.getWidth() / 7;
        int top = frame / 7 * source.getHeight() / 3;
        int bottom = (frame / 7 + 1) * source.getHeight() / 3;
        return resize(source.getSubimage(left, top, right - left, bottom - top), SIZE, SIZE, 18, 18);
    }

    private static BufferedImage resize(BufferedImage source, int width, int height,
                                        int maxWidth, int maxHeight) {
        int left = source.getWidth(), right = -1, top = source.getHeight(), bottom = -1;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); right = Math.max(right, x);
                top = Math.min(top, y); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalArgumentException("Empty Hoverpod pose");
        double scale = Math.min((double) maxWidth / (right - left + 1),
                (double) maxHeight / (bottom - top + 1));
        double startX = (width - (right - left + 1) * scale) / 2;
        double startY = height - 1 - (bottom - top + 1) * scale;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sourceX = left + (x + .5 - startX) / scale;
                double sourceY = top + (y + .5 - startY) / scale;
                if (sourceX < left || sourceX >= right + 1 || sourceY < top || sourceY >= bottom + 1) continue;
                int pixel = source.getRGB((int) sourceX, (int) sourceY);
                if ((pixel >>> 24) >= 128) result.setRGB(x, y, pixel | 0xFF000000);
            }
        }
        return result;
    }

    private static void insertPilot(BufferedImage pod, BufferedImage hero, int cockpitX, int cockpitY) {
        int[] cockpit = findCockpit(pod, cockpitX, cockpitY);
        cockpitX = cockpit[0];
        cockpitY = cockpit[1];
        int width = cockpit[2];
        int height = cockpit[3];
        // Use the hero's unarmored head, rather than the suit row or a generic pilot.
        BufferedImage head = hero.getSubimage(0, 0, 48, 32);
        BufferedImage fitted = resize(head, width, height, Math.min(7, width), Math.min(7, height - 1));
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = fitted.getRGB(x, y);
                if ((pixel >>> 24) == 0) continue;
                int r = pixel >>> 16 & 255, g = pixel >>> 8 & 255, b = pixel & 255;
                // A mild glass tint retains eyes and face/robot detail behind the screen.
                r = (r * 7 + 61) / 8;
                g = (g * 7 + 180) / 8;
                b = (b * 7 + 220) / 8;
                pod.setRGB(cockpitX + x, cockpitY + y, 0xFF000000 | r << 16 | g << 8 | b);
            }
        }
        // Reflection is drawn over the pilot, making the glazing visibly sit in front.
        pod.setRGB(cockpitX + width - 1, cockpitY + 1, 0xFFB4F3FF);
        pod.setRGB(cockpitX + width - 1, cockpitY + 2, 0xFFB4F3FF);
        pod.setRGB(cockpitX + width - 2, cockpitY + 1, 0xFF72D7F3);
    }

    private static int[] findCockpit(BufferedImage pod, int fallbackX, int fallbackY) {
        boolean[] seen = new boolean[SIZE * SIZE];
        int[] queue = new int[SIZE * SIZE];
        int[] best = {fallbackX, fallbackY, 8, 8};
        int largest = 0;
        // The dark-blue interior is separated from the lower hull by the silver sill.
        for (int startY = 1; startY < 13; startY++) {
            for (int startX = 3; startX < 17; startX++) {
                int start = startY * SIZE + startX;
                if (seen[start] || !isCockpitPixel(pod.getRGB(startX, startY))) continue;
                int count = 1, read = 0;
                queue[0] = start;
                seen[start] = true;
                int left = startX, right = startX, top = startY, bottom = startY;
                while (read < count) {
                    int cell = queue[read++];
                    int x = cell % SIZE, y = cell / SIZE;
                    left = Math.min(left, x); right = Math.max(right, x);
                    top = Math.min(top, y); bottom = Math.max(bottom, y);
                    for (int direction = 0; direction < 4; direction++) {
                        int nx = x + (direction == 0 ? -1 : direction == 1 ? 1 : 0);
                        int ny = y + (direction == 2 ? -1 : direction == 3 ? 1 : 0);
                        if (nx < 3 || nx >= 17 || ny < 1 || ny >= 13) continue;
                        int next = ny * SIZE + nx;
                        if (seen[next] || !isCockpitPixel(pod.getRGB(nx, ny))) continue;
                        seen[next] = true;
                        queue[count++] = next;
                    }
                }
                if (count > largest && right - left >= 3 && bottom - top >= 3) {
                    largest = count;
                    int width = Math.min(8, right - left + 1);
                    int height = Math.min(8, bottom - top + 1);
                    best = new int[]{left + (right - left + 1 - width) / 2,
                            bottom - height + 1, width, height};
                }
            }
        }
        return best;
    }

    private static boolean isCockpitPixel(int pixel) {
        int r = pixel >>> 16 & 255, g = pixel >>> 8 & 255, b = pixel & 255;
        return (pixel >>> 24) >= 128 && b > 50 && b < 170 && g < 110
                && b > g * 1.2 && b > r * 1.5;
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) result.setRGB(x, y, source.getRGB(x, y));
        }
        return result;
    }

    private static BufferedImage strip(BufferedImage[] poses) {
        BufferedImage result = new BufferedImage(poses.length * FRAME_SIZE, FRAME_SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int frame = 0; frame < poses.length; frame++) {
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) block(result, frame * FRAME_SIZE + x * 4, y * 4,
                        poses[frame].getRGB(x, y));
            }
        }
        return result;
    }

    private static void block(BufferedImage image, int x, int y, int pixel) {
        for (int dy = 0; dy < 4; dy++) {
            for (int dx = 0; dx < 4; dx++) image.setRGB(x + dx, y + dy, pixel);
        }
    }

    private static void backup(String filename) throws Exception {
        Path target = BACKUPS.resolve(filename.replace(".png", "-before-screened-hoverpod.png"));
        if (!Files.exists(target)) Files.copy(ASSETS.resolve(filename), target);
    }
}
