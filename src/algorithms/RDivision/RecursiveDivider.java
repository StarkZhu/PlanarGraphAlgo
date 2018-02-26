package algorithms.RDivision;

import algorithms.Separator.*;
import selfdualgraph.*;

import java.util.*;

/**
 * O(NlogN) r-division
 * Implementation uses SCS
 */
public class RecursiveDivider extends GraphDivider {
    private Queue<SelfDualGraph> subgraphs;

    public RecursiveDivider(SelfDualGraph g) {
        super(g);
        subgraphs = new LinkedList<>();
    }


    @Override
    public Set<Set<Vertex>> rDivision(int r) {
        return null;
    }

    public void phaseI(SelfDualGraph graph, int r) {
        if (graph.getVertexNum() <= r) {
            subgraphs.add(graph);
            return;
        }
        graph.triangulate();
        Separator sp = new SimpleCycleSeparator(graph);
        Set<Vertex> separator = sp.findSeparator();
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        SelfDualGraph g1 = graph.buildSubgraph(subgraphs[0], separator);
        SelfDualGraph g2 = graph.buildSubgraph(subgraphs[1], separator);
        phaseI(g1, r);
        phaseI(g2, r);
    }

    public void phaseII(int r) {
        while (!subgraphs.isEmpty()) {
            SelfDualGraph subgraph = subgraphs.poll();
            // if boundary size less than 4*sqrt(r)
            if (subgraph.getBoundarySize() <= Math.max(4 * Math.sqrt(r), 6)) {
                regions.add(g.getVerticesFromID(verticesToID(subgraph.getVertices())));
            } else {
                subgraph.triangulate();
                assignWeightToBoundary(subgraph);
                Separator sp = new SimpleCycleSeparator(subgraph);
                Set<Vertex> separator = sp.findSeparator();
                Set<Vertex>[] subs = sp.findSubgraphs();
                SelfDualGraph g1 = subgraph.buildSubgraph(subs[0], separator);
                SelfDualGraph g2 = subgraph.buildSubgraph(subs[1], separator);
                subgraphs.add(g1);
                subgraphs.add(g2);
            }
        }
    }

    private Set<Integer> verticesToID(Set<Vertex> vertices) {
        Set<Integer> ids = new HashSet<>();
        for (Vertex v : vertices) ids.add(v.getID());
        return ids;
    }

    /**
     * Due to SCS, assume boundary is a simple cycle
     * @param graph
     */
    public void assignWeightToBoundary(SelfDualGraph graph) {
        Set<Vertex> boundary = graph.getBoundary();
        for (Vertex f : graph.getFaces()) f.setWeight(0);
        for (Vertex v : graph.getVertices()) {
            if (!boundary.contains(v)) v.setWeight(0);
            else {  // v is on the boundary
                v.setWeight(1);
                for (Dart d : v.getIncidenceList()) {
                    // dart is on the boundary, forms a cycle
                    if (boundary.contains(d.getHead())) {
                        Vertex left = d.getLeft();
                        left.setWeight(0.25 + left.getWeight());
                        Vertex right = d.getRight();
                        right.setWeight(0.25 + right.getWeight());
                    }
                }
            }
        }
    }
}
