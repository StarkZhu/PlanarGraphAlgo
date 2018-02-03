package algorithms.Separator;

import algorithms.LCAHeuristic.LCAHeuristic;
import algorithms.LCAHeuristic.TreeDistHeuristic;
import algorithms.RootFinder.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class ModifiedFCS extends FundamentalCycleSeparator {
    private LCAHeuristic heuristic;

    public ModifiedFCS(SelfDualGraph g) {
        this(g, new TreeDistHeuristic());
    }

    public ModifiedFCS(SelfDualGraph g, LCAHeuristic lca) {
        super(g);
        heuristic = lca;
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
        prepareCoTreeHeuristic(coTree);
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

    /**
     * accurate cycle length by finding LCA of u & v, takes O(N) time, makes FCS O(N^2)
     *
     * @param tree
     * @param node
     * @param primalMap
     * @return
     */
    private int cycleLenHeuristic(Tree tree, Tree.TreeNode node, Map<Vertex, Tree.TreeNode> primalMap) {
        Dart d = node.getParentDart();
        Tree.TreeNode lca = tree.leastCommonAncestor(primalMap.get(d.getTail()), primalMap.get(d.getHead()));
        return primalMap.get(d.getTail()).getDist() + primalMap.get(d.getHead()).getDist() - 2 * lca.getDist();
    }

    /**
     * Conjecture: dist of u,v to their LCA is roughly the deepest child of uv* in coTree
     *
     * @param cotree
     */
    public void prepareCoTreeHeuristic(Tree cotree) {
        cotree.resetDist();
        Stack<Tree.TreeNode> stack = new Stack<>();
        stack.push(cotree.getRoot());
        while (!stack.isEmpty()) {
            Tree.TreeNode node = stack.pop();
            if (node.getDist() < 0) {
                if (node.getChildren().size() == 0) node.setDist(1);
                else {
                    stack.push(node);
                    for (Tree.TreeNode child : node.getChildren()) stack.push(child);
                    node.setDist(0);
                }
            } else if (node.getDist() == 0) {
                for (Tree.TreeNode child : node.getChildren()) {
                    node.setDist(Math.max(node.getDist(), child.getDist()));
                }
                node.setDist(node.getDist() + 1);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/cylinder/1.txt");
        Separator sp = new ModifiedFCS(g, new TreeDistHeuristic());
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(0), null);
        System.out.println(separator.size());
    }
}
