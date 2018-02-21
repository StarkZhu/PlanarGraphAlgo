package algorithms.RDivision;

import algorithms.Separator.*;
import selfdualgraph.*;

import java.util.*;

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

    // TODO: which one is better? pass in Set<Vertex> or Set<Integer>
    /*
    private void divide(Set<Integer> subGVertexIDs, int r, Set<Integer> boundaryIDs) {
        if (subGVertexIDs.size() <= r) {
            gToB.put(g.getVerticesFromID(subGVertexIDs), g.getVerticesFromID(boundaryIDs));
            return;
        }
        SelfDualGraph graph = g.buildSubgraph(subGVertexIDs, boundaryIDs);
        Separator sp = new SimpleCycleSeparator(graph);
        Set<Vertex> separator = sp.findSeparator();
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        Set<Vertex> boundary = graph.getVerticesFromID(boundaryIDs);
        boundary.addAll(separator);
        divide(verticesToID(subgraphs[0]), r, verticesToID(graph.findBoundary(subgraphs[0], boundary)));
        divide(verticesToID(subgraphs[1]), r, verticesToID(graph.findBoundary(subgraphs[1], boundary)));
    }
    */

    private void phaseI(SelfDualGraph graph, int r) {
        if (graph.getVertexNum() <= r) {
            //Set<Integer> subGVertexIDs = verticesToID(graph.getVertices());
            //Set<Integer> boundaryIDs = verticesToID(boundary);
            subgraphs.add(graph);
            return;
        }
        Separator sp = new SimpleCycleSeparator(graph);
        Set<Vertex> separator = sp.findSeparator();
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        SelfDualGraph g1 = graph.buildSubgraph(subgraphs[0], separator);
        SelfDualGraph g2 = graph.buildSubgraph(subgraphs[1], separator);
        phaseI(g1, r);
        phaseI(g2, r);
    }

    private void phaseII(int r) {
        while (!subgraphs.isEmpty()) {
            SelfDualGraph subgraph = subgraphs.poll();
            // TODO: if boundary size less than ?? (4*sqrt(r))
            if (subgraph.getBoundarySize() <= 4 * Math.sqrt(r)) {
                regions.add(g.getVerticesFromID(verticesToID(subgraph.getVertices())));
            } else {
                // TODO: assign weight to boundary vertices only
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
}
