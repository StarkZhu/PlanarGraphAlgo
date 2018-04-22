package algorithms.SSSP;

import selfdualgraph.*;

import java.util.*;

public abstract class SSSP {
    protected SelfDualGraph g;
    protected Set<Vertex> vertices;
    protected Vertex src;

    protected SSSP (SelfDualGraph graph) {
        g = graph;
        vertices = g.getVertices();
    }

    /**
     * find shortest path from src to all other vertices
     * @param src
     */
    public abstract void findSSSP(Vertex src);

    /**
     * build shortest path from src to dest
     * @param src
     * @param dest
     * @return
     */
    public abstract List<Vertex> getPath(Vertex src, Vertex dest);


    /**
     * find distance value from src to dest
     * @param src
     * @param dest
     * @return
     */
    public double distFromTo(Vertex src, Vertex dest) {
        if (src != this.src) findSSSP(src);
        if (!vertices.contains(dest)) {
            throw new RuntimeException("Destination vertex not in graph");
        }
        return dest.getDistance();
    }
}
