package algorithms.Separator;

import algorithms.RootFinder.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class ModifiedFCS extends FundamentalCycleSeparator {
    public ModifiedFCS(SelfDualGraph g) {
        super(g);
    }

    /*
    @Override
    public Tree.TreeNode leafmostHeavyVertex(Tree tree, double alpha, double totalW) {
        Map<Vertex, Tree.TreeNode> map = tree.mapVertexToTreeNode(false);
        Tree.TreeNode result = super.leafmostHeavyVertex(tree, alpha, totalW);
        Dart d = result.getParentDart();
        int cycleLen = map.get(d.getTail()).getDist() + map.get(d.getHead()).getDist();

        Queue<Tree.TreeNode> visiting = new LinkedList<>();
        visiting.add(tree.getRoot());
        while (!visiting.isEmpty()) {
            Tree.TreeNode n = visiting.poll();
            if (n.getDescendantWeightSum() > alpha * totalW && n.getDescendantWeightSum() < (1 - alpha) * totalW) {
                d = n.getParentDart();
                int len = map.get(d.getTail()).getDist() + map.get(d.getHead()).getDist();
                if (len < cycleLen) {
                    cycleLen = len;
                    result = n;
                }
            }
        }

        return result;
    }
    */

    @Override
    protected Tree.TreeNode chooseNode(Tree.TreeNode node, Tree tree, Tree coTree, int maxDegree) {
        double alpha = 1.0 / maxDegree;
        double totalW = coTree.getRoot().getDescendantWeightSum();

        tree.updateDistance();
        Map<Vertex, Tree.TreeNode> map = tree.mapVertexToTreeNode(false);
        Tree.TreeNode result = node;
        Dart d = result.getParentDart();
        int cycleLen = map.get(d.getTail()).getDist() + map.get(d.getHead()).getDist();
        //TODO: correct cycle size calculation make run time O(n^2)
        //Tree.TreeNode lca = coTree.leastCommonAncestor(map.get(d.getTail()), map.get(d.getHead()));
        //int cycleLen = map.get(d.getTail()).getDist() + map.get(d.getHead()).getDist() - 2*lca.getDist();

        Queue<Tree.TreeNode> visiting = new LinkedList<>();
        visiting.add(coTree.getRoot());
        while (!visiting.isEmpty()) {
            Tree.TreeNode n = visiting.poll();
            if (n.getDescendantWeightSum() > alpha * totalW && n.getDescendantWeightSum() < (1 - alpha) * totalW) {
                d = n.getParentDart();
                //lca = coTree.leastCommonAncestor(map.get(d.getTail()), map.get(d.getHead()));
                //int len = map.get(d.getTail()).getDist() + map.get(d.getHead()).getDist() - 2*lca.getDist();
                int len = map.get(d.getTail()).getDist() + map.get(d.getHead()).getDist();
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

    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/cylinder/1.txt");
        Separator sp = new ModifiedFCS(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(0), null);
        System.out.println(separator.size());
    }
}
