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

    public void verifySeparator(int[] expectedVerticies, Set<Vertex> separator) {
        Assert.assertEquals(expectedVerticies.length, separator.size());
        Set<Integer> expectedID = new HashSet<>();
        for (int i : expectedVerticies) expectedID.add(i);
        for (Vertex v : separator) {
            Assert.assertTrue(expectedID.contains(v.ID));
        }
    }

    public void resetTreenodeSelfweight(Tree tree, int[] nodeIDs) {
        Set<Integer> ids = new HashSet<>();
        for (int i : nodeIDs) ids.add(i);
        Tree.TreeNode node = tree.getRoot();
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(node);
        while (!q.isEmpty()) {
            node = q.poll();
            if (ids.contains(node.getData().ID)) {
                node.setSelfWeight(0);
            }
            q.addAll(node.getChildren());
        }
    }

    @Test
    public void testLeafmostHeavyVertex() {
        Tree.TreeNode root = trees[0].getRoot();
        Assert.assertEquals(10, root.getDescendantWeightSum(), 0.0001);
        Tree.TreeNode vertexSeparator = Separator.leafmostHeavyVertex(root, 0.4, root.getDescendantWeightSum());
        Assert.assertEquals(2, vertexSeparator.getData().ID);

        root = trees[1].getRoot();
        Assert.assertEquals(18.05, root.getDescendantWeightSum(), 0.0001);
        vertexSeparator = Separator.leafmostHeavyVertex(root, 0.5, root.getDescendantWeightSum());
        Assert.assertEquals(6, vertexSeparator.getData().ID);
    }

    @Test
    public void testVertexSeparator() {
        Vertex v = Separator.findVertexSeparator(trees[1]);
        Assert.assertEquals(6, v.ID);
    }

    @Test
    public void testLevelSeparatorGivenTree() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.BFSsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.SpecificIdRoot(5)),
                RootFinder.selectRootFace(g, new RootFinder.SpecificIdRoot(0)));
        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.VertexCount());
        Set<Vertex> separator = Separator.findLevelSeparator(trees[0]);
        int[] expectedVertices = new int[]{2, 0, 4};
        verifySeparator(expectedVertices, separator);

        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), new TreeWeightAssigner.VertexAndEdgeWeight());
        separator = Separator.findLevelSeparator(trees[0]);
        expectedVertices = new int[]{2, 0, 4};
        verifySeparator(expectedVertices, separator);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexCount());
        separator = Separator.findLevelSeparator(trees[1]);
        expectedVertices = new int[]{1, 4, 6};
        verifySeparator(expectedVertices, separator);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexAndEdgeWeight());
        Assert.assertEquals(16.55, trees[1].getRoot().getDescendantWeightSum(), 0.0001);
        separator = Separator.findLevelSeparator(trees[1]);
        expectedVertices = new int[]{2, 3, 5};
        verifySeparator(expectedVertices, separator);
    }

    @Test
    public void testLevelSeparatorGivenGraph() {
        Set<Vertex> separator = Separator.findLevelSeparator(g, null, null, null);
        verifySeparator(new int[]{4, 0, 2}, separator);
    }

    @Test
    public void testEdgeSeparator() {
        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g,
                new SpanningTreeSolver.Primsolver(),
                RootFinder.selectRootVertex(g, new RootFinder.SpecificIdRoot(5)),
                RootFinder.selectRootFace(g, new RootFinder.SpecificIdRoot(0)));

        resetTreenodeSelfweight(trees[1], new int[]{1, 6});
        Dart separator = Separator.findEdgeSeparator(trees[1]);
        Assert.assertEquals(16.8, trees[1].getRoot().getDescendantWeightSum(), 0.001);
        Assert.assertTrue(separator.ID == 10 || separator.ID == 11);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexWeight());
        separator = Separator.findEdgeSeparator(trees[1]);
        Assert.assertEquals(5.3, trees[1].getRoot().getDescendantWeightSum(), 0.001);
        Assert.assertTrue(separator.ID == 4 || separator.ID == 5);
    }

    // TODO: add more test based on grid graph
}
