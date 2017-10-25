package selfdualgraph;

import java.util.*;

/**
 * Created by qixinzhu on 10/24/17.
 */
public class Tree<T> {
    private final TreeNode<T> root;

    public Tree(T rootData) {
        root = new TreeNode<>(rootData, null);
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    public static class TreeNode<T> {
        private T data;
        private TreeNode<T> parent;
        private List<TreeNode<T>> children;

        public TreeNode(T nodeData, TreeNode<T> parent) {
            data = nodeData;
            this.parent = parent;
            children = new ArrayList<>();
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
