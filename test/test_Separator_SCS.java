import algorithms.RootFinder.*;
import algorithms.Separator.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class test_Separator_SCS {

    public SelfDualGraph readGraph(String fileName) {
        SelfDualGraph g = new SelfDualGraph();
        try {
            g.buildGraph(fileName);
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
        return g;
    }

    public Dart findUV(SelfDualGraph g, RootFinder rf, Tree[] org_trees) {
        g.flatten();
        g.triangulate();
        SpanningTreeSolver sts = new BFSsolver();
        TreeWeightAssigner vertexCountTWA = new VertexCount();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        org_trees[0] = trees[0];
        org_trees[1] = trees[1];
        Tree.TreeNode coTreeRoot = trees[1].getRoot();
        vertexCountTWA.calcWeightSum(coTreeRoot);
        SimpleCycleSeparator sp = new SimpleCycleSeparator(g);
        Tree.TreeNode separatorNode = sp.leafmostHeavyVertex(coTreeRoot, 1.0 / 3, coTreeRoot.getDescendantWeightSum());
        Map<Vertex, Tree.TreeNode> primalTreeMap = trees[0].mapVertexToTreeNode(false);
        Dart uv = separatorNode.getParentDart();
        Tree.TreeNode root = trees[0].leastCommonAncestor(primalTreeMap.get(uv.getTail()), primalTreeMap.get(uv.getHead()));
        trees[0].reRoot(root);
        sp.rebuildBFStrees(sts, trees, separatorNode, primalTreeMap);
        trees[0].updateDistToRoot();
        return uv;
    }

    @Test
    public void test_grid4x4_rt0() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        SimpleCycleSeparator scs = new SimpleCycleSeparator(g);
        Tree[] trees = new Tree[2];
        Dart uv = findUV(g, new SpecificIdRootFinder(0), trees);
        verify_uv(uv, new int[]{5, 9});

        Map<Vertex, Tree.TreeNode> primalTreeMap = trees[0].mapVertexToTreeNode(false);
        Vertex phi = scs.getVertexPhi(uv, primalTreeMap);
        int h = primalTreeMap.get(phi).getDist();
        verify_phi(9, 2, phi, h);

        List<Set<Vertex>> levels = scs.verticeLevels(primalTreeMap, h);
        verify_setList(new int[][]{{0}, {1, 2, 3, 4, 5, 7, 8, 11, 12, 13, 14, 15}, {6, 9, 10}}, levels);

        Set<Vertex> path = scs.pathToPhi(primalTreeMap, phi);
        verify_set(new int[]{0, 14, 9}, path);

        List<Set<Vertex>> outerBoundaries = scs.identifyBoundaries(primalTreeMap, uv, h, levels, path);
        verify_setList(new int[][]{{0}, {1, 2, 4, 5, 7, 8, 11, 13, 14, 15}, {9, 10}}, outerBoundaries);

        Set<Vertex>[] vertexRegions = scs.identifyVertexRegions(outerBoundaries, trees[0].getRoot());
        int[][] regions = new int[][]{{}, {3, 12}, {6}};
        for (int i = 0; i < vertexRegions.length; i++) {
            verify_set(regions[i], vertexRegions[i]);
        }

        Set<Vertex> separator = scs.findSeparator(null, new SpecificIdRootFinder(0), null);
        verify_set(new int[]{1, 2, 4, 5, 7, 8, 11, 13, 14, 15}, separator);
    }

    @Test
    public void test_grid4x4_rt1() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        SimpleCycleSeparator scs = new SimpleCycleSeparator(g);
        Tree[] trees = new Tree[2];
        Dart uv = findUV(g, new SpecificIdRootFinder(1), trees);
        verify_uv(uv, new int[]{9, 14});

        Map<Vertex, Tree.TreeNode> primalTreeMap = trees[0].mapVertexToTreeNode(false);
        Vertex phi = scs.getVertexPhi(uv, primalTreeMap);
        int h = primalTreeMap.get(phi).getDist();
        verify_phi(14, 2, phi, h);

        List<Set<Vertex>> levels = scs.verticeLevels(primalTreeMap, h);
        verify_setList(new int[][]{{1}, {0, 2, 5, 6}, {3, 4, 7, 8, 9, 10, 11, 12, 13, 14, 15}}, levels);

        Set<Vertex> path = scs.pathToPhi(primalTreeMap, phi);
        verify_set(new int[]{0, 1, 14}, path);

        List<Set<Vertex>> outerBoundaries = scs.identifyBoundaries(primalTreeMap, uv, h, levels, path);
        verify_setList(new int[][]{{1}, {0, 2, 5, 6}, {9, 10, 14}}, outerBoundaries);

        Set<Vertex>[] vertexRegions = scs.identifyVertexRegions(outerBoundaries, trees[0].getRoot());
        int[][] regions = new int[][]{{}, {}, {3, 4, 7, 8, 11, 12, 13, 15}};
        for (int i = 0; i < vertexRegions.length; i++) {
            verify_set(regions[i], vertexRegions[i]);
        }

        Set<Vertex> separator = scs.findSeparator(null, new SpecificIdRootFinder(1), null);
        verify_set(new int[]{0, 2, 5, 6, 9, 14}, separator);
    }

    @Test
    public void test_grid4x4_rt3() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        SimpleCycleSeparator scs = new SimpleCycleSeparator(g);
        Tree[] trees = new Tree[2];
        Dart uv = findUV(g, new SpecificIdRootFinder(3), trees);
        verify_uv(uv, new int[]{9, 5});
        Assert.assertEquals(0, trees[0].getRoot().getData().getID());

        Map<Vertex, Tree.TreeNode> primalTreeMap = trees[0].mapVertexToTreeNode(false);
        Vertex phi = scs.getVertexPhi(uv, primalTreeMap);
        int h = primalTreeMap.get(phi).getDist();
        verify_phi(9, 2, phi, h);

        List<Set<Vertex>> levels = scs.verticeLevels(primalTreeMap, h);
        verify_setList(new int[][]{{0}, {1, 2, 3, 4, 5, 7, 8, 11, 12, 13, 14, 15}, {6, 9, 10}}, levels);

        Set<Vertex> path = scs.pathToPhi(primalTreeMap, phi);
        verify_set(new int[]{0, 14, 9}, path);

        List<Set<Vertex>> outerBoundaries = scs.identifyBoundaries(primalTreeMap, uv, h, levels, path);
        verify_setList(new int[][]{{0}, {1, 2, 4, 5, 7, 8, 11, 13, 14, 15}, {9, 10}}, outerBoundaries);

        Set<Vertex>[] vertexRegions = scs.identifyVertexRegions(outerBoundaries, trees[0].getRoot());
        int[][] regions = new int[][]{{}, {3, 12}, {6}};
        for (int i = 0; i < vertexRegions.length; i++) {
            verify_set(regions[i], vertexRegions[i]);
        }

        Set<Vertex> separator = scs.findSeparator(null, new SpecificIdRootFinder(3), null);
        verify_set(new int[]{1, 2, 4, 5, 7, 8, 11, 13, 14, 15}, separator);
    }

    @Test
    public void test_grid4x4_rt6() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        SimpleCycleSeparator scs = new SimpleCycleSeparator(g);
        Tree[] trees = new Tree[2];
        Dart uv = findUV(g, new SpecificIdRootFinder(6), trees);
        verify_uv(uv, new int[]{9, 13});
        Assert.assertEquals(6, trees[0].getRoot().getData().getID());

        Map<Vertex, Tree.TreeNode> primalTreeMap = trees[0].mapVertexToTreeNode(false);
        Vertex phi = scs.getVertexPhi(uv, primalTreeMap);
        int h = primalTreeMap.get(phi).getDist();
        verify_phi(13, 3, phi, h);

        List<Set<Vertex>> levels = scs.verticeLevels(primalTreeMap, h);
        verify_setList(new int[][]{{6}, {1, 2, 5, 7, 10, 11}, {0, 3, 4, 9, 14, 15}, {8, 12, 13}}, levels);

        Set<Vertex> path = scs.pathToPhi(primalTreeMap, phi);
        verify_set(new int[]{6, 2, 0, 13}, path);

        List<Set<Vertex>> outerBoundaries = scs.identifyBoundaries(primalTreeMap, uv, h, levels, path);
        verify_setList(new int[][]{{6}, {1, 2, 5, 7, 10, 11}, {0, 4, 9, 14}, {13}}, outerBoundaries);

        Set<Vertex>[] vertexRegions = scs.identifyVertexRegions(outerBoundaries, trees[0].getRoot());
        int[][] regions = new int[][]{{}, {}, {3, 15}, {8, 12}};
        for (int i = 0; i < vertexRegions.length; i++) {
            verify_set(regions[i], vertexRegions[i]);
        }

        Set<Vertex> separator = scs.findSeparator(null, new SpecificIdRootFinder(6), new VertexWeight());
        verify_set(new int[]{0, 2, 5, 6}, separator);
    }

    public void verify_uv(Dart uv, int[] ids) {
        Set<Integer> vertexUV = new HashSet<>();
        for (int i : ids) vertexUV.add(i);
        Assert.assertTrue(vertexUV.contains(uv.getHead().getID()));
        Assert.assertTrue(vertexUV.contains(uv.getTail().getID()));
    }

    public void verify_phi(int id, int dist, Vertex phi, int h) {
        Assert.assertEquals(id, phi.getID());
        Assert.assertEquals(dist, h);
    }

    public void verify_setList(int[][] answer, List<Set<Vertex>> levels) {
        int i = 0;
        for (Set<Vertex> level : levels) {
            Assert.assertEquals(answer[i].length, level.size());
            Set<Integer> ids = new HashSet<>();
            for (int j : answer[i]) ids.add(j);
            for (Vertex v : level) {
                Assert.assertTrue(ids.contains(v.getID()));
            }
            i++;
        }
    }

    public void verify_set(int[] answer, Set<Vertex> path) {
        Assert.assertEquals(answer.length, path.size());
        Set<Integer> set = new HashSet<>();
        for (int i : answer) set.add(i);
        for (Vertex v : path) {
            Assert.assertTrue(set.contains(v.getID()));
        }
    }

    // TODO: large data test, verify separator is a cycle
}
