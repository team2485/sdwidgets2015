package edu.wpi.first.wpijavacv;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.awt.image.BufferedImage;

/**
 * This class is the superclass of all images.
 *
 * @author Greg Granito
 */
public class WPIImage extends WPIDisposable {

    /** The underlying {@link IplImage} */
    protected IplImage image;

    /**
     * Instantiates a {@link WPIImage} from a {@link BufferedImage}.
     * Useful for interacting with swing.
     * This will not keep a reference to the given image, so any modification to this
     * {@link WPIImage} will not change the given image.
     * @param image the image to copy into a {@link WPIImage}
     */
    public WPIImage(BufferedImage image) {
        this(IplImage.createFrom(image));
    }

    /**
     * Instantiates a {@link WPIImage} from the given {@link IplImage}.
     * The resulting image will be directly wrapped around the given image, and so any modifications
     * to the {@link WPIImage} will reflect on the given image.
     * @param image the image to wrap
     */
    public WPIImage(IplImage image) {
        this.image = image;
    }

    /**
     * Returns the width of the image.
     * @return the width in pixels of the image
     */
    public int getWidth() {
        validateDisposed();

        return image.width();
    }

    /**
     * Returns the height of the image.
     * @return the height in pixels of the image
     */
    public int getHeight() {
        validateDisposed();

        return image.height();
    }

    /**
     * Copies this {@link WPIImage} into a {@link BufferedImage}.
     * This method will always generate a new image.
     * @return a copy of the image
     */
    public BufferedImage getBufferedImage() {
        validateDisposed();

        return image.getBufferedImage();
    }

    /**
     * Gets a reference to the IplImage.
     * @return the IplImage
     */
    public IplImage getIplImage() {
        return image;
    }

    @Override
    protected void disposed() {
        image.release();
    }
}
