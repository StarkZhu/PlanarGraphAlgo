package algorithms.RDivision;

import algorithms.Separator.*;
import selfdualgraph.SelfDualGraph;
import selfdualgraph.Vertex;

import java.util.HashSet;
import java.util.Set;

public class RecursiveDivider extends GraphDivider {

    public RecursiveDivider(SelfDualGraph g) {
        super(g);
    }


    @Override
    public Set<Set<Vertex>> rDivision(int r) {
        return null;
    }

    // TODO: which one is better? pass in Set<Vertex> or Set<Integer>
    private void divide(Set<Integer> subGVertexIDs, int r, Set<Integer> boundaryIDs) {
        if (subGVertexIDs.size() <= r) {
            regions.put(g.getVerticesFromID(subGVertexIDs), g.getVerticesFromID(boundaryIDs));
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

    private void divide(SelfDualGraph graph, int r, Set<Vertex> boundary) {
        if (graph.getVertexNum() <= r) {
            Set<Integer> subGVertexIDs = verticesToID(graph.getVertices());
            Set<Integer> boundaryIDs = verticesToID(boundary);
            regions.put(g.getVerticesFromID(subGVertexIDs), g.getVerticesFromID(boundaryIDs));
            return;
        }
        Separator sp = new SimpleCycleSeparator(graph);
        Set<Vertex> separator = sp.findSeparator();
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        boundary.addAll(separator);
        divide(graph.buildSubgraph(subgraphs[0]), r, graph.findBoundary(subgraphs[0], boundary));
        divide(graph.buildSubgraph(subgraphs[1]), r, graph.findBoundary(subgraphs[1], boundary));
    }

    private Set<Integer> verticesToID(Set<Vertex> vertices) {
        Set<Integer> ids = new HashSet<>();
        for (Vertex v : vertices) ids.add(v.getID());
        return ids;
    }
}
