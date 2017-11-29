package algorithms.TreeWeightAssigner;

import selfdualgraph.Tree;

public class EdgeWeight extends TreeWeightAssigner {
    @Override
    public double getWeight(Tree.TreeNode root) {
        if (root.getParentDart() == null) return 0;
        return root.getParentDart().getWeight();
    }
}
