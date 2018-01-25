package selfdualgraph;

import java.util.*;

/**
 * Simple tree structure used for spanning trees
 */
public class Tree {
    private TreeNode root;

    public Tree(Vertex rootData) {
        root = new TreeNode(rootData, null, null);
    }

    public TreeNode getRoot() {
        return root;
    }

    /**
     * assign distance (from root to TreeNode) to all nodes in the tree
     */
    public void updateDistance() {
        updateDistance(root, 0);
    }

    private void updateDistance(TreeNode root, int dist) {
        /*
        root.setDist(dist);
        for (TreeNode n : root.children) {
            updateDistance(n, dist+1);
        }
        */
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(root);
        q.add(null);
        while (q.size() > 1) {
            Tree.TreeNode n = q.poll();
            if (n == null) {
                q.add(null);
                dist++;
                continue;
            }
            n.setDist(dist);
            q.addAll(n.children);
        }
    }

    @Override
    public String toString() {
        return printTree(root, 0);
    }

    /**
     * print the tree from the given root, with increased indentation
     * @param root
     * @param spaceNum
     * @return
     */
    public String printTree(TreeNode root, int spaceNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[spaceNum]).replace("\0", " "));
        sb.append(String.format("%s\n", root.getData()));
        for (TreeNode child : root.getChildren()) {
            sb.append(printTree(child, spaceNum+2));
        }
        return sb.toString();
    }

    /**
     * Re-root the tree at the given node
     * @param node must be in this tree, not checked
     */
    public void reRoot(TreeNode node) {
        if (root == node) return;
        Stack<TreeNode> parents = new Stack<>();
        TreeNode current = node;
        while (current != null) {
            parents.push(current);
            current = current.parent;
        }
        current = parents.pop();
        while (!parents.isEmpty()) {
            TreeNode tmp = parents.pop();
            current.parent = tmp;
            current.children.remove(tmp);
            tmp.children.add(current);
            current = tmp;
        }
        node.parent = null;
        root = node;
    }

    /**
     * map all vertices stored in a tree to the corresponding TreeNode that stores it
     * @param resetVertexSelfweight
     * @return
     */
    public Map<Vertex, TreeNode> mapVertexToTreeNode(boolean resetVertexSelfweight) {
        TreeNode node = getRoot();
        Map<Vertex, TreeNode> map = new HashMap<>();
        Queue<TreeNode> q = new LinkedList<>();
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
     * find the least common ancestor of 2 TreeNodes
     * @param p
     * @param q
     * @return
     */
    public TreeNode leastCommonAncestor(TreeNode p, TreeNode q) {
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
        return q;
    }

    public static class TreeNode {
        private Vertex data;
        private double descendantWeightSum;
        private double selfWeight;
        private TreeNode parent;
        private Dart parentDart;
        private List<TreeNode> children;
        private int dist;

        public TreeNode(Vertex nodeData, TreeNode parent, Dart d) {
            data = nodeData;
            this.parent = parent;
            children = new ArrayList<>();
            descendantWeightSum = 0.0;
            parentDart = d;
            selfWeight = nodeData.getWeight();
            dist = -1;
        }

        public Vertex getData() {
            return data;
        }

        public TreeNode getParent() {
            return parent;
        }

        public Dart getParentDart() {
            return parentDart;
        }

        public List<TreeNode> getChildren() {
            return new ArrayList<>(children);
        }

        public void addChild(TreeNode child) {
            children.add(child);
        }

        public void setDescendantWeightSum(double v) {
            if (v < 0) {
                throw new RuntimeException("Sum of children's weigth must be greater than 0");
            }
            descendantWeightSum = v;
        }

        public double getDescendantWeightSum() {
            return descendantWeightSum;
        }

        public double getSelfWeight() {
            return selfWeight;
        }

        public void setSelfWeight(double selfWeight) {
            this.selfWeight = selfWeight;
        }

        public void setDist(int dist) {
            this.dist = dist;
        }

        public int getDist() {
            return dist;
        }

        /*
        public boolean isRoot() {
            return (this.parent == null);
        }

        public boolean isLeaf() {
            return this.children.size() == 0;
        }
        */
    }
}
