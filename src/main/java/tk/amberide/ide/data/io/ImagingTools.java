package tk.amberide.ide.data.io;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 *
 * @author Tudor
 */
public class ImagingTools {

    public static BufferedImage trimAlpha(BufferedImage img) {
        int x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = 0, y2 = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int argb = img.getRGB(x, y);
                if (argb != 0) {
                    x1 = Math.min(x1, x);
                    y1 = Math.min(y1, y);
                    x2 = Math.max(x2, x);
                    y2 = Math.max(y2, y);
                }
            }
        }
        final ColorModel cm = img.getColorModel();
        final WritableRaster r = img.getRaster().createWritableChild(x1, y1, x2 - x1, y2 - y1, 0, 0, null);
        return new BufferedImage(cm, r, cm.isAlphaPremultiplied(), null);
    }

    public static BufferedImage scaleImage(BufferedImage img, int width, int height) {
        double scaledWidth = Math.min((double) width / img.getWidth(), (double) height / img.getHeight());
        Image scaledImage = img.getScaledInstance((int) (img.getWidth() * scaledWidth), (int) (img.getHeight() * scaledWidth), Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(
                scaledImage.getWidth(null),
                scaledImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
        return bufferedImage;
    }
}
