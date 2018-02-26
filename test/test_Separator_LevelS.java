import algorithms.RootFinder.*;
import algorithms.Separator.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import org.junit.*;
import selfdualgraph.*;

import java.util.*;
public class test_Separator_LevelS extends test_Separator{
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
        verifyVertexSet(expectedVertices, separator);
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{5, 2, 0, 4}, subgraphs[0]);
        verifyVertexSet(new int[]{1, 3, 2, 0, 4}, subgraphs[1]);

        twa = new VertexAndEdgeWeight();
        twa.calcWeightSum(trees[0].getRoot());
        separator = sp.findLevelSeparatorOfTree(trees[0]);
        expectedVertices = new int[]{2, 0, 4};
        verifyVertexSet(expectedVertices, separator);

        twa = new VertexCount();
        twa.calcWeightSum(trees[1].getRoot());
        separator = sp.findLevelSeparatorOfTree(trees[1]);
        expectedVertices = new int[]{1, 4, 6};
        verifyVertexSet(expectedVertices, separator);
        subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{0, 1, 4, 6}, subgraphs[0]);
        verifyVertexSet(new int[]{2, 3, 5, 1, 4, 6}, subgraphs[1]);

        twa = new VertexAndEdgeWeight();
        twa.calcWeightSum(trees[1].getRoot());
        Assert.assertEquals(16.55, trees[1].getRoot().getDescendantWeightSum(), 0.0001);
        separator = sp.findLevelSeparatorOfTree(trees[1]);
        expectedVertices = new int[]{2, 3, 5};
        verifyVertexSet(expectedVertices, separator);
        subgraphs = sp.findSubgraphs();
        verifyVertexSet(new int[]{0, 1, 4, 6, 2, 3, 5}, subgraphs[0]);
        verifyVertexSet(new int[]{2, 3, 5}, subgraphs[1]);
    }

    @Test
    public void testLevelSeparatorGivenGraph() {
        LevelSeparator sp = new LevelSeparator(g);
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        Set<Vertex> separator = sp.findSeparator();
        verifyVertexSet(new int[]{4, 0, 2}, separator);
        verifyVertexSet(new int[]{5, 2, 0, 4}, subgraphs[0]);
        verifyVertexSet(new int[]{1, 3, 2, 0, 4}, subgraphs[1]);
    }
}
