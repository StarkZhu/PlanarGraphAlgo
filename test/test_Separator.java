import algorithms.RootFinder;
import algorithms.Separator;
import algorithms.SpanningTreeSolver;
import algorithms.TreeWeightAssigner;
import org.junit.*;
import selfdualgraph.*;
import java.io.FileNotFoundException;
import java.util.*;

public class test_Separator {
    private SelfDualGraph g;
    private Tree[] trees;

    @Before
    public void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./input_data/test_graph_0.txt");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
        trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.Primsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.MaxDegreeRoot()),
                RootFinder.selectRootFace(g, new RootFinder.MaxDegreeRoot()));
        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.VertexAndEdgeWeight());
        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexAndEdgeWeight());
    }

    @Test
    public void testLeafmostHeavyVertex() {
        Tree.TreeNode<Vertex> root = trees[0].getRoot();
        Assert.assertEquals(10, root.getWeightSum(), 0.0001);
        Tree.TreeNode<Vertex> vertexSeparator = Separator.leafmostHeavyVertex(root, 0.4, root.getWeightSum());
        Assert.assertEquals(2, vertexSeparator.getData().ID);

        root = trees[1].getRoot();
        Assert.assertEquals(18.05, root.getWeightSum(), 0.0001);
        vertexSeparator = Separator.leafmostHeavyVertex(root, 0.5, root.getWeightSum());
        Assert.assertEquals(6, vertexSeparator.getData().ID);
    }
}
