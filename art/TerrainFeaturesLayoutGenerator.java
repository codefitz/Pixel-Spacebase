import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class TerrainFeaturesLayoutGenerator {
    private static final int COLUMNS = 16;
    private static final int ROWS = 8;
    private static final int CELL = 64;
    private static final int HEADER = 58;
    private static final int LABEL = 34;

    private static final String[] SHAPES = {
            "DOTS", "WAVES", "GRILL", "STARS", "DIAMOND", "CROSSHAIR", "LARGE DOT", "MINES / STATES"
    };

    private static final String[] COLORS = {
            "red", "orange", "yellow", "green", "teal", "violet", "white", "grey", "black",
            "unused", "unused", "unused", "unused", "unused", "unused", "unused"
    };

    private static final String[] MINE_LABELS = {
            "fire mine", "ice mine", "venom mine", "flash mine",
            "kolto pod", "weak field", "teleport pod", "hunter trap",
            "alien egg", "disorient", "knockout", "adrenal",
            "unused", "unused", "damage A", "damage B"
    };

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: TerrainFeaturesLayoutGenerator <atlas.png> <output.png>");
        }

        BufferedImage atlas = ImageIO.read(new File(args[0]));
        if (atlas.getWidth() != COLUMNS * CELL || atlas.getHeight() != ROWS * CELL) {
            throw new IllegalArgumentException("Expected a 1024x512 terrain-feature atlas");
        }

        int width = COLUMNS * CELL;
        int height = HEADER + ROWS * (CELL + LABEL);
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(19, 22, 24));
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(238, 240, 240));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        g.drawString("Pixel Spacebase terrain-feature overlay frames", 12, 23);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        g.setColor(new Color(190, 196, 198));
        g.drawString("Visual background: core/src/main/assets/terrain_features.png  |  16 columns x 8 rows", 12, 42);
        g.drawString("Trap frame = colour column + (shape row x 16)", 12, 54);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int frame = row * COLUMNS + col;
                int x = col * CELL;
                int y = HEADER + row * (CELL + LABEL);

                g.drawImage(atlas, x, y, x + CELL, y + CELL,
                        x, row * CELL, x + CELL, (row + 1) * CELL, null);

                g.setColor(new Color(244, 32, 135));
                g.drawRect(x, y, CELL - 1, CELL - 1);

                String number = Integer.toString(frame);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
                FontMetrics numberMetrics = g.getFontMetrics();
                int badgeWidth = numberMetrics.stringWidth(number) + 6;
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(x + 2, y + 2, badgeWidth, 17);
                g.setColor(Color.WHITE);
                g.drawString(number, x + 5, y + 15);

                g.setColor(new Color(238, 240, 240));
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));
                drawCentered(g, category(frame, row, col), x, y + CELL + 10, CELL);
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
                drawCentered(g, detail(frame, row, col), x, y + CELL + 22, CELL);
            }
        }

        g.dispose();
        ImageIO.write(output, "png", new File(args[1]));
    }

    private static void drawCentered(Graphics2D g, String text, int x, int baseline, int width) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, x + Math.max(2, (width - metrics.stringWidth(text)) / 2), baseline);
    }

    private static String category(int frame, int row, int col) {
        if (frame == 13 || frame == 14) {
            return "EMBERS";
        }
        if (row < 7) {
            return col <= 8 ? SHAPES[row] : "UNUSED";
        }
        if (col <= 11) {
            return "MINE";
        }
        if (col >= 14) {
            return "DAMAGE";
        }
        return "UNUSED";
    }

    private static String detail(int frame, int row, int col) {
        if (frame == 13) {
            return "variant A";
        }
        if (frame == 14) {
            return "variant B";
        }
        if (row < 7) {
            return col <= 8 ? COLORS[col] : "";
        }
        return MINE_LABELS[col];
    }
}
