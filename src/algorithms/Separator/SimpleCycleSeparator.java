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
        return findSeparator(null, new VertexCount());
    }

    @Override
    public Set<Vertex> findSeparator(SpanningTreeSolver sts, RootFinder rf, TreeWeightAssigner twa) {
        if (sts == null || sts.getClass() != BFSsolver.class) {
            //System.err.printf("SimpleCycleSeparator must use default BFSsolver as SpanningTreeSolver\n");
        }
        return findSeparator(rf, twa);
    }

    public Set<Vertex> findSeparator(RootFinder rf, TreeWeightAssigner twa) {
        g.flatten();
        g.triangulate();
        int sqrtN = (int) Math.sqrt(g.getVertexNum());

        // Primal tree must be built with BFS
        SpanningTreeSolver sts = new BFSsolver();
        if (twa == null) {
            twa = new VertexCount();
        }
        //int sqrtN = (int) Math.sqrt(g.getVertexNum());
        if (rf == null) {
            rf = new MaxDegreeRootFinder();
        }

        // find a balanced cycle separator (T', uv) and re-root the tree at the LCA(u, v)
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);
        Tree.TreeNode primalRoot = trees[0].getRoot();
        twa.calcWeightSum(primalRoot);
        double totalWeight = getTotalPrimalWeight(g.getVertices(), twa);
        Tree.TreeNode coTreeRoot = trees[1].getRoot();
        twa.calcWeightSum(coTreeRoot);
        Tree.TreeNode separatorNode = leafmostHeavyVertex(coTreeRoot, 1.0 / 3, coTreeRoot.getDescendantWeightSum());
        Map<Vertex, Tree.TreeNode> primalTreeMap = trees[0].mapVertexToTreeNode(false);
        Dart uv = separatorNode.getParentDart();
        if (uv == null) {
            throw new RuntimeException("uv is null");
        }
        Tree.TreeNode root = trees[0].leastCommonAncestor(primalTreeMap.get(uv.getTail()), primalTreeMap.get(uv.getHead()));
        trees[0].reRoot(root);

        // rebuild BFS tree and coTree
        rebuildBFStrees(sts, trees, separatorNode, primalTreeMap);
        primalTreeMap = trees[0].mapVertexToTreeNode(false);

        // calculate distance of every vertex and group them by distance to root
        trees[0].updateDistToRoot();
        Vertex phi = getVertexPhi(uv, primalTreeMap);
        int h = primalTreeMap.get(phi).getDist();
        List<Set<Vertex>> levels = verticeLevels(primalTreeMap, h);
        Set<Vertex> path = pathToPhi(primalTreeMap, phi);

        // find outer boundaries for levels
        List<Set<Vertex>> outerBoundaries = identifyBoundaries(primalTreeMap, uv, h, levels, path);

        // identify vertices between level i-1 and i
        Set<Vertex>[] vertexRegions = identifyVertexRegions(outerBoundaries, root);
        // count total number of vertices inside/outside each level boundary
        double[][] vNum_in_out = new double[h + 1][2];
        vNum_in_out[0][0] = 0;
        vNum_in_out[0][1] = totalWeight - getTotalPrimalWeight(outerBoundaries.get(0), twa);
        vNum_in_out[h][0] = totalWeight - getTotalPrimalWeight(outerBoundaries.get(h), twa);
        vNum_in_out[h][1] = 0;
        for (int i = 1; i < h; i++) {
            vNum_in_out[i][0] = vNum_in_out[i - 1][0] + getTotalPrimalWeight(outerBoundaries.get(i - 1), twa)
                    + getTotalPrimalWeight(vertexRegions[i], twa);
            vNum_in_out[i][1] = totalWeight - vNum_in_out[i][0] - getTotalPrimalWeight(outerBoundaries.get(i), twa);
            // check if any level boundary is small and balanced, use it as separator
            if (outerBoundaries.get(i).size() < 4 * sqrtN
                    && vNum_in_out[i][0] < totalWeight / 2
                    && vNum_in_out[i][1] < totalWeight / 2) {
                Set<Vertex> fromRoot = new HashSet<>();
                fromRoot.add(root.getData());
                buildSubgraphs(fromRoot, outerBoundaries.get(i));
                return separator;
            }
        }

        // look for m: out most level such that inside_m < V/2, outside_m+1 < V/2
        int m = h - 1;
        for (; m >= 0; m--) {
            if (vNum_in_out[m + 1][1] < totalWeight / 2 && vNum_in_out[m][0] < totalWeight / 2)
                break;
        }
        int a = m, z = m + 1;
        while (a > 0 && outerBoundaries.get(a).size() > sqrtN) a--;
        while (z < h && outerBoundaries.get(z).size() > sqrtN) z++;

        Set<Vertex> boundary = new HashSet<>();
        boundary.addAll(getCycle(trees[0], separatorNode.getParentDart()));
        boundary.addAll(outerBoundaries.get(a));
        boundary.addAll(outerBoundaries.get(z));

        // identify region A,B,C,D
        Set<Vertex> A = new HashSet<>();
        for (int i = 0; i < a; i++) {
            A.addAll(outerBoundaries.get(i));
            A.addAll(vertexRegions[i + 1]);
        }
        Set<Vertex> D = new HashSet<>();
        for (int i = z + 1; i <= h; i++) {
            D.addAll(outerBoundaries.get(i));
            D.addAll(vertexRegions[i]);
        }
        Set<Vertex> facesInsideFC = getDescendantVertices(separatorNode);
        Set<Vertex> B = getIncidentalVertices(facesInsideFC);
        B.removeAll(A);
        B.removeAll(D);
        B.removeAll(boundary);

        Set<Vertex> C = new HashSet<>(g.getVertices());
        C.removeAll(A);
        C.removeAll(B);
        C.removeAll(D);
        C.removeAll(boundary);

        // case analysis for regions
        if (getTotalPrimalWeight(D, twa) > totalWeight / 3) {
            buildSubgraphs(D, outerBoundaries.get(z));
        } else if (getTotalPrimalWeight(A, twa) > totalWeight / 3) {
            buildSubgraphs(A, outerBoundaries.get(a));
        } else if (getTotalPrimalWeight(B, twa) > totalWeight / 3) {
            buildSubgraphs(B, boundary);
        } else if (getTotalPrimalWeight(C, twa) > totalWeight / 3) {
            buildSubgraphs(C, boundary);
        } else {
            Set<Vertex> AB = new HashSet<>(A);
            AB.addAll(B);
            buildSubgraphs(AB, boundary);
        }

        return separator;
    }

    private double getTotalPrimalWeight(Set<Vertex> vertices, TreeWeightAssigner twa) {
        if (twa.getClass() == VertexCount.class) {
            return vertices.size();
        } else {    // VertexWeightAssigner
            double total = 0;
            for (Vertex v : vertices) total += v.getWeight();
            return total;
        }
    }

    public void rebuildBFStrees(SpanningTreeSolver sts, Tree[] trees, Tree.TreeNode separatorNode, Map<Vertex, Tree.TreeNode> primalTreeMap) {
        Set<Vertex> unchanged = getIncidentalVertices(getDescendantVertices(separatorNode));
        Set<Vertex> cycle = getCycle(trees[0], separatorNode.getParentDart());
        Map<Vertex, Tree.TreeNode> margin = new HashMap<>(primalTreeMap);
        for (Vertex v : new HashSet<>(margin.keySet())) {
            if (!cycle.contains(v)) margin.remove(v);
        }
        sts.rebuildTreeCoTree(trees, g, unchanged, margin);
    }

    public Vertex getVertexPhi(Dart uv, Map<Vertex, Tree.TreeNode> primalTreeMap) {
        Vertex u = uv.getTail();
        Vertex v = uv.getHead();
        Vertex phi = u;
        if (primalTreeMap.get(u).getDist() < primalTreeMap.get(v).getDist()) {
            phi = v;
        }
        return phi;
    }

    /**
     * give vertex set of a region, use BFS to identify the subgraph enclosed by boundary
     * note: removal of boundary vertices may disconnect the region
     *
     * @param region
     * @param boundary
     */
    private void buildSubgraphs(Set<Vertex> region, Set<Vertex> boundary) {
        g.resetAllToUnvisited();
        separator = new HashSet<>();
        subgraphs = new Set[2];
        subgraphs[0] = new HashSet<>();
        for (Vertex vv : region) {
            if (vv.isVisited()) continue;
            Queue<Vertex> q = new LinkedList<>();
            subgraphs[0].add(vv);
            q.add(vv);
            vv.setVisited(true);
            while (!q.isEmpty()) {
                Vertex v = q.poll();
                for (Dart d : v.getIncidenceList()) {
                    Vertex u = d.getHead();
                    if (!u.isVisited()) {
                        u.setVisited(true);
                        subgraphs[0].add(u);
                        if (boundary.contains(u)) {
                            separator.add(u);
                        } else {
                            q.add(u);
                        }
                    }
                }
            }
        }
        subgraphs[1] = g.getVertices();
        subgraphs[1].removeAll(subgraphs[0]);
        subgraphs[1].addAll(separator);
    }

    /**
     * identify vertices between level i-1 and i using accumulative BFS
     *
     * @param outerBoundaries
     * @param root
     * @return
     */
    public Set<Vertex>[] identifyVertexRegions(List<Set<Vertex>> outerBoundaries, Tree.TreeNode root) {
        for (Vertex vertex : g.getVertices()) vertex.setVisited(false);
        root.getData().setVisited(true);
        Set<Vertex>[] VertexRegions = new Set[outerBoundaries.size()];
        VertexRegions[0] = new HashSet<>();
        int totalVNum = 0;
        for (int i = 1; i <= outerBoundaries.size(); i++) {
            if (i < outerBoundaries.size()) VertexRegions[i] = new HashSet<>();
            Queue<Vertex> q = new LinkedList<>();
            q.addAll(outerBoundaries.get(i - 1));
            while (!q.isEmpty()) {
                Vertex curr = q.poll();
                for (Dart d : curr.getIncidenceList()) {
                    Vertex neighbor = d.getHead();
                    if (!neighbor.isVisited()) {
                        neighbor.setVisited(true);
                        if (i == outerBoundaries.size()) {
                            q.add(neighbor);
                            VertexRegions[i - 1].add(neighbor);
                        } else if (!outerBoundaries.get(i).contains(neighbor)) {
                            q.add(neighbor);
                            VertexRegions[i].add(neighbor);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < VertexRegions.length; i++) {
            totalVNum += VertexRegions[i].size();
            totalVNum += outerBoundaries.get(i).size();
        }

        // handle the possible overlap on out-most 2 levels
        int size = outerBoundaries.size();
        if (size > 1) {
            Set<Vertex> intersection = new HashSet<>(outerBoundaries.get(size - 1));
            intersection.retainAll(outerBoundaries.get(size - 2));
            totalVNum -= intersection.size();
        }
        // error checking
        if (totalVNum != g.getVertexNum()) {
            System.out.println(totalVNum);
            System.out.println(g.getVertexNum());
            throw new RuntimeException("Vertex number is not equal.");
        }

        return VertexRegions;
    }

    /**
     * identify the outer boundary of level 0 to h
     * level h has 3 vertices including phi itself but may overlap with level h-1
     *
     * @param primalTreeMap
     * @param uv
     * @param h
     * @param levels
     * @param path
     * @return
     */
    public List<Set<Vertex>> identifyBoundaries(Map<Vertex, Tree.TreeNode> primalTreeMap, Dart uv, int h,
                                                List<Set<Vertex>> levels, Set<Vertex> path) {
        List<Set<Vertex>> outerBoundaries = new ArrayList<>(h + 1);
        for (int i = 0; i < h; i++) {
            outerBoundaries.add(new HashSet<>());
            Vertex startV = null;
            for (Vertex vertex : levels.get(i)) {
                if (path.contains(vertex)) {
                    startV = vertex;
                    break;
                }
            }
            if (startV == null) {
                throw new RuntimeException("No intersecting vertex is found!");
            }
            outerBoundaries.get(i).add(startV);

            // start with the dart pointing "outward"
            Dart nextD = null;
            for (Dart d : startV.getIncidenceList()) {
                Vertex nextV = d.getHead();
                if (path.contains(nextV) && primalTreeMap.get(nextV).getDist() > primalTreeMap.get(startV).getDist()) {
                    nextD = d;
                    break;
                }
            }
            if (nextD == null) {
                throw new RuntimeException("No outward pointing dart is found!");
            }

            LEVEL_LOOP:
            while (nextD != null) {
                Dart d = nextD.getSuccessor();
                while (d != nextD) {
                    Vertex next = d.getHead();
                    if (outerBoundaries.get(i).contains(next)) {
                        if (next != startV) {
                            throw new RuntimeException("Closing vertex is not startV");
                        }
                        break LEVEL_LOOP;
                    }
                    if (levels.get(i).contains(next)) {
                        outerBoundaries.get(i).add(next);
                        nextD = d.getReverse();
                        continue LEVEL_LOOP;
                    }
                    d = d.getSuccessor();
                }
                nextD = null;
            }
            //System.out.printf("%d --> %d\n", levels.get(i).size(), outerBoundaries.get(i).size());
        }

        // identify 1 face incidental to (uv) to be the outer face
        // whether or not forcing outermost boundary to have 3 vertices should NOT matter
        outerBoundaries.add(new HashSet<>());
        Vertex other = uv.getNext().getHead();

        // avoid overlapping, all boundaries are vertex disjoint
        if (primalTreeMap.get(other).getDist() == h) outerBoundaries.get(h).add(other);
        if (primalTreeMap.get(uv.getHead()).getDist() == h) outerBoundaries.get(h).add(uv.getHead());
        if (primalTreeMap.get(uv.getTail()).getDist() == h) outerBoundaries.get(h).add(uv.getTail());
        return outerBoundaries;
    }

    /**
     * find root-to-phi path
     *
     * @param primalTreeMap
     * @param phi
     * @return
     */
    public Set<Vertex> pathToPhi(Map<Vertex, Tree.TreeNode> primalTreeMap, Vertex phi) {
        Set<Vertex> path = new HashSet<>();
        Tree.TreeNode tmp = primalTreeMap.get(phi);
        while (tmp != null) {
            path.add(tmp.getData());
            tmp = tmp.getParent();
        }
        return path;
    }

    /**
     * Group vertices into levels based on distance from root
     *
     * @param primalTreeMap
     * @param h
     * @return
     */
    public List<Set<Vertex>> verticeLevels(Map<Vertex, Tree.TreeNode> primalTreeMap, int h) {
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
        return levels;
    }

    @Override
    public Set<Vertex>[] findSubgraphs() {
        if (separator == null) {
            findSeparator();
        }
        return subgraphs;
    }

    /*
    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/grids/1.txt");
        Separator sp = new SimpleCycleSeparator(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(5), null);
        System.out.println(separator);
    }
    */
}
