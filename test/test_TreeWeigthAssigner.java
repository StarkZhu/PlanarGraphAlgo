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
                RootFinder.selectRootVertex(g, new RootFinder.ZeroIdRoot()),
                RootFinder.selectRootFace(g, new RootFinder.ZeroIdRoot()));

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner());
        double[] treeVertexWeightSum = new double[]{6, 2, 1, 1, 1, 1};
        verifyWeightSumOfTree(trees[0], treeVertexWeightSum);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner());
        double[] coTreeVertexWeightSum = new double[]{7, 6, 2, 1, 1, 1, 1};
        verifyWeightSumOfTree(trees[1], coTreeVertexWeightSum);
    }

    @Test
    public void testVertexCountAsWeightSum_DFS() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.DFSsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.ZeroIdRoot()),
                RootFinder.selectRootFace(g, new RootFinder.ZeroIdRoot()));

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner());
        double[] treeVertexWeightSum = new double[]{6, 5, 1, 4, 3, 2};
        verifyWeightSumOfTree(trees[0], treeVertexWeightSum);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner());
        double[] coTreeVertexWeightSum = new double[]{7, 3, 1, 1, 3, 2, 1};
        verifyWeightSumOfTree(trees[1], coTreeVertexWeightSum);
    }
}
