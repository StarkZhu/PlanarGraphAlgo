import algorithms.RDivision.RecursiveDivider;
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

    // TODO: assignWeightToBoundary_useDart; phase I; phase II, integration test

    @Test
    public void testAssignWeightToBoundary_grid4x4() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        Set<Vertex> boundary = createBoundary(g, new int[]{0, 1, 2, 3, 4, 7, 8, 11, 12, 13, 14, 15});
        g.addToBoundary(boundary);

        RecursiveDivider rd = new RecursiveDivider(g);
        g.assignWeightToBoundary_useDart();
    }
}
