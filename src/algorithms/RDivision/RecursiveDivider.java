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
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        SelfDualGraph g1 = graph.buildSubgraph(subgraphs[0]);
        SelfDualGraph g2 = graph.buildSubgraph(subgraphs[1]);

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
                regions.add(originG.getVerticesFromID(verticesToID(subgraph.getVertices())));
            } else {
                subgraph.triangulate();
                subgraph.assignWeightToBoundary_useDart();
                Separator sp = new SimpleCycleSeparator(subgraph);
                Set<Vertex> separator = sp.findSeparator();
                Set<Vertex>[] subs = sp.findSubgraphs();
                SelfDualGraph g1 = subgraph.buildSubgraph(subs[0]);
                SelfDualGraph g2 = subgraph.buildSubgraph(subs[1]);
                subgraphs.add(g1);
                subgraphs.add(g2);
            }
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/random/5.txt");

        RecursiveDivider rd = new RecursiveDivider(g);
        int r = Math.max(10, (int) (Math.pow(Math.log(g.getVertexNum()) / Math.log(2), 3)));
        System.out.printf("r = %d\n", r);
        long time0 = System.currentTimeMillis();
        Set<Set<Vertex>> regions = rd.rDivision(r);
        long time1 = System.currentTimeMillis();
        System.out.println(regions.size());
        System.out.printf("Time: [%dms]\n", time1 - time0);
    }

}
