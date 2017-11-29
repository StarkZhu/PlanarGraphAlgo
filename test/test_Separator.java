import algorithms.RootFinder.*;
import algorithms.Separator;
import algorithms.SpanningTreeSolver.*;
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
        RootFinder rf = new MaxDegreeRootFinder();
        SpanningTreeSolver sts = new Primsolver();
        trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), rf.selectRootFace(g));
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

    public void verifyWeightSumOfTree(Tree tree, double[][] vertexWeightSum, double[][] vertexSelfWeight) {
        Map<Integer, Double> weightSum = new HashMap<>();
        for (double[] pair : vertexWeightSum) {
            weightSum.put((int) pair[0], pair[1]);
        }
        Map<Integer, Double> selfWeight = new HashMap<>();
        for (double[] pair : vertexSelfWeight) {
            selfWeight.put((int) pair[0], pair[1]);
        }
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(tree.getRoot());
        while (!q.isEmpty()) {
            Tree.TreeNode node = q.poll();
            if (selfWeight.containsKey(node.getData().ID)) {
                Assert.assertEquals(selfWeight.get(node.getData().ID), node.getSelfWeight(), 0.00001);
            }
            if (weightSum.containsKey(node.getData().ID)) {
                Assert.assertEquals(weightSum.get(node.getData().ID), node.getDescendantWeightSum(), 0.00001);
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
        SpanningTreeSolver sts = new BFSsolver();
        Tree[] trees = sts.buildTreeCoTree(g,
                new SpecificIdRootFinder(5).selectRootVertex(g),
                new SpecificIdRootFinder(0).selectRootFace(g));
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
        SpanningTreeSolver sts = new Primsolver();
        Tree[] trees = sts.buildTreeCoTree(g,
                new SpecificIdRootFinder(5).selectRootVertex(g),
                new SpecificIdRootFinder(0).selectRootFace(g));

        resetTreenodeSelfweight(trees[1], new int[]{1, 6});
        Dart separator = Separator.findEdgeSeparator(trees[1]);
        Assert.assertEquals(16.8, trees[1].getRoot().getDescendantWeightSum(), 0.001);
        Assert.assertTrue(separator.ID == 10 || separator.ID == 11);

        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), new TreeWeightAssigner.VertexWeight());
        separator = Separator.findEdgeSeparator(trees[1]);
        Assert.assertEquals(5.3, trees[1].getRoot().getDescendantWeightSum(), 0.001);
        Assert.assertTrue(separator.ID == 4 || separator.ID == 5);
    }

    @Test
    public void testFCS_FaceCount() {
        g.flatten();
        g.triangulate();

        SpanningTreeSolver sts = new BFSsolver();
        RootFinder rf = new MaxDegreeRootFinder();
        TreeWeightAssigner twa = new TreeWeightAssigner.VertexCount();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        Assert.assertEquals(3, trees[0].getRoot().getData().ID);
        Assert.assertEquals(0, trees[1].getRoot().getData().ID);
        Separator.assignCotreeWeight(twa, trees);
        double[][] coTreeWeightSum = new double[][] {{0, 8}, {1, 1}, {4, 3}, {5, 5}, {6, 1}};
        double[][] coTreeSelfWeight = new double[][] {{0, 0}, {1, 0}, {4, 0}, {5, 0}, {6, 0}, {-1, 0}, {-2, 0}, {-3, 0}};
        verifyWeightSumOfTree(trees[1], coTreeWeightSum, coTreeSelfWeight);

        Set<Vertex> separator = Separator.findFundamentalCycleSeparator(g, null, null, null);
        verifySeparator(new int[]{5, 0, 3}, separator);

        separator = Separator.findFundamentalCycleSeparator(g, null, null, new TreeWeightAssigner.VertexWeight());
        verifySeparator(new int[]{5, 0, 3}, separator);
    }

    @Test
    public void testFCS_EdgeWeight() {
        g.flatten();
        g.triangulate();

        SpanningTreeSolver sts = new BFSsolver();
        RootFinder rf = new MinDegreeRootFinder();
        TreeWeightAssigner twa = new TreeWeightAssigner.EdgeWeight();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        Assert.assertEquals(2, trees[0].getRoot().getData().ID);
        Assert.assertEquals(4, trees[1].getRoot().getData().ID);
        Separator.assignCotreeWeight(twa, trees);
        double[][] coTreeWeightSum = new double[][] {{0, 3.5}, {1, 8.25}, {4, 11.5}, {5, 10.25}, {6, 7}};
        double[][] coTreeSelfWeight = new double[][] {{0, 0.5}, {1, 0}, {4, 1.25}, {5, 1}, {6, 0}};
        verifyWeightSumOfTree(trees[1], coTreeWeightSum, coTreeSelfWeight);

        Set<Vertex> separator = Separator.findFundamentalCycleSeparator(g, new BFSsolver(),
                new MinDegreeRootFinder(), new TreeWeightAssigner.EdgeWeight());
        verifySeparator(new int[]{4, 0, 3, 2}, separator);
    }

    // TODO: add more test based on grid graph
}
