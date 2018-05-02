package algorithms.SSSP;

import selfdualgraph.*;

import java.util.*;

/**
 * Assume:
 * graph is connected, otherwise some vertices have distance Double.MAX_VALUE
 * edges are symmetric, d.weight == d.reverse.weight
 */
public class Dijkstra extends SSSP{
    protected Map<Vertex, Vertex> vPathPrev;

    public Dijkstra(SelfDualGraph graph) {
        super(graph);
    }

    public Dijkstra(SelfDualGraph graph, int dist_measure) {
        super(graph, dist_measure);
    }


    @Override
    public void findSSSP(Vertex src, int r) {
        findSSSP(src);
    }

    @Override
    public void findSSSP(Vertex src) {
        if (!vertices.contains(src)) {
            throw new RuntimeException("Source vertex not in graph");
        }
        this.src = src;
        for (Vertex v : vertices) {
            v.setVisited(false);
            v.setDistance(Double.MAX_VALUE);
        }
        // TODO: implement a PQ that supports decrease-key
        PriorityQueue<Vertex> pq = new PriorityQueue<>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                if (o1.getDistance() - o2.getDistance() < 0) {
                    return -1;
                } else if (o1.getDistance() - o2.getDistance() > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        vPathPrev = new HashMap<>();
        src.setDistance(0);
        pq.add(src);
        while (!pq.isEmpty()) {
            Vertex v = pq.poll();
            if (v.isVisited()) continue;
            v.setVisited(true);
            for (Dart d : v.getIncidenceList()) {
                Vertex vv = d.getHead();
                if (vv.isVisited()) continue;
                if (v.getDistance() + getDartDist(d) < vv.getDistance()) {
                    vv.setDistance(v.getDistance() + getDartDist(d));
                    pq.add(vv);
                    vPathPrev.put(vv, v);
                }
            }
        }
    }

    public List<Vertex> getPath(Vertex src, Vertex dest) {
        if (src != this.src) findSSSP(src);
        LinkedList<Vertex> path = new LinkedList<>();

        // build path from dest to src
        Vertex v = dest;
        while (v != src) {
            path.addFirst(v);
            v = vPathPrev.get(v);
        }
        path.addFirst(v);
        return path;
    }
}
