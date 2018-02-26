import algorithms.RDivision.*;
import algorithms.RootFinder.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class test_RDivision_NlogN {
    public SelfDualGraph readGraph(String fileName) {
        SelfDualGraph g = new SelfDualGraph();
        try {
            g.buildGraph(fileName);
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
        return g;
    }

    public Set<Vertex> createBoundary(SelfDualGraph g, int[] IDs) {
        Set<Integer> vID = new HashSet<>();
        for (int i : IDs) vID.add(i);
        Set<Vertex> boundary = new HashSet<>();
        for (Vertex v : g.getVertices()) {
            if (vID.contains(v.getID())) boundary.add(v);
        }
        return boundary;
    }

    public void verifyVerticeID(SelfDualGraph graph, Set<Integer> IDs) {
        Assert.assertEquals(IDs.size(), graph.getVertexNum());
        for (Vertex v : graph.getVertices()) {
            Assert.assertTrue(IDs.contains(v.getID()));
        }
    }

    // TODO: hase I; phase II, integration test

    @Test
    public void test_phaseI_r14() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        RecursiveDivider rd = new RecursiveDivider(g);
        rd.phaseI(g, 14);

        Queue<SelfDualGraph> subgraphs = rd.getSubgraphsAfterPhaseI();
        Assert.assertEquals(2, subgraphs.size());
        while (!subgraphs.isEmpty()) {
            SelfDualGraph sub1 = subgraphs.poll();
            RootFinder rf = new SpecificIdRootFinder(0);
            try {
                rf.selectRootVertex(sub1);
                verifyVerticeID(sub1, new HashSet<>(Arrays.asList(new Integer[]{0, 3, 12, 1, 2, 4, 5, 7, 8, 11, 13, 14, 15})));
            } catch (RuntimeException e) {
                verifyVerticeID(sub1, new HashSet<>(Arrays.asList(new Integer[]{6, 9, 10, 1, 2, 4, 5, 7, 8, 11, 13, 14, 15})));
            }
        }
    }
}
