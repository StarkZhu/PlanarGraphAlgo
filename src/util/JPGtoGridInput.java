package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class JPGtoGridInput {
    public final String fileName;
    public final BufferedImage jpgImage;
    public final int[][][] pixelRGB;
    public final int vNum, eNum, fNum;

    public JPGtoGridInput(String fileName) throws IOException {
        this.fileName = fileName.split("\\.jp")[0];
        jpgImage = ImageIO.read(new File(fileName));
        pixelRGB = convertTo2DWithoutUsingGetRGB(jpgImage);
        int r = jpgImage.getHeight();
        int c = jpgImage.getWidth();
        vNum = r * c;
        eNum = ((r - 1) * c + (c - 1) * r) * 2;
        fNum = (r - 1) * (c - 1) + 1;
    }

    /**
     * get pixel data from a BufferedImage
     *
     * @param image
     * @return
     */
    public int[][][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        int w = image.getWidth();
        int h = image.getHeight();
        int[] pixels = image.getRGB(0, 0, w, h, null, 0, w);

        int[][][] result = new int[h][w][4];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                Color c = new Color(pixels[i * w + j]);
                result[i][j][0] = c.getAlpha();
                result[i][j][1] = c.getRed();
                result[i][j][2] = c.getGreen();
                result[i][j][3] = c.getBlue();
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
        int[] rgb1 = pixelRGB[v1[0]][v1[1]];
        int[] rgb2 = pixelRGB[v2[0]][v2[1]];
        double diff = 0;
        for (int i = 1; i < 4; i++) {
            diff += Math.abs(rgb1[i] - rgb2[i]) * 1.0;
        }
        return diff * rgb1[0] / 255.0;
    }

    /**
     * auto-generate input txt file for the grid planar graph, follow format in 'input_format.txt'
     */
    public void generateGridInput() throws FileNotFoundException {
        generateGridInput(String.format("%s_grid.txt", fileName));
    }

    public void generateGridInput(String outputFileName) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFileName);
        out.printf("%d %d %d\n", vNum, eNum, fNum);

        String vertices = generateVerticesString();
        out.print(vertices);
        String darts = generateDartsString();
        out.print(darts);
        String faces = generateFacesString();
        out.print(faces);
        out.close();
    }

    public String generateFacesString() {
        int r = jpgImage.getHeight();
        int c = jpgImage.getWidth();
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

        // all degree-4 inner faces
        for (int i = 0; i < r - 1; i++) {
            for (int j = 0; j < c - 1; j++) {
                int faceId = i * (c - 1) + j + 1;
                int d1 = (i * (2 * c - 1) + j) * 2;
                int d2 = (i * (2 * c - 1) + (c - 1) + j + 1) * 2;
                int d3 = ((i + 1) * (2 * c - 1) + j) * 2 + 1;
                int d4 = (i * (2 * c - 1) + (c - 1) + j) * 2 + 1;
                faces.append(String.format("%d 4 %d %d %d %d\n", faceId, d1, d2, d3, d4));
            }
        }
        return faces.toString();
    }

    public String generateDartsString() {
        int r = jpgImage.getHeight();
        int c = jpgImage.getWidth();
        // write darts: [ID reverse_ID tail_V_ID head_V_ID weight=1.0 capacity]
        StringBuilder darts = new StringBuilder();
        for (int i = 0; i < r - 1; i++) {
            // c - 1 horizontal edges
            for (int j = 0; j < c - 1; j++) {
                int edge = i * (2 * c - 1) + j;
                int d1 = edge * 2;
                int d2 = edge * 2 + 1;
                int v1 = i * c + j;
                int v2 = v1 + 1;
                double capacity = getCapacity(new int[]{i, j}, new int[]{i, j + 1});
                darts.append(String.format("%d %d %d %d 1 %f\n", d1, d2, v1, v2, capacity));
                darts.append(String.format("%d %d %d %d 1 %f\n", d2, d1, v2, v1, capacity));
            }
            // c vertical edges
            for (int j = 0; j < c; j++) {
                int edge = i * (2 * c - 1) + (c - 1) + j;
                int d1 = edge * 2;
                int d2 = edge * 2 + 1;
                int v1 = i * c + j;
                int v2 = v1 + c;
                double capacity = getCapacity(new int[]{i, j}, new int[]{i + 1, j});
                darts.append(String.format("%d %d %d %d 1 %f\n", d1, d2, v1, v2, capacity));
                darts.append(String.format("%d %d %d %d 1 %f\n", d2, d1, v2, v1, capacity));
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
            darts.append(String.format("%d %d %d %d 1 %f\n", d1, d2, v1, v2, capacity));
            darts.append(String.format("%d %d %d %d 1 %f\n", d2, d1, v2, v1, capacity));
        }
        return darts.toString();
    }

    public String generateVerticesString() {
        int r = jpgImage.getHeight();
        int c = jpgImage.getWidth();
        int vNum = r * c;
        // write vertices: [ID coordX coordY]
        StringBuilder vertices = new StringBuilder();
        for (int i = 0; i < vNum; i++) {
            vertices.append(String.format("%d %d %d\n", i, i / c, i % c));
        }
        return vertices.toString();
    }


    public static void main(String[] args) throws IOException {
        JPGtoGridInput testImg = new JPGtoGridInput("./input_data/test_img_3x4.jpeg");
        System.out.println(testImg.jpgImage.getWidth());
        System.out.println(testImg.jpgImage.getHeight());
        testImg.generateGridInput();
    }
}
