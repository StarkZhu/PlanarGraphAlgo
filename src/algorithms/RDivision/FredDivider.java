package algorithms.RDivision;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import selfdualgraph.*;

import java.util.*;

public class FredDivider extends GraphDivider {


    public FredDivider(SelfDualGraph g) {
        super(g);
    }

    public Map<Vertex, Set<Vertex>> rhoClustering(int rho) {
        Map<Vertex, Set<Vertex>> vertexToCluster = new HashMap<>();
        SpanningTreeSolver sts = new BFSsolver();
        RootFinder rf = new MaxDegreeRootFinder();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);

        Map<Vertex, Set<Vertex>> faceToSet = new HashMap<>();
        Stack<Tree.TreeNode> stack = new Stack<>();
        stack.push(trees[1].getRoot());
        while (!stack.isEmpty()) {
            Tree.TreeNode node = stack.pop();
            Vertex v = node.getData();
            if (faceToSet.get(v) == null) {   // first visit
                Set<Vertex> set = new HashSet<>();
                faceToSet.put(v, set);
                for (Dart d : v.getIncidenceList()) {
                    if (vertexToCluster.get(d.getHead()) == null) {
                        set.add(d.getHead());
                        vertexToCluster.put(d.getHead(), set);
                    }
                }
                if (node.getChildren().size() > 0) {
                    stack.push(node);
                    for (Tree.TreeNode child : node.getChildren()) stack.push(child);
                }
                continue;
            }
            Set<Vertex> vSet = faceToSet.get(v);
            for (Tree.TreeNode child : node.getChildren()) {
                if (faceToSet.get(child.getData()).size() >= rho) continue;   // already clustered
                vSet.addAll(faceToSet.get(child.getData()));
            }
            if (vSet.size() >= rho) {
                for (Vertex vv : vSet) vertexToCluster.put(vv, vSet);
            }
        }
        return vertexToCluster;
    }

    public Set<Set<Vertex>> rDivision(int r) {

        return null;
    }
}
