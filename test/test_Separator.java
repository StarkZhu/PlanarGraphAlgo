import algorithms.RootFinder.*;
import algorithms.Separator.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class test_Separator {
    protected SelfDualGraph g;
    protected Tree[] trees;

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

    public void verifyVertexSet(int[] expectedVerticies, Set<Vertex> separator) {
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
}
