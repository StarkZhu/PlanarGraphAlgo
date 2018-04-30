package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class JPGtoGridInput {
    public final String fileName;
    public final BufferedImage jpgImage;
    public final byte[][][] pixelRGB;

    public JPGtoGridInput(String fileName) throws IOException {
        this.fileName = fileName.split("\\.jp")[0];
        jpgImage = ImageIO.read(new File(fileName));
        pixelRGB = convertTo2DWithoutUsingGetRGB(jpgImage);
    }

    /**
     * get pixel data from a BufferedImage
     *
     * @param image
     * @return
     */
    public byte[][][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        int w = image.getWidth();
        int h = image.getHeight();
        System.out.printf("w=%d, h=%d, w*h*4*sizeof(int)=%d\n", w, h, w*h*4*4);
        int[] pixels = image.getRGB(0, 0, w, h, null, 0, w);

        byte[][][] result = new byte[h][w][4];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                Color c = new Color(pixels[i * w + j]);
                result[i][j][0] = (byte) c.getAlpha();
                result[i][j][1] = (byte) c.getRed();
                result[i][j][2] = (byte) c.getGreen();
                result[i][j][3] = (byte) c.getBlue();
            }
        }

        return result;
    }

    /**
     * transparency % * (sum of absolute difference in R, G, B channel)
     *
     * @param v1
     * @param v2
     * @return
     */
    private double getCapacity(int[] v1, int v2[]) {
        byte[] rgb1 = pixelRGB[v1[0]][v1[1]];
        byte[] rgb2 = pixelRGB[v2[0]][v2[1]];
        double diff = 0;
        for (int i = 1; i < 4; i++) {
            int tmp1 = rgb1[i] & 0xFF;
            int tmp2 = rgb2[i] & 0xFF;
            diff += Math.abs(tmp1 - tmp2) * 1.0;
        }
        return diff * (rgb1[0] & 0xFF) / 255.0;
    }

    public int getVNum() {
        int r = jpgImage.getHeight();
        int c = jpgImage.getWidth();
        return r * c;
    }

    public int getENum() {
        int r = jpgImage.getHeight();
        int c = jpgImage.getWidth();
        return ((r - 1) * c + (c - 1) * r) * 2;
    }

    public int getFNum() {
        int r = jpgImage.getHeight();
        int c = jpgImage.getWidth();
        return (r - 1) * (c - 1) + 1;
    }

    /**
     * auto-generate input txt file for the grid planar graph, follow format in 'input_format.txt'
     */
    public void generateGridInput() throws FileNotFoundException {
        generateGridInput(String.format("%s_grid.txt", fileName));
    }

    public void generateGridInput(String outputFileName) throws FileNotFoundException {
        generateGridInput(outputFileName, jpgImage.getHeight(), jpgImage.getWidth());
    }

    public void generateGridInput(String outputFileName, int r, int c) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFileName);
        int vNum = r * c;
        int eNum = ((r - 1) * c + (c - 1) * r) * 2;
        int fNum = (r - 1) * (c - 1) + 1;
        out.printf("%d %d %d\n", vNum, eNum, fNum);

        generateVerticesString(out, r, c);
        generateDartsString(out, r, c);
        generateFacesString(out, r, c);
        out.close();
    }

    public void generateVerticesString(PrintWriter out, int r, int c) {
        int vNum = r * c;
        // write vertices: [ID coordX coordY]
        for (int i = 0; i < vNum; i++) {
            out.printf("%d %d %d\n", i, i / c, i % c);
        }
    }

    public void generateDartsString(PrintWriter out, int r, int c) {
        // write darts: [ID reverse_ID tail_V_ID head_V_ID weight=1.0 capacity]
        for (int i = 0; i < r - 1; i++) {
            // c - 1 horizontal edges
            for (int j = 0; j < c - 1; j++) {
                int edge = i * (2 * c - 1) + j;
                int d1 = edge * 2;
                int d2 = edge * 2 + 1;
                int v1 = i * c + j;
                int v2 = v1 + 1;
                double capacity = getCapacity(new int[]{i, j}, new int[]{i, j + 1});
                out.printf("%d %d %d %d 1 %f\n", d1, d2, v1, v2, capacity);
                out.printf("%d %d %d %d 1 %f\n", d2, d1, v2, v1, capacity);
            }
            // c vertical edges
            for (int j = 0; j < c; j++) {
                int edge = i * (2 * c - 1) + (c - 1) + j;
                int d1 = edge * 2;
                int d2 = edge * 2 + 1;
                int v1 = i * c + j;
                int v2 = v1 + c;
                double capacity = getCapacity(new int[]{i, j}, new int[]{i + 1, j});
                out.printf("%d %d %d %d 1 %f\n", d1, d2, v1, v2, capacity);
                out.printf("%d %d %d %d 1 %f\n", d2, d1, v2, v1, capacity);
            }
        }
        // c - 1 horizontal edges in the last row
        for (int j = 0; j < c - 1; j++) {
            int edge = (r - 1) * (2 * c - 1) + j;
            int d1 = edge * 2;
            int d2 = edge * 2 + 1;
            int v1 = (r - 1) * c + j;
            int v2 = v1 + 1;
            double capacity = getCapacity(new int[]{r - 1, j}, new int[]{r - 1, j + 1});
            out.printf("%d %d %d %d 1 %f\n", d1, d2, v1, v2, capacity);
            out.printf("%d %d %d %d 1 %f\n", d2, d1, v2, v1, capacity);
        }
    }

    public void generateFacesString(PrintWriter out, int r, int c) {

        // write faces: [D degree_N ...N dart_ID in clockwise...]
        StringBuilder faces = new StringBuilder();
        // 1 outer face
        faces.append(String.format("0 %d", 2 * (r + c - 2)));
        for (int i = 0; i < r - 1; i++) {
            int d = (i * (2 * c - 1) + (c - 1)) * 2;
            faces.append(String.format(" %d", d));
        }
        for (int j = 0; j < c - 1; j++) {
            int d = ((r - 1) * (2 * c - 1) + j) * 2;
            faces.append(String.format(" %d", d));
        }
        for (int i = r - 1; i > 0; i--) {
            int d = i * (2 * c - 1) * 2 - 1;
            faces.append(String.format(" %d", d));
        }
        for (int j = c - 2; j >= 0; j--) {
            int d = j * 2 + 1;
            faces.append(String.format(" %d", d));
        }
        faces.append("\n");

        out.print(faces.toString());

        // all degree-4 inner faces
        for (int i = 0; i < r - 1; i++) {
            for (int j = 0; j < c - 1; j++) {
                int faceId = i * (c - 1) + j + 1;
                int d1 = (i * (2 * c - 1) + j) * 2;
                int d2 = (i * (2 * c - 1) + (c - 1) + j + 1) * 2;
                int d3 = ((i + 1) * (2 * c - 1) + j) * 2 + 1;
                int d4 = (i * (2 * c - 1) + (c - 1) + j) * 2 + 1;
                out.printf("%d 4 %d %d %d %d\n", faceId, d1, d2, d3, d4);
            }
        }
    }

    public void generateGrids() throws FileNotFoundException {
        int[][] dims = new int[][]{{9, 7}, {28, 21}, {89, 67}, {282, 211}, {892, 668}, {2820, 2113}};

        generateGridInput(String.format("./input_data/grids/%d.txt", 7));
        for (int i = 5; i < dims.length; i++) {
            generateGridInput(String.format("./input_data/grids/%d.txt", i+1), dims[i][1], dims[i][0]);
        }
    }


    /**
     * to generate grid files from 10^1 to 10^7 magnitude
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        JPGtoGridInput testImg = new JPGtoGridInput("./input_data/grids/heic1509a.jpg");
        System.out.println(testImg.jpgImage.getWidth());
        System.out.println(testImg.jpgImage.getHeight());
        testImg.generateGrids();
    }
}
