import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class PanelTileUpdater {
    private static final int TILE = 64;
    private static final int COLUMNS = 16;

    public static void main(String[] args) throws Exception {
        File atlasFile = new File(args[0]);
        BufferedImage atlas = ImageIO.read(atlasFile);
        BufferedImage source = copy(atlas);

        // Mirror the first exposed-wire tile to make an edge-compatible variation.
        copyTileMirrored(source, atlas, 9, 41);

        // Loose panel variant one: an otherwise normal panel with a restrained crack.
        copyTile(source, atlas, 1, 15, 0, 0);
        Graphics2D g = tileGraphics(atlas, 15);
        g.setColor(new Color(69, 75, 73));
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g.drawLine(46, 25, 41, 31);
        g.drawLine(41, 31, 45, 36);
        g.drawLine(41, 31, 36, 34);
        g.dispose();

        // Loose panel variant two: shift the normal panel to expose a thin cable edge.
        copyTile(source, atlas, 9, 44, 0, 0);
        copyTile(source, atlas, 1, 44, 4, 0);

        ImageIO.write(atlas, "png", atlasFile);

        File featuresFile = new File(args[1]);
        BufferedImage features = ImageIO.read(featuresFile);
        drawDamageOverlay(features, 126, false);
        drawDamageOverlay(features, 127, true);
        ImageIO.write(features, "png", featuresFile);
    }

    private static Graphics2D tileGraphics(BufferedImage atlas, int tile) {
        Graphics2D g = atlas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.translate((tile % COLUMNS) * TILE, (tile / COLUMNS) * TILE);
        g.clipRect(0, 0, TILE, TILE);
        return g;
    }

    private static void copyTile(BufferedImage source, BufferedImage target,
                                 int sourceTile, int targetTile, int xOffset, int yOffset) {
        int sx = (sourceTile % COLUMNS) * TILE;
        int sy = (sourceTile / COLUMNS) * TILE;
        int tx = (targetTile % COLUMNS) * TILE + xOffset;
        int ty = (targetTile / COLUMNS) * TILE + yOffset;
        int width = TILE - Math.max(0, xOffset);
        int height = TILE - Math.max(0, yOffset);
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(source, tx, ty, tx + width, ty + height,
                sx, sy, sx + width, sy + height, null);
        g.dispose();
    }

    private static void copyTileMirrored(BufferedImage source, BufferedImage target,
                                         int sourceTile, int targetTile) {
        int sx = (sourceTile % COLUMNS) * TILE;
        int sy = (sourceTile / COLUMNS) * TILE;
        int tx = (targetTile % COLUMNS) * TILE;
        int ty = (targetTile / COLUMNS) * TILE;
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(source, tx, ty, tx + TILE, ty + TILE,
                sx + TILE, sy, sx, sy + TILE, null);
        g.dispose();
    }

    private static void drawDamageOverlay(BufferedImage features, int tile, boolean alternate) {
        Graphics2D g = tileGraphics(features, tile);
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, TILE, TILE);
        g.setComposite(AlphaComposite.SrcOver);

        g.setColor(new Color(13, 14, 15, 150));
        g.fillRect(alternate ? 12 : 20, 20, 36, 28);
        g.fillRect(alternate ? 20 : 12, 28, 36, 20);
        g.setColor(new Color(38, 34, 31, 190));
        g.fillRect(alternate ? 24 : 28, 20, 16, 32);
        g.fillRect(alternate ? 16 : 24, 28, 32, 16);

        g.setColor(new Color(255, 116, 20, 255));
        g.fillRect(alternate ? 16 : 44, 16, 4, 8);
        g.fillRect(alternate ? 44 : 16, 40, 8, 4);
        g.setColor(new Color(255, 224, 72, 255));
        g.fillRect(alternate ? 16 : 44, 16, 4, 4);
        g.fillRect(alternate ? 48 : 16, 40, 4, 4);
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
