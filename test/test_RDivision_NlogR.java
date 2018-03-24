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

    protected Set<Vertex> findVertexSetByID(Set<Vertex> vertices, int[] ids) {
        Set<Integer> targets = new HashSet<>();
        Set<Vertex> ans = new HashSet<>();
        for (int i : ids) targets.add(i);
        for (Vertex v : vertices) {
            if (targets.contains(v.getID())) ans.add(v);
        }
        return ans;
    }

    @Test
    public void test_rhoClustering8() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        FredDivider rd = new FredDivider(g);
        Map<Vertex, Set<Vertex>> vertexToCluster = rd.rhoClustering(8);
        int[] expectedCluster = new int[]{0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1};
        int[] actualCluster = new int[16];
        for (Set<Vertex> set : vertexToCluster.values()) {
            if (set.contains(findVertexByID(g.getVertices(), 4))) {
                for (Vertex v : set) actualCluster[v.getID()] = 1;
            }
        }
        for (int i = 0; i < expectedCluster.length; i++) {
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
        for (Set<Vertex> set : vertexToCluster.values()) {
            if (set.contains(findVertexByID(g.getVertices(), 4))) {
                for (Vertex v : set) actualCluster[v.getID()] = 2;
            } else if (set.contains(findVertexByID(g.getVertices(), 2))) {
                for (Vertex v : set) actualCluster[v.getID()] = 1;
            }
        }
        for (int i = 0; i < expectedCluster.length; i++) {
            Assert.assertEquals(expectedCluster[i], actualCluster[i]);
        }
    }

    @Test
    public void test_contractedGraph() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        Set<Set<Vertex>> clusters = new HashSet<>();
        int[][] clusterID = new int[][]{{0, 1, 4, 5}, {2, 3, 6, 7}, {8, 9, 12, 13}, {10, 11, 14, 15}};
        for (int[] ids : clusterID) {
            clusters.add(findVertexSetByID(g.getVertices(), ids));
        }
        FredDivider rd = new FredDivider(g);
        SelfDualGraph contracted = rd.contractedGraph(clusters);
        Assert.assertEquals(4, contracted.getVertexNum());
        Assert.assertEquals(4, contracted.getFaceNum());
        List<Vertex> vertices = new LinkedList<>(g.getVertices());
        Collections.sort(vertices, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return o1.getID() - o2.getID();
            }
        });
        int[] vDegree = new int[]{};
        for (Vertex v : vertices) {
            // TODO:  add tests
        }
    }
}
