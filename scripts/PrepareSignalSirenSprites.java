import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Native 12x15 frames at 4x scale, sampled from a generated 5x3 floating alien sheet. */
public class PrepareSignalSirenSprites {
    public static void main(String[] args) throws Exception {
        Path target = Path.of("core/src/main/assets/signal_siren.png");
        Path backup = Path.of("docs/sprite-backups/signal-siren-before-floating-alien.png");
        if (!Files.exists(backup)) Files.copy(target, backup);
        BufferedImage source = ImageIO.read(Path.of(args[0]).toFile());
        BufferedImage sheet = new BufferedImage(240, 180, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 45; y++) for (int x = 0; x < 60; x++) {
            if (y >= 30 && x >= 36) continue;
            int pixel = source.getRGB((int)((x + .5) * source.getWidth() / 60),
                    (int)((y + .5) * source.getHeight() / 45));
            pixel = (pixel >>> 24) < 128 ? 0 : pixel | 0xFF000000;
            for (int dy = 0; dy < 4; dy++) for (int dx = 0; dx < 4; dx++) {
                sheet.setRGB(x * 4 + dx, y * 4 + dy, pixel);
            }
        }
        for (int frame = 0; frame < 13; frame++) {
            int opaque = 0;
            for (int y = 0; y < 60; y++) for (int x = 0; x < 48; x++) {
                if ((sheet.getRGB(frame % 5 * 48 + x, frame / 5 * 60 + y) >>> 24) != 0) opaque++;
            }
            if (opaque == 0 || opaque == 2880) throw new IllegalStateException("Invalid frame " + frame);
        }
        ImageIO.write(sheet, "png", target.toFile());
        BufferedImage preview = new BufferedImage(720, 540, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 540; y++) for (int x = 0; x < 720; x++) {
            preview.setRGB(x, y, sheet.getRGB(x / 3, y / 3));
        }
        ImageIO.write(preview, "png", Path.of("docs/signal-siren-preview.png").toFile());
    }
}
