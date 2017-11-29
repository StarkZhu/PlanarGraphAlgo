package algorithms.TreeWeightAssigner;

import selfdualgraph.*;

public abstract class TreeWeightAssigner {

    /**
     * default: count each vertex as 1
     * @return
     */
    public abstract double getWeight(Tree.TreeNode root);

    public double calcWeightSum(Tree.TreeNode root) {
        if (root == null) return 0;
        double weightSum = getWeight(root);
        for (Tree.TreeNode child : root.getChildren()) {
            weightSum += calcWeightSum(child);
        }
        root.setDescendantWeightSum(weightSum);
        return weightSum;
    }

}
