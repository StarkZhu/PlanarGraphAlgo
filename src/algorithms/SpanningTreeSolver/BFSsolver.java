package algorithms.SpanningTreeSolver;

import selfdualgraph.*;

import java.util.*;

/**
 * build spanning tree using BFS method
 */
public class BFSsolver extends SpanningTreeSolver {
    @Override
    public void buildTreeFromRoot(Tree.TreeNode root) {
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(root);
        root.getData().setVisited(true);
        while (!q.isEmpty()) {
            Tree.TreeNode node = q.poll();
            Vertex vertex = node.getData();
            for (Dart d : vertex.getIncidenceList()) {
                Vertex v = d.getHead();
                if (!d.isVisited() && !v.isVisited()) {
                    Tree.TreeNode child = new Tree.TreeNode(v, node, d);
                    node.addChild(child);
                    v.setVisited(true);
                    d.setVisited(true);
                    d.getReverse().setVisited(true);
                    q.add(child);
                }
            }
        }
    }

    @Override
    public void rebuildTreeFromRoot(Tree.TreeNode root, Map<Vertex, Tree.TreeNode> boundary) {
        Queue<Tree.TreeNode> q = new LinkedList<>();
        q.add(root);
        root.getData().setVisited(true);
        boundary.remove(root.getData());
        while (!q.isEmpty()) {
            Tree.TreeNode node = q.poll();
            Vertex vertex = node.getData();
            for (Dart d : vertex.getIncidenceList()) {
                Vertex v = d.getHead();
                if (!d.isVisited() && !v.isVisited()) {
                    Tree.TreeNode child = new Tree.TreeNode(v, node, d);
                    node.addChild(child);
                    v.setVisited(true);
                    d.setVisited(true);
                    d.getReverse().setVisited(true);
                    q.add(child);
                } else if (d.isVisited() && v.isVisited() && boundary.containsKey(v)) {
                    q.add(boundary.get(v));
                    boundary.remove(v);
                }
            }
        }
    }
}
