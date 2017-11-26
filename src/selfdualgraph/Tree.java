package selfdualgraph;

import java.util.*;

/**
 * Simple tree structure used for spanning trees
 */
public class Tree {
    private final TreeNode root;

    public Tree(Vertex rootData) {
        root = new TreeNode(rootData, null, null);
    }

    public TreeNode getRoot() {
        return root;
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

    public static class TreeNode {
        private Vertex data;
        private double descendantWeightSum;
        private double selfWeight;
        private TreeNode parent;
        private Dart parentDart;
        private List<TreeNode> children;

        public TreeNode(Vertex nodeData, TreeNode parent, Dart d) {
            data = nodeData;
            this.parent = parent;
            children = new ArrayList<>();
            descendantWeightSum = 0.0;
            parentDart = d;
            selfWeight = nodeData.getWeight();
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
