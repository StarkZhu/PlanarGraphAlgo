package algorithms.LCAHeuristic;

import selfdualgraph.*;

import java.util.*;

public class CombinedHeuristic extends LCAHeuristic {
    private Map<Vertex, Tree.TreeNode> primalMap;

    @Override
    public void setTrees(Tree tree, Tree cotree) {
        super.setTrees(tree, cotree);
        primalMap = tree.mapVertexToTreeNode(false);
        cotree.resetDist();
        cotree.updateDistToLeaf();
    }

    @Override
    /**
     * estimate cycle length by adding the distance-to-root of u & v
     */
    public int cycleLenHeuristic(Tree.TreeNode coTreeNode) {
        Dart d = coTreeNode.getParentDart();
        int distToRoot = primalMap.get(d.getTail()).getDist() + primalMap.get(d.getHead()).getDist();
        int distToLeaf = coTreeNode.getDist() + 2;
        return Math.min(distToLeaf, distToRoot);
    }
}
