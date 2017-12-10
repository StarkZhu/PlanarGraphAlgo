package algorithms.Separator;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.util.*;

public class LevelSeparator extends Separator {

    public LevelSeparator(SelfDualGraph g) {
        super(g);
    }

    /**
     * find the median level vertices as a balanced separator
     *
     * @param tree
     * @return
     */
    public Set<Vertex> findLevelSeparatorOfTree(Tree tree) {
        double totalSum = tree.getRoot().getDescendantWeightSum();
        if (totalSum <= 0) {
            throw new RuntimeException("Weight has not been assigned to this tree");
        }
        List<Set<Tree.TreeNode>> list = buildVertexLevels(tree.getRoot(), new ArrayList<>(), 0);

        int medianLevel = findMedianLevel(list, totalSum);
        separator = new HashSet<>();
        for (Tree.TreeNode node : list.get(medianLevel)) {
            separator.add(node.getData());
        }
        buildSubgraphs(list, medianLevel);
        return separator;
    }

    /**
     * find a set of vertices as separator for the given planar graph
     *
     * @param sts if null, use default BFSsolver
     * @param rf  if null, use default MaxDegreeRoot
     * @param twa if null, use default VertexCount
     * @return
     */
    public Set<Vertex> findSeparator(SpanningTreeSolver sts, RootFinder rf, TreeWeightAssigner twa) {
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
        return findLevelSeparatorOfTree(trees[0]);
    }

    @Override
    public Set<Vertex> findSeparator() {
        return findSeparator(null, null, null);
    }


    @Override
    public Set<Vertex>[] findSubgraphs() {
        if (separator == null) {
            findSeparator();
        }
        return subgraphs;
    }

    /**
     * all vertices above mid-level is one subgraph, the rest is the other one
     * both subgraph include the level separator, for future r-division use
     */
    private void buildSubgraphs(List<Set<Tree.TreeNode>> levelList, int mLevel) {
        subgraphs = new Set[2];
        subgraphs[0] = getVerticesBetweenLevels(levelList, 0, mLevel);
        subgraphs[1] = getVerticesBetweenLevels(levelList, mLevel, levelList.size() - 1);
    }

}
