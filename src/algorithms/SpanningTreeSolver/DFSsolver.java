package algorithms.SpanningTreeSolver;

import algorithms.RootFinder.MinDegreeRootFinder;
import algorithms.RootFinder.RootFinder;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * build spanning tree using DFS method
 */
public class DFSsolver extends SpanningTreeSolver{
    @Override
    public void buildTreeFromRoot(Tree.TreeNode root) {
        // TODO: change to iterative implementation
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

    @Override
    public void rebuildTreeFromRoot(Tree.TreeNode root, Map<Vertex, Tree.TreeNode> boundary) {
        throw new RuntimeException("Function not implemented");
    }

    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/grids/5.txt");
        SpanningTreeSolver sts = new DFSsolver();
        RootFinder rf = new MinDegreeRootFinder();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        System.out.println(trees[0].getRoot().getData());
    }
}
