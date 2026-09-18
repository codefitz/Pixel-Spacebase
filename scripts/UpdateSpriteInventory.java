import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Refreshes physical sheet and frame sizes while preserving hand-written notes. */
public class UpdateSpriteInventory {
    private static final Pattern PNG = Pattern.compile("`([^`]+\\.png)`");
    private static final Pattern SIZE = Pattern.compile("(\\d+)x(\\d+)");

    public static void main(String[] args) throws Exception {
        Path inventory = Path.of("SPRITE_INVENTORY.md");
        List<String> updated = new ArrayList<>();
        for (String line : Files.readAllLines(inventory)) {
            String[] cells = line.split("\\|", -1);
            if (cells.length < 7) {
                updated.add(line);
                continue;
            }
            Matcher asset = PNG.matcher(cells[2]);
            Matcher frame = SIZE.matcher(cells[4]);
            if (!frame.find()) {
                updated.add(line);
                continue;
            }
            String assetName = asset.find() ? asset.group(1) :
                    cells[2].contains("hero class sheet") ? "commander.png" : null;
            if (assetName == null) {
                updated.add(line);
                continue;
            }
            Path png = Path.of("core/src/main/assets", assetName);
            BufferedImage image = ImageIO.read(png.toFile());
            if (cells[3].trim().equals(image.getWidth() + "x" + image.getHeight())) {
                updated.add(line);
                continue;
            }
            int frameWidth = Integer.parseInt(frame.group(1)) * 4;
            int frameHeight = Integer.parseInt(frame.group(2)) * 4;
            int columns = image.getWidth() / frameWidth;
            int rows = image.getHeight() / frameHeight;
            cells[3] = " " + image.getWidth() + "x" + image.getHeight() + " ";
            cells[4] = " " + frameWidth + "x" + frameHeight + " ";
            cells[5] = " " + columns + "x" + rows + " ";
            cells[6] = " " + columns * rows + " ";
            updated.add(String.join("|", cells));
        }
        Files.write(inventory, updated);
    }
}
