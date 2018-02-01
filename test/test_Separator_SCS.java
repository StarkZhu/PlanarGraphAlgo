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
        Tree.TreeNode separatorNode = (new SimpleCycleSeparator(g)).leafmostHeavyVertex(coTreeRoot, 1.0 / 3, coTreeRoot.getDescendantWeightSum());
        Map<Vertex, Tree.TreeNode> primalTreeMap = trees[0].mapVertexToTreeNode(false);
        Dart uv = separatorNode.getParentDart();
        Tree.TreeNode root = trees[0].leastCommonAncestor(primalTreeMap.get(uv.getTail()), primalTreeMap.get(uv.getHead()));
        trees[0].reRoot(root);
        trees[0].updateDistance();
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
}