import algorithms.LCAHeuristic.CotreeDepthHeuristic;
import algorithms.RootFinder.*;
import algorithms.Separator.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class test_Separator_modifiedFCS {
    private SelfDualGraph g;

    @Before
    /**
     * executed before each test
     */
    public void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./test/benchmark_img_4x4.txt");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
        Dart.uniqueID = 0;
        Vertex.uniqueID = 0;
    }

    public void verifyVertexSet(int[] expectedVerticies, Set<Vertex> separator) {
        Assert.assertEquals(expectedVerticies.length, separator.size());
        Set<Integer> expectedID = new HashSet<>();
        for (int i : expectedVerticies) expectedID.add(i);
        for (Vertex v : separator) {
            Assert.assertTrue(expectedID.contains(v.getID()));
        }
    }

    @Test
    public void testFCS() {
        g.flatten();
        g.triangulate();

        FundamentalCycleSeparator sp = new FundamentalCycleSeparator(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(0), null, -1);
        verifyVertexSet(new int[]{0, 5, 9, 14}, separator);
    }

    @Test
    public void testFCSmod() {
        g.flatten();
        g.triangulate();

        FundamentalCycleSeparator sp = new ModifiedFCS(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(0), null, -1);
        verifyVertexSet(new int[]{0, 5, 10, 11}, separator);
    }

    @Test
    public void testCotreeHeuristic() throws FileNotFoundException {
        g = new SelfDualGraph();
        g.buildGraph("./input_data/test_graph_0.txt");
        String treeBenchmark = "V<0>\n  V<1>\n    V<3>\n  V<2>\n  V<5>\n  V<4>\n";
        String coTreeBenchmark = "F<0>\n  F<1>\n    F<2>\n      F<4>\n    F<3>\n    F<5>\n    F<6>\n";
        SpanningTreeSolver bfs = new BFSsolver();
        RootFinder rf = new SpecificIdRootFinder(0);
        Tree[] trees = bfs.buildTreeCoTree(g, rf.selectRootVertex(g), rf.selectRootFace(g));

        ModifiedFCS sp = new ModifiedFCS(g, new CotreeDepthHeuristic());
        sp.prepareCoTreeHeuristic(trees[1]);
        verify_tree_dist(new int[]{4, 3, 2, 1, 1, 1, 1}, trees[1]);
    }

    public void verify_tree_dist(int[] dists, Tree tree) {
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(tree.getRoot());
        while (!q.isEmpty()) {
            Tree.TreeNode node = q.poll();
            Assert.assertEquals(dists[node.getData().getID()], node.getDist());
            q.addAll(node.getChildren());
        }
    }
}
