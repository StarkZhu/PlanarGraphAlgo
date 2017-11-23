package algorithms;

import selfdualgraph.*;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * provide static methods to build Tree/coTree of a planar graph
 * provide DFS and BFS solver to build spanning tree in linear time
 * provide Prim's and Boruvka's algorithm to build MST in O(VlogV) and O(V) time
 */
public abstract class SpanningTreeSolver {

    public abstract void buildTreeFromRoot(Tree.TreeNode<Vertex> root);

    /**
     * build spanning tree/cotree of graph G rooted at any vertex/face
     * @param g
     * @param solver
     * @return
     */
    public static Tree<Vertex>[] buildTreeCoTree(SelfDualGraph g, SpanningTreeSolver solver) {
        return buildTreeCoTree(g, solver, g.getVertices().iterator().next(), g.getFaces().iterator().next());
    }

    /**
     * build spanning tree/cotree of graph G rooted at the given vertex/face
     * @param g
     * @param solver
     * @param vertex
     * @param face
     * @return array of 2 trees, [0] is the primal spanning Tree, [1] is the corresponding coTree
     */
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

    /**
     * build coTree based on the existing spanning tree, called only after a primal spanning tree is built
     * @param root  root of the existing primal spanning tree
     */
    private static void buildCoTree(Tree.TreeNode<Vertex> root) {
        // build coTree with DFS, but child should be the face from dart.getLeft()
        Vertex vertex = root.getData();
        vertex.setVisited(true);
        for (Dart d : vertex.getIncidenceList()) {
            Vertex f = d.getLeft();
            if (!d.isVisited() && !f.isVisited()) {
                Tree.TreeNode<Vertex> child = new Tree.TreeNode<>(f, root, d);
                root.addChild(child);
                d.setVisited(true);
                d.getReverse().setVisited(true);
                buildCoTree(child);
            }
        }
    }


    /**
     * Subclasses, implement different tree-building strategy
     */

    /**
     * build spanning tree using DFS method
     */
    public static class DFSsolver extends SpanningTreeSolver{
        @Override
        public void buildTreeFromRoot(Tree.TreeNode<Vertex> root) {
            Vertex vertex = root.getData();
            vertex.setVisited(true);
            for (Dart d : vertex.getIncidenceList()) {
                Vertex v = d.getHead();
                if (!d.isVisited() && !v.isVisited()) {
                    Tree.TreeNode<Vertex> child = new Tree.TreeNode<>(v, root, d);
                    root.addChild(child);
                    d.setVisited(true);
                    d.getReverse().setVisited(true);
                    buildTreeFromRoot(child);
                }
            }
        }
    }

    /**
     * build spanning tree using BFS method
     */
    public static class BFSsolver extends SpanningTreeSolver {
        @Override
        public void buildTreeFromRoot(Tree.TreeNode<Vertex> root) {
            Queue<Tree.TreeNode<Vertex>> q = new LinkedList<>();
            q.add(root);
            root.getData().setVisited(true);
            while (!q.isEmpty()) {
                Tree.TreeNode<Vertex> node = q.poll();
                Vertex vertex = node.getData();
                for (Dart d : vertex.getIncidenceList()) {
                    Vertex v = d.getHead();
                    if (!d.isVisited() && !v.isVisited()) {
                        Tree.TreeNode<Vertex> child = new Tree.TreeNode<>(v, node, d);
                        node.addChild(child);
                        v.setVisited(true);
                        d.setVisited(true);
                        d.getReverse().setVisited(true);
                        q.add(child);
                    }
                }
            }
        }
    }

    /**
     * build the Minimum spanning tree using Prim's algorithm
     * time complexity: O(VlogV), E = O(V)
     */
    public static class Primsolver extends SpanningTreeSolver {
        @Override
        public void buildTreeFromRoot(Tree.TreeNode<Vertex> root) {
            Map<Vertex, Tree.TreeNode<Vertex>> map = new HashMap<>();
            PriorityQueue<Dart> frontier = new PriorityQueue<>();
            Vertex vertex = root.getData();
            map.put(vertex, root);

            while (true) {
                vertex.setVisited(true);
                for (Dart d : vertex.getIncidenceList()) {
                    if (!d.isVisited() && !d.getHead().isVisited()) {
                        frontier.add(d);
                    }
                }
                Dart toAdd;
                do {
                    toAdd = frontier.poll();
                } while (toAdd != null && toAdd.getHead().isVisited());

                if (toAdd == null) break;

                toAdd.setVisited(true);
                toAdd.getReverse().setVisited(true);
                Vertex v = toAdd.getHead();
                Tree.TreeNode<Vertex> node = map.get(toAdd.getTail());
                Tree.TreeNode<Vertex> child = new Tree.TreeNode<>(v, node, toAdd);
                node.addChild(child);
                map.put(v, child);
                vertex = v;
            }
        }
    }

    // TODO: minimum spanning tree algorithms: O(N) - Boruvka's

    // for debug
    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/test_graph_0.txt");
        Tree[] trees = buildTreeCoTree(g, new DFSsolver());
        //Tree[] trees = buildTreeCoTree(g, new BFSsolver());
        //Tree[] trees = buildTreeCoTree(g, new Primsolver());
        System.out.printf("%s-\n", trees[0]);
        System.out.println(trees[1]);
    }
}
