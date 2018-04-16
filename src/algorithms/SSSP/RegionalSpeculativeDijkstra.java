package algorithms.SSSP;

import algorithms.RDivision.*;
import selfdualgraph.*;

import java.util.*;

public class RegionalSpeculativeDijkstra {
    protected SelfDualGraph g;
    protected Map<Dart, Region[]> dartRegionMap;
    protected GraphDivider graphDivider;

    public RegionalSpeculativeDijkstra(SelfDualGraph g, GraphDivider gd) {
        this.g = g;
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
            if (d.getHead().getDistance() > d.getTail().getDistance() + d.getWeight()) {
                d.getHead().setDistance(d.getTail().getDistance() + d.getWeight());
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

    public void findSSSP(Vertex src, int r) {
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
}
