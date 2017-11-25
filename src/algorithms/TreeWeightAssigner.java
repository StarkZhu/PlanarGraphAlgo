package algorithms;

import selfdualgraph.*;

public abstract class TreeWeightAssigner {

    /**
     * default: count each vertex as 1
     * @return
     */
    public abstract double getWeight(Tree.TreeNode<Vertex> root);

    public static double calcWeightSum(Tree.TreeNode<Vertex> root, TreeWeightAssigner twa) {
        if (root == null) return 0;
        double weightSum = twa.getWeight(root);
        for (Tree.TreeNode<Vertex> child : root.getChildren()) {
            weightSum += calcWeightSum(child, twa);
        }
        root.setDescendantWeightSum(weightSum);
        return weightSum;
    }

    public static class VertexCount extends TreeWeightAssigner {
        @Override
        public double getWeight(Tree.TreeNode<Vertex> root) {
            return 1;
        }
    }

    public static class VertexWeight extends TreeWeightAssigner {
        @Override
        public double getWeight(Tree.TreeNode<Vertex> root) {
            return root.getData().getWeight();
        }
    }

    public static class EdgeWeight extends TreeWeightAssigner {
        @Override
        public double getWeight(Tree.TreeNode<Vertex> root) {
            if (root.getParentDart() == null) return 0;
            return root.getParentDart().getWeight();
        }
    }

    public static class VertexAndEdgeWeight extends TreeWeightAssigner {
        @Override
        public double getWeight(Tree.TreeNode<Vertex> root) {
            return root.getData().getWeight() + (root.getParentDart() == null ? 0 : root.getParentDart().getWeight());
        }
    }

}
