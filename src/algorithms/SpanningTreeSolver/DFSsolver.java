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
        Stack<Tree.TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Tree.TreeNode node = stack.pop();
            vertex = node.getData();
            for (Dart d : vertex.getIncidenceList()) {
                Vertex v = d.getHead();
                if (!d.isVisited() && !v.isVisited()) {
                    Tree.TreeNode child = new Tree.TreeNode(v, node, d);
                    node.addChild(child);
                    v.setVisited(true);
                    d.setVisited(true);
                    d.getReverse().setVisited(true);
                    stack.push(node);
                    stack.push(child);
                    break;
                }
            }
        }
    }

    @Override
    public void rebuildTreeFromRoot(Tree.TreeNode root, Map<Vertex, Tree.TreeNode> boundary) {
        throw new RuntimeException("Function not implemented");
    }

    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/grids/4.txt");
        SpanningTreeSolver sts = new DFSsolver();
        RootFinder rf = new MinDegreeRootFinder();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        System.out.println(trees[0].getRoot().getData());
    }
}
