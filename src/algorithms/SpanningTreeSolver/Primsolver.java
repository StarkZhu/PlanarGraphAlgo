package algorithms.SpanningTreeSolver;

import selfdualgraph.*;

import java.util.*;

/**
 * build the Minimum spanning tree using Prim's algorithm
 * time complexity: O(VlogV), E = O(V)
 */
public class Primsolver extends SpanningTreeSolver {
    @Override
    public void buildTreeFromRoot(Tree.TreeNode root) {
        Map<Vertex, Tree.TreeNode> map = new HashMap<>();
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
            Tree.TreeNode node = map.get(toAdd.getTail());
            Tree.TreeNode child = new Tree.TreeNode(v, node, toAdd);
            node.addChild(child);
            map.put(v, child);
            vertex = v;
        }
    }

    @Override
    public void rebuildTreeFromRoot(Tree.TreeNode root, Map<Vertex, Tree.TreeNode> boundary) {
        throw new RuntimeException("Function not implemented");
    }
}
