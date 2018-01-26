package algorithms.Separator;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class SimpleCycleSeparator extends Separator {

    public SimpleCycleSeparator(SelfDualGraph g) {
        super(g);
    }

    @Override
    public Set<Vertex> findSeparator() {
        return findSeparator(null);
    }

    @Override
    public Set<Vertex> findSeparator(SpanningTreeSolver sts, RootFinder rf, TreeWeightAssigner twa) {
        if (sts == null || sts.getClass() != BFSsolver.class) {
            System.err.printf("SimpleCycleSeparator must use default BFSsolver as SpanningTreeSolver\n");
        }
        if (twa == null || twa.getClass() != VertexCount.class) {
            System.err.printf("SimpleCycleSeparator must use default VertexCount as TreeWeightAssigner\n");
        }
        return findSeparator(rf);
    }

    public Set<Vertex> findSeparator(RootFinder rf) {
        g.flatten();
        g.triangulate();

        // Primal tree must be built with BFS
        SpanningTreeSolver sts = new BFSsolver();
        TreeWeightAssigner vertexCountTWA = new VertexCount();
        //int sqrtN = (int) Math.sqrt(g.getVertexNum());
        if (rf == null) {
            rf = new MaxDegreeRootFinder();
        }

        // find a balanced cycle separator (T', uv) and re-root the tree at the LCA(u, v)
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        Tree.TreeNode coTreeRoot = trees[1].getRoot();
        vertexCountTWA.calcWeightSum(coTreeRoot);
        Tree.TreeNode separatorNode = leafmostHeavyVertex(coTreeRoot, 1.0 / 3, coTreeRoot.getDescendantWeightSum());
        Map<Vertex, Tree.TreeNode> primalTreeMap = trees[0].mapVertexToTreeNode(false);
        Dart uv = separatorNode.getParentDart();
        Vertex u = uv.getTail();
        Vertex v = uv.getHead();
        Tree.TreeNode root = trees[0].leastCommonAncestor(primalTreeMap.get(u), primalTreeMap.get(v));
        trees[0].reRoot(root);
        // TODO: path to max(u, v), not cycle
        Set<Vertex> cycle = getCycle(trees[0], uv);

        // calculate distance of every vertex and group them by distance to root
        trees[0].updateDistance();
        int h = Math.max(primalTreeMap.get(u).getDist(), primalTreeMap.get(v).getDist());
        List<Set<Vertex>> levels = new ArrayList<>(h + 1);

        for (int i = 0; i <= h; i++) {
            levels.add(new HashSet<>());
        }
        for (Vertex vertex : g.getVertices()) {
            int dist = primalTreeMap.get(vertex).getDist();
            if (dist <= h) {
                levels.get(dist).add(vertex);
            }
        }

        //identify the outer boundary of level 0 to h
        List<Set<Vertex>> outerBoundaries = new ArrayList<>(h + 1);
        for (int i = 0; i <= h; i++) {
            outerBoundaries.add(new HashSet<>());
            Vertex curr = null;
            for (Vertex vertex : levels.get(i)) {
                if (cycle.contains(vertex)) {
                    curr = vertex;
                    break;
                }
            }
            if (curr == null) {
                throw new RuntimeException("No intersecting vertex is found!");
            }
            outerBoundaries.get(i).add(curr);

            LEVEL_LOOP: while (curr != null) {
                for (Dart d : curr.getIncidenceList()) {
                    Vertex next = d.getHead();
                    if (levels.get(i).contains(next) && !outerBoundaries.get(i).contains(next)) {
                        outerBoundaries.get(i).add(next);
                        curr = next;
                        continue LEVEL_LOOP;
                    }
                }
                curr = null;
            }
        }

        // TODO:
        // if not all the outer-most ring's 3 vertices have distance h
        System.out.println(outerBoundaries.get(h).size());

        return separator;
    }

    @Override
    public Set<Vertex>[] findSubgraphs() {
        if (separator == null) {
            findSeparator();
        }
        return subgraphs;
    }

    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/random/1.txt");
        Separator sp = new SimpleCycleSeparator(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(5), null);
    }
}
