import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Installs icons matching the orange Loader and updates its compact hero portraits. */
public class InstallLoaderIcons {
    private static final Path ASSETS = Path.of("core/src/main/assets");
    private static final Path BACKUPS = Path.of("docs/sprite-backups");

    public static void main(String[] args) throws Exception {
        BufferedImage source = ImageIO.read(Path.of(args[0]).toFile());
        BufferedImage[] icons = new BufferedImage[2];
        for (int i = 0; i < icons.length; i++) {
            int left = i * source.getWidth() / 2;
            int right = (i + 1) * source.getWidth() / 2;
            icons[i] = fit(source.getSubimage(left, 0, right - left, source.getHeight()), 16, 16, 14, 14);
        }
        Path itemsPath = ASSETS.resolve("items.png");
        backup(itemsPath, "items-before-loader-icons.png");
        BufferedImage items = ImageIO.read(itemsPath.toFile());
        BufferedImage preview = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
        int[] targetX = {64, 256}, targetY = {384, 640};
        String[] iconNames = {"loader_arm.png", "loader_equip.png"};
        for (int i = 0; i < icons.length; i++) {
            BufferedImage enlarged = upscale(icons[i], 4);
            ImageIO.write(enlarged, "png", ASSETS.resolve(iconNames[i]).toFile());
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 64; x++) items.setRGB(targetX[i] + x, targetY[i] + y, enlarged.getRGB(x, y));
            }
            for (int y = 0; y < 128; y++) {
                for (int x = 0; x < 128; x++) preview.setRGB(i * 128 + x, y, icons[i].getRGB(x / 8, y / 8));
            }
        }
        ImageIO.write(items, "png", itemsPath.toFile());
        ImageIO.write(preview, "png", Path.of("art/heavy-loader/loader-icons-preview.png").toFile());
        writeArmSvg(icons[0]);
        int[] poses = {0, 1, 2, 3, 4, 5, 2, 3, 10, 10, 11, 11, 11, 6, 7, 6, 8, 9, 0, 8, 9};
        for (String hero : new String[]{"commander", "dm3000", "shapeshifter", "captain"}) {
            Path heroPath = ASSETS.resolve(hero + ".png");
            backup(heroPath, hero + "-before-loader-icon-update.png");
            BufferedImage heroAtlas = ImageIO.read(heroPath.toFile());
            BufferedImage loader = ImageIO.read(ASSETS.resolve("loader_" + hero + ".png").toFile());
            for (int i = 0; i < poses.length; i++) {
                int frame = poses[i];
                BufferedImage portrait = upscale(fit(loader.getSubimage(frame % 4 * 192,
                        frame / 4 * 112, 192, 112), 12, 15, 10, 13), 4);
                for (int y = 0; y < 60; y++) {
                    for (int x = 0; x < 48; x++) heroAtlas.setRGB(i * 48 + x, 300 + y, portrait.getRGB(x, y));
                }
            }
            ImageIO.write(heroAtlas, "png", heroPath.toFile());
        }
        sizePreview();
    }

    private static BufferedImage fit(BufferedImage source, int width, int height, int maxWidth, int maxHeight) {
        int left = source.getWidth(), right = -1, top = source.getHeight(), bottom = -1;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); right = Math.max(right, x);
                top = Math.min(top, y); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalArgumentException("Empty Loader artwork");
        double scale = Math.min((double) maxWidth / (right - left + 1), (double) maxHeight / (bottom - top + 1));
        double startX = (width - (right - left + 1) * scale) / 2;
        double startY = height - 1 - (bottom - top + 1) * scale;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sx = left + (x + .5 - startX) / scale;
                double sy = top + (y + .5 - startY) / scale;
                if (sx < left || sx >= right + 1 || sy < top || sy >= bottom + 1) continue;
                int pixel = source.getRGB((int) sx, (int) sy);
                if ((pixel >>> 24) >= 128) result.setRGB(x, y, pixel | 0xFF000000);
            }
        }
        return result;
    }

    private static BufferedImage upscale(BufferedImage source, int scale) {
        BufferedImage result = new BufferedImage(source.getWidth() * scale, source.getHeight() * scale,
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) result.setRGB(x, y, source.getRGB(x / scale, y / scale));
        }
        return result;
    }

    private static void writeArmSvg(BufferedImage icon) throws Exception {
        Path svgPath = ASSETS.resolve("loader_arm.svg");
        backup(svgPath, "loader_arm-before-orange-claw.svg");
        StringBuilder svg = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"64\" height=\"64\" viewBox=\"0 0 16 16\" shape-rendering=\"crispEdges\">\n");
        svg.append("  <!-- Pixel-grid source matching the installed orange hydraulic Loader claw. -->\n");
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int pixel = icon.getRGB(x, y);
                if ((pixel >>> 24) == 0) continue;
                svg.append(String.format("  <rect x=\"%d\" y=\"%d\" width=\"1\" height=\"1\" fill=\"#%06x\"/>\n", x, y, pixel & 0xFFFFFF));
            }
        }
        svg.append("</svg>\n");
        Files.writeString(svgPath, svg);
    }

    private static void sizePreview() throws Exception {
        BufferedImage loader = ImageIO.read(ASSETS.resolve("loader_commander.png").toFile());
        BufferedImage hoverpod = ImageIO.read(ASSETS.resolve("hoverpod_commander.png").toFile());
        BufferedImage comparison = new BufferedImage(320, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = comparison.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(loader.getSubimage(0, 0, 192, 112), 11, 0, 137, 80, null);
        graphics.drawImage(hoverpod.getSubimage(0, 0, 80, 80), 200, 0, null);
        graphics.dispose();
        ImageIO.write(upscale(comparison, 2), "png", Path.of("art/heavy-loader/loader-size-preview.png").toFile());
    }

    private static void backup(Path source, String filename) throws Exception {
        Path backup = BACKUPS.resolve(filename);
        if (!Files.exists(backup)) Files.copy(source, backup);
    }
}
