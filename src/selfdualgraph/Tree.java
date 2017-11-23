package selfdualgraph;

import java.util.*;

/**
 * Simple tree structure used for spanning trees
 */
public class Tree<T> {
    private final TreeNode<T> root;

    public Tree(T rootData) {
        root = new TreeNode<>(rootData, null);
    }

    public TreeNode<T> getRoot() {
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
    public String printTree(TreeNode<T> root, int spaceNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[spaceNum]).replace("\0", " "));
        sb.append(String.format("%s\n", root.getData()));
        for (Tree.TreeNode<T> child : root.getChildren()) {
            sb.append(printTree(child, spaceNum+2));
        }
        return sb.toString();
    }

    public static class TreeNode<T> {
        private T data;
        private double weightSum;
        private TreeNode<T> parent;
        private List<TreeNode<T>> children;

        public TreeNode(T nodeData, TreeNode<T> parent) {
            data = nodeData;
            this.parent = parent;
            children = new ArrayList<>();
            weightSum = 0.0;
        }

        public T getData() {
            return data;
        }

        public TreeNode<T> getParent() {
            return parent;
        }

        public List<TreeNode<T>> getChildren() {
            return new ArrayList<>(children);
        }

        public void addChild(TreeNode<T> child) {
            children.add(child);
        }

        public void setWeightSum(double v) {
            if (v < 0) {
                throw new RuntimeException("Sum of children's weigth must be greater than 0");
            }
            weightSum = v;
        }

        public double getWeightSum() {
            return weightSum;
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
