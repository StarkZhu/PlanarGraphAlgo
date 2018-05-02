package algorithms.RDivision;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

/**
 * Algorithm:
 * given r
 * rho-cluster: N -> N'=N/sqrt(r)
 * contract: O(N)
 * r-division: O(N'logN')
 * expand: O(N)
 * size per expanded region: r^1.5
 * r-division on each: O(rlogr)
 * total time T: O(NlogN/sqrt(r) + Nlogr)
 * <p>
 * T = O(NloglogN) < O(NlogN) if r >= (logN)^2
 * <p>
 * to divide contracted G into more than 1 region: N' >= r -> N >= r^1.5
 * => N >= (logN)^3
 * => N ~
 */
public class FredDivider extends GraphDivider {
    private Map<Vertex, Set<Vertex>> contractedVertexToVSet;
    public static int disconnectedComponentsNum;

    public FredDivider(SelfDualGraph g) {
        super(g);
        disconnectedComponentsNum = 0;
    }

    /**
     * generate a rho-clustering, each of which is a vertex-disjoint connected piece with O(rho) vertices
     *
     * @param rho cluster size
     * @return mapping from original vertex to its cluster
     */
    public Map<Vertex, Set<Vertex>> rhoClustering(int rho) {
        Map<Vertex, Set<Vertex>> vertexToCluster = new HashMap<>();
        SpanningTreeSolver sts = new DFSsolver();
        RootFinder rf = new MinDegreeRootFinder();
        Tree[] trees = sts.buildTreeCoTree(originG, rf.selectRootVertex(originG), null);

        Stack<Tree.TreeNode> stack = new Stack<>();
        stack.push(trees[0].getRoot());
        Vertex v = null;
        while (!stack.isEmpty()) {
            Tree.TreeNode node = stack.pop();
            v = node.getData();
            if (vertexToCluster.get(v) == null) {   // first visit
                Set<Vertex> set = new HashSet<>();
                set.add(v);
                vertexToCluster.put(v, set);
                if (node.getChildren().size() > 0) {
                    stack.push(node);
                    for (Tree.TreeNode child : node.getChildren()) stack.push(child);
                }
                continue;
            }
            Set<Vertex> descendantSet = vertexToCluster.get(v);
            for (Tree.TreeNode child : node.getChildren()) {
                Set<Vertex> childSet = vertexToCluster.get(child.getData());
                if (childSet.size() >= rho) continue;   // already clustered
                descendantSet.addAll(childSet);
            }
            if (descendantSet.size() >= rho) {
                for (Vertex vv : descendantSet) vertexToCluster.put(vv, descendantSet);
            }
        }
        Set<Vertex> descendantSet = vertexToCluster.get(v);
        for (Vertex vv : descendantSet) vertexToCluster.put(vv, descendantSet);
        return vertexToCluster;
    }

    /**
     * Create a new graph, clone original graph, for each vertex set merge them to be a single vertex in new graph
     * To enforce processing order, pass in TreeSet<Vertex> for each cluster
     *
     * @param clusters
     * @return
     */
    public SelfDualGraph contractedGraph(Set<Set<Vertex>> clusters) {
        Set<Vertex> subgraphV = originG.getVertices();
        // map old vertices to new graph
        Map<Vertex, Vertex> vMap = new HashMap<>();     // originV -> copiedV
        for (Vertex v : subgraphV) {
            Vertex v2 = new Vertex(v);
            vMap.put(v, v2);
        }

        contractedVertexToVSet = new HashMap<>();       // contractedV -> original Vset
        SelfDualGraph contractedG = originG.cloneSubgraph(vMap, originG.getBoundary());
        for (Set<Vertex> cluster : clusters) {
            List<Vertex> clonedCluster = new LinkedList<>();
            // processing order is ensured if pass-in vertex set is TreeSet
            for (Vertex v : cluster) clonedCluster.add(vMap.get(v));    // original vertex -> cloned vertex
            Vertex vc = contractedG.mergeConnectedPiece(clonedCluster);
            contractedVertexToVSet.put(vc, cluster);
        }
        contractedG.flatten();
        contractedG.triangulate();
        return contractedG;
    }

    /**
     * expand a region of contracted vertices back to original vertices
     * Algorithm:
     * each face F is assigned to exactly 1 region R which it belongs to:
     *      iff F is free && all F's vertices are in R
     * each region contains all incidental vertices of faces in R
     *      a vertex may belong to more than 1 region
     *
     * @param contractedRegion
     * @return
     */
    public Set<SelfDualGraph> expandRegion(Set<Vertex> contractedRegion) {
        Set<Vertex> expanded = new HashSet<>();
        Set<Vertex> candidates = new HashSet<>();   // originV
        Set<Vertex> faceGroup = new HashSet<>();
        for (Vertex v : contractedRegion) {
            candidates.addAll(contractedVertexToVSet.get(v));
        }
        for (Vertex v : candidates) {
            for (Dart d : v.getIncidenceList()) {
                Vertex face = d.getRight();
                if (face.isVisited()) continue;
                boolean inRegion = true;
                for (Dart dd : face.getIncidenceList()) {
                    if (!candidates.contains(dd.getTail())) {
                        inRegion = false;
                        break;
                    }
                }
                if (inRegion) {
                    face.setVisited(true);
                    faceGroup.add(face);
                }
            }
        }
        for (Vertex face : faceGroup) {
            for (Dart d : face.getIncidenceList()) {
                expanded.add(d.getTail());
            }
        }

        // contracted piece may be connected through artificial edges
        // expended region may NOT be connected with edges in original graph, should treat each connected component as a subregion
        Set<Set<Vertex>> connectedComponents = identifyConnectedComponent(expanded);
        Set<SelfDualGraph> subgraphs = new HashSet<>();
        for (Set<Vertex> component : connectedComponents) {
            subgraphs.add(originG.buildSubgraph(component));
        }
        return subgraphs;
    }

