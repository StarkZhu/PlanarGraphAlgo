package algorithms.RDivision;

import algorithms.RootFinder.*;
import algorithms.SpanningTreeSolver.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class FredDivider extends GraphDivider {
    private Map<Vertex, Set<Vertex>> contractedVertexToVSet;

    public FredDivider(SelfDualGraph g) {
        super(g);
    }

    /**
     * generate a rho-clustering, each of which is a vertex-disjoint connected piece with O(rho) vertices
     *
     * @param rho cluster size
     * @return mapping from original vertex to its cluster
     */
    /*
    public Map<Vertex, Set<Vertex>> rhoClustering(int rho) {
        Map<Vertex, Set<Vertex>> vertexToCluster = new HashMap<>();
        SpanningTreeSolver sts = new BFSsolver();
        RootFinder rf = new MaxDegreeRootFinder();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);

        Map<Vertex, Set<Vertex>> faceToSet = new HashMap<>();
        Stack<Tree.TreeNode> stack = new Stack<>();
        stack.push(trees[1].getRoot());
        while (!stack.isEmpty()) {
            Tree.TreeNode node = stack.pop();
            Vertex v = node.getData();
            if (faceToSet.get(v) == null) {   // first visit
                Set<Vertex> set = new HashSet<>();
                faceToSet.put(v, set);
                for (Dart d : v.getIncidenceList()) {
                    if (vertexToCluster.get(d.getHead()) == null) {
                        set.add(d.getHead());
                        vertexToCluster.put(d.getHead(), set);
                    }
                }
                if (node.getChildren().size() > 0) {
                    stack.push(node);
                    for (Tree.TreeNode child : node.getChildren()) stack.push(child);
                }
                continue;
            }
            Set<Vertex> vSet = faceToSet.get(v);
            for (Tree.TreeNode child : node.getChildren()) {
                if (faceToSet.get(child.getData()).size() >= rho) continue;   // already clustered
                vSet.addAll(faceToSet.get(child.getData()));
            }
            if (vSet.size() >= rho) {
                for (Vertex vv : vSet) vertexToCluster.put(vv, vSet);
            }
        }
        return vertexToCluster;
    }
    */
    public Map<Vertex, Set<Vertex>> rhoClustering(int rho) {
        Map<Vertex, Set<Vertex>> vertexToCluster = new HashMap<>();
        SpanningTreeSolver sts = new DFSsolver();
        RootFinder rf = new MinDegreeRootFinder();
        Tree[] trees = sts.buildTreeCoTree(g, rf.selectRootVertex(g), null);

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
        Set<Vertex> subgraphV = g.getVertices();
        // map old Vertice, Darts to new graph
        Map<Vertex, Vertex> vMap = new HashMap<>();
        Map<Dart, Dart> dMap = new HashMap<>();
        for (Vertex v : subgraphV) {
            Vertex v2 = new Vertex(v);
            vMap.put(v, v2);
            for (Dart d : v.getIncidenceList()) {
                if (subgraphV.contains(d.getHead())) dMap.put(d, new Dart(d));
            }
        }

        contractedVertexToVSet = new HashMap<>();
        SelfDualGraph contractedG = g.cloneSubgraph(vMap, dMap, g.getBoundary());
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
     *
     * @param contractedRegion
     * @return
     */
    public SelfDualGraph expandRegion(Set<Vertex> contractedRegion) {
        Set<Vertex> expanded = new HashSet<>();
        for (Vertex v : contractedRegion) {
            expanded.addAll(contractedVertexToVSet.get(v));
        }
        // find boundary of subgraph
        Set<Vertex> boundary = new HashSet<>();
        for (Vertex v : expanded) {
            for (Dart d : v.getIncidenceList()) {
                if (!expanded.contains(d.getHead())) {
                    boundary.add(v);
                    break;
                }
            }
        }
        SelfDualGraph subgraph = g.buildSubgraph(expanded, boundary);
        return subgraph;
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
        if (disconnectedComponent.size() > 1)
            System.out.printf("A region contains %d connected components\n", disconnectedComponent.size());
        return disconnectedComponent;
    }

    @Override
    public Set<Set<Vertex>> rDivision(int r) {
        g.flatten();
        g.triangulate();
        // rho-clustering, rho = sqrt(r)
        int rho = (int) Math.sqrt(r);
        Map<Vertex, Set<Vertex>> vertexToCluster = rhoClustering(rho);

        // contract each cluster into 1 single node, make new graph with ((n/sqrt(r)) vertices
        SelfDualGraph contracted = contractedGraph(new HashSet<>(vertexToCluster.values()));

        // recursive division on new graph
        RecursiveDivider rd = new RecursiveDivider(contracted);
        Set<Set<Vertex>> contractedRegions = rd.rDivision(r);
        contractedRegions = filterBoundaryVertices(contractedRegions);


        // expend each piece
        for (Set<Vertex> region : contractedRegions) {
            SelfDualGraph expandedSubgraph = expandRegion(region);

            /*
            if (expandedSubgraph.getVertexNum() == 15) {
                try {
                    expandedSubgraph.saveToFile("./test/bug_graph_0.txt");
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }

                int visitedV = 0, visitedF = 0, visitedD = 0, totalD = 0;
                for (Vertex f : expandedSubgraph.getFaces()) if (f.isVisited()) visitedF++;
                for (Vertex v : expandedSubgraph.getVertices()) {
                    if (v.isVisited()) visitedV++;
                    for (Dart d : v.getIncidenceList()) {
                        if (d.isVisited()) visitedD++;
                        totalD++;
                    }
                }
                System.out.println(" ");
            }
            */

            // O(log(r)) levels of recursive division on each piece
            rd = new RecursiveDivider(expandedSubgraph);
            Set<Set<Vertex>> subgraphRegions = rd.rDivision(r);
            subgraphRegions = filterBoundaryVertices(subgraphRegions);
            for (Set<Vertex> subRegion : subgraphRegions) {
                regions.add(g.getVerticesFromID(verticesToID(subRegion)));
            }
        }
        return regions;
    }

    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/random/3.txt");

        FredDivider fd = new FredDivider(g);
        //int r = Math.max(10, (int) (Math.log(g.getVertexNum()) / Math.log(2)));
        int r = 12;
        System.out.printf("r = %d\n", r);
        long time0 = System.currentTimeMillis();
        Set<Set<Vertex>> regions = fd.rDivision(r);
        long time1 = System.currentTimeMillis();
        System.out.printf("Time: [%dms]\n", time1 - time0);
    }
}
