import algorithms.RDivision.*;
import algorithms.SSSP.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class test_SSSP_RSD extends test_SSSP_Dijkstra {

    public void verifyRegionDartID(Set<Region> regions, int[] ids) {
        Set<Integer> darts = new HashSet<>();
        for (int i : ids) darts.add(i);
        Assert.assertEquals(darts.size(), regions.size());
        for (Region r: regions) {
            Dart d = r.getDart();
            Assert.assertTrue(darts.contains(d.getID()));
        }
    }

    @Test
    public void test_regionTree() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        GraphDivider gd = new RecursiveDivider(g.buildSubgraph(g.getVertices()));
        RegionalSpeculativeDijkstra rsd = new RegionalSpeculativeDijkstra(g, gd);
        Set<Set<Vertex>> divisions = gd.rDivision(13);
        Set<Set<Vertex>> originalVertices = new HashSet<>();
        for (Set<Vertex> division : divisions) {
            originalVertices.add(g.getVerticesFromID(gd.verticesToID(division)));
        }
        Region rG = rsd.buildRegionTree(g, originalVertices);

        List<Region> level1 = new LinkedList<>(rG.getAllSubregion());
        Assert.assertEquals(2, level1.size());
        Collections.sort(level1, new Comparator<Region>() {
            @Override
            public int compare(Region o1, Region o2) {
                return o1.getAllSubregion().size() - o2.getAllSubregion().size();
            }
        });

        int[][] dartIDs = new int[][]{
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, 14, 15, 20, 21, 26, 27, 34, 35, 40, 41, 42, 43, 44, 45, 46, 47},
                {2, 3, 8, 9, 10, 11, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 36, 37, 38, 39, 40, 41, 44, 45, 46, 47}
        };
        for (int i = 0; i < 2; i++) {
            Set<Region> sub = level1.get(i).getAllSubregion();
            verifyRegionDartID(sub, dartIDs[i]);
        }
    }

    @Test
    public void test_dist_grid_weight() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        int r = 13;
        GraphDivider gd = new RecursiveDivider(g.buildSubgraph(g.getVertices()));
        RegionalSpeculativeDijkstra rsd = new RegionalSpeculativeDijkstra(g, gd, SSSP.WEIGHT_AS_DISTANCE);
        Vertex src = findVertexByID(g.getVertices(), 0);
        rsd.findSSSP(src, r);
        double[] dist = new double[]{0, 1, 2, 3, 1, 2, 3, 4, 2, 3, 4, 5, 3, 4, 5, 6};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(dist[v.getID()], v.getDistance(), 0.001);
        }
    }

    @Test
    public void test_grid_capacity() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        int r = 13;
        GraphDivider gd = new RecursiveDivider(g.buildSubgraph(g.getVertices()));
        RegionalSpeculativeDijkstra rsd = new RegionalSpeculativeDijkstra(g, gd, SSSP.CAPACITY_AS_DISTANCE);
        Vertex src = findVertexByID(g.getVertices(), 0);
        rsd.findSSSP(src, r);
        double[] dist = new double[]{0, 28, 70, 151, 13, 74, 135, 142, 145, 290, 298, 247, 344, 340, 350, 352};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(dist[v.getID()], v.getDistance(), 0.001);
        }
        List<Vertex> path = rsd.getPath(src, findVertexByID(g.getVertices(), 15));
        int[] pathID = new int[]{0, 4, 5, 6, 10, 14, 15};
        for (int i=0; i<path.size(); i++) {
            Assert.assertEquals(pathID[i], path.get(i).getID());
        }
    }

    @Test
    public void test_dist_g0() {
        SelfDualGraph g = readGraph("./input_data/test_graph_0.txt");
        int r = 10;
        GraphDivider gd = new RecursiveDivider(g.buildSubgraph(g.getVertices()));
        RegionalSpeculativeDijkstra rsd = new RegionalSpeculativeDijkstra(g, gd);

        rsd.findSSSP(findVertexByID(g.getVertices(), 0), r);
        double[] dist = new double[]{0, 1, 1.5, 2, 1.5, 1};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(dist[v.getID()], v.getDistance(), 0.001);
        }

        rsd.findSSSP(findVertexByID(g.getVertices(), 4), r);
        dist = new double[]{1.5, 2.5, 1.5, 2, 0, 0.5};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(dist[v.getID()], v.getDistance(), 0.001);
        }
    }

    @Test
    public void test_dist_compareDijk() throws FileNotFoundException {
        String fileName = "./test/large_rnd.txt";
        generateLargeTextFile(fileName);
        SelfDualGraph g = readGraph(fileName);

        Dijkstra sssp = new Dijkstra(g);
        sssp.findSSSP(findVertexByID(g.getVertices(), 0));
        Map<Integer, Double> vDist = new HashMap<>();
        for (Vertex v : g.getVertices()) vDist.put(v.getID(), v.getDistance());

        g = readGraph(fileName);
        int r = 100;
        GraphDivider gd = new RecursiveDivider(g);
        RegionalSpeculativeDijkstra rsd = new RegionalSpeculativeDijkstra(g, gd);
        rsd.findSSSP(findVertexByID(g.getVertices(), 0), r);
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(vDist.get(v.getID()), v.getDistance(), 0.00001);
        }
        File tmpFile = new File(fileName);
        tmpFile.delete();
    }

    /*
    @Test
    public void test_path_compareDijk() {
        SelfDualGraph g = readGraph("./test/grid_9x7.txt");
        SSSP sssp = new Dijkstra(g);
        Vertex v0 = findVertexByID(g.getVertices(), 0);
        Vertex v62 = findVertexByID(g.getVertices(), 62);
        sssp.findSSSP(v0);
        List<Vertex> path_dijk = sssp.getPath(v0, v62);

        sssp = new RegionalSpeculativeDijkstra(g, new RecursiveDivider(g));
        List<Vertex> path_rsd = sssp.getPath(v0, v62);

        for (int i = 0; i < path_dijk.size(); i++) {
            Assert.assertEquals(path_dijk.get(i).getID(), path_rsd.get(i).getID());
        }
    }
    */
}
