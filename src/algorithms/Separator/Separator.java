package algorithms.Separator;

import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.util.*;

public abstract class Separator {

    public abstract Set<Vertex> findSeparator(SelfDualGraph g);

    /**
     * linear-time to find a vertex such that w(v0) > alpha * totalW, and every child v of v0 has w(v) <= alpha * totalW
     *
     * @param root
     * @param alpha  in range (0, 1)
     * @param totalW the weight of the root
     * @return
     */
    public Tree.TreeNode leafmostHeavyVertex(Tree.TreeNode root, double alpha, double totalW) {
        if (alpha <= 0 || alpha >= 1) {
            throw new RuntimeException("alpha must be in range (0, 1)");
        }
        for (Tree.TreeNode child : root.getChildren()) {
            if (child.getDescendantWeightSum() > alpha * totalW) {
                return leafmostHeavyVertex(child, alpha, totalW);
            }
        }
        return root;
    }


    /**
     * find a vertex v such that every component in Tree - {v} has total weight at most W/2
     *
     * @param tree
     * @return
     */
    public Vertex findVertexSeparator(Tree tree) {
        Tree.TreeNode root = tree.getRoot();
        if (root.getDescendantWeightSum() <= 0) {
            // no weight assigned, use default vertex count as weight
            new VertexCount().calcWeightSum(root);
        }
        Tree.TreeNode separatorNode = leafmostHeavyVertex(root, 0.5, root.getDescendantWeightSum());
        return separatorNode.getData();
    }


    /**
     * check if the tree is binary
     * check if all degree-3 vertices have zero weight
     *
     * @param root
     */
    private void checkZeroWeightVerticesOfTree(Tree.TreeNode root, int maxDegree) {
        if (root.getChildren().size() > maxDegree -1) {
            throw new RuntimeException(String.format("This is not a valid %d-degree tree", maxDegree));
        }
        if (root.getParentDart() != null && root.getChildren().size() >= maxDegree - 1 && root.getSelfWeight() != 0) {
            throw new RuntimeException(String.format("Degree-%d vertices mush have 0 weight", maxDegree));
        }
        for (Tree.TreeNode node : root.getChildren()) {
            checkZeroWeightVerticesOfTree(node, maxDegree);
        }
    }

    /**
     * Note: degree-three vertices in the given tree must be assigned 0 weight
     *
     * @param tree a tree of degree at most three, and with 1/3-proper assignment of weights to edges and vertices
     * @return
     */
    public Dart findEdgeSeparator(Tree tree, int maxDegree) {
        Tree.TreeNode root = tree.getRoot();
        checkZeroWeightVerticesOfTree(root, maxDegree);
        if (root.getDescendantWeightSum() <= 0) {
            // no weight assigned, use default
            new VertexAndEdgeWeight().calcWeightSum(root);
        }
        Tree.TreeNode separatorNode = leafmostHeavyVertex(root, 1.0 / maxDegree, root.getDescendantWeightSum());
        return separatorNode.getParentDart();
    }

    public Dart findEdgeSeparator(Tree tree) {
        return findEdgeSeparator(tree, 3);
    }


    public static void main(String[] args) {
        TreeWeightAssigner tmp = new VertexWeight();
        System.out.println(tmp.getClass() == VertexWeight.class);
    }

}
