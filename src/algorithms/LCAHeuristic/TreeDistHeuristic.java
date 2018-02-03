package algorithms.LCAHeuristic;

import selfdualgraph.*;

import java.util.*;

public class TreeDistHeuristic extends LCAHeuristic{
    private Map<Vertex, Tree.TreeNode> primalMap;

    @Override
    public void setTrees(Tree tree, Tree cotree) {
        super.setTrees(tree, cotree);
        primalMap = tree.mapVertexToTreeNode(false);
    }

    @Override
    /**
     * estimate cycle length by adding the distance-to-root of u & v
     */
    public int cycleLenHeuristic(Tree.TreeNode coTreeNode) {
        Dart d = coTreeNode.getParentDart();
        return primalMap.get(d.getTail()).getDist() + primalMap.get(d.getHead()).getDist();
    }
}
