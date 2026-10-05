import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Installs twelve colour variants of one injector into the existing plasmid slots. */
public class InstallPlasmidInjectorArtwork {
    private static final int[] LIQUID_COLOURS = {
            0xED2348, 0xFF941F, 0xFFE13C, 0x39D65A,
            0x25D9CF, 0x358DFF, 0x6048EB, 0xE93ADE,
            0x805A2C, 0x454952, 0xBFCFD7, 0xF3EBC5
    };

    public static void main(String[] args) throws Exception {
        Path assets = Path.of("core/src/main/assets");
        Path atlasPath = assets.resolve("items.png");
        Path backup = Path.of("docs/sprite-backups/items-before-plasmid-injectors.png");
        BufferedImage generated = ImageIO.read(Path.of(args[0]).toFile());
        // Use the azure injector as a common template: column 2, row 2 of the 4x3 sheet.
        int cellLeft = generated.getWidth() / 4;
        int cellRight = generated.getWidth() / 2;
        int cellTop = generated.getHeight() / 3;
        int cellBottom = generated.getHeight() * 2 / 3;
        int left = cellRight, right = -1, top = cellBottom, bottom = -1;
        for (int y = cellTop; y < cellBottom; y++) {
            for (int x = cellLeft; x < cellRight; x++) {
                if ((generated.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); right = Math.max(right, x);
                top = Math.min(top, y); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalArgumentException("Empty azure injector");
        double sourceSize = Math.max(right - left + 1, bottom - top + 1);
        double startX = (left + right + 1 - sourceSize) / 2;
        double startY = (top + bottom + 1 - sourceSize) / 2;
        BufferedImage template = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 1; y < 15; y++) {
            for (int x = 1; x < 15; x++) {
                int sx = (int) (startX + (x - .5) * sourceSize / 14);
                int sy = (int) (startY + (y - .5) * sourceSize / 14);
                if (sx < left || sx > right || sy < top || sy > bottom) continue;
                int pixel = generated.getRGB(sx, sy);
                if ((pixel >>> 24) >= 128) template.setRGB(x, y, pixel | 0xFF000000);
            }
        }
        BufferedImage atlas = ImageIO.read(atlasPath.toFile());
        BufferedImage strip = new BufferedImage(768, 64, BufferedImage.TYPE_INT_ARGB);
        BufferedImage preview = new BufferedImage(512, 384, BufferedImage.TYPE_INT_ARGB);
        for (int index = 0; index < LIQUID_COLOURS.length; index++) {
            int colour = LIQUID_COLOURS[index];
            float[] target = hsv(colour);
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    int pixel = template.getRGB(x, y);
                    float[] source = hsv(pixel);
                    // Recolour only the blue reservoir; retain the metal, needle and highlights.
                    if ((pixel >>> 24) != 0 && source[1] > .3f
                            && source[0] >= .45f && source[0] <= .75f) {
                        pixel = Color.HSBtoRGB(target[0], target[1], source[2] * target[2]);
                    }
                    for (int dy = 0; dy < 4; dy++) {
                        for (int dx = 0; dx < 4; dx++) {
                            strip.setRGB(index * 64 + x * 4 + dx, y * 4 + dy, pixel);
                            atlas.setRGB(index * 64 + x * 4 + dx, 1280 + y * 4 + dy, pixel);
                        }
                    }
                    for (int dy = 0; dy < 8; dy++) {
                        for (int dx = 0; dx < 8; dx++) {
                            preview.setRGB(index % 4 * 128 + x * 8 + dx,
                                    index / 4 * 128 + y * 8 + dy, pixel);
                        }
                    }
                }
            }
        }
        if (!Files.exists(backup)) Files.copy(atlasPath, backup);
        ImageIO.write(strip, "png", assets.resolve("plasmid_injectors.png").toFile());
        ImageIO.write(preview, "png", Path.of("docs/plasmid-injectors-preview.png").toFile());
        ImageIO.write(atlas, "png", atlasPath.toFile());
    }

    private static float[] hsv(int colour) {
        return Color.RGBtoHSB(colour >>> 16 & 255, colour >>> 8 & 255, colour & 255, null);
    }
}
