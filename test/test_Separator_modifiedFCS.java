import algorithms.LCAHeuristic.DistToLeafHeuristic;
import algorithms.RootFinder.*;
import algorithms.Separator.*;
import algorithms.SpanningTreeSolver.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class test_Separator_modifiedFCS {
    private SelfDualGraph g;

    @Before
    /**
     * executed before each test
     */
    public void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./test/benchmark_img_4x4.txt");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
        Dart.uniqueID = 0;
        Vertex.uniqueID = 0;
    }

    public void verifyVertexSet(int[] expectedVerticies, Set<Vertex> separator) {
        Assert.assertEquals(expectedVerticies.length, separator.size());
        Set<Integer> expectedID = new HashSet<>();
        for (int i : expectedVerticies) expectedID.add(i);
        for (Vertex v : separator) {
            Assert.assertTrue(expectedID.contains(v.getID()));
        }
    }

    @Test
    public void testFCS() {
        g.flatten();
        g.triangulate();

        FundamentalCycleSeparator sp = new FundamentalCycleSeparator(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(0), null, -1);
        verifyVertexSet(new int[]{0, 5, 9, 14}, separator);
    }

    @Test
    public void testFCSmod() {
        g.flatten();
        g.triangulate();

        FundamentalCycleSeparator sp = new ModifiedFCS(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(0), null, -1);
        verifyVertexSet(new int[]{0, 5, 10, 11}, separator);
    }
}
