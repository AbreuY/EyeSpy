package shu.apps.eyespy.utilities;

import android.media.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Saves a JPEG {@link Image} into the specified {@link File}.
 * Runs on another thread, so not to block the main UI thread.
 */
public class ImageSaver implements Runnable {

    private Logger LOGGER = Logger.getLogger(ImageSaver.class.getName());

    private final Image mImage; // Image that we are wanting to save.
    private final File mFile; // File that we are going to save to.

    public ImageSaver(Image image, File file) {
        this.mImage = image;
        this.mFile = file;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer(); // As image is saved as JPEG, get the first plane.
        byte[] bytes = new byte[buffer.remaining()]; // Create array with the size of the bytebuffer
        buffer.get(bytes); // Populate the array with the bytes from the buffer.

        FileOutputStream output = null;

        try {
            output = new FileOutputStream(mFile); // Open the file we would like to save too.
            output.write(bytes); // Write the bytes to the file.
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Trying to write image to file failed. ", e);
        } finally {
            mImage.close();
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close FileOutputStream. ", e);
                }
            }
        }
    }
}
