package algorithms.RDivision;

import algorithms.Separator.*;
import selfdualgraph.SelfDualGraph;
import selfdualgraph.Vertex;

import java.util.Set;

public class RecursiveDivider extends GraphDivider {

    public RecursiveDivider(SelfDualGraph g) {
        super(g);
    }


    @Override
    public Set<Set<Vertex>> rDivision(int r) {
        return null;
    }

    private void divide(Set<Vertex> subGVertices, int r, Set<Vertex> boundary) {
        if (subGVertices.size() <= r) {
            regions.put(subGVertices, boundary);
            return;
        }
        SelfDualGraph graph = buildSubgraph(subGVertices, boundary);
        Separator sp = new SimpleCycleSeparator(graph);
        Set<Vertex> separator = sp.findSeparator();
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        boundary.addAll(separator);
        divide(subgraphs[0], r, findBoundary(subgraphs[0], boundary));
        divide(subgraphs[1], r, findBoundary(subgraphs[1], boundary));
    }
}
