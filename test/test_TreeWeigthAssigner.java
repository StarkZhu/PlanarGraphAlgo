import algorithms.RootFinder;
import algorithms.SpanningTreeSolver;
import algorithms.TreeWeightAssigner;
import org.junit.*;
import selfdualgraph.*;
import java.io.FileNotFoundException;
import java.util.*;

public class test_TreeWeigthAssigner {
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

    public void verifyWeightSumOfTree(Tree tree, double[] vertexWeightSum) {

        Queue<Tree.TreeNode<Vertex>> q = new LinkedList<>();
        q.add(tree.getRoot());
        while (!q.isEmpty()) {
            Tree.TreeNode<Vertex> node = q.poll();
            Assert.assertEquals(vertexWeightSum[node.getData().ID], node.getWeightSum(), 0.00001);
            for (Tree.TreeNode<Vertex> child : node.getChildren()) {
                q.add(child);
            }
        }
    }

    @Test
    public void testVertexCountAsWeightSum_BFS() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.BFSsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.SpecificIdRoot()),
                RootFinder.selectRootFace(g, new RootFinder.SpecificIdRoot()));

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.VertexCount());
        double[] treeVertexWeightSum = new double[]{6, 2, 1, 1, 1, 1};
        verifyWeightSumOfTree(trees[0], treeVertexWeightSum);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexCount());
        double[] coTreeVertexWeightSum = new double[]{7, 6, 2, 1, 1, 1, 1};
        verifyWeightSumOfTree(trees[1], coTreeVertexWeightSum);
    }

    @Test
    public void testVertexCountAsWeightSum_DFS() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.DFSsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.SpecificIdRoot()),
                RootFinder.selectRootFace(g, new RootFinder.SpecificIdRoot()));

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.VertexCount());
        double[] treeVertexWeightSum = new double[]{6, 5, 1, 4, 3, 2};
        verifyWeightSumOfTree(trees[0], treeVertexWeightSum);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexCount());
        double[] coTreeVertexWeightSum = new double[]{7, 3, 1, 1, 3, 2, 1};
        verifyWeightSumOfTree(trees[1], coTreeVertexWeightSum);
    }

    @Test
    public void testVertexWeigthAsWeightSum_BFS() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.BFSsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.MaxDegreeRoot()),
                RootFinder.selectRootFace(g, new RootFinder.MaxDegreeRoot()));

        String treeBenchmark = "V<5>\n  V<4>\n    V<3>\n  V<0>\n    V<1>\n  V<2>\n";
        String coTreeBenchmark = "F<1>\n  F<0>\n    F<4>\n    F<6>\n      F<5>\n  F<2>\n  F<3>\n";
        Assert.assertEquals(trees[0].toString(), treeBenchmark);
        Assert.assertEquals(trees[1].toString(), coTreeBenchmark);

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.VertexWeight());
        double[] treeVertexWeightSum = new double[]{1.0, 0.5, 1.5, 1.5, 2.5, 6.0};
        verifyWeightSumOfTree(trees[0], treeVertexWeightSum);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexWeight());
        double[] coTreeVertexWeightSum = new double[]{3.95, 6.55, 1.0, 0.6, 1.0, 0.7, 0.95};
        verifyWeightSumOfTree(trees[1], coTreeVertexWeightSum);
    }

    @Test
    public void testVertexWeigthAsWeightSum_DFS() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.DFSsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.MaxDegreeRoot()),
                RootFinder.selectRootFace(g, new RootFinder.MaxDegreeRoot()));

        String treeBenchmark = "V<5>\n  V<4>\n    V<0>\n      V<1>\n        V<3>\n          V<2>\n";
        String coTreeBenchmark = "F<1>\n  F<2>\n    F<4>\n      F<0>\n      F<5>\n  F<3>\n  F<6>\n";
        Assert.assertEquals(trees[0].toString(), treeBenchmark);
        Assert.assertEquals(trees[1].toString(), coTreeBenchmark);

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.VertexWeight());
        double[] treeVertexWeightSum = new double[]{4.0, 3.5, 1.5, 3.0, 5, 6};
        verifyWeightSumOfTree(trees[0], treeVertexWeightSum);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexWeight());
        double[] coTreeVertexWeightSum = new double[]{2, 6.55, 4.7, 0.6, 3.7, 0.7, 0.25};
        verifyWeightSumOfTree(trees[1], coTreeVertexWeightSum);
    }

    @Test
    public void testEdgeWeigthAsWeightSum_Prim() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.Primsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.MinDegreeRoot()),
                RootFinder.selectRootFace(g, new RootFinder.MinDegreeRoot()));

        String treeBenchmark = "V<1>\n  V<0>\n    V<5>\n      V<4>\n      V<2>\n        V<3>\n";
        String coTreeBenchmark = "F<3>\n  F<1>\n    F<2>\n    F<6>\n      F<5>\n      F<0>\n        F<4>\n";
        Assert.assertEquals(trees[0].toString(), treeBenchmark);
        Assert.assertEquals(trees[1].toString(), coTreeBenchmark);

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.EdgeWeight());
        double[] treeVertexWeightSum = new double[]{4.0, 4, 1.5, 0.5, 0.5, 3.0};
        verifyWeightSumOfTree(trees[0], treeVertexWeightSum);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.EdgeWeight());
        double[] coTreeVertexWeightSum = new double[]{3.5, 11.5, 2.0, 11.5, 1.5, 2, 7.5};
        verifyWeightSumOfTree(trees[1], coTreeVertexWeightSum);
    }

    @Test
    public void testVertexAndEdgeWeightAsWeightSum_Prim() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.Primsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.MaxDegreeRoot()),
                RootFinder.selectRootFace(g, new RootFinder.MaxDegreeRoot()));

        String treeBenchmark = "V<5>\n  V<4>\n  V<0>\n    V<1>\n  V<2>\n    V<3>\n";
        String coTreeBenchmark = "F<1>\n  F<2>\n  F<3>\n  F<6>\n    F<5>\n    F<0>\n      F<4>\n";
        Assert.assertEquals(trees[0].toString(), treeBenchmark);
        Assert.assertEquals(trees[1].toString(), coTreeBenchmark);

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.VertexAndEdgeWeight());
        double[] treeVertexWeightSum = new double[]{3, 1.5, 4.5, 2, 1.5, 10};
        verifyWeightSumOfTree(trees[0], treeVertexWeightSum);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexAndEdgeWeight());
        double[] coTreeVertexWeightSum = new double[]{6.5, 18.05, 3.0, 2.6, 2.5, 2.7, 11.45};
        verifyWeightSumOfTree(trees[1], coTreeVertexWeightSum);
    }
}
