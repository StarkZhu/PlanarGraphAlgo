import algorithms.RootFinder;
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
        Vertex root = RootFinder.selectRootVertex(g, new RootFinder.SpecificIdRoot());
        Assert.assertEquals(0, root.ID);
        root = RootFinder.selectRootFace(g, new RootFinder.SpecificIdRoot());
        Assert.assertEquals(0, root.ID);
    }

    @Test
    public void testMaxDegreeRoot() {
        Vertex root = RootFinder.selectRootVertex(g, new RootFinder.MaxDegreeRoot());
        Assert.assertEquals(5, root.ID);
        root = RootFinder.selectRootFace(g, new RootFinder.MaxDegreeRoot());
        Assert.assertEquals(1, root.ID);
    }

    @Test
    public void testMinDegreeRoot() {
        Vertex root = RootFinder.selectRootVertex(g, new RootFinder.MinDegreeRoot());
        Assert.assertEquals(1, root.ID);
        root = RootFinder.selectRootFace(g, new RootFinder.MinDegreeRoot());
        Assert.assertEquals(3, root.ID);
    }

    @Test
    public void testRandomRoot() {
        Vertex root = RootFinder.selectRootVertex(g, new RootFinder.RandomRoot());
        Assert.assertTrue(root.ID >= 0 && root.ID <= 5);
        root = RootFinder.selectRootFace(g, new RootFinder.RandomRoot());
        Assert.assertTrue(root.ID >= 0 && root.ID <= 6);
    }
}
