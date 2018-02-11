package util;

import algorithms.LCAHeuristic.CombinedHeuristic;
import algorithms.LCAHeuristic.DistToLeafHeuristic;
import algorithms.LCAHeuristic.DistToRootHeuristic;
import algorithms.LCAHeuristic.ExactLCA;
import algorithms.RootFinder.*;
import algorithms.Separator.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class CompareLCA {
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
        out.printf("\t\tExact LCA\t\tDistToRoot Heuristic\t\t\tDistToLeaf Heuristic\t\t\tCombined Heuristic\n");
        out.printf("\tSeparator Size\tBalance Ratio\tRuntime (ms)\tSeparator Size\tBalance Ratio\tRuntime (ms)\tSeparator Size\tBalance Ratio\tRuntime (ms)\tSeparator Size\tBalance Ratio\tRuntime (ms)\n");
        // TODO: for testing and comparison purpose
        Random random = new Random(-1);
        Separator sp;
        for (int i = 0; i < trials; i++) {
            System.out.printf("Iteration %d\n", i);
            Vertex root = rootCandidates.get(random.nextInt(rootCandidates.size()));
            StringBuilder sb = new StringBuilder(String.format("%d", i));

            sp = new ModifiedFCS(g, new ExactLCA());
            testSeparator(sp, root, sb);
            System.out.println("DistToRootHeuristic done");

            sp = new ModifiedFCS(g, new DistToRootHeuristic());
            testSeparator(sp, root, sb);
            System.out.println("DistToRootHeuristic done");

            sp = new ModifiedFCS(g, new DistToLeafHeuristic());
            testSeparator(sp, root, sb);
            System.out.println("DistToLeafHeuristic done");

            sp = new ModifiedFCS(g, new CombinedHeuristic());
            testSeparator(sp, root, sb);
            System.out.println("CombinedHeuristic done");

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

    public static void runTest() throws FileNotFoundException {
        String[] types = new String[]{"cylinder/rnd/4", "cylinder/symm/4", "cylinder/unsymm/4", "./grids/4", "./random/4", "./sphere/c_8"};
        for (String type : types) {
            String input = String.format("./input_data/%s.txt", type);
            String output = String.format("./output/lcaHeuristic/4/%s.txt", type.substring(type.indexOf("/"), type.lastIndexOf("/")));
            runTest(input, 64, false, output);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        runTest();
    }
}
