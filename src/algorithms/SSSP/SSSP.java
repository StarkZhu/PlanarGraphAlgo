package algorithms.SSSP;

import selfdualgraph.*;

import java.util.*;

public abstract class SSSP {
    public static final int WEIGHT_AS_DISTANCE = 0;
    public static final int CAPACITY_AS_DISTANCE = 1;

    protected SelfDualGraph g;
    protected Set<Vertex> vertices;
    protected Vertex src;
    protected int distance_measure;

    protected SSSP (SelfDualGraph graph) {
        g = graph;
        vertices = g.getVertices();
        distance_measure = WEIGHT_AS_DISTANCE;
    }

    protected SSSP (SelfDualGraph graph, int dist_measure) {
        g = graph;
        vertices = g.getVertices();
        distance_measure = dist_measure;
    }

    /**
     * find shortest path from src to all other vertices
     * @param src
     */
    public abstract void findSSSP(Vertex src);

    /**
     * find shortest path from src to all other vertices
     * @param src
     */
    public abstract void findSSSP(Vertex src, int r);

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

    /**
     * changing distance measure: use dart weight or dart capacity
     * @param dist_measure
     */
    public void setDistance_measure(int dist_measure){
        distance_measure = dist_measure;
    }

    protected double getDartDist(Dart d) {
        if (distance_measure == WEIGHT_AS_DISTANCE) return d.getWeight();
        else if (distance_measure == CAPACITY_AS_DISTANCE) return d.getCapacity();
        else throw new RuntimeException("Distance measure not set correctly.");
    }
}
