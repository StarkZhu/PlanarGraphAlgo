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
    public static void runTest(SelfDualGraph g, int trials, boolean rndMaxDegRoot, String outputFileName) throws FileNotFoundException {
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
        out.printf("Graph Info:\tx\tNumber of Vertices\t%d\n", g.getVertexNum());
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
            System.out.println("ExactLCA done");

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
        String str = String.format("\t%d\t%.5f\t%.2f", separator.size(), ratio, (endTime - startTime) / 1000000.0);
        System.out.println(str);
        sb.append(str);
    }

    public static void runTest1() throws FileNotFoundException {
        String[] types = new String[]{"cylinder/rnd/5", "cylinder/symm/5", "cylinder/unsymm/5", "./grids/5"};
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            SelfDualGraph g;
            String input = String.format("./input_data/%s.txt", type);
            g = new SelfDualGraph();
            g.buildGraph(input);
            String output = String.format("./output/lcaHeuristic/5/%s.txt", type.substring(type.indexOf("/"), type.lastIndexOf("/")));
            runTest(g, 32, false, output);
        }
    }

    public static void runTest2() throws FileNotFoundException {
        SelfDualGraph g;
        g = new SelfDualGraph();
        g.buildGraph("./input_data/random/0.txt");
        RandomSubgraphGenerator rsg = new RandomSubgraphGenerator(g);
        rsg.generateRandomGraph(5);
        runTest(g, 32, false, "./output/lcaHeuristic/5/random.txt");

        g = new SelfDualGraph();
        g.buildGraph("./input_data/sphere/c_0.txt");
        SphereGenerator sg = new SphereGenerator(g);
        sg.generateRandomSubgraph(11);
        runTest(g, 32, false, "./output/lcaHeuristic/5/sphere_c.txt");

        g = new SelfDualGraph();
        g.buildGraph("./input_data/sphere/t_0.txt");
        sg = new SphereGenerator(g);
        sg.generateRandomSubgraph(11);
        runTest(g, 32, false, "./output/lcaHeuristic/5/sphere_t.txt");
    }

    public static void tryRootSelection() throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/cylinder/unsymm/3.txt");
        g.flatten();
        g.triangulate();    // g is always triangulated

        PrintWriter out = new PrintWriter("./output/lcaHeuristic/3/result.txt");
        out.printf("Graph Info:\tNumber of Vertices\t%d\n", g.getVertexNum());
        out.printf("\t\tExact LCA\n");
        out.printf("\tSeparator Size\tBalance Ratio\tRuntime (ms)\n");

        int vNum = g.getVertexNum();
        for (int i = 0; i < vNum; i++) {

            System.out.printf("Iteration %d\n", i);
            RootFinder rf = new SpecificIdRootFinder(i);
            Vertex root = rf.selectRootVertex(g);
            StringBuilder sb = new StringBuilder(String.format("%d", i));
            Separator sp = new ModifiedFCS(g, new ExactLCA());
            testSeparator(sp, root, sb);
            out.println(sb.toString());
        }
        out.close();
    }

    public static void main(String[] args) throws FileNotFoundException {
        //runTest1();
        runTest2();
        //tryRootSelection();
    }
}
