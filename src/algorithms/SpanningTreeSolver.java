package algorithms;

import selfdualgraph.*;

import java.io.FileNotFoundException;

/**
 * Created by qixinzhu on 10/24/17.
 */
public abstract class SpanningTreeSolver {

    public abstract void buildTreeFromRoot(Tree.TreeNode<Vertex> root);

    public static String printTree(Tree.TreeNode<Vertex> root, int spaceNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(new String(new char[spaceNum]).replace("\0", " "));
        sb.append(String.format("%s\n", root.getData()));
        for (Tree.TreeNode<Vertex> child : root.getChildren()) {
            sb.append(printTree(child, spaceNum+2));
        }
        return sb.toString();
    }

    public static Tree<Vertex>[] buildTreeCoTree(SelfDualGraph g, SpanningTreeSolver solver) {
        return buildTreeCoTree(g, solver, g.getVertices().iterator().next(), g.getFaces().iterator().next());
    }

    public static Tree<Vertex>[] buildTreeCoTree(SelfDualGraph g, SpanningTreeSolver solver, Vertex vertex, Vertex face) {
        // set every vertex, face, dart to be unvisited
        for (Vertex v : g.getVertices()) {
            v.setVisited(false);
            for (Dart d : v.getIncidenceList()) {
                d.setVisited(false);
            }
        }
        for (Vertex f : g.getFaces()) {
            f.setVisited(false);
        }
        Tree<Vertex>[] treeAndcoTree = new Tree[2];
        treeAndcoTree[0] = new Tree<>(vertex);
        solver.buildTreeFromRoot(treeAndcoTree[0].getRoot());

        treeAndcoTree[1] = new Tree<>(face);
        buildCoTree(treeAndcoTree[1].getRoot());

        return treeAndcoTree;
    }

    private static void buildCoTree(Tree.TreeNode<Vertex> root) {
        // build coTree with DFS, but child should be the face from dart.getLeft()
        Vertex vertex = root.getData();
        vertex.setVisited(true);
        for (Dart d : vertex.getIncidenceList()) {
            Vertex f = d.getLeft();
            if (!d.isVisited() && !f.isVisited()) {
                Tree.TreeNode<Vertex> child = new Tree.TreeNode<>(f, root);
                root.addChild(child);
                d.setVisited(true);
                d.getReverse().setVisited(true);
                buildCoTree(child);
            }
        }
    }

    public static class DFSsolver extends SpanningTreeSolver{
        @Override
        public void buildTreeFromRoot(Tree.TreeNode<Vertex> root) {
            Vertex vertex = root.getData();
            vertex.setVisited(true);
            for (Dart d : vertex.getIncidenceList()) {
                Vertex v = d.getHead();
                if (!d.isVisited() && !v.isVisited()) {
                    Tree.TreeNode<Vertex> child = new Tree.TreeNode<>(v, root);
                    root.addChild(child);
                    d.setVisited(true);
                    d.getReverse().setVisited(true);
                    buildTreeFromRoot(child);
                }
            }
        }
    }

    // for debug
    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/test_graph_0.txt");
        Tree[] trees = buildTreeCoTree(g, new DFSsolver());
        System.out.println(printTree(trees[0].getRoot(), 0));
        System.out.println(printTree(trees[1].getRoot(), 0));
    }
}
