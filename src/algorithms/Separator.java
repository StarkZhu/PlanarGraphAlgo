package algorithms;

import selfdualgraph.*;
import java.util.*;

public abstract class Separator {

    /**
     * linear-time to find a vertex such that w(v0) > alpha * totalW, and every child v of v0 has w(v) <= alpha * totalW
     * @param root
     * @param alpha in range (0, 1)
     * @param totalW the weight of the root
     * @return
     */
    public static Tree.TreeNode<Vertex> leafmostHeavyVertex(Tree.TreeNode<Vertex> root, double alpha, double totalW) {
        if (alpha <= 0 || alpha >= 1) {
            throw new RuntimeException("alpha must be in range (0, 1)");
        }
        for (Tree.TreeNode<Vertex> child : root.getChildren()) {
            if (child.getWeightSum() > alpha * totalW) {
                return leafmostHeavyVertex(child, alpha, totalW);
            }
        }
        return root;
    }

    /**
     * find a vertex v such that every component in Tree - {v} has totalt weight at most W/2
     * @param tree
     * @return
     */
    public static Vertex findVertexSeparator(Tree tree) {
        Tree.TreeNode<Vertex> root = tree.getRoot();
        if (root.getWeightSum() <= 0) {
            // no weight assigned, use default vertex count as weight
            TreeWeightAssigner.calcWeightSum(root, new TreeWeightAssigner.VertexCount());
        }
        Tree.TreeNode<Vertex> separatorNode = leafmostHeavyVertex(root, 0.5, root.getWeightSum());
        return separatorNode.getData();
    }

    /**
     *
     * @param tree a tree of degree at most three, and with 1/3-proper assignment of weights to edges
     * @return
     */
    /*
    public static Dart findEdgeSeparator(Tree tree) {

    }
    */

    /**
     * build a list of vertices in each level
     * @param root
     * @return
     */
    private static List<Set<Vertex>> buildVertexLevels(Tree.TreeNode<Vertex> root, List<Set<Vertex>> list, int level) {
        if (level >= list.size()) list.add(new HashSet<>());
        list.get(level).add(root.getData());
        for (Tree.TreeNode<Vertex> child : root.getChildren()) {
            buildVertexLevels(child, list, level + 1);
        }
        return list;
    }

    /**
     * find the median level vertices as a balanced separator
     * @param tree
     * @return
     */
    public static Set<Vertex> findLevelSeparator(Tree<Vertex> tree) {
        TreeWeightAssigner.calcWeightSum(tree.getRoot(), new TreeWeightAssigner.VertexCount());
        List<Set<Vertex>> list = buildVertexLevels(tree.getRoot(), new ArrayList<>(), 0);
        int totalCount = 0;
        for (int i=0; i<list.size(); i++) {
            if (totalCount + list.get(i).size() >= tree.getRoot().getWeightSum() / 2) {
                return list.get(i);
            }
            totalCount += list.get(i).size();
        }
        throw new RuntimeException("Cannot find median level");
    }

}
