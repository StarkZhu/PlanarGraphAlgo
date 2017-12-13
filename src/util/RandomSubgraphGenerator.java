package util;

import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class RandomSubgraphGenerator {
    private SelfDualGraph g;

    public RandomSubgraphGenerator(SelfDualGraph g) {
        this.g = g;
    }

    /**
     * the same G is modified
     * to get a different graph, G need to be reloaded from text file, since it does not support deep copy yet
     * @param modifyRatio 0 ~ 1.0, probability of deleting/contracting each edge
     * @return
     */
    public void generateRandomSubgraph(double modifyRatio) {
        Set<Dart> darts = new HashSet<>();
        for (Vertex v : g.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                if (!darts.contains(d) && !darts.contains(d.getReverse())) {
                    darts.add(d);
                }
            }
        }
        Random r = new Random();
        for (Dart d : darts) {
            if (r.nextDouble() < modifyRatio) {
                if (d.getHead() == d.getTail()) {
                    g.deleteEdge(d);
                }
                else if (d.getLeft() == d.getRight()) {
                    g.contractEdge(d);
                } else {
                    if (r.nextDouble() < 0.5) {
                        g.contractEdge(d);
                    } else {
                        g.deleteEdge(d);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        for (int i=0; i<6;i++) {
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph("./input_data/grids/5.txt");
            System.out.println(g.getFaceNum());
            System.out.println(g.getVertexNum());

            RandomSubgraphGenerator rsg = new RandomSubgraphGenerator(g);
            rsg.generateRandomSubgraph(0.01 * (1 << i));
            g.saveToFile(String.format("./input_data/random/%d.txt", (1 << i)));
        }
    }
}
