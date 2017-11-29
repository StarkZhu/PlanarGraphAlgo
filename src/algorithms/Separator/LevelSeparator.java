package algorithms.Separator;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.util.*;

public class LevelSeparator extends Separator {

    /**
     * build a list of vertices in each level
     *
     * @param root
     * @return
     */
    private List<Set<Tree.TreeNode>> buildVertexLevels(Tree.TreeNode root,
                                                              List<Set<Tree.TreeNode>> list, int level) {
        if (level >= list.size()) list.add(new HashSet<>());
        list.get(level).add(root);
        for (Tree.TreeNode child : root.getChildren()) {
            buildVertexLevels(child, list, level + 1);
        }
        return list;
    }

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

        for (int i = list.size() - 1; i >= 0; i--) {
            double leafwardSum = 0;
            Set<Vertex> separator = new HashSet<>();
            for (Tree.TreeNode node : list.get(i)) {
                leafwardSum += node.getDescendantWeightSum();
                separator.add(node.getData());
            }
            if (leafwardSum >= totalSum / 2) {
                return separator;
            }
        }
        throw new RuntimeException("Cannot find median level");
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
        if (sts == null) {
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