    /**
     * assign boundary vertices to exactly 1 region, no overlapping between any 2 regions
     *
     * @param regions
     * @return
     */
    public Set<Set<Vertex>> filterBoundaryVertices(Set<Set<Vertex>> regions) {
        Set<Vertex> visited = new HashSet<>();
        for (Set<Vertex> region : regions) visited.addAll(region);
        Set<Set<Vertex>> filteredRegions = new HashSet<>();
        for (Set<Vertex> region : regions) {
            Set<Vertex> filtered = new HashSet<>();
            for (Vertex v : region) {
                if (visited.contains(v)) {
                    filtered.add(v);
                    visited.remove(v);
                }
            }
            if (filtered.size() > 0) filteredRegions.add(filtered);
        }
        Set<Set<Vertex>> connectedRegions = new HashSet<>();
        for (Set<Vertex> region : filteredRegions) {
            connectedRegions.addAll(identifyConnectedComponent(region));
        }
        return connectedRegions;
    }

    /**
     * use BFS to identify connected components in a region, each component forms a sub-region
     *
     * @param region
     * @return
     */
    private Set<Set<Vertex>> identifyConnectedComponent(Set<Vertex> region) {
        Set<Set<Vertex>> disconnectedComponent = new HashSet<>();
        Set<Vertex> visited = new HashSet<>();
        for (Vertex v : region) {
            if (visited.contains(v)) continue;
            Set<Vertex> connectedComponent = new HashSet<>();
            Queue<Vertex> q = new LinkedList<>();
            q.add(v);
            visited.add(v);
            connectedComponent.add(v);
            while (!q.isEmpty()) {
                Vertex vv = q.poll();
                for (Dart d : vv.getIncidenceList()) {
                    Vertex n = d.getHead();
                    if (visited.contains(n) || !region.contains(n)) continue;
                    q.add(n);
                    visited.add(n);
                    connectedComponent.add(n);
                }
            }
            disconnectedComponent.add(connectedComponent);
        }
        if (disconnectedComponent.size() > 1) {
            disconnectedComponentsNum += disconnectedComponent.size();
            //System.out.printf("A region contains %d connected components\n", disconnectedComponent.size());
        }
        return disconnectedComponent;
    }

    @Override
    public Set<Set<Vertex>> rDivision(int r) {
        disconnectedComponentsNum = 0;
        g.flatten();
        g.triangulate();
        // rho-clustering, rho = sqrt(r)
        int rho = (int) Math.sqrt(r);
        Map<Vertex, Set<Vertex>> vertexToCluster = rhoClustering(rho);  // original V

        // contract each cluster into 1 single node, make new graph with ((n/sqrt(r)) vertices
        SelfDualGraph contracted = contractedGraph(new HashSet<>(vertexToCluster.values()));

        // recursive division on new graph
        RecursiveDivider rd = new RecursiveDivider(contracted);
        Set<Set<Vertex>> contractedRegions = rd.rDivision(r);
        //contractedRegions = filterBoundaryVertices(contractedRegions);

        // expend each piece
        for (Vertex f : originG.getFaces()) f.setVisited(false);
        for (Set<Vertex> region : contractedRegions) {
            Set<SelfDualGraph> expandedSubgraphs = expandRegion(region);
            // O(log(r)) levels of recursive division on each piece
            for (SelfDualGraph expandedSubgraph : expandedSubgraphs) {
                rd = new RecursiveDivider(expandedSubgraph);
                Set<Set<Vertex>> subgraphRegions = rd.rDivision(r);
                //subgraphRegions = filterBoundaryVertices(subgraphRegions);
                for (Set<Vertex> subRegion : subgraphRegions) {
                    regions.add(originG.getVerticesFromID(verticesToID(subRegion)));
                }
            }
        }
        return regions;
    }


    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        //g.buildGraph("./input_data/random/5.txt");
        g.buildGraph("./input_data/grids/5.txt");

        FredDivider fd = new FredDivider(g);
        int[] rs = new int[]{50};
        for (int r : rs) {
            System.out.printf("r = %d\n", r);
            long time0 = System.currentTimeMillis(), time1, time2, time3;
            int rho = (int) Math.sqrt(r);
            Map<Vertex, Set<Vertex>> vertexToCluster = fd.rhoClustering(rho);
            time1 = System.currentTimeMillis();
            System.out.printf("clustering done [%dms]\n", time1 - time0);
            SelfDualGraph contracted = fd.contractedGraph(new HashSet<>(vertexToCluster.values()));
            RecursiveDivider rd = new RecursiveDivider(contracted);
            Set<Set<Vertex>> contractedRegions = rd.rDivision(r);
            contractedRegions = fd.filterBoundaryVertices(contractedRegions);
            time2 = System.currentTimeMillis();
            System.out.printf("contraction done [%dms]\n", time2 - time1);
            for (Set<Vertex> region : contractedRegions) {
                Set<SelfDualGraph> expandedSubgraphs = fd.expandRegion(region);
            }
            time3 = System.currentTimeMillis();
            System.out.printf("expansion done [%dms]\n", time3 - time2);
            System.out.printf("Total Time: [%dms]\n", time3 - time0);
            System.out.println("------");
        }
    }

}
