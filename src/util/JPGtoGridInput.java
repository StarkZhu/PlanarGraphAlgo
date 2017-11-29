package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class JPGtoGridInput {
    public final BufferedImage jpgImage;
    public final int[][][] pixelRGB;

    public JPGtoGridInput(String fileName) throws IOException {
        jpgImage = ImageIO.read(new File(fileName));
        pixelRGB = convertTo2DWithoutUsingGetRGB(jpgImage);
    }

    /**
     * https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
     * get pixel data from a BufferedImage is much faster without using getRGB()
     *
     * @param image
     * @return
     */
    public int[][][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][][] result = new int[height][width][4];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                result[row][col][0] = (((int) pixels[pixel] & 0xff) << 24); // alpha
                result[row][col][1] = ((int) pixels[pixel + 1] & 0xff); // blue
                result[row][col][2] = (((int) pixels[pixel + 2] & 0xff) << 8); // green
                result[row][col][3] = (((int) pixels[pixel + 3] & 0xff) << 16); // red
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                result[row][col][0] = 255; // 255 alpha
                result[row][col][1] = ((int) pixels[pixel] & 0xff); // blue
                result[row][col][2] = (((int) pixels[pixel + 1] & 0xff) << 8); // green
                result[row][col][3] = (((int) pixels[pixel + 2] & 0xff) << 16); // red
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }

    /**
     * auto-generate input txt file for the grid planar graph, follow format in 'input_format.txt'
     */
    public void generateGridInput() {

    }


    public static void main(String[] args) throws IOException {
        JPGtoGridInput testImg = new JPGtoGridInput("./input_data/test_img_4.jpeg");
        System.out.println(testImg.jpgImage.getWidth());
        System.out.println(testImg.jpgImage.getHeight());
        System.out.println(testImg.jpgImage.getAlphaRaster() != null);
    }
}
