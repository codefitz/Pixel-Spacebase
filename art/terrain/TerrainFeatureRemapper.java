import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class TerrainFeatureRemapper {
    private static final int CELL = 64;
    private static final int COLUMNS = 16;
    private static final int FEATURE_ROWS = 7;

    private static final int MINE_START = 112;
    private static final int MINE_END = 123;
    private static final int DAMAGE_A = 126;
    private static final int DAMAGE_B = 127;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: TerrainFeatureRemapper <terrain_features.png>");
        }

        File atlasFile = new File(args[0]);
        BufferedImage atlas = ImageIO.read(atlasFile);
        if (atlas.getWidth() != COLUMNS * CELL || atlas.getHeight() != 8 * CELL) {
            throw new IllegalArgumentException("Expected a 1024x512 terrain-feature atlas");
        }

        BufferedImage source = copy(atlas);

        // Reuse the twelve existing mine/device graphics for every legacy feature row.
        for (int row = 0; row < FEATURE_ROWS; row++) {
            for (int column = 0; column <= MINE_END - MINE_START; column++) {
                copyCell(source, atlas, MINE_START + column, row * COLUMNS + column);
            }
        }

        // The old ember-like frames occupied columns 13 and 14 in the first five rows.
        for (int row = 0; row < 5; row++) {
            copyCell(source, atlas, DAMAGE_A, row * COLUMNS + 13);
            copyCell(source, atlas, DAMAGE_B, row * COLUMNS + 14);
        }

        // Column 12 has no mapped source, and column 15 is unused in these rows.
        for (int row = 0; row < FEATURE_ROWS; row++) {
            clearCell(atlas, row * COLUMNS + 12);
            clearCell(atlas, row * COLUMNS + 15);
        }

        ImageIO.write(atlas, "png", atlasFile);
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

    private static void clearCell(BufferedImage atlas, int cell) {
        Graphics2D g = atlas.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect((cell % COLUMNS) * CELL, (cell / COLUMNS) * CELL, CELL, CELL);
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
