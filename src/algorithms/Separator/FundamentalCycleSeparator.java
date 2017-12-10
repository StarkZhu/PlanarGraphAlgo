package algorithms.Separator;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.util.*;

public class FundamentalCycleSeparator extends Separator{

    public FundamentalCycleSeparator(SelfDualGraph g) {
        super(g);
    }

    @Override
    public  Set<Vertex> findSeparator() {
        return findSeparator(null, null, null);
    }

    /**
     * Vertex/Face nodes' selfWeight will be overwrote in this method
     *
     * @param sts if null, use default BFSsolver
     * @param rf  if null, use default MaxDegreeRoot
     * @param twa if null, use default EdgeWeight
     * @return
     */
    public  Set<Vertex> findSeparator(SpanningTreeSolver sts,
                                      RootFinder rf, TreeWeightAssigner twa) {
        System.err.printf("G will be triangulated after invoking this method\n");
        g.flatten();
        g.triangulate();
        return findSeparator(sts, rf, twa, 3);
    }

    public  Set<Vertex> findSeparator(SpanningTreeSolver sts,
                                      RootFinder rf, TreeWeightAssigner twa, int maxDegree) {
        if (sts == null) {
            sts = new BFSsolver();
        }

        if (rf == null) {
            rf = new MaxDegreeRootFinder();
        }

        if (twa == null ) {
            twa = new VertexCount();
        }
        else if (twa.getClass() == VertexWeight.class
                || twa.getClass() == VertexAndEdgeWeight.class) {
            System.err.printf("Current Fundamental Cycle Separator does NOT support this user specified TreeWeightAssigner\n");
            System.err.printf("Default Vertex/Face Count TreeWeightAssigner is used\n");
            twa = new VertexCount();
        }

        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        assignCotreeWeight(twa, trees);

        if (maxDegree <= 0) {
            maxDegree = findMaxDegreeOfTree(trees[1].getRoot());
            System.err.printf(String.format("Max-degree in coTree is not specified, automatically find the degree to be %d\n", maxDegree));
        }

        Tree.TreeNode separatorNode = findEdgeSeparator(trees[1], maxDegree);
        separator = getCycle(trees[0], separatorNode.getParentDart());

        buildSubgraphs(separatorNode);
        return separator;
    }

    /**
     * assign edge weights in the primal tree to the vertices in the dual tree
     * @param trees
     */
    private void reassignTreeNodeWeight(Tree[] trees) {
        // reset all TreeNodes' selfWeight to 0 in the coTree, build mapping Vertex --> TreeNode
        Map<Vertex, Tree.TreeNode> map = mapVertexToTreeNode(trees[1], true);

        //traverse primal Tree, split edge weight evenly to faces on both sides
        Queue<Tree.TreeNode> q = new LinkedList<>();
        Tree.TreeNode node  = trees[0].getRoot();
        q.addAll(node.getChildren());
        while (!q.isEmpty()) {
            node = q.poll();
            Dart d = node.getParentDart();
            Vertex f = d.getRight();
            Tree.TreeNode n = map.get(f);
            n.setSelfWeight(n.getSelfWeight() + d.getWeight() / 2.0);
            f = d.getLeft();
            n = map.get(f);
            n.setSelfWeight(n.getSelfWeight() + d.getWeight() / 2.0);
            q.addAll(node.getChildren());
        }
    }


    /**
     * used by Fundamental Cycle Separator
     * @param twa
     * @param trees
     */
    public void assignCotreeWeight(TreeWeightAssigner twa, Tree[] trees) {
        // reset all vertices selfweigth to 0 in coTree
        mapVertexToTreeNode(trees[1], true);
        if (twa.getClass() == EdgeWeight.class) {
            reassignTreeNodeWeight(trees);
            // each faceVertex contains weight from edges in primal Tree
            twa = new VertexAndEdgeWeight();
        }
        twa.calcWeightSum(trees[1].getRoot());
    }


    /**
     * all vertices inside the cycle is one subgraph, the rest is the other one
     * both subgraph includes the level separator, for future r-division use
     */
    protected void buildSubgraphs(Tree.TreeNode separatorNode) {
        subgraphs = new Set[2];
        Set<Vertex> insideFaces = getDescendantVertices(separatorNode);

        subgraphs[0] = getIncidentalVertices(insideFaces);
        subgraphs[1] = g.getVertices();
        subgraphs[1].removeAll(subgraphs[0]);
        subgraphs[1].addAll(separator);
    }


    @Override
    public Set<Vertex>[] findSubgraphs() {
        if (separator == null) {
            findSeparator();
        }
        return subgraphs;
    }


}
