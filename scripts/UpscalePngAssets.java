import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** One-time migration of game atlases to four source pixels per game unit. */
public class UpscalePngAssets {
    private static final int SCALE = 4;

    public static void main(String[] args) throws IOException {
        Path root = Path.of("core/src/main/assets");
        Path marker = root.resolve("asset_pixel_scale.txt");
        if (Files.exists(marker)) {
            throw new IllegalStateException("Assets are already migrated; edit the 4x PNGs directly.");
        }

        List<Path> pngs;
        try (Stream<Path> paths = Files.walk(root)) {
            pngs = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".png"))
                    .filter(path -> !path.getFileName().toString().equals("pixel_font.png"))
                    .filter(path -> !path.getFileName().toString().equals("font1x.png"))
                    .filter(path -> !path.getFileName().toString().equals("font2x.png"))
                    .sorted()
                    .collect(Collectors.toList());
        }

        for (Path path : pngs) {
            BufferedImage source = ImageIO.read(path.toFile());
            if (source == null) {
                throw new IOException("Cannot read PNG: " + path);
            }
            BufferedImage enlarged = new BufferedImage(source.getWidth() * SCALE,
                    source.getHeight() * SCALE, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < source.getHeight(); y++) {
                for (int x = 0; x < source.getWidth(); x++) {
                    int color = source.getRGB(x, y);
                    for (int dy = 0; dy < SCALE; dy++) {
                        for (int dx = 0; dx < SCALE; dx++) {
                            enlarged.setRGB(x * SCALE + dx, y * SCALE + dy, color);
                        }
                    }
                }
            }
            Path output = Files.createTempFile(path.getParent(), "upscaled-", ".png");
            try {
                if (!ImageIO.write(enlarged, "png", output.toFile())) {
                    throw new IOException("PNG writer unavailable");
                }
                Files.move(output, path, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(output);
            }
        }
        Files.writeString(marker, Integer.toString(SCALE) + "\n", StandardCharsets.US_ASCII);
        System.out.println("Upscaled " + pngs.size() + " PNG assets to " + SCALE + "x source resolution.");
    }
}
