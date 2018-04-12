import algorithms.SSSP.Dijkstra;
import org.junit.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class test_SSSP_Dijkstra {

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
    public void test_dist() {
        SelfDualGraph g = readGraph("./input_data/test_graph_0.txt");
        Dijkstra sssp = new Dijkstra(g);
        sssp.findSSSP(findVertexByID(g.getVertices(), 0));
        double[] dist = new double[]{0, 1, 1.5, 2, 1.5, 1};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(dist[v.getID()], v.getDistance(), 0.001);
        }

        sssp.findSSSP(findVertexByID(g.getVertices(), 4));
        dist = new double[]{1.5, 2.5, 1.5, 2, 0, 0.5};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(dist[v.getID()], v.getDistance(), 0.001);
        }
    }

    @Test
    public void test_path() {
        SelfDualGraph g = readGraph("./input_data/test_graph_0.txt");
        Dijkstra sssp = new Dijkstra(g);
        Vertex v0 = findVertexByID(g.getVertices(), 0);
        sssp.findSSSP(v0);
        List<Vertex> path = sssp.getPath(v0, findVertexByID(g.getVertices(), 3));
        int[] ids = new int[]{0, 2, 3};
        for (int i = 0; i < path.size(); i++) {
            Assert.assertEquals(ids[i], path.get(i).getID());
        }
    }

    @Test
    public void test_grid() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        Dijkstra sssp = new Dijkstra(g);
        Vertex v0 = findVertexByID(g.getVertices(), 0);
        sssp.findSSSP(v0);
        double[] dist = new double[]{0, 1, 2, 3, 1, 2, 3, 4, 2, 3, 4, 5, 3, 4, 5, 6};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(dist[v.getID()], v.getDistance(), 0.001);
        }
    }
}
