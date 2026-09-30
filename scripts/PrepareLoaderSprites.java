import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Packs generated loader poses and each existing hero's head into 4x game atlases. */
public class PrepareLoaderSprites {
    // Extra transparent width accommodates the punching claw without resizing the chassis.
    private static final int WIDTH = 48, HEIGHT = 28, SCALE = 4;

    public static void main(String[] args) throws Exception {
        Path sourcePath = Path.of(args[0]);
        Path assets = Path.of("core/src/main/assets");
        BufferedImage source = ImageIO.read(sourcePath.toFile());
        BufferedImage[] poses = new BufferedImage[12];
        int cw = source.getWidth() / 4, ch = source.getHeight() / 3;
        for (int pose = 0; pose < 12; pose++) {
            int cellWidth = pose == 6 ? cw + cw / 8 : cw;
            poses[pose] = source.getSubimage((pose % 4) * cw, (pose / 4) * ch, cellWidth, ch);
        }
        // Source coordinates of the cockpit center in each cell, supplied after art inspection.
        String[] coordinates = args[1].split(",");
        if (coordinates.length != 20) throw new IllegalArgumentException("Ten cockpit x,y pairs required");
        int[] cx = new int[10], cy = new int[10];
        for (int i = 0; i < 10; i++) {
            cx[i] = Integer.parseInt(coordinates[i * 2]);
            cy[i] = Integer.parseInt(coordinates[i * 2 + 1]);
        }
        // One uniform scale keeps the chassis the same size in walking and punching poses.
        double ratio = Double.parseDouble(args[2]);
        BufferedImage chassis = new BufferedImage(WIDTH * 4, HEIGHT * 3, BufferedImage.TYPE_INT_ARGB);
        for (int pose = 0; pose < 12; pose++) {
            BufferedImage cell = poses[pose];
            int bottom = 0;
            for (int y = 0; y < ch; y++) for (int x = 0; x < cell.getWidth(); x++) {
                if ((cell.getRGB(x, y) >>> 24) >= 128) bottom = Math.max(bottom, y);
            }
            int anchorX = pose < 10 ? cx[pose] : cw / 2;
            int offsetX = WIDTH / 2 - (int) Math.round(anchorX * ratio);
            int offsetY = HEIGHT - 2 - (int) Math.round(bottom * ratio);
            Graphics2D graphics = chassis.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int tx = (pose % 4) * WIDTH, ty = (pose / 4) * HEIGHT;
            graphics.setClip(tx, ty, WIDTH, HEIGHT);
            graphics.drawImage(cell, tx + offsetX, ty + offsetY,
                    (int) Math.round(cell.getWidth() * ratio), (int) Math.round(ch * ratio), null);
            graphics.dispose();
            // Retain per-frame cockpit y after uniform scaling and grounding.
            if (pose < 10) cy[pose] = offsetY + (int) Math.round(cy[pose] * ratio);
        }
        BufferedImage preview = new BufferedImage(32 * SCALE * 4, HEIGHT * SCALE, BufferedImage.TYPE_INT_ARGB);
        int heroIndex = 0;
        for (String hero : new String[]{"commander", "dm3000", "shapeshifter", "captain"}) {
            BufferedImage heroSheet = ImageIO.read(assets.resolve(hero + ".png").toFile());
            BufferedImage atlas = new BufferedImage(WIDTH * 4, HEIGHT * 3, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = atlas.createGraphics();
            graphics.drawImage(chassis, 0, 0, null);
            graphics.dispose();
            for (int pose = 0; pose < 10; pose++) {
                // Fit only the naked hero's head into the cockpit, using nearest-neighbour pixels.
                for (int y = 0; y < 6; y++) for (int x = 0; x < 6; x++) {
                    int sx = 2 + x * 8 / 6, sy = y * 7 / 6;
                    int pixel = heroSheet.getRGB(sx * SCALE + 2, sy * SCALE + 2);
                    if ((pixel >>> 24) < 128) continue;
                    int tx = (pose % 4) * WIDTH + WIDTH / 2 - 3 + x;
                    int ty = (pose / 4) * HEIGHT + cy[pose] - 3 + y;
                    if (ty < (pose / 4) * HEIGHT || ty >= (pose / 4 + 1) * HEIGHT) {
                        throw new IllegalStateException("Cockpit head outside frame " + pose);
                    }
                    atlas.setRGB(tx, ty, pixel);
                }
            }
            BufferedImage output = upscale(atlas);
            Path destination = assets.resolve("loader_" + hero + ".png");
            ImageIO.write(output, "png", destination.toFile());
            Graphics2D pg = preview.createGraphics();
            pg.drawImage(output.getSubimage((WIDTH - 32) / 2 * SCALE, 0, 32 * SCALE, HEIGHT * SCALE),
                    heroIndex++ * 32 * SCALE, 0, null);
            pg.dispose();
            validate(output, hero);
        }
        Path art = Path.of("art/heavy-loader");
        Files.createDirectories(art);
        Files.copy(sourcePath, art.resolve("loader-source.png"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        ImageIO.write(preview, "png", art.resolve("hero-loader-preview.png").toFile());
        System.out.println("Packed and validated twelve RGBA frames for all four heroes.");
    }

    private static BufferedImage upscale(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth() * SCALE, image.getHeight() * SCALE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < result.getHeight(); y++) for (int x = 0; x < result.getWidth(); x++) {
            result.setRGB(x, y, image.getRGB(x / SCALE, y / SCALE));
        }
        return result;
    }

    private static void validate(BufferedImage sheet, String hero) {
        for (int pose = 0; pose < 12; pose++) {
            int opaque = 0, clear = 0;
            for (int y = 0; y < HEIGHT * SCALE; y++) for (int x = 0; x < WIDTH * SCALE; x++) {
                int alpha = sheet.getRGB((pose % 4) * WIDTH * SCALE + x, (pose / 4) * HEIGHT * SCALE + y) >>> 24;
                if (alpha == 0) clear++; else if (alpha >= 128) opaque++;
            }
            if (clear == 0 || opaque == 0) throw new IllegalStateException(hero + " invalid alpha/frame " + pose);
        }
    }
}
