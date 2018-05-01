package util;

import algorithms.RootFinder.SpecificIdRootFinder;
import algorithms.Separator.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class SeparatorTester {
    public static void runTest(String inputFileName, int trials, boolean rndMaxDegRoot, String outputFileName) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph(inputFileName);
        System.out.println("Graph loaded");
        runTest(g, inputFileName, trials, rndMaxDegRoot, outputFileName);
    }

    public static void runTest(SelfDualGraph g, String graphType, int trials, boolean rndMaxDegRoot, String outputFileName) throws FileNotFoundException {
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
        out.printf("Graph Info:\t%s\tNumber of Vertices\t%d\n", graphType, g.getVertexNum());
        out.printf("Current run parameter:\tuse_max_degree_root = %b\n", rndMaxDegRoot);
        out.printf("\t\tLevel Separator\t\t\tFundamental Cycle Separator\t\t\tModifiedFCS\t\t\tLipton-Tarjan Separator\t\t\tSimple Cycle Separator\n");
        out.printf("\tSeparator Size\tBalance Ratio\tRuntime (ms)\tSeparator Size\tBalance Ratio\tRuntime (ms)\tSeparator Size\tBalance Ratio\tRuntime (ms)\tSeparator Size\tBalance Ratio\tRuntime (s)\tSeparator Size\tBalance Ratio\tRuntime (s)\n");
        Random random = new Random(-1);
        Separator sp;
        for (int i = 0; i < trials; i++) {
            System.out.printf("Iteration %d\n", i);
            Vertex root = rootCandidates.get(random.nextInt(rootCandidates.size()));
            StringBuilder sb = new StringBuilder(String.format("%d", i));

            sp = new LevelSeparator(g);
            testSeparator(sp, root, sb);
            System.out.println("LevelSeparator done");

            sp = new FundamentalCycleSeparator(g);
            testSeparator(sp, root, sb);
            System.out.println("FundamentalCycleSeparator done");

            sp = new ModifiedFCS(g);
            testSeparator(sp, root, sb);
            System.out.println("ModifiedFCS done");

            sp = new LiptonTarjanSeparator(g);
            testSeparator(sp, root, sb);
            System.out.println("LiptonTarjan done");

            sp = new SimpleCycleSeparator(g);
            testSeparator(sp, root, sb);
            System.out.println("SimpleCycleSeparator done");

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
        for (int i = 6; i <= 6; i++) {
            System.out.println(i);
            String input = String.format("./input_data/grids/%d.txt", i);
            String output = String.format("./output/grids/%d.txt", i);
            runTest(input, i == 6 ? 1 : 32, false, output);
        }
    }

    public static void testCylinder() throws FileNotFoundException {
        String[] types = new String[]{"rnd", "symm", "unsymm"};
        for (String type : types) {
            for (int i = 1; i <= 6; i++) {
                String input = String.format("./input_data/cylinder/%s/%d.txt", type, i);
                String output = String.format("./output/cylinder/%s/%d.txt", type, i);
                runTest(input, 32, false, output);
            }
        }
    }

    public static void testSphere() throws FileNotFoundException {
        for (int i = 1; i <= 12; i++) {
            System.out.println(i);
            SelfDualGraph g = new SelfDualGraph();

            g.buildGraph("./input_data/sphere/c_0.txt");
            SphereGenerator rsg = new SphereGenerator(g);
            rsg.generateRandomSubgraph(i);
            String output = String.format("./output/sphere/c_%d.txt", i);
            runTest(g, "sphere_Cube", 32, false, output);

            g = new SelfDualGraph();
            g.buildGraph("./input_data/sphere/t_0.txt");
            rsg = new SphereGenerator(g);
            rsg.generateRandomSubgraph(i);
            output = String.format("./output/sphere/t_%d.txt", i);
            runTest(g, "sphere_Tetrahedron", 32, false, output);
        }
    }

    public static void testRandom() throws FileNotFoundException {
        for (int i = 1; i <= 5; i++) {
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph("./input_data/random/0.txt");
            RandomSubgraphGenerator rsg = new RandomSubgraphGenerator(g);
            rsg.generateRandomGraph(i);
            String output = String.format("./output/random/%d.txt", i);
            runTest(g, String.format("random %d", i), 32, false, output);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        testGrids();
        //testCylinder();

        //testSphere();
        //testRandom();
    }
}
