import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class BreakerTileUpdater {
    private static final int CELL = 64;
    private static final int COLUMNS = 16;
    private static final int FLOOR = 1;
    private static final int BREAKER_OFF = 25;
    private static final int BREAKER_ON = 26;

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: BreakerTileUpdater <tiles.png>...");
        }

        for (String path : args) {
            File atlasFile = new File(path);
            BufferedImage atlas = ImageIO.read(atlasFile);
            if (atlas.getWidth() != COLUMNS * CELL || atlas.getHeight() != 4 * CELL) {
                throw new IllegalArgumentException("Expected a 1024x256 terrain atlas: " + path);
            }

            BufferedImage source = copy(atlas);
            copyCell(source, atlas, FLOOR, BREAKER_OFF);
            copyCell(source, atlas, FLOOR, BREAKER_ON);
            drawBreaker(atlas, BREAKER_OFF, false);
            drawBreaker(atlas, BREAKER_ON, true);
            ImageIO.write(atlas, "png", atlasFile);
        }
    }

    private static void drawBreaker(BufferedImage atlas, int cell, boolean powered) {
        Graphics2D g = cellGraphics(atlas, cell);

        // Shadow and outer mounting plate.
        g.setColor(new Color(8, 12, 14, 190));
        g.fillRect(10, 10, 44, 48);
        g.setColor(new Color(24, 32, 35));
        g.fillRect(10, 6, 44, 48);
        g.setColor(new Color(116, 134, 136));
        g.fillRect(14, 10, 36, 40);
        g.setColor(new Color(64, 78, 81));
        g.fillRect(18, 14, 28, 32);

        // Status lamp with a bright core and restrained spill.
        Color statusDark = powered ? new Color(20, 116, 82) : new Color(126, 42, 32);
        Color status = powered ? new Color(72, 238, 166) : new Color(246, 80, 56);
        g.setColor(statusDark);
        g.fillRect(22, 16, 20, 8);
        g.setColor(status);
        g.fillRect(26, 16, 12, 4);

        // Recessed vertical switch track.
        g.setColor(new Color(12, 18, 20));
        g.fillRect(22, 28, 20, 14);
        g.setColor(new Color(42, 52, 54));
        g.fillRect(26, 30, 12, 10);
        g.setColor(new Color(152, 164, 162));
        g.fillRect(28, powered ? 28 : 36, 8, 8);
        g.setColor(new Color(218, 224, 218));
        g.fillRect(28, powered ? 28 : 36, 8, 4);

        // Corner fasteners make the control read as mounted equipment.
        g.setColor(new Color(188, 198, 194));
        g.fillRect(14, 10, 4, 4);
        g.fillRect(46, 10, 4, 4);
        g.fillRect(14, 46, 4, 4);
        g.fillRect(46, 46, 4, 4);
        g.dispose();
    }

    private static Graphics2D cellGraphics(BufferedImage atlas, int cell) {
        Graphics2D g = atlas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.translate((cell % COLUMNS) * CELL, (cell / COLUMNS) * CELL);
        g.clipRect(0, 0, CELL, CELL);
        return g;
    }

    private static void copyCell(BufferedImage source, BufferedImage target,
                                 int sourceCell, int targetCell) {
        int sourceX = (sourceCell % COLUMNS) * CELL;
        int sourceY = (sourceCell / COLUMNS) * CELL;
        int targetX = (targetCell % COLUMNS) * CELL;
        int targetY = (targetCell / COLUMNS) * CELL;
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(source,
                targetX, targetY, targetX + CELL, targetY + CELL,
                sourceX, sourceY, sourceX + CELL, sourceY + CELL, null);
        g.dispose();
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return result;
    }
}
