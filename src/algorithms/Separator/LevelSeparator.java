package algorithms.Separator;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.util.*;

public class LevelSeparator extends Separator {


    /**
     * find the median level vertices as a balanced separator
     *
     * @param tree
     * @return
     */
    public  Set<Vertex> findLevelSeparatorOfTree(Tree tree) {
        double totalSum = tree.getRoot().getDescendantWeightSum();
        if (totalSum <= 0) {
            throw new RuntimeException("Weight has not been assigned to this tree");
        }
        List<Set<Tree.TreeNode>> list = buildVertexLevels(tree.getRoot(), new ArrayList<>(), 0);

        int medianLevel = findMedianLevel(list, totalSum);
        Set<Vertex> separator = new HashSet<>();
        for (Tree.TreeNode node : list.get(medianLevel)) {
            separator.add(node.getData());
        }
        return separator;
    }

    /**
     * find a set of vertices as separator for the given planar graph
     *
     * @param g
     * @param sts if null, use default BFSsolver
     * @param rf  if null, use default MaxDegreeRoot
     * @param twa if null, use default VertexCount
     * @return
     */
    public Set<Vertex> findSeparator(SelfDualGraph g, SpanningTreeSolver sts, RootFinder rf, TreeWeightAssigner twa) {
        if (sts == null || sts.getClass() != BFSsolver.class) {
            System.err.printf("Level Separator must BFS Tree\n");
            sts = new BFSsolver();
        }

        if (rf == null) {
            rf = new MaxDegreeRootFinder();
        }

        if (twa == null) {
            twa = new VertexCount();
        }

        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        twa.calcWeightSum(trees[0].getRoot());
        Set<Vertex> separator = findLevelSeparatorOfTree(trees[0]);
        return separator;
    }

    @Override
    public Set<Vertex> findSeparator(SelfDualGraph g) {
        return findSeparator(g, null, null, null);
    }
}
