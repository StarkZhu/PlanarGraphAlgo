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

    public LiptonTarjanSeparator(SelfDualGraph g) {
        super(g);
    }

    @Override
    public Set<Vertex> findSeparator() {
        return findSeparator(null);
    }

    public Set<Vertex> findSeparator(RootFinder rf) {
        g.flatten();
        g.triangulate();

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

        if (list.get(mLevel).size() < 2 * sqrtN) {
            Set<Vertex> Lm = getVerticesBetweenLevels(list, mLevel, mLevel);
            buildSubgraphs(list, mLevel);
            return Lm;
        }

        // find La < Lm, Lz > Lm such that:
        // |La| < sqrt(N), |Lz| < sqrt(N)
        int aLevel = mLevel, zLevel = mLevel;
        while (aLevel > 0 && list.get(aLevel).size() > sqrtN) aLevel--;
        while (zLevel < list.size() && list.get(zLevel).size() > sqrtN) zLevel++;
        Set<Vertex> heavyMiddle = getVerticesBetweenLevels(list, aLevel + 1, zLevel - 1);

        // TODO: verify correctness - use vertex weight instead of modify graph or tree
        // adjust coTree node weight such that:
        // faces (dual nodes) outside La and Lz has zero weight
        // then the FCS will be balanced in terms of faces inside (La, Lz) strip
        Set<Vertex> outsideMiddle = g.getVertices();
        outsideMiddle.removeAll(heavyMiddle);
        Map<Vertex, Tree.TreeNode> faceToNode = mapVertexToTreeNode(trees[1], false);
        for (Vertex v : outsideMiddle) {
            for (Dart d : v.getIncidenceList()) {
                if (outsideMiddle.contains(d.getHead())) {
                    faceToNode.get(d.getRight()).setSelfWeight(0);
                    faceToNode.get(d.getLeft()).setSelfWeight(0);
                }
            }
        }

        // the given graph must be triangulated
        // this cycle is a 1/3 balanced separator of the heavyMiddle vertices
        // note: the degree-3 node may have non-zero weight, use leafmostHeavyVertex() instead of findEdgeSeparator()
        Tree.TreeNode coTreeRoot = trees[1].getRoot();
        vertexWeightTWA.calcWeightSum(coTreeRoot);
        Tree.TreeNode separatorNode = leafmostHeavyVertex(coTreeRoot, 1.0 / 3, coTreeRoot.getDescendantWeightSum());
        Dart separatorDart = separatorNode.getParentDart();
        separator = getCycle(trees[0], separatorDart);

        separator.retainAll(heavyMiddle);
        separator.addAll(getVerticesBetweenLevels(list, aLevel, aLevel));
        separator.addAll(getVerticesBetweenLevels(list, zLevel, zLevel));

        buildSubgraphs(list, aLevel, zLevel, separatorNode);
        return separator;
    }


    @Override
    public Set<Vertex>[] findSubgraphs() {
        if (separator == null) {
            findSeparator();
        }
        return subgraphs;
    }

    /**
     * use the 2 levels and the fundamental cycle to construct the most balanced subgraphs
     * @param levelList
     * @param aLevel
     * @param zLevel
     * @param separatorNode
     */
    private void buildSubgraphs(List<Set<Tree.TreeNode>> levelList, int aLevel, int zLevel, Tree.TreeNode separatorNode) {
        subgraphs = new Set[2];
        Set<Vertex>[] sets = new Set[4];
        sets[0] = getVerticesBetweenLevels(levelList, 0, aLevel - 1);
        sets[1] = getVerticesBetweenLevels(levelList, zLevel + 1, levelList.size() - 1);
        sets[3] = getVerticesBetweenLevels(levelList, aLevel + 1, zLevel - 1);

        Set<Vertex> insideFaces = getDescendantVertices(separatorNode);
        sets[2] = getIncidentalVertices(insideFaces);
        sets[2].retainAll(sets[3]);
        sets[3].removeAll(sets[2]);
        for (Set<Vertex> set : sets) set.addAll(separator);

        // find the most balance combination of 4 subset vertices
        double bestRatio = 2.0;
        for (int i = 0; i < 4; i++) {
            for (int j = i; j< 4; j++) {
                Set<Vertex> subgraph = new HashSet<>(sets[i]);
                subgraph.addAll(sets[j]);
                double ratio = subgraph.size() * 1.0 / (g.getVertexNum() + separator.size());
                if (Math.abs(ratio - 0.5) < Math.abs(bestRatio - 0.5)) {
                    bestRatio = ratio;
                    subgraphs[0] = subgraph;
                    subgraphs[1] = g.getVertices();
                    subgraphs[1].removeAll(subgraph);
                    subgraphs[1].addAll(separator);
                }
            }
        }
    }

    /*
    private Set<Vertex> getFacesBetweenLevels(List<Set<Tree.TreeNode>> list, int startLevel, int endLevel) {
        Set<Vertex> vertices = new HashSet<>();
        if (startLevel < 0 || endLevel < startLevel || endLevel >= list.size()) {
            System.err.println("Invalid input, returning empty set");
            return vertices;
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
    */
}
