import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Packs Knight and Master art into two rows of seventeen 64px frames. */
public class InstallJedaKnightArtwork {
    public static void main(String[] args) throws Exception {
        BufferedImage source = ImageIO.read(Path.of(args[0]).toFile());
        int[] rows = {0, rowGap(source, .25, .45), rowGap(source, .55, .78), source.getHeight()};
        BufferedImage sheet = new BufferedImage(17 * 64, 128, BufferedImage.TYPE_INT_ARGB);
        BufferedImage preview = new BufferedImage(512, 256, BufferedImage.TYPE_INT_ARGB);
        int[] previewFrames = {0, 4, 6, 10};
        for (int frame = 0; frame < 17; frame++) {
            int cellLeft = frame % 6 * source.getWidth() / 6;
            int cellRight = (frame % 6 + 1) * source.getWidth() / 6;
            int cellTop = rows[frame / 6];
            int cellBottom = rows[frame / 6 + 1];
            int left = cellRight, right = -1, top = cellBottom, bottom = -1;
            for (int y = cellTop; y < cellBottom; y++) {
                for (int x = cellLeft; x < cellRight; x++) {
                    if ((source.getRGB(x, y) >>> 24) < 128) continue;
                    left = Math.min(left, x); right = Math.max(right, x);
                    top = Math.min(top, y); bottom = Math.max(bottom, y);
                }
            }
            if (right < left) throw new IllegalArgumentException("Empty Knight frame " + frame);
            double scale = Math.min(14.0 / (right - left + 1), 14.0 / (bottom - top + 1));
            double startX = 8 - (right - left + 1) * scale / 2;
            double startY = 15 - (bottom - top + 1) * scale;
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    double sourceX = left + (x + .5 - startX) / scale;
                    double sourceY = top + (y + .5 - startY) / scale;
                    int pixel = 0;
                    if (sourceX >= left && sourceX < right + 1 && sourceY >= top && sourceY < bottom + 1) {
                        int sampled = source.getRGB((int) sourceX, (int) sourceY);
                        if ((sampled >>> 24) >= 128) pixel = sampled | 0xFF000000;
                    }
                    for (int variant = 0; variant < 2; variant++) {
                        int result = variant == 0 ? pixel : masterPixel(pixel);
                        block(sheet, frame * 64 + x * 4, variant * 64 + y * 4, 4, result);
                        for (int i = 0; i < previewFrames.length; i++) {
                            if (previewFrames[i] == frame) block(preview, i * 128 + x * 8,
                                    variant * 128 + y * 8, 8, result);
                        }
                    }
                }
            }
        }
        Path target = Path.of("core/src/main/assets/jeda_knight.png");
        Path backup = Path.of("docs/sprite-backups/jeda-knight-before-unhooded-reskin.png");
        if (!Files.exists(backup)) Files.copy(target, backup);
        ImageIO.write(sheet, "png", target.toFile());
        ImageIO.write(preview, "png", Path.of("docs/jeda-knight-preview.png").toFile());
    }

    private static int masterPixel(int pixel) {
        if ((pixel >>> 24) == 0) return pixel;
        int r = pixel >>> 16 & 255, g = pixel >>> 8 & 255, b = pixel & 255;
        // The Master retains the same cream robes and has a green energy blade.
        if (b > 100 && b > r * 1.3 && b > g * .95) {
            return 0xFF000000 | Math.max(40, r) << 16 | b << 8 | Math.max(55, g / 2);
        }
        return pixel;
    }

    private static int rowGap(BufferedImage source, double from, double to) {
        int longestStart = -1, longestLength = 0, start = -1;
        for (int y = (int) (source.getHeight() * from); y < source.getHeight() * to; y++) {
            int opaque = 0;
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getRGB(x, y) >>> 24) >= 128) opaque++;
            }
            if (opaque < source.getWidth() * .02) {
                if (start < 0) start = y;
                if (y - start + 1 > longestLength) {
                    longestStart = start;
                    longestLength = y - start + 1;
                }
            } else {
                start = -1;
            }
        }
        if (longestStart < 0) throw new IllegalArgumentException("No gap between generated sprite rows");
        return longestStart + longestLength / 2;
    }

    private static void block(BufferedImage image, int x, int y, int scale, int pixel) {
        for (int dy = 0; dy < scale; dy++) {
            for (int dx = 0; dx < scale; dx++) image.setRGB(x + dx, y + dy, pixel);
        }
    }
}
