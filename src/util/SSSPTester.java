package util;

import algorithms.RDivision.*;
import algorithms.RootFinder.*;
import algorithms.SSSP.*;
import algorithms.Separator.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class SSSPTester {

    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/random/0.txt");
        RandomSubgraphGenerator rsg1 = new RandomSubgraphGenerator(g);
        rsg1.generateRandomGraph(5);
        System.out.println("Graph loaded");

        Vertex src = g.getVertices().iterator().next();

        SSSP sssp = new RegionalSpeculativeDijkstra(g, new FredDivider(g),SSSP.CAPACITY_AS_DISTANCE);
        long startTime = System.nanoTime();
        sssp.findSSSP(src, 1000);
        long endTime = System.nanoTime();
        String str = String.format("\t%.2f", (endTime - startTime) / 1000000.0);
        System.out.println(str);

        sssp = new Dijkstra(g, SSSP.CAPACITY_AS_DISTANCE);
        startTime = System.nanoTime();
        sssp.findSSSP(src, 1000);
        endTime = System.nanoTime();
        str = String.format("\t%.2f", (endTime - startTime) / 1000000.0);
        System.out.println(str);
    }
}
