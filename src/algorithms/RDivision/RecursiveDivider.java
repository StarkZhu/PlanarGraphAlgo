package algorithms.RDivision;

import algorithms.Separator.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
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
        g.flatten();
        phaseI(g, r);
        phaseII(r);
        return regions;
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

    public Queue<SelfDualGraph> getSubgraphsAfterPhaseI() {
        return subgraphs;
    }

    public void phaseII(int r) {
        while (!subgraphs.isEmpty()) {
            SelfDualGraph subgraph = subgraphs.poll();
            // if boundary size less than 4*sqrt(r)
            if (subgraph.getBoundarySize() <= Math.max(4 * Math.sqrt(r), 6)) {
                regions.add(g.getVerticesFromID(verticesToID(subgraph.getVertices())));
            } else {
                subgraph.triangulate();
                subgraph.assignWeightToBoundary_useDart();
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


    /*
    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/random/4.txt");
        long time0 = System.currentTimeMillis();
        long count = 0;
        for (Vertex v1 : g.getVertices()) {
            for (Vertex v2 : g.getVertices())
                count++;
        }
        long time1 = System.currentTimeMillis();
        System.out.printf("N^2 Counting Time: [%dms]\n", time1 - time0);
        System.out.println(count);

        time0 = System.currentTimeMillis();
        List<Vertex> list = new LinkedList<>(g.getVertices());
        Collections.sort(list, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return o1.getID() - o2.getID();
            }
        });
        time1 = System.currentTimeMillis();
        System.out.printf("Sorting Time: [%dms]\n", time1 - time0);
    }
    */
}
