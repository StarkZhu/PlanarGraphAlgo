package algorithms.TreeWeightAssigner;

import selfdualgraph.Tree;

public class VertexWeight extends TreeWeightAssigner {
    @Override
    public double getWeight(Tree.TreeNode root) {
        return root.getSelfWeight();
    }
}
