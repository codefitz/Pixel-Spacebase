import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/** Desktop pixel-level checks, kept outside Android's restricted Java test bootclasspath. */
public class VerifyClassArmourArtwork {
    private static final String[] HEROES = {"commander", "captain", "dm3000", "shapeshifter"};

    public static void main(String[] args) throws Exception {
        VerifyClassArmourArtwork checks = new VerifyClassArmourArtwork();
        checks.dedicatedSheetsProvideEveryPoseAtFull64PixelResolution();
        checks.shapeshifterKeepsItsExactExistingArtworkInTheSquareFrames();
        checks.originalTributePosesAndIconsRemainRecoverable();
        System.out.println("Three class-armour pixel checks passed: 84 square poses, fine detail, exact Cthulhu pixels and legacy recovery.");
    }

    public void dedicatedSheetsProvideEveryPoseAtFull64PixelResolution() throws Exception {
        for (String hero : HEROES) {
            String file = "class_armor_" + hero + ".png";
            BufferedImage image = read("src/main/assets/" + file);
            assertEquals(file, 21 * 64, image.getWidth());
            assertEquals(file, 64, image.getHeight());
            for (int pose = 0; pose < 21; pose++) {
                int visible = 0;
                for (int y = 0; y < 64; y++) {
                    for (int x = 0; x < 64; x++) {
                        int alpha = image.getRGB(pose * 64 + x, y) >>> 24;
                        if (alpha != 0) visible++;
                        if (x == 0 || x == 63 || y == 0 || y == 63) {
                            assertEquals(file + " pose " + pose + " clips its frame", 0, alpha);
                        }
                    }
                }
                assertTrue(file + " has an empty/sparse pose " + pose, visible > 100);
            }
            if (!hero.equals("shapeshifter")) {
                int fineDetails = 0;
                for (int y = 0; y < 64; y += 4) {
                    for (int x = 0; x < 64; x += 4) {
                        int pixel = image.getRGB(x, y);
                        for (int dy = 0; dy < 4; dy++) for (int dx = 0; dx < 4; dx++) {
                            if (pixel != image.getRGB(x + dx, y + dy)) fineDetails++;
                        }
                    }
                }
                assertTrue(file + " lost its high-resolution detail", fineDetails > 200);
            }
        }
    }

    public void shapeshifterKeepsItsExactExistingArtworkInTheSquareFrames() throws Exception {
        BufferedImage original = read("../docs/sprite-backups/shapeshifter-before-class-armour.png");
        BufferedImage current = read("src/main/assets/shapeshifter.png");
        assertImageEquals(original, current);
        BufferedImage dedicated = read("src/main/assets/class_armor_shapeshifter.png");
        for (int pose = 0; pose < 21; pose++) {
            for (int y = 0; y < 60; y++) for (int x = 0; x < 48; x++) {
                assertEquals(original.getRGB(pose * 48 + x, 360 + y), dedicated.getRGB(pose * 64 + 8 + x, 2 + y));
            }
        }
    }

    public void originalTributePosesAndIconsRemainRecoverable() throws Exception {
        String[] heroes = {"commander", "captain", "dm3000"};
        String[] tributes = {"darth-vader", "metroid", "robocop"};
        int[] slots = {165, 168, 166};
        BufferedImage originalItems = read("../docs/sprite-backups/items-before-class-armour.png");
        for (int i = 0; i < heroes.length; i++) {
            BufferedImage original = read("../docs/sprite-backups/" + heroes[i] + "-before-class-armour.png");
            BufferedImage legacy = read("../art/class-armour/legacy/" + tributes[i] + "-" + heroes[i] + ".png");
            assertImageEquals(original.getSubimage(0, 360, 1008, 60), legacy);
            BufferedImage icon = read("../art/class-armour/legacy/" + tributes[i] + "-icon.png");
            assertImageEquals(originalItems.getSubimage(slots[i] % 16 * 64, slots[i] / 16 * 64, 64, 64), icon);
        }
    }

    private static BufferedImage read(String path) throws Exception {
        path = path.startsWith("../") ? path.substring(3) : "core/" + path;
        BufferedImage image = ImageIO.read(new File(path));
        assertNotNull(path, image);
        return image;
    }

    private static void assertImageEquals(BufferedImage expected, BufferedImage actual) {
        assertEquals(expected.getWidth(), actual.getWidth());
        assertEquals(expected.getHeight(), actual.getHeight());
        for (int y = 0; y < expected.getHeight(); y++) for (int x = 0; x < expected.getWidth(); x++) {
            assertEquals("Pixel " + x + "," + y, expected.getRGB(x, y), actual.getRGB(x, y));
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        assertEquals("Values differ", expected, actual);
    }

    private static void assertEquals(String message, Object expected, Object actual) {
        if (!expected.equals(actual)) throw new AssertionError(message + ": expected " + expected + ", got " + actual);
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) throw new AssertionError(message);
    }

    private static void assertNotNull(String message, Object value) {
        if (value == null) throw new AssertionError(message);
    }
}
