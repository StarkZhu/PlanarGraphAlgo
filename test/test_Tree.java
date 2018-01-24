import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.Map;

public class test_Tree {
    private SelfDualGraph g;
    private Vertex treeRootVertex, coTreeRootVertex;

    @Before
    public void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./input_data/test_graph_0.txt");
            for (Vertex v : g.getVertices()) {
                if (v.getID() == 0) treeRootVertex = v;
            }
            for (Vertex f : g.getFaces()) {
                if (f.getID() == 0) coTreeRootVertex = f;
            }
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
    }


    @Test
    public void testReRootDFS() {
        String originTree = "V<0>\n  V<1>\n    V<3>\n      V<4>\n        V<5>\n          V<2>\n";
        String reRootTree1 = "V<1>\n  V<3>\n    V<4>\n      V<5>\n        V<2>\n  V<0>\n";
        String reRootTree2 = "V<3>\n  V<4>\n    V<5>\n      V<2>\n  V<1>\n    V<0>\n";
        SpanningTreeSolver dfs = new DFSsolver();
        Tree[] trees = dfs.buildTreeCoTree(g, treeRootVertex, coTreeRootVertex);
        Assert.assertEquals(trees[0].toString(), originTree);

        Tree.TreeNode root = trees[0].getRoot();
        Tree.TreeNode node1 = root.getChildren().iterator().next();
        Tree.TreeNode node2 = node1.getChildren().iterator().next();

        trees[0].reRoot(node1);
        Assert.assertEquals(trees[0].toString(), reRootTree1);

        trees[0].reRoot(root);
        Assert.assertEquals(trees[0].toString(), originTree);

        trees[0].reRoot(node2);
        Assert.assertEquals(trees[0].toString(), reRootTree2);

        trees[0].reRoot(root);
        Assert.assertEquals(trees[0].toString(), originTree);
    }


    @Test
    public void testReRootTreeBFS() {
        String originTree = "V<2>\n  V<0>\n    V<1>\n    V<4>\n  V<3>\n  V<5>\n";
        String reRootTree1 = "V<0>\n  V<1>\n  V<4>\n  V<2>\n    V<3>\n    V<5>\n";
        String originTree2 = "V<2>\n  V<3>\n  V<5>\n  V<0>\n    V<1>\n    V<4>\n";
        String reRootTree2 = "V<1>\n  V<0>\n    V<4>\n    V<2>\n      V<3>\n      V<5>\n";

        RootFinder rf = new SpecificIdRootFinder(2);
        Vertex root = rf.selectRootVertex(g);
        Assert.assertEquals(2, root.getID());
        Vertex v1 = (new SpecificIdRootFinder(0)).selectRootVertex(g);
        Assert.assertEquals(0, v1.getID());
        Vertex v2 = (new SpecificIdRootFinder(1)).selectRootVertex(g);
        Assert.assertEquals(1, v2.getID());

        SpanningTreeSolver bfs = new BFSsolver();
        Tree[] trees = bfs.buildTreeCoTree(g, root, null);
        Map<Vertex, Tree.TreeNode> map = trees[0].mapVertexToTreeNode(false);
        Assert.assertEquals(trees[0].toString(), originTree);

        trees[0].reRoot(map.get(v1));
        Assert.assertEquals(trees[0].toString(), reRootTree1);

        trees[0].reRoot(map.get(root));
        Assert.assertEquals(trees[0].toString(), originTree2);

        trees[0].reRoot(map.get(v2));
        Assert.assertEquals(trees[0].toString(), reRootTree2);
    }

    @Test
    public void testLCA() {
        RootFinder rf = new SpecificIdRootFinder(2);
        Vertex root = rf.selectRootVertex(g);
        SpanningTreeSolver bfs = new BFSsolver();
        Tree[] trees = bfs.buildTreeCoTree(g, root, null);
        Map<Vertex, Tree.TreeNode> map = trees[0].mapVertexToTreeNode(false);
        Tree.TreeNode n1 = map.get((new SpecificIdRootFinder(1)).selectRootVertex(g));
        int[] v = new int[]{1, 2, 4, 5, 0};
        int[] LCAs = new int[]{1, 2, 0, 2, 0};
        for (int i = 0; i < v.length; i++) {
            Tree.TreeNode n2 = map.get((new SpecificIdRootFinder(v[i])).selectRootVertex(g));
            Assert.assertEquals(LCAs[i], trees[0].leastCommonAncestor(n1, n2).getData().getID());
        }
    }

    @Test
    public void testDist() {
        RootFinder rf = new SpecificIdRootFinder(2);
        Vertex root = rf.selectRootVertex(g);
        SpanningTreeSolver bfs = new BFSsolver();
        Tree[] trees = bfs.buildTreeCoTree(g, root, null);
        Map<Vertex, Tree.TreeNode> map = trees[0].mapVertexToTreeNode(false);
        trees[0].updateDistance();
        int[] dist = new int[]{1, 2, 0, 1, 2, 1};
        for (Vertex v : g.getVertices()) {
            Tree.TreeNode n = map.get(v);
            Assert.assertEquals(dist[v.getID()], n.getDist());
        }

        Vertex v1 = (new SpecificIdRootFinder(1)).selectRootVertex(g);
        trees[0].reRoot(map.get(v1));
        trees[0].updateDistance();
        int[] dist1 = new int[]{1, 0, 2, 3, 2, 3};
        for (Vertex v : g.getVertices()) {
            Tree.TreeNode n = map.get(v);
            Assert.assertEquals(dist1[v.getID()], n.getDist());
        }
    }
}
