package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.apache.commons.io.FilenameUtils;
import org.uristmaps.util.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.uristmaps.Uristmaps.conf;

/**
 * DOCME
 */
public class BmpConverter {

    /**
     * DOCME
     */
    public static void convert() {
        Log.debug("BmpConverter", "Started");

        File[] bmps = new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith(".bmp"));

        Progress progress = new TimedProgress(bmps.length, 10000, "BmpConverter");
        for (File bmp : bmps) {
            try {
                Log.debug("BmpConverter", "Converting " + bmp);
                BufferedImage image = ImageIO.read(bmp);
                ImageIO.write(image, "PNG", new File(FilenameUtils.removeExtension(bmp.getAbsolutePath()) + ".png"));
                bmp.delete();
                progress.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
