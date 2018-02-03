package algorithms.LCAHeuristic;

import selfdualgraph.*;

public abstract class LCAHeuristic {
    protected Tree primalTree, dualTree;

    public void setTrees(Tree tree, Tree cotree) {
        primalTree = tree;
        dualTree = cotree;
    }

    public abstract int cycleLenHeuristic(Tree.TreeNode coTreeNode);
}
