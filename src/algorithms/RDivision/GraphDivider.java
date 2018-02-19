package algorithms.RDivision;

import selfdualgraph.*;

import java.util.*;

public abstract class GraphDivider {
    protected SelfDualGraph g;
    protected Map<Set<Vertex>, Set<Vertex>> regions;
    protected Map<Integer, Vertex> idToV;

    public GraphDivider(SelfDualGraph g) {
        this.g = g;
        this.g.flatten();
        this.g.triangulate();
        regions = new HashMap<>();
        idToV = new HashMap<>();
        for (Vertex v : this.g.getVertices()) idToV.put(v.getID(), v);
    }

    public abstract Set<Set<Vertex>> rDivision(int r);

    public Set<Vertex> getVerticesFromID(Set<Integer> ids) {
        Set<Vertex> vertices = new HashSet<>();
        for (int i : ids) vertices.add(idToV.get(i));
        return vertices;
    }

    public SelfDualGraph buildSubgraph(Set<Vertex> vertices, Set<Vertex> boundary) {
        Set<Integer> vID = new HashSet<>();
        for (Vertex v : vertices) vID.add(v.getID());
        Set<Integer> bID = new HashSet<>();
        for (Vertex v : boundary) bID.add(v.getID());
        // TODO: deep copy vertices from g, modify incidence list to only have edges inside subgraph
        return null;
    }

    public Set<Vertex> findBoundary(Set<Vertex> subgraph, Set<Vertex> boundaryUnion) {
        return null;
    }
}
