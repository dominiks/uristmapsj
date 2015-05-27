package org.uristmaps;

import com.esotericsoftware.minlog.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.uristmaps.Uristmaps.conf;

/**
 * Created by dominik on 27.05.2015.
 */
public class BmpConverter {

    public static void convert() {
        Log.debug("BmpConverter", "Started");
        File[] bmps = new File(conf.fetch("Paths", "region")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith(".bmp"));

        for (File bmp : bmps) {
            try {
                Log.debug("BmpConverter", "Converting " + bmp);
                BufferedImage image = ImageIO.read(bmp);
                ImageIO.write(image, "PNG", new File(Util.removeExtension(bmp.getAbsolutePath()) + ".png"));
                bmp.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
