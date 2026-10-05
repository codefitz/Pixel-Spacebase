import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Fits generated art to native 16-pixel terrain and NPC cells without editing station atlases. */
public class InstallAlienPlanetArtwork {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("Expected terrain sheet and alien source");
        Path docs = Path.of("docs/artwork/alien-planet");
        Path assets = Path.of("core/src/main/assets");
        Files.createDirectories(docs);
        BufferedImage source = ImageIO.read(Path.of(args[0]).toFile());
        BufferedImage tiles = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 32; y++) for (int x = 0; x < 32; x++) {
            int sx = Math.min(source.getWidth()-1, (int)((x+.5)*source.getWidth()/32));
            int sy = Math.min(source.getHeight()-1, (int)((y+.5)*source.getHeight()/32));
            tiles.setRGB(x, y, source.getRGB(sx,sy) | 0xFF000000);
        }
        BufferedImage alien = ImageIO.read(Path.of(args[1]).toFile());
        int left=alien.getWidth(), top=alien.getHeight(), right=-1, bottom=-1;
        for (int y=0;y<alien.getHeight();y++) for(int x=0;x<alien.getWidth();x++) {
            if ((alien.getRGB(x,y)>>>24)<128) continue;
            left=Math.min(left,x); top=Math.min(top,y); right=Math.max(right,x); bottom=Math.max(bottom,y);
        }
        if(right<left) throw new IllegalArgumentException("Empty alien source");
        double scale=Math.min(14.0/(right-left+1),14.0/(bottom-top+1));
        double ox=(16-(right-left+1)*scale)/2, oy=(16-(bottom-top+1)*scale)/2;
        BufferedImage sprite=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
        for(int y=0;y<16;y++) for(int x=0;x<16;x++) {
            int sx=(int)Math.floor(left+(x+.5-ox)/scale), sy=(int)Math.floor(top+(y+.5-oy)/scale);
            if(sx<left||sx>right||sy<top||sy>bottom) continue;
            int pixel=alien.getRGB(sx,sy);
            if((pixel>>>24)>=128) sprite.setRGB(x,y,pixel|0xFF000000);
        }
        ImageIO.write(tiles,"png",assets.resolve("alien_planet_tiles.png").toFile());
        ImageIO.write(sprite,"png",assets.resolve("alien_colonist.png").toFile());
        Files.copy(Path.of(args[0]),docs.resolve("terrain-generated.png"),StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Path.of(args[1]),docs.resolve("colonist-generated.png"),StandardCopyOption.REPLACE_EXISTING);
        BufferedImage preview=new BufferedImage(400,256,BufferedImage.TYPE_INT_ARGB);
        for(int y=0;y<256;y++) for(int x=0;x<256;x++) preview.setRGB(x,y,tiles.getRGB(x/8,y/8));
        for(int y=0;y<128;y++) for(int x=0;x<128;x++) preview.setRGB(264+x,64+y,sprite.getRGB(x/8,y/8));
        ImageIO.write(preview,"png",docs.resolve("preview.png").toFile());
        System.out.println("Installed new alien planet tiles and colonist sprite.");
    }
}
