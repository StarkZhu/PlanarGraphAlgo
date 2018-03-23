import algorithms.RDivision.FredDivider;
import org.junit.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class test_RDivision_NlogR {
    public SelfDualGraph readGraph(String fileName) {
        SelfDualGraph g = new SelfDualGraph();
        try {
            g.buildGraph(fileName);
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
        Dart.uniqueID = 0;
        Vertex.uniqueID = 0;
        return g;
    }

    protected Vertex findVertexByID(Set<Vertex> vertices, int id) {
        for (Vertex v : vertices) {
            if (v.getID() == id) return v;
        }
        return null;
    }

    @Test
    public void test_rhoClustering8() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        FredDivider rd = new FredDivider(g);
        Map<Vertex, Set<Vertex>> vertexToCluster = rd.rhoClustering(8);
        int[] expectedCluster = new int[]{0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1};
        int[] actualCluster = new int[16];
        for (Set<Vertex> set : vertexToCluster.values()){
            if (set.contains(findVertexByID(g.getVertices(), 4))) {
                for (Vertex v : set) actualCluster[v.getID()] = 1;
            }
        }
        for (int i=0; i< expectedCluster.length; i++) {
            Assert.assertEquals(expectedCluster[i], actualCluster[i]);
        }
    }
    @Test
    public void test_rhoClustering5() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        FredDivider rd = new FredDivider(g);
        Map<Vertex, Set<Vertex>> vertexToCluster = rd.rhoClustering(5);
        int[] expectedCluster = new int[]{0, 0, 1, 1, 2, 0, 0, 1, 2, 1, 1, 1, 2, 2, 2, 1};
        int[] actualCluster = new int[16];
        for (Set<Vertex> set : vertexToCluster.values()){
            if (set.contains(findVertexByID(g.getVertices(), 4))) {
                for (Vertex v : set) actualCluster[v.getID()] = 2;
            } else if (set.contains(findVertexByID(g.getVertices(), 2))) {
                for (Vertex v : set) actualCluster[v.getID()] = 1;
            }
        }
        for (int i=0; i< expectedCluster.length; i++) {
            Assert.assertEquals(expectedCluster[i], actualCluster[i]);
        }
    }
}
