import algorithms.RootFinder.*;
import algorithms.Separator.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import org.junit.*;
import selfdualgraph.*;

import java.util.*;
public class test_Separator_FCS extends test_Separator{
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
        verifyVertexSet(new int[]{5, 0, 3}, separator);
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{5, 0, 3, 2}, subgraphs[0]);
        verifyVertexSet(new int[]{5, 0, 3, 1, 4}, subgraphs[1]);

        separator = sp.findSeparator(null, null, new VertexWeight());
        verifyVertexSet(new int[]{5, 0, 3}, separator);
        subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{5, 0, 3, 2}, subgraphs[0]);
        verifyVertexSet(new int[]{5, 0, 3, 1, 4}, subgraphs[1]);
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
        verifyVertexSet(new int[]{4, 0, 3, 2}, separator);
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{4, 0, 3, 2, 1}, subgraphs[0]);
        verifyVertexSet(new int[]{4, 0, 3, 2, 5}, subgraphs[1]);
    }
}
