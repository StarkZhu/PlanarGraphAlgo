package algorithms.TreeWeightAssigner;

import selfdualgraph.*;

import java.util.*;

public abstract class TreeWeightAssigner {

    /**
     * default: count each vertex as 1
     * @return
     */
    public abstract double getWeight(Tree.TreeNode node);

    /*
    public double calcWeightSum(Tree.TreeNode root) {
        if (root == null) return 0;
        double weightSum = getWeight(root);
        for (Tree.TreeNode child : root.getChildren()) {
            weightSum += calcWeightSum(child);
        }
        root.setDescendantWeightSum(weightSum);
        return weightSum;
    }
    */

    public double calcWeightSum(Tree.TreeNode root) {
        if (root == null) return 0;
        setUnvisited(root);
        Stack<Tree.TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Tree.TreeNode node = stack.pop();
            Vertex vertex = node.getData();
            if (!vertex.isVisited()) {
                vertex.setVisited(true);
                stack.push(node);
                for (Tree.TreeNode child : node.getChildren()) stack.push(child);
            }
            else {
                double weightSum = getWeight(node);
                for (Tree.TreeNode child : node.getChildren()) {
                    weightSum += child.getDescendantWeightSum();
                }
                node.setDescendantWeightSum(weightSum);
            }
        }

        return root.getDescendantWeightSum();
    }

    private void setUnvisited(Tree.TreeNode root) {
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            Tree.TreeNode node = q.poll();
            node.getData().setVisited(false);
            q.addAll(node.getChildren());
        }
    }
}
