package util;

import algorithms.RDivision.*;
import algorithms.SSSP.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class SSSPTester {

    public static void runTest(SelfDualGraph g, String graphType, int trials, int[] Rs, String outputFileName) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFileName);
        out.printf("Graph Info:\t%s\tNumber of Vertices\t%d\n", graphType, g.getVertexNum());
        out.printf("\tRSD SSSP\t\t\t\t\t\tDijkstra SSSP\n");
        out.printf("\tRuntime (ms)\tRuntime (ms)\tRuntime (ms)\tRuntime (ms)\tRuntime (ms)\tRuntime (ms)\tRuntime (ms)\tRuntime (ms)\n");
        GraphDivider gd = new RecursiveDivider(g);
        gd.rDivision(Rs[0]);
        System.out.println("Worm-up run finished");

        for (int i = 0; i < Rs.length; i++) {
            Vertex[] sources = new Vertex[trials];
            Iterator<Vertex> iter = g.getVertices().iterator();
            for (int j=0; j<trials; j++) {
                sources[j] = iter.next();
            }
            int r = Rs[i];
            System.out.printf("r = %d\n", r);
            StringBuilder sb = new StringBuilder(String.format("%d", r));

            SSSP sssp = new RegionalSpeculativeDijkstra(g, new FredDivider(g),SSSP.CAPACITY_AS_DISTANCE);
            for (int j = 0; j < trials; j++) {
                testSSSP(sssp, sources[j], r, sb);
            }
            System.out.println("RegionalSpeculativeDijkstra done");

            sssp = new Dijkstra(g, SSSP.CAPACITY_AS_DISTANCE);
            for (int j = 0; j < trials; j++) {
                testSSSP(sssp, sources[j], r, sb);
            }
            System.out.println("Dijkstra done");

            out.println(sb.toString());
        }

        out.close();
    }

    public static void testSSSP(SSSP sssp, Vertex src, int r, StringBuilder sb) {
        long startTime = System.nanoTime();
        sssp.findSSSP(src, r);
        long endTime = System.nanoTime();
        String str = String.format("\t%.2f", (endTime - startTime) / 1000000.0);
        sb.append(str);
        System.out.println(str);
    }

    public static void testGridsCylinder(int[] Rs, int[] fileSize, String[] paths) throws FileNotFoundException {
        for (int j = 0; j < paths.length; j++) {
            String path = paths[j];
            int size = fileSize[j];
            String inputFileName = String.format("./input_data/%s/%d.txt", path, size);
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph(inputFileName);
            System.out.println("Graph loaded");
            String outputFileName = String.format("./output/r-division/%s_%d.txt", path, size);
            runTest(g, String.format("%s_%d", path, size), 4, Rs, outputFileName);

        }
    }

    public static void testRandom(int[] Rs, int size) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/random/0.txt");
        RandomSubgraphGenerator rsg1 = new RandomSubgraphGenerator(g);
        rsg1.generateRandomGraph(size);
        System.out.println("Graph loaded");
        String output = String.format("./output/r-division/random_%d.txt", size);
        runTest(g, String.format("random_%d", size), 4, Rs, output);
    }

    public static void testSphere(int[] Rs, int size) throws FileNotFoundException {
        String[] names = new String[]{"c", "t"};
        for (String name : names) {
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph(String.format("./input_data/sphere/%s_0.txt", name));
            SphereGenerator rsg2 = new SphereGenerator(g);
            rsg2.generateRandomSubgraph(size);
            System.out.println("Graph loaded");
            String output = String.format("./output/r-division/sphere_%s.txt", name);
            runTest(g, String.format("Sphere_%s_12", name), 4, Rs, output);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        int[] Rs = new int[]{400, 600, 800, 900, 1000, 1100, 1200, 1400, 1600, 1800, 2000};
        String[] paths = new String[]{"grids", "cylinder/rnd", "cylinder/symm", "cylinder/unsymm"};
        //String[] paths = new String[]{"cylinder/symm", "cylinder/unsymm"};
        int[] fileSize = new int[]{5, 5, 5, 5};
        testGridsCylinder(Rs, fileSize, paths);
        testRandom(Rs, 5);
        testSphere(Rs, 11);
    }
}
