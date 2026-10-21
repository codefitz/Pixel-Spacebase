import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Replaces DM3000's existing launcher icon, preserving all other item pixels. */
public class InstallDM3000LauncherArm {
    private static final Path ART = Path.of("art/dm3000-launcher-arm");
    private static final Path ATLAS = Path.of("core/src/main/assets/items.png");
    private static final Path BACKUP = Path.of("docs/sprite-backups/items-before-dm3000-launcher-arm.png");
    private static final int SLOT = 101; // ItemSpriteSheet.DM3000_ROCKET_LAUNCHER
    private static final int SIZE = 64;

    public static void main(String[] args) throws Exception {
        BufferedImage source = ImageIO.read(ART.resolve("launcher-arm-source.png").toFile());
        BufferedImage before = ImageIO.read(ATLAS.toFile());
        if (before.getWidth() != 1024 || before.getHeight() != 2048) {
            throw new IllegalStateException("Unexpected item atlas dimensions");
        }
        if (!Files.exists(BACKUP)) Files.copy(ATLAS, BACKUP);
        BufferedImage atlas = ImageIO.read(ATLAS.toFile());
        BufferedImage icon = fit(source);
        int left = SLOT % 16 * SIZE, top = SLOT / 16 * SIZE;
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) atlas.setRGB(left + x, top + y, icon.getRGB(x, y));
        }
        for (int y = 0; y < atlas.getHeight(); y++) {
            for (int x = 0; x < atlas.getWidth(); x++) {
                boolean allowed = x >= left && x < left + SIZE && y >= top && y < top + SIZE;
                if (!allowed && before.getRGB(x, y) != atlas.getRGB(x, y)) {
                    throw new IllegalStateException("Unrelated item pixel changed at " + x + "," + y);
                }
            }
        }
        ImageIO.write(icon, "png", ART.resolve("launcher-arm-icon.png").toFile());
        ImageIO.write(atlas, "png", ATLAS.toFile());
        preview(icon);
        System.out.println("Installed launcher arm in slot 101; all other item pixels preserved.");
    }

    private static BufferedImage fit(BufferedImage source) {
        int left = source.getWidth(), top = source.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); top = Math.min(top, y);
                right = Math.max(right, x); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalArgumentException("Empty launcher arm source");
        int width = right - left + 1, height = bottom - top + 1;
        // Match the new DM3000 suits' crisp 2x2 physical-pixel clusters.
        double scale = Math.min(28.0 / width, 28.0 / height);
        double startX = (32 - width * scale) / 2, startY = (32 - height * scale) / 2;
        BufferedImage icon = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        int visible = 0;
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                double sx = left + (x + .5 - startX) / scale;
                double sy = top + (y + .5 - startY) / scale;
                if (sx < left || sx >= right + 1 || sy < top || sy >= bottom + 1) continue;
                int pixel = source.getRGB((int) sx, (int) sy);
                if ((pixel >>> 24) < 128) continue;
                visible++;
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) icon.setRGB(x * 2 + dx, y * 2 + dy, pixel | 0xFF000000);
                }
            }
        }
        if (visible < 100) throw new IllegalStateException("Launcher arm silhouette lost during packing");
        return icon;
    }

    private static void preview(BufferedImage icon) throws Exception {
        BufferedImage preview = new BufferedImage(640, 376, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = preview.createGraphics();
        graphics.setColor(new Color(20, 27, 39));
        graphics.fillRect(0, 0, preview.getWidth(), preview.getHeight());
        graphics.setColor(new Color(236, 241, 250));
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        graphics.drawString("DM3000's integrated launcher arm", 24, 30);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        graphics.drawString("Previous launcher", 30, 64);
        graphics.drawString("Reinforced fist + forearm launcher", 294, 64);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        BufferedImage original = ImageIO.read(ART.resolve("before.png").toFile());
        graphics.drawImage(original, 32, 90, 224, 224, null);
        graphics.drawImage(icon, 318, 90, 224, 224, null);
        graphics.drawImage(icon, 552, 242, 64, 64, null);
        graphics.setColor(new Color(166, 183, 202));
        graphics.drawString("Installed icon, enlarged and at native size. In-game review pending.", 24, 352);
        graphics.dispose();
        ImageIO.write(preview, "png", ART.resolve("preview.png").toFile());
    }
}
