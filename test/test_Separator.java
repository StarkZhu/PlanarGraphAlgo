import algorithms.RootFinder.*;
import algorithms.Separator.FundamentalCycleSeparator;
import algorithms.Separator.LevelSeparator;
import algorithms.Separator.Separator;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
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
        TreeWeightAssigner twa = new VertexAndEdgeWeight();
        twa.calcWeightSum(trees[0].getRoot());
        twa.calcWeightSum(trees[1].getRoot());
    }

    public void verifySeparator(int[] expectedVerticies, Set<Vertex> separator) {
        Assert.assertEquals(expectedVerticies.length, separator.size());
        Set<Integer> expectedID = new HashSet<>();
        for (int i : expectedVerticies) expectedID.add(i);
        for (Vertex v : separator) {
            Assert.assertTrue(expectedID.contains(v.getID()));
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
            if (ids.contains(node.getData().getID())) {
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
            if (selfWeight.containsKey(node.getData().getID())) {
                Assert.assertEquals(selfWeight.get(node.getData().getID()), node.getSelfWeight(), 0.00001);
            }
            if (weightSum.containsKey(node.getData().getID())) {
                Assert.assertEquals(weightSum.get(node.getData().getID()), node.getDescendantWeightSum(), 0.00001);
            }
            q.addAll(node.getChildren());
        }
    }

    @Test
    public void testLeafmostHeavyVertex() {
        Tree.TreeNode root = trees[0].getRoot();
        Assert.assertEquals(10, root.getDescendantWeightSum(), 0.0001);
        Separator sp = new LevelSeparator(g);
        Tree.TreeNode vertexSeparator = sp.leafmostHeavyVertex(root, 0.4, root.getDescendantWeightSum());
        Assert.assertEquals(2, vertexSeparator.getData().getID());

        root = trees[1].getRoot();
        Assert.assertEquals(18.05, root.getDescendantWeightSum(), 0.0001);
        vertexSeparator = sp.leafmostHeavyVertex(root, 0.5, root.getDescendantWeightSum());
        Assert.assertEquals(6, vertexSeparator.getData().getID());
    }

    @Test
    public void testVertexSeparator() {
        Vertex v = new LevelSeparator(g).findVertexSeparator(trees[1]);
        Assert.assertEquals(6, v.getID());
    }

    @Test
    public void testLevelSeparatorOfTree() {
        SpanningTreeSolver sts = new BFSsolver();
        Tree[] trees = sts.buildTreeCoTree(g,
                new SpecificIdRootFinder(5).selectRootVertex(g),
                new SpecificIdRootFinder(0).selectRootFace(g));
        TreeWeightAssigner twa = new VertexCount();
        twa.calcWeightSum(trees[0].getRoot());
        LevelSeparator sp = new LevelSeparator(g);
        Set<Vertex> separator = sp.findLevelSeparatorOfTree(trees[0]);
        int[] expectedVertices = new int[]{2, 0, 4};
        verifySeparator(expectedVertices, separator);

        twa = new VertexAndEdgeWeight();
        twa.calcWeightSum(trees[0].getRoot());
        separator = sp.findLevelSeparatorOfTree(trees[0]);
        expectedVertices = new int[]{2, 0, 4};
        verifySeparator(expectedVertices, separator);

        twa = new VertexCount();
        twa.calcWeightSum(trees[1].getRoot());
        separator = sp.findLevelSeparatorOfTree(trees[1]);
        expectedVertices = new int[]{1, 4, 6};
        verifySeparator(expectedVertices, separator);

        twa = new VertexAndEdgeWeight();
        twa.calcWeightSum(trees[1].getRoot());
        Assert.assertEquals(16.55, trees[1].getRoot().getDescendantWeightSum(), 0.0001);
        separator = sp.findLevelSeparatorOfTree(trees[1]);
        expectedVertices = new int[]{2, 3, 5};
        verifySeparator(expectedVertices, separator);
    }

    @Test
    public void testLevelSeparatorGivenGraph() {
        Set<Vertex> separator = new LevelSeparator(g).findSeparator(null, null, null);
        verifySeparator(new int[]{4, 0, 2}, separator);
    }

    @Test
    public void testEdgeSeparator() {
        SpanningTreeSolver sts = new Primsolver();
        Tree[] trees = sts.buildTreeCoTree(g,
                new SpecificIdRootFinder(5).selectRootVertex(g),
                new SpecificIdRootFinder(0).selectRootFace(g));

        resetTreenodeSelfweight(trees[1], new int[]{1, 6});
        Separator sp = new FundamentalCycleSeparator(g);
        Dart separator = sp.findEdgeSeparator(trees[1] ,3).getParentDart();
        Assert.assertEquals(16.8, trees[1].getRoot().getDescendantWeightSum(), 0.001);
        Assert.assertTrue(separator.getID() == 10 || separator.getID() == 11);

        TreeWeightAssigner twa = new VertexWeight();
        twa.calcWeightSum(trees[1].getRoot());
        separator = sp.findEdgeSeparator(trees[1], 3).getParentDart();
        Assert.assertEquals(5.3, trees[1].getRoot().getDescendantWeightSum(), 0.001);
        Assert.assertTrue(separator.getID() == 4 || separator.getID() == 5);
    }

    @Test
    public void testFCS_FaceCount() {
        g.flatten();
        g.triangulate();

        SpanningTreeSolver sts = new BFSsolver();
        RootFinder rf = new MaxDegreeRootFinder();
        TreeWeightAssigner twa = new VertexCount();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        Assert.assertEquals(3, trees[0].getRoot().getData().getID());
        Assert.assertEquals(0, trees[1].getRoot().getData().getID());
        FundamentalCycleSeparator sp = new FundamentalCycleSeparator(g);
        sp.assignCotreeWeight(twa, trees);
        double[][] coTreeWeightSum = new double[][] {{0, 8}, {1, 1}, {4, 3}, {5, 5}, {6, 1}};
        double[][] coTreeSelfWeight = new double[][] {{0, 0}, {1, 0}, {4, 0}, {5, 0}, {6, 0}, {-1, 0}, {-2, 0}, {-3, 0}};
        verifyWeightSumOfTree(trees[1], coTreeWeightSum, coTreeSelfWeight);

        Set<Vertex> separator = sp.findSeparator(null, null, null);
        verifySeparator(new int[]{5, 0, 3}, separator);

        separator = sp.findSeparator(null, null, new VertexWeight());
        verifySeparator(new int[]{5, 0, 3}, separator);
    }

    @Test
    public void testFCS_EdgeWeight() {
        g.flatten();
        g.triangulate();

        SpanningTreeSolver sts = new BFSsolver();
        RootFinder rf = new MinDegreeRootFinder();
        TreeWeightAssigner twa = new EdgeWeight();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        Assert.assertEquals(2, trees[0].getRoot().getData().getID());
        Assert.assertEquals(4, trees[1].getRoot().getData().getID());
        FundamentalCycleSeparator sp = new FundamentalCycleSeparator(g);
        sp.assignCotreeWeight(twa, trees);
        double[][] coTreeWeightSum = new double[][] {{0, 3.5}, {1, 8.25}, {4, 11.5}, {5, 10.25}, {6, 7}};
        double[][] coTreeSelfWeight = new double[][] {{0, 0.5}, {1, 0}, {4, 1.25}, {5, 1}, {6, 0}};
        verifyWeightSumOfTree(trees[1], coTreeWeightSum, coTreeSelfWeight);

        Set<Vertex> separator = sp.findSeparator(new BFSsolver(),
                new MinDegreeRootFinder(), new EdgeWeight());
        verifySeparator(new int[]{4, 0, 3, 2}, separator);
    }

    // TODO: add more test based on grid graph
}
