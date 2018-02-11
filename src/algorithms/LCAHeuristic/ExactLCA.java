package algorithms.LCAHeuristic;

import selfdualgraph.*;

import java.util.*;

public class ExactLCA extends LCAHeuristic {
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
        Tree.TreeNode u = primalMap.get(d.getTail());
        Tree.TreeNode v = primalMap.get(d.getHead());
        Tree.TreeNode lca = primalTree.leastCommonAncestor(u, v);
        return u.getDist() + v.getDist() - 2 * lca.getDist();
    }
}
