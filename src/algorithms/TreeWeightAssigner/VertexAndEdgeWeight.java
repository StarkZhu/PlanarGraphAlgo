package algorithms.TreeWeightAssigner;

import selfdualgraph.Tree;

public class VertexAndEdgeWeight extends TreeWeightAssigner {
    @Override
    public double getWeight(Tree.TreeNode root) {
        return root.getSelfWeight() + (root.getParentDart() == null ? 0 : root.getParentDart().getWeight());
    }
}
