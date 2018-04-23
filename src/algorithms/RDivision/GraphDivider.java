package algorithms.RDivision;

import selfdualgraph.*;

import java.util.*;

public abstract class GraphDivider {
    protected SelfDualGraph g;
    protected Set<Set<Vertex>> regions;
    protected SelfDualGraph originG;

    public GraphDivider(SelfDualGraph g) {
        originG = g;
        this.g = g.buildSubgraph(g.getVertices());
        this.g.flatten();
        this.g.triangulate();
        regions = new HashSet<>();
    }

    public abstract Set<Set<Vertex>> rDivision(int r);

    public Set<Set<Vertex>> getRegions() {
        return regions;
    }


    public Set<Integer> verticesToID(Set<Vertex> vertices) {
        Set<Integer> ids = new HashSet<>();
        for (Vertex v : vertices) ids.add(v.getID());
        return ids;
    }

    public void setGraph(SelfDualGraph graph) {
        g = graph;
    }

}
