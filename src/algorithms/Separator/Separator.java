package algorithms.Separator;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.util.*;

public abstract class Separator {
    protected SelfDualGraph g;
    protected Set<Vertex> separator;
    protected Set<Vertex>[] subgraphs;

    public Separator(SelfDualGraph g) {
        this.g = g;
    }

    public abstract Set<Vertex> findSeparator();
    public abstract Set<Vertex> findSeparator(SpanningTreeSolver sts, RootFinder rf, TreeWeightAssigner twa);
    public abstract Set<Vertex>[] findSubgraphs();

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
        Tree.TreeNode node = root;
        while (true) {
            boolean finished = true;
            for (Tree.TreeNode child : node.getChildren()) {
                if (child.getDescendantWeightSum() > alpha * totalW) {
                    node = child;
                    finished = false;
                }
            }
            if (finished) return node;
        }
    }
    /*
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
    */


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
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            Tree.TreeNode node = q.poll();
            if (node.getChildren().size() > maxDegree - 1) {
                System.err.println(node.getData());
                throw new RuntimeException(String.format("This is not a valid %d-degree tree", maxDegree));
            }
            if (node.getParentDart() != null && node.getChildren().size() >= maxDegree - 1 && node.getSelfWeight() != 0) {
                throw new RuntimeException(String.format("Degree-%d vertices mush have 0 weight", maxDegree));
            }
            for (Tree.TreeNode child : node.getChildren()) q.add(child);
        }
    }
    /*
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
    */

    /**
     * Note: degree-three vertices in the given tree must be assigned 0 weight
     *
     * @param tree a tree of degree at most three, and with 1/3-proper assignment of weights to edges and vertices
     * @return
     */
    public Tree.TreeNode findEdgeSeparator(Tree tree, int maxDegree) {
        Tree.TreeNode root = tree.getRoot();
        checkZeroWeightVerticesOfTree(root, maxDegree);
        if (root.getDescendantWeightSum() <= 0) {
            // no weight assigned, use default
            new VertexAndEdgeWeight().calcWeightSum(root);
        }
        Tree.TreeNode separatorNode = leafmostHeavyVertex(root, 1.0 / maxDegree, root.getDescendantWeightSum());
        return separatorNode;
    }

    /**
     * build a list of vertices in each level
     *
     * @param root
     * @return
     */
    protected List<Set<Tree.TreeNode>> buildVertexLevels(Tree.TreeNode root,
                                                         List<Set<Tree.TreeNode>> list, int level) {
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(root);
        q.add(null);
        while (!q.isEmpty()) {
            if (level >= list.size()) list.add(new HashSet<>());
            Tree.TreeNode node = q.poll();
            if (node == null) {
                level++;
                if (q.isEmpty()) break;
                q.add(null);
                continue;
            }
            list.get(level).add(node);
            for (Tree.TreeNode child : node.getChildren()) q.add(child);
        }
        return list;
    }
    /*
    protected List<Set<Tree.TreeNode>> buildVertexLevels(Tree.TreeNode root,
                                                       List<Set<Tree.TreeNode>> list, int level) {
        if (level >= list.size()) list.add(new HashSet<>());
        list.get(level).add(root);
        for (Tree.TreeNode child : root.getChildren()) {
            buildVertexLevels(child, list, level + 1);
        }
        return list;
    }
    */

    /**
     * find the median level of a Tree
     * @param list a list of set of TreeNodes at each level of a Tree
     * @param totalSum the weightSum at tree root node
     * @return
     */
    protected int findMedianLevel(List<Set<Tree.TreeNode>> list, double totalSum) {
        for (int i = list.size() - 1; i >= 0; i--) {
            double leafwardSum = 0;
            for (Tree.TreeNode node : list.get(i)) {
                leafwardSum += node.getDescendantWeightSum();
            }
            if (leafwardSum >= totalSum / 2) {
                return  i;
            }
        }
        throw new RuntimeException("Cannot find median level");
    }

    /**
     * find the maximum degree of TreeNodes in a Tree
     * @param root
     * @return
     */
    protected int findMaxDegreeOfTree(Tree.TreeNode root) {
        int degree = root.getChildren().size() + 1;
        for (Tree.TreeNode child : root.getChildren()) {
            degree = Math.max(degree, findMaxDegreeOfTree(child));
        }
        return degree;
    }


    /**
     * given primal Tree and a non-tree edge, find the fundamental cycle
     * @param tree
     * @param separatorDart
     * @return
     */
    protected Set<Vertex> getCycle(Tree tree, Dart separatorDart) {
        // build Vertex --> TreeNode mapping for the primal Tree
        Map<Vertex, Tree.TreeNode> map = mapVertexToTreeNode(tree, false);

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
     * map all vertices stored in a tree to the corresponding TreeNode that stores it
     * @param tree
     * @param resetVertexSelfweight
     * @return
     */
    protected Map<Vertex, Tree.TreeNode> mapVertexToTreeNode(Tree tree, boolean resetVertexSelfweight) {
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

    /**
     * give a list of TreeNodes for each level, find the set of vertices within the given level range
     * @param list
     * @param startLevel
     * @param endLevel
     * @return
     */
    protected Set<Vertex> getVerticesBetweenLevels(List<Set<Tree.TreeNode>> list, int startLevel, int endLevel) {
        Set<Vertex> vertices = new HashSet<>();
        if (startLevel < 0 || endLevel < startLevel || endLevel >= list.size()) {
            System.err.println("Invalid input, returning empty set");
            return vertices;
        }
        for (int i = startLevel; i <= endLevel; i++) {
            for (Tree.TreeNode node : list.get(i)) {
                vertices.add(node.getData());
            }
        }
        return vertices;
    }

    /**
     * give a TreeNode, find all vertices stored in its descendant nodes
     * @param root
     * @return
     */
    protected Set<Vertex> getDescendantVertices(Tree.TreeNode root) {
        Set<Vertex> childrenVertices = new HashSet<>();
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            Tree.TreeNode node = q.poll();
            childrenVertices.add(node.getData());
            q.addAll(node.getChildren());
        }
        return childrenVertices;
    }

    /**
     * given a set of faces, find all incidental vertices
     * @param faces
     * @return
     */
    protected Set<Vertex> getIncidentalVertices(Set<Vertex> faces) {
        Set<Vertex> vertices = new HashSet<>();
        for (Vertex f : faces) {
            for (Dart d : f.getIncidenceList()) {
                vertices.add(d.getTail());
            }
        }
        return vertices;
    }


    /**
     * all vertices above mid-level is one subgraph, the rest is the other one
     * both subgraph include the level separator, for future r-division use
     */
    protected void buildSubgraphs(List<Set<Tree.TreeNode>> levelList, int mLevel) {
        subgraphs = new Set[2];
        subgraphs[0] = getVerticesBetweenLevels(levelList, 0, mLevel);
        subgraphs[1] = getVerticesBetweenLevels(levelList, mLevel, levelList.size() - 1);
    }

    public static void main(String[] args) {
        TreeWeightAssigner tmp = new VertexWeight();
        System.out.println(tmp.getClass() == VertexWeight.class);
    }

}
