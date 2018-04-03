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


        if (graph.getVertexNum() == 823) {
            int visitedV = 0, visitedF = 0, visitedD = 0, totalD = 0;
            for (Vertex f : graph.getFaces()) if (f.isVisited()) visitedF++;
            for (Vertex v : graph.getVertices()) {
                if (v.isVisited()) visitedV++;
                for (Dart d : v.getIncidenceList()) {
                    if (d.isVisited()) visitedD++;
                    totalD++;
                }
            }
            System.out.println(" ");
        }


        Separator sp = new SimpleCycleSeparator(graph);
        Set<Vertex> separator = sp.findSeparator();
        Set<Vertex>[] subgraphs = sp.findSubgraphs();
        SelfDualGraph g1 = graph.buildSubgraph(subgraphs[0], separator);

        int visitedV1 = 0, visitedF1 = 0, visitedD1 = 0, totalD1 = 0;
        for (Vertex f : g1.getFaces()) if (f.isVisited()) visitedF1++;
        for (Vertex v : g1.getVertices()) {
            if (v.isVisited()) visitedV1++;
            for (Dart d : v.getIncidenceList()) {
                if (d.isVisited()) visitedD1++;
                totalD1++;
            }
        }

        SelfDualGraph g2 = graph.buildSubgraph(subgraphs[1], separator);


        int visitedV2 = 0, visitedF2 = 0, visitedD2 = 0, totalD2 = 0;
        for (Vertex f : g2.getFaces()) if (f.isVisited()) visitedF2++;
        for (Vertex v : g2.getVertices()) {
            if (v.isVisited()) visitedV2++;
            for (Dart d : v.getIncidenceList()) {
                if (d.isVisited()) visitedD2++;
                totalD2++;
            }
        }
        System.out.println(" ");


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


    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/random/3.txt");

        RecursiveDivider rd = new RecursiveDivider(g);
        int r = (int) (Math.log(g.getVertexNum()) / Math.log(2));
        System.out.printf("r = %d\n", r);
        long time0 = System.currentTimeMillis();
        Set<Set<Vertex>> regions = rd.rDivision(r);
        long time1 = System.currentTimeMillis();
        System.out.printf("Time: [%dms]\n", time1 - time0);
    }

}
