package algorithms;

import algorithms.RootFinder.MaxDegreeRootFinder;
import algorithms.RootFinder.RootFinder;
import selfdualgraph.*;

import java.util.*;

public abstract class Separator {

    /**
     * linear-time to find a vertex such that w(v0) > alpha * totalW, and every child v of v0 has w(v) <= alpha * totalW
     *
     * @param root
     * @param alpha  in range (0, 1)
     * @param totalW the weight of the root
     * @return
     */
    public static Tree.TreeNode leafmostHeavyVertex(Tree.TreeNode root, double alpha, double totalW) {
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

    // ----------------------------

    /**
     * find a vertex v such that every component in Tree - {v} has total weight at most W/2
     *
     * @param tree
     * @return
     */
    public static Vertex findVertexSeparator(Tree tree) {
        Tree.TreeNode root = tree.getRoot();
        if (root.getDescendantWeightSum() <= 0) {
            // no weight assigned, use default vertex count as weight
            TreeWeightAssigner.calcWeightSum(root, new TreeWeightAssigner.VertexCount());
        }
        Tree.TreeNode separatorNode = leafmostHeavyVertex(root, 0.5, root.getDescendantWeightSum());
        return separatorNode.getData();
    }

    // ----------------------------

    /**
     * check if the tree is binary
     * check if all degree-3 vertices have zero weight
     *
     * @param root
     */
    private static void checkZeroWeightVerticesBinaryTree(Tree.TreeNode root) {
        if (root.getChildren().size() > 2) {
            throw new RuntimeException("This is not a valid binary tree");
        }
        if (root.getParentDart() != null && root.getChildren().size() >= 2 && root.getSelfWeight() != 0) {
            throw new RuntimeException("Degree-3 vertices mush have 0 weight");
        }
        for (Tree.TreeNode node : root.getChildren()) {
            checkZeroWeightVerticesBinaryTree(node);
        }
    }

    /**
     * Note: degree-three vertices in the given tree must be assigned 0 weight
     *
     * @param tree a tree of degree at most three, and with 1/3-proper assignment of weights to edges and vertices
     * @return
     */
    public static Dart findEdgeSeparator(Tree tree) {
        Tree.TreeNode root = tree.getRoot();
        checkZeroWeightVerticesBinaryTree(root);
        if (root.getDescendantWeightSum() <= 0) {
            // no weight assigned, use default
            TreeWeightAssigner.calcWeightSum(root, new TreeWeightAssigner.VertexAndEdgeWeight());
        }
        Tree.TreeNode separatorNode = leafmostHeavyVertex(root, 1.0 / 3, root.getDescendantWeightSum());
        return separatorNode.getParentDart();
    }

    /**
     * map all vertices stored in a tree to the corresponding TreeNode that stores it
     * @param tree
     * @param resetVertexSelfweight
     * @return
     */
    private static Map<Vertex, Tree.TreeNode> mapVertexToTreeNode(Tree tree, boolean resetVertexSelfweight) {
        Tree.TreeNode node = tree.getRoot();
        Map<Vertex, Tree.TreeNode> map = new HashMap<>();
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(node);
        while (!q.isEmpty()) {
            node = q.poll();
            map.put(node.getData(), node);
            if (resetVertexSelfweight) node.setSelfWeight(0);
            q.addAll(node.getChildren());
        }
        return map;
    }

    private static void reassignTreeNodeWeight(Tree[] trees) {
        // reset all TreeNodes' selfWeight to 0 in the coTree, build mapping Vertex --> TreeNode
        Map<Vertex, Tree.TreeNode> map = mapVertexToTreeNode(trees[1], true);

        //traverse primal Tree, split edge weight evenly to faces on both sides
        Queue<Tree.TreeNode> q = new LinkedList<>();
        Tree.TreeNode node  = trees[0].getRoot();
        q.addAll(node.getChildren());
        while (!q.isEmpty()) {
            node = q.poll();
            Dart d = node.getParentDart();
            Vertex f = d.getRight();
            Tree.TreeNode n = map.get(f);
            n.setSelfWeight(n.getSelfWeight() + d.getWeight() / 2.0);
            f = d.getLeft();
            n = map.get(f);
            n.setSelfWeight(n.getSelfWeight() + d.getWeight() / 2.0);
            q.addAll(node.getChildren());
        }
    }


    /**
     * Vertex/Face nodes' selfWeight will be overwrote in this method
     *
     * @param g   must be flattened and triangulated
     * @param sts if null, use default BFSsolver
     * @param rf  if null, use default MaxDegreeRoot
     * @param twa if null, use default EdgeWeight
     * @return
     */
    public static Set<Vertex> findFundamentalCycleSeparator(SelfDualGraph g, SpanningTreeSolver sts, RootFinder rf, TreeWeightAssigner twa) {
        if (sts == null) {
            sts = new SpanningTreeSolver.BFSsolver();
        }

        if (rf == null) {
            rf = new MaxDegreeRootFinder();
        }

        if (twa == null ) {
            twa = new TreeWeightAssigner.VertexCount();
        }
        else if (twa.getClass() == TreeWeightAssigner.VertexWeight.class
                || twa.getClass() == TreeWeightAssigner.VertexAndEdgeWeight.class) {
            System.err.printf("Current Fundamental Cycle Separator does NOT support this user specified TreeWeightAssigner\n");
            System.err.printf("Default Vertex/Face Count TreeWeightAssigner is used\n");
            twa = new TreeWeightAssigner.VertexCount();
        }

        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g, sts, rf.selectRootVertex(g), null);
        assignCotreeWeight(twa, trees);

        Dart separatorDart = findEdgeSeparator(trees[1]);

        // build Vertex --> TreeNode mapping for the primal Tree
        Map<Vertex, Tree.TreeNode> map = mapVertexToTreeNode(trees[0], false);

        // find the least common ancestor of the 2 ends of separatorDart
        Tree.TreeNode p = map.get(separatorDart.getTail());
        Tree.TreeNode q = map.get(separatorDart.getHead());
        Set<Tree.TreeNode> parents = new HashSet<>();
        parents.add(p);
        // store p's parents all the way to root
        while (p.getParent() != null) {
            parents.add(p.getParent());
            p = p.getParent();
        }
        // find first TreeNode contained in p's parents, it is the least common ancestor (LCA)
        while (!parents.contains(q)) {
            parents.add(q);
            q = q.getParent();
        }
        // remove all LCA's parent, as they are not in the cycle separator
        while (q.getParent() != null) {
            parents.remove(q.getParent());
            q = q.getParent();
        }

        Set<Vertex> cycleSeparator = new HashSet<>();
        for (Tree.TreeNode n : parents) cycleSeparator.add(n.getData());
        return cycleSeparator;
    }

