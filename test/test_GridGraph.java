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

    public void verifySeparator(int[] expectedVerticies, Set<Vertex> separator) {
        Assert.assertEquals(expectedVerticies.length, separator.size());
        Set<Integer> expectedID = new HashSet<>();
        for (int i : expectedVerticies) expectedID.add(i);
        for (Vertex v : separator) {
            Assert.assertTrue(expectedID.contains(v.ID));
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
        Set<Vertex> separator = new LevelSeparator().findSeparator(g, null, new SpecificIdRootFinder(0), null);
        verifySeparator(new int[]{3, 6, 9, 12}, separator);

        separator = new LevelSeparator().findSeparator(g, null, null, null);
        verifySeparator(new int[]{2, 5, 7, 8, 13, 15}, separator);

        g.flatten();
        g.triangulate();
        separator = new LevelSeparator().findSeparator(g, null, null, null);
        verifySeparator(new int[]{1, 2, 3, 4, 5, 7, 8, 11, 12, 13, 14, 15}, separator);
    }

    @Test
    public void testFCS_FaceCount() {
        FundamentalCycleSeparator sp = new FundamentalCycleSeparator();
        Set<Vertex> separator = sp.findSeparator(g, null, new SpecificIdRootFinder(0), null, -1);
        verifySeparator(new int[]{0, 1, 4, 5, 8, 9, 12, 13}, separator);
    }
}
