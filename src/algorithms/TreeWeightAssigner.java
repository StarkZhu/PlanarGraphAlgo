package algorithms;

import selfdualgraph.*;
import java.util.*;

public class TreeWeightAssigner {

    /**
     * default: count each vertex as 1
     * @return
     */
    public double getWeight(Tree.TreeNode<Vertex> root) {
        return 1;
    }

    public static double calcWeightSum(Tree.TreeNode<Vertex> root, TreeWeightAssigner twa) {
        if (root == null) return 0;
        double weightSum = twa.getWeight(root);
        for (Tree.TreeNode<Vertex> child : root.getChildren()) {
            weightSum += calcWeightSum(child, twa);
        }
        root.setWeightSum(weightSum);
        return weightSum;
    }

}
