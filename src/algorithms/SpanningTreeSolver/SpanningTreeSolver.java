package algorithms.SpanningTreeSolver;

import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * provide static methods to build Tree/coTree of a planar graph
 * provide DFS and BFS solver to build spanning tree in linear time
 * provide Prim's and Boruvka's algorithm to build MST in O(VlogV) and O(V) time
 */
public abstract class SpanningTreeSolver {

    public abstract void buildTreeFromRoot(Tree.TreeNode root);
    public abstract void rebuildTreeFromRoot(Tree.TreeNode root, Map<Vertex, Tree.TreeNode> boundary);

    /**
     * build spanning tree/cotree of graph G rooted at any vertex/face
     *
     * @param g
     * @return
     */
    public Tree[] buildTreeCoTree(SelfDualGraph g) {
        return buildTreeCoTree(g, g.getVertices().iterator().next(), g.getFaces().iterator().next());
    }

    /**
     * build spanning tree/cotree of graph G rooted at the given vertex/face
     *
     * @param g
     * @param vertex
     * @param face   if null, select a face with at least 1 incidental edge included in primal tree
     * @return array of 2 trees, [0] is the primal spanning Tree, [1] is the corresponding coTree
     */
    public Tree[] buildTreeCoTree(SelfDualGraph g, Vertex vertex, Vertex face) {
        g.resetAllToUnvisited();

        Tree[] treeAndcoTree = new Tree[2];
        treeAndcoTree[0] = new Tree(vertex);
        buildTreeFromRoot(treeAndcoTree[0].getRoot());

        if (face == null) {
            Tree.TreeNode primalRoot = treeAndcoTree[0].getRoot();
            if (primalRoot.getChildren().size() < 1) {
                throw new RuntimeException("Primal tree has only 1 node");
            }
            Tree.TreeNode child = primalRoot.getChildren().iterator().next();
            face = child.getParentDart().getRight();
            //System.out.printf("Default root for dual tree is selected to be face ID = %d\n", face.getID());
        }
        treeAndcoTree[1] = new Tree(face);
        buildCoTree(treeAndcoTree[1].getRoot());

        return treeAndcoTree;
    }

    /**
     * rebuild tree and cotree from root, keep unchanged part
     *
     * @param trees
     * @param g
     * @param unchanged
     */
    public void rebuildTreeCoTree(Tree[] trees, SelfDualGraph g, Set<Vertex> unchanged, Map<Vertex, Tree.TreeNode> boundary) {
        // set every vertex, face, dart to be unvisited
        g.resetAllToUnvisited();
        // traverse primal tree, keep unchanged part and mark them visited
        pruneTree(trees[0].getRoot(), unchanged);

        // rebuild tree using information of boundary vertices
        rebuildTreeFromRoot(trees[0].getRoot(), boundary);
        // rebuild cotree
        trees[1] = new Tree(trees[1].getRoot().getData());
        buildCoTree(trees[1].getRoot());
    }

    public void pruneTree(Tree.TreeNode root, Set<Vertex> unchanged) {
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(root);
        root.getData().setVisited(true);
        while (!q.isEmpty()) {
            Tree.TreeNode node = q.poll();
            Set<Tree.TreeNode> toRemove = new HashSet<>();
            for (Tree.TreeNode child : node.getChildren()) {
                if (!unchanged.contains(child.getData())) {
                    toRemove.add(child);
                } else {
                    child.getData().setVisited(true);
                    Dart d = child.getParentDart();
                    d.setVisited(true);
                    d.getReverse().setVisited(true);
                    q.add(child);
                }
            }
            node.removeChildren(toRemove);
        }
    }

    /**
     * build coTree based on the existing spanning tree, called only after a primal spanning tree is built
     * coTree is unique given primal tree, so is independent of what algrothim used
     *
     * @param root root of the existing primal spanning tree
     */
    /*
    // causing StackOverflow when graph has large depth, such as cylinders
    private void buildCoTree(Tree.TreeNode root) {
        // build coTree with DFS, but child should be the face from dart.getLeft()
        Vertex vertex = root.getData();
        vertex.setVisited(true);
        for (Dart d : vertex.getIncidenceList()) {
            Vertex f = d.getLeft();
            if (!d.isVisited() && !f.isVisited()) {
                Tree.TreeNode child = new Tree.TreeNode(f, root, d);
                root.addChild(child);
                d.setVisited(true);
                d.getReverse().setVisited(true);
                buildCoTree(child);
            }
        }
    }
    */

    // rewrite a stack version, instead of recursion
    public void buildCoTree(Tree.TreeNode root) {
        // build coTree with DFS, but child should be the face from dart.getLeft()
        Stack<Tree.TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Tree.TreeNode node = stack.pop();
            Vertex vertex = node.getData();
            vertex.setVisited(true);
            for (Dart d : vertex.getIncidenceList()) {
                Vertex f = d.getLeft();
                if (!d.isVisited() && !f.isVisited()) {
                    Tree.TreeNode child = new Tree.TreeNode(f, node, d);
                    node.addChild(child);
                    d.setVisited(true);
                    d.getReverse().setVisited(true);
                    stack.push(child);
                }
            }
        }

    }


    // TODO: minimum spanning tree algorithms: O(N) - Boruvka's

    // for debug
    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/test_graph_0.txt");
        Tree[] trees = new DFSsolver().buildTreeCoTree(g);
        //Tree[] trees = buildTreeCoTree(g, new BFSsolver());
        //Tree[] trees = buildTreeCoTree(g, new Primsolver());
        System.out.printf("%s-\n", trees[0]);
        System.out.println(trees[1]);
    }
}
