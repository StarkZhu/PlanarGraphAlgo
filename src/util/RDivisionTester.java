package util;

import algorithms.RDivision.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class RDivisionTester {
    public static void runTest(SelfDualGraph g, String graphType, int trials, int r, String outputFileName) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFileName);
        out.printf("Graph Info:\t%s\tNumber of Vertices\t%d, Regsion Size\t%d\n", graphType, g.getVertexNum(), r);
        out.printf("\t\tRecursive Divider\t\t\tFrederickson Divider\n");
        out.printf("\tRegion Number\tRuntime (ms)\tRegion Number\tRuntime (ms)\n");
        GraphDivider gd;
        for (int i = 0; i < trials; i++) {
            System.out.printf("Iteration %d\n", i);
            StringBuilder sb = new StringBuilder(String.format("%d", i));

            gd = new RecursiveDivider(g);
            testDivider(gd, r, sb);
            System.out.println("RecursiveDivider done");

            gd = new FredDivider(g);
            testDivider(gd, r, sb);
            System.out.println("FredDivider done");

            out.println(sb.toString());
        }

        out.close();
    }

    public static void testDivider(GraphDivider gd, int r, StringBuilder sb) {
        long startTime = System.nanoTime();
        Set<Set<Vertex>> regions = gd.rDivision(r);
        long endTime = System.nanoTime();
        sb.append(String.format("\t%d\t%.2f", regions.size(), (endTime - startTime) / 1000000.0));
    }

    public static void testGridsCylinder(int[] Rs) throws FileNotFoundException {
        String[] paths = new String[]{"grids", "cylinder/rnd", "cylinder/symm", "cylinder/unsymm"};
        int[] fileIndex = new int[]{5, 6, 6, 6};
        for (int j = 0; j < paths.length; j++) {
            String path = paths[j];
            int index = fileIndex[j];
            String inputFileName = String.format("./input_data/%s/%d.txt", path, index);
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph(inputFileName);
            System.out.println("Graph loaded");
            for (int i = 0; i < Rs.length; i++) {
                int r = Rs[i];
                System.out.println(r);
                String outputFileName = String.format("./output/r-division/%s/%d.txt", path, i + 1);
                runTest(g, inputFileName, 16, r, outputFileName);
            }
        }
    }

    public static void testRandom(int[] Rs) throws FileNotFoundException {
        SelfDualGraph g_r5 = new SelfDualGraph();
        g_r5.buildGraph("./input_data/random/0.txt");
        RandomSubgraphGenerator rsg1 = new RandomSubgraphGenerator(g_r5);
        rsg1.generateRandomGraph(5);
        for (int i = 0; i < Rs.length; i++) {
            int r = Rs[i];
            System.out.println(r);
            String output = String.format("./output/r-division/random/%d.txt", i + 1);
            runTest(g_r5, "random_5", 16, r, output);
        }
    }

    public static void testSphere(int[] Rs) throws FileNotFoundException {
        String[] names = new String[]{"c", "t"};
        for (String name : names) {
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph(String.format("./input_data/sphere/%s_0.txt", name));
            SphereGenerator rsg2 = new SphereGenerator(g);
            rsg2.generateRandomSubgraph(12);
            for (int i = 0; i < Rs.length; i++) {
                int r = Rs[i];
                System.out.println(r);
                String output = String.format("./output/r-division/sphere/%s_%d.txt", name, i + 1);
                runTest(g, String.format("Sphere_%s_12", name), 16, r, output);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        int[] Rs = new int[]{25, 50, 100, 200, 400, 600, 800, 1000, 1200, 1600, 2000, 4000, 10000, 20000, 40000, 100000};
        //int[] Rs = new int[]{100, 200};
        //testGridsCylinder(Rs);
        testRandom(Rs);
        //testSphere(Rs);
    }
}
