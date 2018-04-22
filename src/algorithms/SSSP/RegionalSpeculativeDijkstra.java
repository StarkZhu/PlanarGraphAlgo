package algorithms.SSSP;

import algorithms.RDivision.*;
import selfdualgraph.*;

import java.util.*;

/**
 * reference: Faster Shortest-Path Algorithms for Planar Graphs
 * https://ac.els-cdn.com/S0022000097914938/1-s2.0-S0022000097914938-main.pdf?_tid=8a8eba64-c235-4275-9ba9-11ce0d25b255&acdnat=1524076731_07efe8c169cddcc414b7cf56b4d22f6d
 */
public class RegionalSpeculativeDijkstra extends SSSP{
    protected Map<Dart, Region[]> dartRegionMap;
    protected GraphDivider graphDivider;

    public RegionalSpeculativeDijkstra(SelfDualGraph g, GraphDivider gd) {
        super(g);
        graphDivider = gd;
        dartRegionMap = new HashMap<>();
    }

    public RegionalSpeculativeDijkstra(SelfDualGraph g, GraphDivider gd, int dist_measure) {
        super(g, dist_measure);
        graphDivider = gd;
        dartRegionMap = new HashMap<>();
    }

    private void globalUpdate(Region r, Region subr, double key) {
        double oldMinKey = r.minKey();
        r.updateKey(subr, key);
        if (key < oldMinKey && r.getParent() != null) globalUpdate(r.getParent(), r, key);
    }

    private void processRegion(Region r) {
        if (r.isAtomic()) {
            Dart d = r.getDart();
            if (d.getHead().getDistance() > d.getTail().getDistance() + getDartDist(d)) {
                d.getHead().setDistance(d.getTail().getDistance() + getDartDist(d));
                for (Dart dd : d.getHead().getIncidenceList()) {
                    Region[] atomicRs = dartRegionMap.get(dd);
                    globalUpdate(atomicRs[0], atomicRs[1], d.getHead().getDistance());
                }
            }
            r.updateKey(dartRegionMap.get(d)[1], Double.POSITIVE_INFINITY);
        } else {
            int i = 0;
            while (i++ < r.getAlpha() && r.minKey() < Double.POSITIVE_INFINITY) {
                Region rr = r.minItem();
                processRegion(rr);
                r.updateKey(rr, rr.minKey());
            }
        }
    }

    /**
     * use r-division, build a regrion from each piece after division
     * @param g
     * @param divisions
     * @return
     */
    public Region buildRegionTree(SelfDualGraph g, Set<Set<Vertex>> divisions) {
        Region rG = new Region(Double.POSITIVE_INFINITY, null);
        double alpha1 = Math.log(g.getVertexNum()) / Math.log(2);
        for (Set<Vertex> division : divisions) {
            Region rg = new Region(alpha1, null);
            for (Vertex v : division) {
                for (Dart d : v.getIncidenceList()) {
                    if (!division.contains(d.getHead())) continue;
                    Region atomic0 = new Region(1, d);
                    Region atomic1 = new Region(1, d);
                    dartRegionMap.put(d, new Region[]{atomic0, atomic1});
                    atomic0.addSubRegion(atomic1);
                    rg.addSubRegion(atomic0);
                }
            }
            rG.addSubRegion(rg);
        }
        return rG;
    }

    @Override
    public void findSSSP(Vertex src) {
        int r = Math.max(10, (int) (Math.pow(Math.log(g.getVertexNum()) / Math.log(2), 2)));
        findSSSP(src, r);
    }

    public void findSSSP(Vertex src, int r) {
        if (!vertices.contains(src)) {
            throw new RuntimeException("Source vertex not in graph");
        }
        this.src = src;

        for (Vertex v : g.getVertices()) v.setDistance(Double.POSITIVE_INFINITY);
        graphDivider.setGraph(g.buildSubgraph(g.getVertices()));
        Set<Set<Vertex>> divisions = graphDivider.rDivision(r);
        Set<Set<Vertex>> originalVertices = new HashSet<>();
        for (Set<Vertex> division : divisions) {
            originalVertices.add(g.getVerticesFromID(graphDivider.verticesToID(division)));
        }
        Region rG = buildRegionTree(g, originalVertices);
        src.setDistance(0);
        for (Dart d : src.getIncidenceList()) {
            Region[] atomicRs = dartRegionMap.get(d);
            globalUpdate(atomicRs[0], atomicRs[1], 0);
        }
        processRegion(rG);
    }

    @Override
    public List<Vertex> getPath(Vertex src, Vertex dest) {
        if (src != this.src) findSSSP(src);
        LinkedList<Vertex> path = new LinkedList<>();

        // build path from dest to src
        Vertex v = dest;
        while (v != src) {
            path.addFirst(v);
            for (Dart d : v.getIncidenceList()) {
                if (d.getHead().getDistance() == v.getDistance() - getDartDist(d)) {
                    v = d.getHead();
                    break;
                }
            }
        }
        path.addFirst(v);
        return path;
    }
}
