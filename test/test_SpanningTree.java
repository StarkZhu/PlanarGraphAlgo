import algorithms.RootFinder.RootFinder;
import algorithms.RootFinder.SpecificIdRootFinder;
import algorithms.SpanningTreeSolver.*;
import org.junit.*;
import selfdualgraph.*;
import java.io.FileNotFoundException;
import java.util.*;

public class test_SpanningTree {
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
    public void testDFS() {
        String treeBenchmark = "V<0>\n  V<1>\n    V<3>\n      V<4>\n        V<5>\n          V<2>\n";
        String coTreeBenchmark = "F<0>\n  F<4>\n    F<5>\n      F<6>\n  F<1>\n    F<2>\n    F<3>\n";
        SpanningTreeSolver dfs = new DFSsolver();
        Tree[] trees = dfs.buildTreeCoTree(g, treeRootVertex, coTreeRootVertex);
        Assert.assertEquals(trees[0].toString(), treeBenchmark);
        Assert.assertEquals(trees[1].toString(), coTreeBenchmark);
    }

    @Test
    public void testParent() {
        Vertex randomTreeRootVertex = (g.getVertices().toArray(new Vertex[0]))[new Random().nextInt(g.getVertexNum())];
        Vertex randomCotTreeRootVertex = (g.getFaces().toArray(new Vertex[0]))[new Random().nextInt(g.getFaceNum())];
        SpanningTreeSolver dfs = new DFSsolver();
        Tree[] trees = dfs.buildTreeCoTree(g, randomTreeRootVertex, randomCotTreeRootVertex);
        Queue<Tree.TreeNode> q = new LinkedList<>();
        for (int i=0; i<2; i++) {
            q.add(trees[i].getRoot());
            while (!q.isEmpty()) {
                Tree.TreeNode node = q.poll();
                for (Tree.TreeNode child : node.getChildren()) {
                    Assert.assertEquals(node, child.getParent());
                    q.add(child);
                }
            }
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
    public void testBFS() {
        String treeBenchmark = "V<0>\n  V<1>\n    V<3>\n  V<2>\n  V<5>\n  V<4>\n";
        String coTreeBenchmark = "F<0>\n  F<1>\n    F<2>\n      F<4>\n    F<3>\n    F<5>\n    F<6>\n";
        SpanningTreeSolver bfs = new BFSsolver();
        Tree[] trees = bfs.buildTreeCoTree(g, treeRootVertex, coTreeRootVertex);
        Assert.assertEquals(trees[0].toString(), treeBenchmark);
        Assert.assertEquals(trees[1].toString(), coTreeBenchmark);
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
    public void testPrim() {
        String treeBenchmark = "V<0>\n  V<1>\n  V<5>\n    V<4>\n    V<2>\n      V<3>\n";
        String coTreeBenchmark = "F<0>\n  F<4>\n  F<6>\n    F<5>\n    F<1>\n      F<2>\n      F<3>\n";
        SpanningTreeSolver prim = new Primsolver();
        Tree[] trees = prim.buildTreeCoTree(g, treeRootVertex, coTreeRootVertex);
        Assert.assertEquals(trees[0].toString(), treeBenchmark);
        Assert.assertEquals(trees[1].toString(), coTreeBenchmark);
    }
}
