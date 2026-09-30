import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/** Fits a background-removed 4x3 generated sheet to the game's 18x17 frame grid. */
public class PreparePredatorSprites {
    public static void main(String[] args) throws Exception {
        BufferedImage source = ImageIO.read(Path.of(args[0]).toFile());
        BufferedImage sheet = new BufferedImage(288, 204, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 51; y++) {
            for (int x = 0; x < 72; x++) {
                int sx = (int) ((x + .5) * source.getWidth() / 72);
                int sy = (int) ((y + .5) * source.getHeight() / 51);
                int pixel = source.getRGB(sx, sy);
                pixel = (pixel >>> 24) < 128 ? 0 : pixel | 0xFF000000;
                for (int dy = 0; dy < 4; dy++) {
                    for (int dx = 0; dx < 4; dx++) sheet.setRGB(x * 4 + dx, y * 4 + dy, pixel);
                }
            }
        }
        for (int frame = 0; frame < 11; frame++) {
            int opaque = 0, transparent = 0;
            int left = frame % 4 * 72, top = frame / 4 * 68;
            for (int y = top; y < top + 68; y++) {
                for (int x = left; x < left + 72; x++) {
                    if ((sheet.getRGB(x, y) >>> 24) == 0) transparent++;
                    else opaque++;
                }
            }
            if (opaque == 0 || transparent == 0) {
                throw new IllegalStateException("Invalid predator animation frame " + frame);
            }
        }
        ImageIO.write(sheet, "png", Path.of("core/src/main/assets/predator.png").toFile());
    }
}
