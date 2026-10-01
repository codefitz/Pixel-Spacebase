import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Fits the generated 4x3 bug sheet to 12x15 logical frames at the game's 4x asset scale. */
public class PrepareSignalLeechSprites {
    public static void main(String[] args) throws Exception {
        Path target = Path.of("core/src/main/assets/signal_leech.png");
        Path backup = Path.of("docs/sprite-backups/signal-leech-before-plasma-bug.png");
        if (!Files.exists(backup)) Files.copy(target, backup);
        BufferedImage source = ImageIO.read(Path.of(args[0]).toFile());
        BufferedImage sheet = new BufferedImage(192, 180, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 45; y++) for (int x = 0; x < 48; x++) {
            if (x >= 36 && y >= 30) continue;
            int pixel = source.getRGB((int)((x + .5) * source.getWidth() / 48),
                    (int)((y + .5) * source.getHeight() / 45));
            pixel = (pixel >>> 24) < 128 ? 0 : pixel | 0xFF000000;
            for (int dy = 0; dy < 4; dy++) for (int dx = 0; dx < 4; dx++) {
                sheet.setRGB(x * 4 + dx, y * 4 + dy, pixel);
            }
        }
        for (int frame = 0; frame < 11; frame++) {
            int opaque = 0;
            for (int y = 0; y < 60; y++) for (int x = 0; x < 48; x++) {
                if ((sheet.getRGB(frame % 4 * 48 + x, frame / 4 * 60 + y) >>> 24) != 0) opaque++;
            }
            if (opaque == 0 || opaque == 2880) throw new IllegalStateException("Invalid frame " + frame);
        }
        ImageIO.write(sheet, "png", target.toFile());
        BufferedImage preview = new BufferedImage(576, 540, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 540; y++) for (int x = 0; x < 576; x++) {
            preview.setRGB(x, y, sheet.getRGB(x / 3, y / 3));
        }
        ImageIO.write(preview, "png", Path.of("docs/signal-leech-preview.png").toFile());
    }
}
