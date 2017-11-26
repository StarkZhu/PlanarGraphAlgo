package algorithms;

import selfdualgraph.*;

public abstract class TreeWeightAssigner {

    /**
     * default: count each vertex as 1
     * @return
     */
    public abstract double getWeight(Tree.TreeNode root);

    public static double calcWeightSum(Tree.TreeNode root, TreeWeightAssigner twa) {
        if (root == null) return 0;
        double weightSum = twa.getWeight(root);
        for (Tree.TreeNode child : root.getChildren()) {
            weightSum += calcWeightSum(child, twa);
        }
        root.setDescendantWeightSum(weightSum);
        return weightSum;
    }

    public static class VertexCount extends TreeWeightAssigner {
        @Override
        public double getWeight(Tree.TreeNode root) {
            return 1;
        }
    }

    public static class VertexWeight extends TreeWeightAssigner {
        @Override
        public double getWeight(Tree.TreeNode root) {
            return root.getSelfWeight();
        }
    }

    public static class EdgeWeight extends TreeWeightAssigner {
        @Override
        public double getWeight(Tree.TreeNode root) {
            if (root.getParentDart() == null) return 0;
            return root.getParentDart().getWeight();
        }
    }

    public static class VertexAndEdgeWeight extends TreeWeightAssigner {
        @Override
        public double getWeight(Tree.TreeNode root) {
            return root.getSelfWeight() + (root.getParentDart() == null ? 0 : root.getParentDart().getWeight());
        }
    }

}