    /**
     * used by Fundamental Cycle Separator
     * @param twa
     * @param trees
     */
    public static void assignCotreeWeight(TreeWeightAssigner twa, Tree[] trees) {
        // reset all vertices selfweigth to 0 in coTree
        mapVertexToTreeNode(trees[1], true);
        if (twa.getClass() == TreeWeightAssigner.EdgeWeight.class) {
            reassignTreeNodeWeight(trees);
            // each faceVertex contains weight from edges in primal Tree
            twa = new TreeWeightAssigner.VertexAndEdgeWeight();
        }
        TreeWeightAssigner.calcWeightSum(trees[1].getRoot(), twa);
    }

    // ----------------------------

    /**
     * build a list of vertices in each level
     *
     * @param root
     * @return
     */
    private static List<Set<Tree.TreeNode>> buildVertexLevels(Tree.TreeNode root,
                                                                      List<Set<Tree.TreeNode>> list, int level) {
        if (level >= list.size()) list.add(new HashSet<>());
        list.get(level).add(root);
        for (Tree.TreeNode child : root.getChildren()) {
            buildVertexLevels(child, list, level + 1);
        }
        return list;
    }

    /**
     * find the median level vertices as a balanced separator
     *
     * @param tree
     * @return
     */
    public static Set<Vertex> findLevelSeparator(Tree tree) {
        double totalSum = tree.getRoot().getDescendantWeightSum();
        if (totalSum <= 0) {
            throw new RuntimeException("Weight has not been assigned to this tree");
        }
        List<Set<Tree.TreeNode>> list = buildVertexLevels(tree.getRoot(), new ArrayList<>(), 0);

        for (int i = list.size() - 1; i >= 0; i--) {
            double leafwardSum = 0;
            Set<Vertex> separator = new HashSet<>();
            for (Tree.TreeNode node : list.get(i)) {
                leafwardSum += node.getDescendantWeightSum();
                separator.add(node.getData());
            }
            if (leafwardSum >= totalSum / 2) {
                return separator;
            }
        }
        throw new RuntimeException("Cannot find median level");
    }

    /**
     * find a set of vertices as separator for the given planar graph
     *
     * @param g
     * @param sts if null, use default BFSsolver
     * @param rf  if null, use default MaxDegreeRoot
     * @param twa if null, use default VertexCount
     * @return
     */
    public static Set<Vertex> findLevelSeparator(SelfDualGraph g, SpanningTreeSolver sts, RootFinder rf, TreeWeightAssigner twa) {
        if (sts == null) {
            sts = new SpanningTreeSolver.BFSsolver();
        }

        if (rf == null) {
            rf = new MaxDegreeRootFinder();
        }

        if (twa == null) {
            twa = new TreeWeightAssigner.VertexCount();
        }

        Tree[] trees = SpanningTreeSolver.buildTreeCoTree(g, sts, rf.selectRootVertex(g), null);
        TreeWeightAssigner.calcWeightSum(trees[0].getRoot(), twa);
        Set<Vertex> separator = findLevelSeparator(trees[0]);
        return separator;
    }

    public static void main(String[] args) {
        TreeWeightAssigner.VertexWeight tmp = new TreeWeightAssigner.VertexWeight();
        System.out.println(tmp.getClass() == TreeWeightAssigner.VertexWeight.class);
    }

}
