package util;

import selfdualgraph.Dart;
import selfdualgraph.SelfDualGraph;
import selfdualgraph.Vertex;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SphereGenerator {
    private SelfDualGraph g;

    public SphereGenerator(SelfDualGraph g) {
        this.g = g;
    }

    /**
     * the same G is modified
     * to get a different graph, G need to be reloaded from text file, since it does not support deep copy yet
     *
     * @param iterations 1 ~ 15
     * @return
     */
    public void generateRandomSubgraph(int iterations) {
        for (int i = 0; i < iterations; i++) {
            Set<Vertex> faces = g.getFaces();
            for (Vertex face : faces) {
                g.addVertex(face);
            }
        }
        g.renumberIDs();
    }

    public static void main(String[] args) throws FileNotFoundException {
        for (int i = 8; i < 11; i++) {
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph("./input_data/sphere/c_0.txt");
            System.out.println(g.getFaceNum());
            System.out.println(g.getVertexNum());

            SphereGenerator rsg = new SphereGenerator(g);
            rsg.generateRandomSubgraph(i);
            g.saveToFile(String.format("./input_data/sphere/c_%d.txt", i));
        }
    }
}
