package algorithms.Separator;

import algorithms.LCAHeuristic.CombinedHeuristic;
import algorithms.LCAHeuristic.DistToRootHeuristic;
import algorithms.LCAHeuristic.LCAHeuristic;
import algorithms.RootFinder.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class ModifiedFCS extends FundamentalCycleSeparator {
    private LCAHeuristic heuristic;

    public ModifiedFCS(SelfDualGraph g) {
        this(g, new CombinedHeuristic());
    }

    public ModifiedFCS(SelfDualGraph g, LCAHeuristic lca) {
        super(g);
        heuristic = lca;
    }

    @Override
    public Tree.TreeNode chooseNode(Tree.TreeNode node, Tree tree, Tree coTree, int maxDegree) {
        double alpha = 1.0 / maxDegree;
        double totalW = coTree.getRoot().getDescendantWeightSum();

        tree.updateDistToRoot();
        Map<Vertex, Tree.TreeNode> map = tree.mapVertexToTreeNode(false);
        Tree.TreeNode result = node;

        heuristic.setTrees(tree, coTree);
        int cycleLen = heuristic.cycleLenHeuristic(result);

        Queue<Tree.TreeNode> visiting = new LinkedList<>();
        visiting.add(coTree.getRoot());
        while (!visiting.isEmpty()) {
            Tree.TreeNode n = visiting.poll();
            if (n.getDescendantWeightSum() > alpha * totalW && n.getDescendantWeightSum() < (1 - alpha) * totalW) {
                int len = heuristic.cycleLenHeuristic(n);
                if (len < cycleLen) {
                    cycleLen = len;
                    result = n;
                } else if (len == cycleLen && Math.abs(0.5 - n.getDescendantWeightSum() / totalW)
                        < Math.abs(0.5 - result.getDescendantWeightSum() / totalW)) {
                    result = n;
                }
            }
            visiting.addAll(n.getChildren());
        }

        return result;
    }

    /*
    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/cylinder/unsymm/1.txt");
        Separator sp = new ModifiedFCS(g, new DistToRootHeuristic());
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(32), null);
        System.out.println(separator.size());
    }
    */
}
