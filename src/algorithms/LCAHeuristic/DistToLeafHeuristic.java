package algorithms.LCAHeuristic;

import selfdualgraph.*;


public class DistToLeafHeuristic extends LCAHeuristic{

    @Override
    public void setTrees(Tree tree, Tree cotree) {
        super.setTrees(tree, cotree);
        cotree.resetDist();
        cotree.updateDistToLeaf();
    }

    @Override
    /**
     * estimate cycle length by adding the distance-to-root of u & v
     */
    public int cycleLenHeuristic(Tree.TreeNode coTreeNode) {
        return coTreeNode.getDist() + 2;
    }
}
