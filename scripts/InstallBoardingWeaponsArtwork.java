import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Installs generated weapon art in two existing item cells and preserves every other pixel. */
public class InstallBoardingWeaponsArtwork {
    private static final Path ASSETS = Path.of("core/src/main/assets");
    private static final Path BACKUP = Path.of("docs/sprite-backups/items-before-boarding-weapons.png");
    private static final Path ARTWORK = Path.of("docs/artwork/boarding-weapons");
    private static final int BATON_SLOT = 107;
    private static final int CUTTER_SLOT = 113;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("Expected baton and cutter source PNGs");
        Files.createDirectories(ARTWORK);
        Files.createDirectories(BACKUP.getParent());
        Path atlasFile = ASSETS.resolve("items.png");
        if (!Files.exists(BACKUP)) Files.copy(atlasFile, BACKUP);
        BufferedImage before = ImageIO.read(atlasFile.toFile());
        BufferedImage atlas = ImageIO.read(atlasFile.toFile());
        int scale = atlas.getWidth() / 256;
        if (scale < 1 || atlas.getWidth() != 256 * scale || atlas.getHeight() != 512 * scale) {
            throw new IllegalStateException("Unexpected item atlas dimensions");
        }
        String[] names = {"repulsor_baton", "phase_cutter"};
        int[] slots = {BATON_SLOT, CUTTER_SLOT};
        BufferedImage[] icons = new BufferedImage[2];
        for (int i = 0; i < 2; i++) {
            Path sourceFile = Path.of(args[i]);
            Files.copy(sourceFile, ARTWORK.resolve(names[i] + "-generated.png"),
                    StandardCopyOption.REPLACE_EXISTING);
            icons[i] = fit(ImageIO.read(sourceFile.toFile()), scale);
            ImageIO.write(icons[i], "png", ASSETS.resolve(names[i] + ".png").toFile());
            int ox = slots[i] % 16 * 16 * scale;
            int oy = slots[i] / 16 * 16 * scale;
            for (int y = 0; y < icons[i].getHeight(); y++) {
                for (int x = 0; x < icons[i].getWidth(); x++) {
                    atlas.setRGB(ox + x, oy + y, icons[i].getRGB(x, y));
                }
            }
        }
        for (int y = 0; y < atlas.getHeight(); y++) {
            for (int x = 0; x < atlas.getWidth(); x++) {
                int slot = x / (16 * scale) + 16 * (y / (16 * scale));
                if (slot != BATON_SLOT && slot != CUTTER_SLOT && before.getRGB(x, y) != atlas.getRGB(x, y)) {
                    throw new IllegalStateException("Unrelated atlas cell changed: " + slot);
                }
            }
        }
        ImageIO.write(atlas, "png", atlasFile.toFile());
        BufferedImage preview = new BufferedImage(288, 144, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 2; i++) {
            for (int y = 0; y < 128; y++) {
                for (int x = 0; x < 128; x++) {
                    preview.setRGB(8 + i * 144 + x, 8 + y,
                            icons[i].getRGB(x * icons[i].getWidth() / 128, y * icons[i].getHeight() / 128));
                }
            }
        }
        ImageIO.write(preview, "png", ARTWORK.resolve("preview.png").toFile());
        System.out.println("Installed Repulsor Baton and Phase Cutter; all other atlas cells preserved.");
    }

    private static BufferedImage fit(BufferedImage source, int scale) {
        int left = source.getWidth(), top = source.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); right = Math.max(right, x);
                top = Math.min(top, y); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalStateException("Empty generated weapon");
        int width = right - left + 1, height = bottom - top + 1;
        double ratio = Math.min(14.0 / width, 14.0 / height);
        double ox = (16 - width * ratio) / 2, oy = (16 - height * ratio) / 2;
        BufferedImage icon = new BufferedImage(16 * scale, 16 * scale, BufferedImage.TYPE_INT_ARGB);
        int visible = 0;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int sx = (int) Math.floor(left + (x + .5 - ox) / ratio);
                int sy = (int) Math.floor(top + (y + .5 - oy) / ratio);
                if (sx < left || sx > right || sy < top || sy > bottom) continue;
                int pixel = source.getRGB(sx, sy);
                if ((pixel >>> 24) < 128) continue;
                pixel |= 0xFF000000;
                visible++;
                for (int dy = 0; dy < scale; dy++) {
                    for (int dx = 0; dx < scale; dx++) {
                        icon.setRGB(x * scale + dx, y * scale + dy, pixel);
                    }
                }
            }
        }
        if (visible < 20) throw new IllegalStateException("Weapon silhouette is too sparse at game size");
        return icon;
    }
}
