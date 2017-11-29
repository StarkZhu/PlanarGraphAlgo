package algorithms.SpanningTreeSolver;

import selfdualgraph.*;

/**
 * build spanning tree using DFS method
 */
public class DFSsolver extends SpanningTreeSolver{
    @Override
    public void buildTreeFromRoot(Tree.TreeNode root) {
        Vertex vertex = root.getData();
        vertex.setVisited(true);
        for (Dart d : vertex.getIncidenceList()) {
            Vertex v = d.getHead();
            if (!d.isVisited() && !v.isVisited()) {
                Tree.TreeNode child = new Tree.TreeNode(v, root, d);
                root.addChild(child);
                d.setVisited(true);
                d.getReverse().setVisited(true);
                buildTreeFromRoot(child);
            }
        }
    }
}
