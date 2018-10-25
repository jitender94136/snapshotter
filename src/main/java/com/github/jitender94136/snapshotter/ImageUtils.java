package com.github.jitender94136.snapshotter;

import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Utils to be used for images related tasks
 *
 * @author erandi
 */
public class ImageUtils {

    private static Java2DFrameConverter frameConverter = new Java2DFrameConverter();
    private static OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();


    /**
     * Method to get resized buffered image when user passes the relevant frame and video panel.
     *
     * @param frame      frame to be converted to {@link BufferedImage}
     * @param videoPanel the {@link JPanel} which is to be used to obtain panel size
     * @return resized {@link BufferedImage}
     */
    public static BufferedImage getResizedBufferedImage(Frame frame, JPanel videoPanel) {
        BufferedImage resizedImage = null;

        try {
            /*
             * We get notified about the frames that are being added. Then we pass each frame to BufferedImage. I have used
             * a library called Thumbnailator to achieve the resizing effect along with performance
             */
            resizedImage = Thumbnails.of(frameConverter.getBufferedImage(frame))
                    .size(videoPanel.getWidth(), videoPanel.getHeight())
                    .asBufferedImage();
        } catch (IOException e) {
           System.out.println("Unable to convert the image to a buffered image"+e);
        }

        return resizedImage;
    }
}