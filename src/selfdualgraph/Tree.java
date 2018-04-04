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
    public void updateDistToRoot() {
        updateDistToRoot(root, 0);
    }

    private void updateDistToRoot(TreeNode root, int dist) {
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
     *
     * @param root
     * @param spaceNum
     * @return
     */
    public String printTree(TreeNode root, int spaceNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[spaceNum]).replace("\0", " "));
        sb.append(String.format("%s\n", root.getData()));
        for (TreeNode child : root.getChildren()) {
            sb.append(printTree(child, spaceNum + 2));
        }
        return sb.toString();
    }

    /**
     * Re-root the tree at the given node
     *
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
     *
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

    public void resetDist() {
        TreeNode node = getRoot();
        Queue<TreeNode> q = new LinkedList<>();
        q.add(node);
        while (!q.isEmpty()) {
            node = q.poll();
            node.setDist(-1);
            q.addAll(node.getChildren());
        }
    }

    /**
     * find the least common ancestor of 2 TreeNodes
     *
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

    /**
     * Conjecture: dist of u,v to their LCA is roughly the deepest child of uv* in coTree
     *
     */
    public void updateDistToLeaf() {
        Stack<TreeNode> stack = new Stack<>();
        stack.push(getRoot());
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (node.getDist() == -1) {
                if (node.getChildren().size() == 0) node.setDist(0);
                else {
                    stack.push(node);
                    for (TreeNode child : node.getChildren()) stack.push(child);
                    node.setDist(-2);   // indicate all its children are visited
                }
            } else if (node.getDist() == -2) {
                if (node.children.size() == 1) {
                    node.setDist(node.children.iterator().next().getDist() + 1);
                }
                else {  // have to children
                    for (TreeNode child : node.getChildren()) {
                        node.setDist(Math.max(node.getDist(), child.getDist()));
                    }
                }
            }
        }
    }

    /**
     * for dubegging purpose, traverse a tree to check no cycle exists
     * @return a dart causing the cycle in the given tree if found, null otherwise
     */
    public Dart detectCycle() {
        Set<Vertex> visitedV = new HashSet<>();
        Set<Dart> visitedD = new HashSet<>();
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        visitedV.add(root.getData());
        while (!q.isEmpty()) {
            TreeNode n = q.poll();
            Vertex v = n.getData();
            for (TreeNode child : n.getChildren()) {
                Dart d = child.getParentDart();
                if (d.getTail() != v)
                    throw new RuntimeException("Parent dart not pointing to parent vertex");
                if (visitedD.contains(d) || visitedV.contains(child.getData())) {
                    //throw new RuntimeException("Dart or Vertex already visited");
                    return d;
                }
                visitedD.add(d);
                visitedV.add(child.getData());
                q.add(child);
            }
        }
        System.out.printf("%d V, %d D\n", visitedV.size(), visitedD.size());
        if (visitedV.size() != visitedD.size()+1)
            throw new RuntimeException("E+1=N is not satisfied");
        return null;
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

        public void removeChildren(Set<TreeNode> toRemove) {
            Set<TreeNode> remainSet = new HashSet<>(children);
            remainSet.removeAll(toRemove);
            List<TreeNode> remainList = new ArrayList<>();
            for (TreeNode child : children) {
                if (remainSet.contains(child)) remainList.add(child);
            }
            children = remainList;
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
