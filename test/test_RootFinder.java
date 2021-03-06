import algorithms.RootFinder.*;
import org.junit.*;
import selfdualgraph.*;
import java.io.FileNotFoundException;

public class test_RootFinder {
    private SelfDualGraph g;

    @Before
    public void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./input_data/test_graph_0.txt");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGivenIdRoot() {
        RootFinder rf = new SpecificIdRootFinder();
        Vertex root = rf.selectRootVertex(g);
        Assert.assertEquals(0, root.getID());
        root = rf.selectRootFace(g);
        Assert.assertEquals(0, root.getID());
    }

    @Test
    public void testMaxDegreeRoot() {
        RootFinder rf = new MaxDegreeRootFinder();
        Vertex root = rf.selectRootVertex(g);
        Assert.assertEquals(5, root.getID());
        root = rf.selectRootFace(g);
        Assert.assertEquals(1, root.getID());
    }

    @Test
    public void testMinDegreeRoot() {
        RootFinder rf = new MinDegreeRootFinder();
        Vertex root = rf.selectRootVertex(g);
        Assert.assertEquals(1, root.getID());
        root = rf.selectRootFace(g);
        Assert.assertEquals(3, root.getID());
    }

    @Test
    public void testRandomRoot() {
        RootFinder rf = new RandomRootFinder();
        Vertex root = rf.selectRootVertex(g);
        Assert.assertTrue(root.getID() >= 0 && root.getID() <= 5);
        root = rf.selectRootFace(g);
        Assert.assertTrue(root.getID() >= 0 && root.getID() <= 6);
    }
}
