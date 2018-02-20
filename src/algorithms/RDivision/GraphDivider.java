package algorithms.RDivision;

import selfdualgraph.*;

import java.util.*;

public abstract class GraphDivider {
    protected SelfDualGraph g;
    protected Map<Set<Vertex>, Set<Vertex>> regions;

    public GraphDivider(SelfDualGraph g) {
        this.g = g;
        this.g.flatten();
        this.g.triangulate();
        regions = new HashMap<>();
    }

    public abstract Set<Set<Vertex>> rDivision(int r);

}
