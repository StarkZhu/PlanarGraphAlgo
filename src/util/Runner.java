package util;

import algorithms.RootFinder.SpecificIdRootFinder;
import algorithms.Separator.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class Runner {
    public static void runTest(String inputFileName, int trials, boolean rndMaxDegRoot, String outputFileName) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph(inputFileName);
        g.flatten();
        g.triangulate();    // g is always triangulated

        Set<Vertex> vertices = g.getVertices();
        if (rndMaxDegRoot) {
            // select all vertices with degree equal to maxDegree
            Set<Vertex> maxDegreeV = new HashSet<>();
            int maxDegree = 0;
            for (Vertex v : g.getVertices()) {
                if (v.getDegree() > maxDegree) {
                    maxDegreeV = new HashSet<>();
                    maxDegreeV.add(v);
                    maxDegree = v.getDegree();
                } else if (v.getDegree() == maxDegree) {
                    maxDegreeV.add(v);
                }
            }
            vertices = maxDegreeV;
        }
        List<Vertex> rootCandidates = new ArrayList<>(vertices);

        PrintWriter out = new PrintWriter(outputFileName);
        out.printf("Graph Info:\t%s\tNumber of Vertices\t%d\n", inputFileName, g.getVertexNum());
        out.printf("Current run parameter:\tuse_max_degree_root = %b\n", rndMaxDegRoot);
        out.printf("\t\tLevel Separator\t\t\tFundamental Cycle Separator\t\t\tLipton-Tarjan Separator\n");
        out.printf("\tSeparator Size\tBalance Ratio\tRuntime (ms)\tSeparator Size\tBalance Ratio\tRuntime (ms)\tSeparator Size\tBalance Ratio\tRuntime (s)\n");
        Random random = new Random(-1);
        Separator sp;
        for (int i = 0; i < trials; i++) {
            System.out.printf("Iteration %d\n", i);
            Vertex root = rootCandidates.get(random.nextInt(rootCandidates.size()));
            StringBuilder sb = new StringBuilder(String.format("%d", i));

            sp = new LevelSeparator(g);
            testSeparator(sp, root, sb);
            System.out.println("LevelSeparator done");

            //sp = new FundamentalCycleSeparator(g);
            sp = new ModifiedFCS(g);
            testSeparator(sp, root, sb);
            System.out.println("FundamentalCycleSeparator done");

            sp = new LiptonTarjanSeparator(g);
            testSeparator(sp, root, sb);

            out.println(sb.toString());
        }

        out.close();
    }

    public static void testSeparator(Separator sp, Vertex root, StringBuilder sb) {
        long startTime = System.nanoTime();
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(root.getID()), null);
        long endTime = System.nanoTime();
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        double ratio = Math.max(subgraphs[0].size() * 1.0 / subgraphs[1].size(), subgraphs[1].size() * 1.0 / subgraphs[0].size());
        sb.append(String.format("\t%d\t%.5f\t%.2f", separator.size(), ratio, (endTime - startTime) / 1000000.0));
    }

    public static void testGrids() throws FileNotFoundException {
        for (int i = 1; i <= 5; i++) {
            String input = String.format("./input_data/grids/%d.txt", i);
            String output = String.format("./output/grids/%d.txt", i);
            runTest(input, 32, false, output);
        }
    }

    public static void testCylinder() throws FileNotFoundException {
        for (int i = 1; i <= 5; i++) {
            String input = String.format("./input_data/cylinder/%d.txt", i);
            String output = String.format("./output/cylinder/%d.txt", i);
            runTest(input, 32, false, output);
        }
    }

    public static void testSphere() throws FileNotFoundException {
        for (int i = 1; i <= 10; i++) {
            String input = String.format("./input_data/sphere/c_%d.txt", i);
            String output = String.format("./output/sphere/c_%d.txt", i);
            runTest(input, 32, false, output);
        }
        for (int i = 1; i <= 11; i++) {
            String input = String.format("./input_data/sphere/t_%d.txt", i);
            String output = String.format("./output/sphere/t_%d.txt", i);
            runTest(input, 32, false, output);
        }
    }

    public static void testRandom() throws FileNotFoundException {
        for (int i = 1; i <= 5; i++) {
            String input = String.format("./input_data/random/%d.txt", i);
            String output = String.format("./output/random/%d.txt", i);
            System.out.println(input);
            runTest(input, 32, false, output);
        }
    }

    public static void testCylinderModified() throws FileNotFoundException {
        for (int i = 1; i <= 5; i++) {
            String input = String.format("./input_data/cylinder/%d.txt", i);
            String output = String.format("./output/cylinder_m/%d.txt", i);
            runTest(input, 32, false, output);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        //testGrids();
        testCylinder();
        //testSphere();
        //testRandom();
        testCylinderModified();
    }
}
