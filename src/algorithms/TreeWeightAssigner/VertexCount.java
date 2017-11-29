package algorithms.TreeWeightAssigner;

import selfdualgraph.Tree;

public class VertexCount extends TreeWeightAssigner {
    @Override
    public double getWeight(Tree.TreeNode root) {
        return 1;
    }
}
