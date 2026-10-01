import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Packs the generated 2x2 artwork into the existing habitat atlas layout. */
public class PackTransporterTiles {
    public static void main(String[] args) throws Exception {
        BufferedImage source = ImageIO.read(new File(args[0]));
        BufferedImage old = ImageIO.read(new File(args[1]));
        if (old.getWidth() != 1024 || old.getHeight() > 512) throw new IllegalArgumentException("Unexpected atlas size");
        // Retain power-of-two texture dimensions for older supported devices.
        BufferedImage atlas = new BufferedImage(1024, 512, BufferedImage.TYPE_INT_ARGB);
        for (int y=0;y<old.getHeight();y++) for(int x=0;x<old.getWidth();x++) atlas.setRGB(x,y,old.getRGB(x,y));
        int[] targets = {8,7,64,65}; // arrival, departure, floor, wall
        Graphics2D g = atlas.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        for(int i=0;i<4;i++) {
            int sx=(i%2)*source.getWidth()/2,sy=(i/2)*source.getHeight()/2;
            int ex=(i%2+1)*source.getWidth()/2,ey=(i/2+1)*source.getHeight()/2;
            int x=targets[i]%16*64,y=targets[i]/16*64;
            g.drawImage(source,x,y,x+64,y+64,sx,sy,ex,ey,null);
        }
        g.dispose();
        for(int y=0;y<old.getHeight();y++) for(int x=0;x<old.getWidth();x++) {
            int index=x/64+(y/64)*16;
            if(index!=7&&index!=8&&index!=64&&index!=65&&atlas.getRGB(x,y)!=old.getRGB(x,y))
                throw new IllegalStateException("Unexpected change in tile "+index);
        }
        for(int tile:targets) for(int y=0;y<64;y++) for(int x=0;x<64;x++)
            if((atlas.getRGB(tile%16*64+x,tile/16*64+y)>>>24)!=255)throw new IllegalStateException("Transparent transport tile "+tile);
        ImageIO.write(atlas,"png",new File(args[2]));

        BufferedImage preview=new BufferedImage(704,368,BufferedImage.TYPE_INT_RGB);
        g=preview.createGraphics();g.setColor(new Color(16,23,31));g.fillRect(0,0,704,368);
        for(int room=0;room<2;room++) for(int y=0;y<5;y++) for(int x=0;x<5;x++) {
            int tile=x==0||y==0||x==4||y==4?65:64;
            if(x==2&&y==2)tile=room==0?8:7;
            if(x==2&&y==4)tile=5;
            int dx=room*352+x*64,dy=y*64;
            g.drawImage(atlas,dx,dy,dx+64,dy+64,tile%16*64,tile/16*64,tile%16*64+64,tile/16*64+64,null);
        }
        g.setColor(Color.WHITE);g.drawString("Arrival: previous deck",16,349);g.drawString("Departure: next deck",368,349);g.dispose();
        ImageIO.write(preview,"png",new File(args[3]));
        System.out.println("Atlas 1024x512: tiles 7/8 replaced; 64/65 added; all other existing pixels preserved. All four new tiles opaque.");
    }
}
