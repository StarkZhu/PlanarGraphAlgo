package algorithms.Separator;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.util.*;

public class LiptonTarjanSeparator extends Separator{

    @Override
    public Set<Vertex> findSeparator(SelfDualGraph g) {
        return findSeparator(g, null, null, null, -1);
    }

    public Set<Vertex> findSeparator(SelfDualGraph g, SpanningTreeSolver sts,
                                     RootFinder rf, TreeWeightAssigner twa, int maxDegree) {
        if (sts == null) {
            sts = new BFSsolver();
        }

        if (rf == null) {
            rf = new MaxDegreeRootFinder();
        }

        if (twa == null || twa.getClass() != VertexCount.class) {
            System.err.printf("Lipton-Tarjan Separator only support Vertex/Face Count TreeWeightAssigner at this stage\n");
            twa = new VertexCount();
        }

        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        twa.calcWeightSum(trees[0].getRoot());
        twa.calcWeightSum(trees[1].getRoot());

        // equivalent to level separator
        List<Set<Tree.TreeNode>> list = buildVertexLevels(trees[0].getRoot(), new ArrayList<>(), 0);
        double totalSum = trees[0].getRoot().getDescendantWeightSum();
        int medianLevel = findMedianLevel(list, totalSum);
        Set<Vertex> Lm = new HashSet<>();
        for (Tree.TreeNode node : list.get(medianLevel)) {
            Lm.add(node.getData());
        }
        if (Lm.size() <= Math.sqrt(g.getVertexNum())){
            return Lm;
        }

        // equivalent to FCS
        if (maxDegree <= 0) {
            maxDegree = findMaxDegreeOfTree(trees[1].getRoot());
            System.err.printf(String.format("Max-degree in coTree is not specified, automatically find the degree to be %d\n", maxDegree));
        }
        Dart separatorDart = findEdgeSeparator(trees[1], maxDegree);
        Set<Vertex> cycleSeparator = getCycle(trees[0], separatorDart);
        if (cycleSeparator.size() <= 4 * Math.sqrt(g.getVertexNum())) {
            return cycleSeparator;
        }

        return null;
    }



}
