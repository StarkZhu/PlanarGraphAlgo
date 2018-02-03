package algorithms.LCAHeuristic;

import selfdualgraph.*;


public class CotreeDepthHeuristic extends LCAHeuristic{
    @Override
    /**
     * estimate cycle length by adding the distance-to-root of u & v
     */
    public int cycleLenHeuristic(Tree.TreeNode coTreeNode) {
        return coTreeNode.getDist();
    }
}
