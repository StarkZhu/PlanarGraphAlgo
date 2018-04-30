package util;

import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class CylinderGenerator {
    private SelfDualGraph g;

    public CylinderGenerator(SelfDualGraph g) {
        this.g = g;
    }

    /**
     * the same G is modified
     * to get a different graph, G need to be reloaded from text file, since it does not support deep copy yet
     *
     * @param magnitude 1 ~ 5
     * @return
     */
    public void generateWeirdCylinders(int magnitude) {
        //int limit = (int) Math.pow(10, magnitude);
        int limit = 2;
        Iterator<Vertex> it = g.getFaces().iterator();
        Vertex outerFace = it.next();
        while (outerFace.getID() != 0) outerFace = it.next();

        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < 3; j++) {
                Vertex newV = g.addVertex(outerFace);
                Vertex minNeighbor = newV.getFirstDart().getHead();
                for (Dart d : newV.getIncidenceList()) {
                    if (d.getHead().getID() < minNeighbor.getID()) minNeighbor = d.getHead();
                }
                Dart newDart = newV.getFirstDart();
                for (Dart d : newV.getIncidenceList()) {
                    if (d.getID() < newDart.getID()) newDart = d;
                }
                outerFace = newDart.getLeft();

            }
            outerFace.setDart(outerFace.getFirstDart().getPrev());
            //System.out.println(outerFace.getID());
        }
        g.renumberIDs();
    }

    /**
     * randomly pick a newly added face, add a vertex on it
     *
     * @param magnitude
     */
    public void generateRandomCylinders(int magnitude) {
        int limit = 3 * (int) Math.pow(10, magnitude);
        Vertex newFace = g.getFaces().iterator().next();
        for (int i = 0; i < limit; i++) {
            Vertex newV = g.addVertex(newFace);
            Dart newDart = newV.getFirstDart();
            int rnd = (int) (Math.random() * 3);
            for (int j = 0; j < rnd; j++) newDart = newDart.getSuccessor();
            newFace = newDart.getLeft();
        }
        g.renumberIDs();
    }

    /**
     * hard code the process of generating each ring
     *
     * @param magnitude 1 ~ 5
     * @return
     */
    public void generateySymmetricCylinder(int magnitude, String outputFileName) throws FileNotFoundException {
        int limit = (int) Math.pow(10, magnitude);
        int vID = 3;
        int dID = 6;
        int fID = 1;
        StringBuilder vString = new StringBuilder("0 0 1\n1 -1 0\n2 1 0\n");
        StringBuilder dString = new StringBuilder("0 1 0 1\n1 0 1 0\n2 3 1 2\n3 2 2 1\n4 5 2 0\n5 4 0 2\n");
        StringBuilder fString = new StringBuilder("0 3 5 3 1\n");
        for (int i = 0; i < limit; i++) {
            vString.append(String.format("%d 0 %d\n", vID++, i + 2));
            vString.append(String.format("%d %d 0\n", vID++, -(i + 2)));
            vString.append(String.format("%d %d 0\n", vID++, i + 2));

            for (int j = 0; j < 3; j++) {
                dString.append(String.format("%d %d %d %d\n%d %d %d %d\n",
                        dID, dID + 1, i * 3 + j, (i + 1) * 3 + j,
                        dID + 1, dID, (i + 1) * 3 + j, i * 3 + j));
                dID += 2;
            }
            for (int j = 0; j < 3; j++) {
                dString.append(String.format("%d %d %d %d\n%d %d %d %d\n",
                        dID, dID + 1, i * 3 + j, (i + 1) * 3 + (j + 1) % 3,
                        dID + 1, dID, (i + 1) * 3 + (j + 1) % 3, i * 3 + j));
                dID += 2;
            }
            for (int j = 0; j < 3; j++) {
                dString.append(String.format("%d %d %d %d\n%d %d %d %d\n",
                        dID, dID + 1, (i + 1) * 3 + j, (i + 1) * 3 + (j + 1) % 3,
                        dID + 1, dID, (i + 1) * 3 + (j + 1) % 3, (i + 1) * 3 + j));
                dID += 2;
            }

            for (int j = 0; j < 3; j++) {
                fString.append(String.format("%d 3 %d %d %d\n", fID++,
                        i * 18 + j * 2, 6 + ((j + 1) % 3) * 2 + i * 18, 13 + i * 18 + j * 2));
            }
            for (int j = 0; j < 3; j++) {
                fString.append(String.format("%d 3 %d %d %d\n", fID++,
                        7 + i * 18 + j * 2, 12 + i * 18 + j * 2, 19 + i * 18 + j * 2));
            }
        }

        // out most face
        fString.append(String.format("%d 3 %d %d %d\n", fID++, 18 * limit, 18 * limit + 2, 18 * limit + 4));

        PrintWriter out = new PrintWriter(new File(outputFileName));
        out.print(String.format("%d %d %d\n", 3 + limit * 3, 6 + limit * 18, 2 + limit * 6));
        out.print(vString.toString());
        out.print(dString.toString());
        out.print(fString.toString());
        out.close();
    }

    public void generateUnsymmetricCylinder(int magnitude, String outputFileName) throws FileNotFoundException {
        int limit = (int) Math.pow(10, magnitude);
        //int limit = 1;
        int vID = 3;
        int dID = 6;
        int fID = 1;
        double sqrt3 = Math.sqrt(3);
        StringBuilder vString = new StringBuilder(String.format("0 0 1\n1 %f -0.5\n2 %f -0.5\n", -0.5 * sqrt3, 0.5 * sqrt3));
        StringBuilder dString = new StringBuilder("0 1 0 1\n1 0 1 0\n2 3 1 2\n3 2 2 1\n4 5 2 0\n5 4 0 2\n");
        StringBuilder fString = new StringBuilder("0 3 5 3 1 0 0\n");
        for (int i = 0; i < limit; i++) {
            vString.append(String.format("%d 0 %d\n", vID++, i + 2));
            vString.append(String.format("%d %f %f\n", vID++, -(i + 2) * sqrt3 * 0.5, -0.5 * (i + 2)));
            vString.append(String.format("%d %f %f\n", vID++, (i + 2) * sqrt3 * 0.5, -0.5 * (i + 2)));

            for (int j = 0; j < 3; j++) {
                dString.append(String.format("%d %d %d %d\n%d %d %d %d\n",
                        dID, dID + 1, i * 3 + j, (i + 1) * 3 + j,
                        dID + 1, dID, (i + 1) * 3 + j, i * 3 + j));
                dID += 2;
            }
            for (int j = 0; j < 2; j++) {
                dString.append(String.format("%d %d %d %d\n%d %d %d %d\n",
                        dID, dID + 1, i * 3 + j, (i + 1) * 3 + (j + 1) % 3,
                        dID + 1, dID, (i + 1) * 3 + (j + 1) % 3, i * 3 + j));
                dID += 2;
            }
            dString.append(String.format("%d %d %d %d\n%d %d %d %d\n",
                    dID, dID + 1, i * 3, (i + 1) * 3 + 2,
                    dID + 1, dID, (i + 1) * 3 + 2, i * 3));
            dID += 2;

            for (int j = 0; j < 3; j++) {
                dString.append(String.format("%d %d %d %d\n%d %d %d %d\n",
                        dID, dID + 1, (i + 1) * 3 + j, (i + 1) * 3 + (j + 1) % 3,
                        dID + 1, dID, (i + 1) * 3 + (j + 1) % 3, (i + 1) * 3 + j));
                dID += 2;
            }

            for (int j = 0; j < 2; j++) {
                fString.append(String.format("%d 3 %d %d %d\n", fID++,
                        i * 18 + j * 2, 6 + ((j + 1) % 3) * 2 + i * 18, 13 + i * 18 + j * 2));
            }
            fString.append(String.format("%d 3 %d %d %d\n", fID++,
                    i * 18 + 4, 16 + i * 18, 11 + i * 18));
            for (int j = 0; j < 2; j++) {
                fString.append(String.format("%d 3 %d %d %d\n", fID++,
                        7 + i * 18 + j * 2, 12 + i * 18 + j * 2, 19 + i * 18 + j * 2));
            }
            fString.append(String.format("%d 3 %d %d %d\n", fID++,
                    17 + i * 18, 6 + i * 18, 23 + i * 18));
        }

        // out most face
        fString.append(String.format("%d 3 %d %d %d 0 %d\n", fID++, 18 * limit, 18 * limit + 2, 18 * limit + 4, limit + 2));

        PrintWriter out = new PrintWriter(new File(outputFileName));
        out.print(String.format("%d %d %d\n", 3 + limit * 3, 6 + limit * 18, 2 + limit * 6));
        out.print(vString.toString());
        out.print(dString.toString());
        out.print(fString.toString());
        out.close();
    }

    public static void main(String[] args) throws FileNotFoundException {
        /*
        for (int i = 0; i < 1; i++) {
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph("./input_data/cylinder/0.txt");
            CylinderGenerator cg = new CylinderGenerator(g);
            cg.generateRandomylinders(i + 1);
            g.saveToFile(String.format("./input_data/cylinder/rnd/%d.txt", i + 1));
        }
        */

        for (int i = 0; i < 6; i++) {
            CylinderGenerator cg = new CylinderGenerator(null);
            cg.generateUnsymmetricCylinder(i + 1, String.format("./input_data/cylinder/unsymm/%d.txt", i + 1));

            cg = new CylinderGenerator(null);
            cg.generateySymmetricCylinder(i + 1, String.format("./input_data/cylinder/symm/%d.txt", i + 1));

            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph("./input_data/cylinder/0.txt");
            cg = new CylinderGenerator(g);
            cg.generateRandomCylinders(i + 1);
            g.saveToFile(String.format("./input_data/cylinder/rnd/%d.txt", i + 1));
        }

    }
}
