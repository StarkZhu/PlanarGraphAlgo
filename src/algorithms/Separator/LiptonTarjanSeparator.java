package algorithms.Separator;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.util.*;

/**
 * Lipton-Tarjan separator is guaranteed to be at most 6*sqrt(N) in size
 */
public class LiptonTarjanSeparator extends Separator {

    @Override
    public Set<Vertex> findSeparator(SelfDualGraph g) {
        return findSeparator(g, null);
    }

    /**
     * G has to be triangulated
     *
     * @param g
     * @param rf
     * @return
     */
    public Set<Vertex> findSeparator(SelfDualGraph g, RootFinder rf) {
        // Primal tree must be built with BFS
        SpanningTreeSolver sts = new BFSsolver();
        TreeWeightAssigner vertexCountTWA = new VertexCount();
        TreeWeightAssigner vertexWeightTWA = new VertexWeight();

        int sqrtN = (int) Math.sqrt(g.getVertexNum());

        if (rf == null) {
            rf = new MaxDegreeRootFinder();
        }

        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        vertexCountTWA.calcWeightSum(trees[0].getRoot());

        // find median level, equivalent to level separator
        List<Set<Tree.TreeNode>> list = buildVertexLevels(trees[0].getRoot(), new ArrayList<>(), 0);
        double totalSum = trees[0].getRoot().getDescendantWeightSum();
        int mLevel = findMedianLevel(list, totalSum);

        if (list.get(mLevel).size() <= 2 * sqrtN) {
            Set<Vertex> Lm = getVerticesBetweenLevels(list, mLevel, mLevel);
            return Lm;
        }

        // find La < Lm, Lz > Lm such that:
        // |La| < sqrt(N), |Lz| < sqrt(N)
        int aLevel = mLevel, zLevel = mLevel;
        while (list.get(aLevel).size() > sqrtN) aLevel--;
        while (list.get(zLevel).size() > sqrtN) zLevel++;
        Set<Vertex> heavyMiddle = getVerticesBetweenLevels(list, aLevel, zLevel);

        // TODO: verify correctness - use vertex weight instead of modify graph or tree
        // adjust coTree node weight such that:
        // faces (dual nodes) outside La and Lz has zero weight
        // then the FCS will be balanced in terms of faces inside (La, Lz) strip
        Map<Vertex, Tree.TreeNode> faceToNode = mapVertexToTreeNode(trees[1], true);
        Set<Vertex> faces = getFacesBetweenLevels(list, aLevel, zLevel);
        for (Vertex face : faces) {
            faceToNode.get(face).setSelfWeight(1);
        }
        vertexWeightTWA.calcWeightSum(trees[1].getRoot());

        // the given graph must be triangulated
        // this cycle is a 1/3 balanced separator of the heavyMiddle vertices
        Dart separatorDart = findEdgeSeparator(trees[1], 3);
        Set<Vertex> separator = getCycle(trees[0], separatorDart);

        separator.retainAll(heavyMiddle);
        separator.addAll(getVerticesBetweenLevels(list, aLevel, aLevel));
        separator.addAll(getVerticesBetweenLevels(list, zLevel, zLevel));

        return separator;
    }

    private Set<Vertex> getVerticesBetweenLevels(List<Set<Tree.TreeNode>> list, int startLevel, int endLevel) {
        if (startLevel < 0 || endLevel < startLevel || endLevel >= list.size()) {
            throw new RuntimeException("Invalid input");
        }
        Set<Vertex> vertices = new HashSet<>();
        for (int i = startLevel; i <= endLevel; i++) {
            for (Tree.TreeNode node : list.get(i)) {
                vertices.add(node.getData());
            }
        }
        return vertices;
    }

    private Set<Vertex> getFacesBetweenLevels(List<Set<Tree.TreeNode>> list, int startLevel, int endLevel) {
        if (startLevel < 0 || endLevel < startLevel || endLevel >= list.size()) {
            throw new RuntimeException("Invalid input");
        }
        Set<Vertex> faces = new HashSet<>();
        for (int i = startLevel + 1; i <= endLevel; i++) {
            for (Tree.TreeNode node : list.get(i)) {
                Dart parentDart = node.getParentDart();
                faces.add(parentDart.getRight());
                faces.add(parentDart.getLeft());
            }
        }
        return faces;
    }


}
