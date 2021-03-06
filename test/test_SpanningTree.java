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
        Assert.assertEquals(treeBenchmark, trees[0].toString());
        Assert.assertEquals(coTreeBenchmark, trees[1].toString());
    }

    @Test
    public void testParent() {
        Vertex randomTreeRootVertex = (g.getVertices().toArray(new Vertex[0]))[new Random().nextInt(g.getVertexNum())];
        Vertex randomCotTreeRootVertex = (g.getFaces().toArray(new Vertex[0]))[new Random().nextInt(g.getFaceNum())];
        SpanningTreeSolver dfs = new DFSsolver();
        Tree[] trees = dfs.buildTreeCoTree(g, randomTreeRootVertex, randomCotTreeRootVertex);
        Queue<Tree.TreeNode> q = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
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
    public void testBFS() {
        String treeBenchmark = "V<0>\n  V<1>\n    V<3>\n  V<2>\n  V<5>\n  V<4>\n";
        String coTreeBenchmark = "F<0>\n  F<1>\n    F<2>\n      F<4>\n    F<3>\n    F<5>\n    F<6>\n";
        SpanningTreeSolver bfs = new BFSsolver();
        Tree[] trees = bfs.buildTreeCoTree(g, treeRootVertex, coTreeRootVertex);
        Assert.assertEquals(treeBenchmark, trees[0].toString());
        Assert.assertEquals(coTreeBenchmark, trees[1].toString());
    }

    @Test
    public void testPrim() {
        String treeBenchmark = "V<0>\n  V<1>\n  V<5>\n    V<4>\n    V<2>\n      V<3>\n";
        String coTreeBenchmark = "F<0>\n  F<4>\n  F<6>\n    F<5>\n    F<1>\n      F<2>\n      F<3>\n";
        SpanningTreeSolver prim = new Primsolver();
        Tree[] trees = prim.buildTreeCoTree(g, treeRootVertex, coTreeRootVertex);
        Assert.assertEquals(treeBenchmark, trees[0].toString());
        Assert.assertEquals(coTreeBenchmark, trees[1].toString());
    }

    @Test
    public void testPruneRebuildTrees() {
        String treeBenchmark = "V<0>\n  V<1>\n    V<3>\n  V<2>\n  V<5>\n  V<4>\n";
        String coTreeBenchmark = "F<0>\n  F<1>\n    F<2>\n      F<4>\n    F<3>\n    F<5>\n    F<6>\n";
        SpanningTreeSolver bfs = new BFSsolver();
        Tree[] trees = bfs.buildTreeCoTree(g, treeRootVertex, coTreeRootVertex);
        g.resetAllToUnvisited();

        Set<Vertex> unchanged = getVertices(new int[]{0, 1, 2});
        bfs.pruneTree(trees[0].getRoot(), unchanged);
        String prunedTree = "V<0>\n  V<1>\n  V<2>\n";
        Assert.assertEquals(prunedTree, trees[0].toString());

        Map<Vertex, Tree.TreeNode> boundary = trees[0].mapVertexToTreeNode(false);
        for (Vertex v : new HashSet<>(boundary.keySet())) {
            if (!unchanged.contains(v)) boundary.remove(v);
        }
        bfs.rebuildTreeFromRoot(trees[0].getRoot(), boundary);
        Assert.assertEquals(treeBenchmark, trees[0].toString());

        trees[1] = new Tree(trees[1].getRoot().getData());
        bfs.buildCoTree(trees[1].getRoot());
        Assert.assertEquals(coTreeBenchmark, trees[1].toString());
    }

    @Test
    public void testRebuildTreeCoTree() {
        String treeBenchmark = "V<0>\n  V<1>\n    V<3>\n  V<2>\n  V<5>\n  V<4>\n";
        String coTreeBenchmark = "F<0>\n  F<1>\n    F<2>\n      F<4>\n    F<3>\n    F<5>\n    F<6>\n";

        SpanningTreeSolver bfs = new BFSsolver();
        Tree[] trees = bfs.buildTreeCoTree(g, treeRootVertex, coTreeRootVertex);
        Set<Vertex> unchanged = getVertices(new int[]{0, 1, 2});
        Map<Vertex, Tree.TreeNode> boundary = trees[0].mapVertexToTreeNode(false);
        for (Vertex v : new HashSet<>(boundary.keySet())) {
            if (!unchanged.contains(v)) boundary.remove(v);
        }
        bfs.pruneTree(trees[0].getRoot(), unchanged);

        bfs.rebuildTreeCoTree(trees, g, unchanged, boundary);
        Assert.assertEquals(treeBenchmark, trees[0].toString());
        Assert.assertEquals(coTreeBenchmark, trees[1].toString());
    }

    private Set<Vertex> getVertices(int[] ids) {
        Set<Integer> set = new HashSet<>();
        for (int i : ids) set.add(i);
        Set<Vertex> vertices = new HashSet<>();
        for (Vertex v : g.getVertices()) {
            if (set.contains(v.getID())) vertices.add(v);
        }
        return vertices;
    }
}
