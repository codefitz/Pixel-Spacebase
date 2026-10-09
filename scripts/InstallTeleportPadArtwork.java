import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/** Packs transparent circular overlays and previews them on the deck floor textures. */
public class InstallTeleportPadArtwork {
    public static void main(String[] args) throws Exception {
        Path artwork = Path.of(args[0]);
        Path assets = Path.of("core/src/main/assets");
        BufferedImage strip = new BufferedImage(128, 64, BufferedImage.TYPE_INT_ARGB);
        BufferedImage[] icons = new BufferedImage[2];
        String[] names = {"orange", "blue"};
        for (int i = 0; i < names.length; i++) {
            icons[i] = fit(ImageIO.read(artwork.resolve(names[i] + "-source.png").toFile()));
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 64; x++) strip.setRGB(i * 64 + x, y, icons[i].getRGB(x, y));
            }
            ImageIO.write(icons[i], "png", artwork.resolve(names[i] + "-pad.png").toFile());
        }
        ImageIO.write(strip, "png", assets.resolve("teleport_pads.png").toFile());

        String[] floors = {"tiles1.png", "tiles2.png", "tiles3.png", "tiles4.png",
                "holodeck_tiles_active.png", "holodeck_tiles_powered_down.png"};
        String[] labels = {"Security", "Engineering", "Habitat", "Command", "Holodeck", "Holodeck offline"};
        BufferedImage preview = new BufferedImage(768, 352, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = preview.createGraphics();
        g.setColor(new Color(0x0A141D));
        g.fillRect(0, 0, preview.getWidth(), preview.getHeight());
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        for (int i = 0; i < floors.length; i++) {
            BufferedImage atlas = ImageIO.read(assets.resolve(floors[i]).toFile());
            int visual = i == 2 ? 64 : 1;
            BufferedImage floor = atlas.getSubimage(visual % 16 * 64, visual / 16 * 64, 64, 64);
            int left = i % 3 * 256, top = i / 3 * 176;
            g.setColor(new Color(0xD7E8F4));
            g.drawString(labels[i], left + 20, top + 23);
            for (int direction = 0; direction < 2; direction++) {
                int x = left + 20 + direction * 116;
                int y = top + 40;
                g.drawImage(floor, x, y, 104, 104, null);
                g.drawImage(icons[direction], x, y, 104, 104, null);
            }
        }
        g.dispose();
        ImageIO.write(preview, "png", artwork.resolve("deck-preview.png").toFile());
    }

    private static BufferedImage fit(BufferedImage source) {
        int left = source.getWidth(), top = source.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getRGB(x, y) >>> 24) < 128) continue;
                left = Math.min(left, x); right = Math.max(right, x);
                top = Math.min(top, y); bottom = Math.max(bottom, y);
            }
        }
        if (right < left) throw new IllegalArgumentException("Empty teleport pad");
        double size = Math.max(right - left + 1, bottom - top + 1);
        double startX = (left + right + 1 - size) / 2;
        double startY = (top + bottom + 1 - size) / 2;
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for (int y = 2; y < 30; y++) {
            for (int x = 2; x < 30; x++) {
                int sx = (int) (startX + (x - 1.5) * size / 28);
                int sy = (int) (startY + (y - 1.5) * size / 28);
                if (sx < left || sx > right || sy < top || sy > bottom) continue;
                int pixel = source.getRGB(sx, sy);
                if ((pixel >>> 24) < 128) continue;
                pixel |= 0xFF000000;
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) icon.setRGB(x * 2 + dx, y * 2 + dy, pixel);
                }
            }
        }
        return icon;
    }
}
