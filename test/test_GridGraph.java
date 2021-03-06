import algorithms.RootFinder.*;
import algorithms.Separator.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class test_GridGraph {
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
    public void testTraingulation() {
        g.flatten();
        g.triangulate();
        Assert.assertEquals(28, g.getFaceNum());
        for (Vertex face : g.getFaces()) {
            Assert.assertEquals(3, face.getDegree());
            Dart d = face.getFirstDart();
            for (int i=0; i<3; i++) {
                Assert.assertEquals(face, d.getRight());
                d = d.getNext();
            }
            Assert.assertEquals(face.getFirstDart(), d);
        }
    }

    @Test
    public void testLevelSeparator() {
        LevelSeparator sp = new LevelSeparator(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(0), null);
        verifyVertexSet(new int[]{3, 6, 9, 12}, separator);
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{3, 6, 9, 12, 0, 1, 2, 4, 5, 8}, subgraphs[0]);
        verifyVertexSet(new int[]{3, 6, 9, 12, 7, 10, 11, 13, 14, 15}, subgraphs[1]);

        separator = sp.findSeparator(null, null, null);
        verifyVertexSet(new int[]{2, 5, 7, 8, 13, 15}, separator);
        subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{2, 5, 7, 8, 13, 15, 6, 9, 10, 11, 14}, subgraphs[0]);
        verifyVertexSet(new int[]{2, 5, 7, 8, 13, 15, 0, 1, 3, 4, 12}, subgraphs[1]);

        g.flatten();
        g.triangulate();
        separator = sp.findSeparator(null, null, null);
        verifyVertexSet(new int[]{1, 2, 3, 4, 5, 7, 8, 11, 12, 13, 14, 15}, separator);
        subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{1, 2, 3, 4, 5, 7, 8, 11, 12, 13, 14, 15, 0}, subgraphs[0]);
        verifyVertexSet(new int[]{1, 2, 3, 4, 5, 7, 8, 11, 12, 13, 14, 15, 6, 9, 10}, subgraphs[1]);
    }

    @Test
    public void testFCS_FaceCount() {
        FundamentalCycleSeparator sp = new FundamentalCycleSeparator(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(0), null, -1);
        verifyVertexSet(new int[]{0, 1, 4, 5, 8, 9, 12, 13}, separator);
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, subgraphs[0]);
        verifyVertexSet(new int[]{0, 1, 4, 5, 8, 9, 12, 13}, subgraphs[1]);
    }
}
