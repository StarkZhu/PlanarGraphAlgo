import algorithms.RDivision.FredDivider;
import org.junit.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class test_RDivision_NlogR extends test_RDivision_NlogN {

    protected Vertex findVertexByID(Set<Vertex> vertices, int id) {
        for (Vertex v : vertices) {
            if (v.getID() == id) return v;
        }
        return null;
    }

    protected Set<Vertex> findVertexSetByID(Set<Vertex> vertices, int[] ids) {
        Set<Integer> targets = new HashSet<>();
        Set<Vertex> ans = new TreeSet<>();
        for (int i : ids) targets.add(i);
        for (Vertex v : vertices) {
            if (targets.contains(v.getID())) ans.add(v);
        }
        return ans;
    }

    protected void verifyVertexIncidenceList(int[] orbit, Vertex v) {
        Assert.assertEquals(orbit.length, v.getDegree());
        int i = 0;
        while (i < orbit.length && orbit[i] != v.getFirstDart().getHead().getID()) i++;
        if (i == orbit.length) {
            Assert.assertTrue(false);
        }
        Dart d = v.getFirstDart();
        for (int j = 0; j < orbit.length; j++) {
            Assert.assertEquals(orbit[(i + j) % orbit.length], d.getHead().getID());
            d = d.getSuccessor();
        }
        Assert.assertEquals(orbit[i], d.getHead().getID());
    }

    @Test
    public void test_rhoClustering8() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        FredDivider rd = new FredDivider(g);
        Map<Vertex, Set<Vertex>> vertexToCluster = rd.rhoClustering(8);
        int[] expectedCluster = new int[]{0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1};
        int[] actualCluster = new int[16];
        for (Set<Vertex> set : vertexToCluster.values()) {
            if (set.contains(findVertexByID(g.getVertices(), 2))) {
                for (Vertex v : set) actualCluster[v.getID()] = 1;
            }
        }
        for (int i = 0; i < expectedCluster.length; i++) {
            Assert.assertEquals(expectedCluster[i], actualCluster[i]);
        }
    }

    @Test
    public void test_rhoClustering6() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        FredDivider rd = new FredDivider(g);
        Map<Vertex, Set<Vertex>> vertexToCluster = rd.rhoClustering(6);
        int[] expectedCluster = new int[]{0, 1, 2, 2, 0, 1, 2, 2, 0, 1, 1, 2, 0, 1, 1, 2};
        int[] actualCluster = new int[16];
        for (Set<Vertex> set : vertexToCluster.values()) {
            if (set.contains(findVertexByID(g.getVertices(), 1))) {
                for (Vertex v : set) actualCluster[v.getID()] = 1;
            } else if (set.contains(findVertexByID(g.getVertices(), 2))) {
                for (Vertex v : set) actualCluster[v.getID()] = 2;
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
        List<Vertex> vertices = new ArrayList<>(contracted.getVertices());
        Collections.sort(vertices, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return o1.getID() - o2.getID();
            }
        });
        int[][] vOrbit = new int[][]{{6, 10, 8, 10}, {0, 10}, {0, 10}, {0, 8, 0, 6}};
        for (int i = 0; i < vertices.size(); i++) {
            verifyVertexIncidenceList(vOrbit[i], vertices.get(i));
        }
    }

    @Test
    public void test_expandRegion() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        Set<Set<Vertex>> clusters = new HashSet<>();
        int[][] clusterID = new int[][]{{0, 1, 4, 5}, {2, 3, 6, 7}, {8, 9, 12, 13}, {10, 11, 14, 15}};
        for (int[] ids : clusterID) {
            clusters.add(findVertexSetByID(g.getVertices(), ids));
        }
        FredDivider rd = new FredDivider(g);

        SelfDualGraph contracted = rd.contractedGraph(clusters);
        List<Vertex> vertices = new ArrayList<>(contracted.getVertices());
        Collections.sort(vertices, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return o1.getID() - o2.getID();
            }
        });

        for (int i = 0; i < vertices.size(); i++) {
            Set<Vertex> region = new HashSet<>();
            region.add(vertices.get(i));
            SelfDualGraph subgraph = rd.expandRegion(region);
            Assert.assertEquals(4, subgraph.getVertexNum());
            Assert.assertEquals(3, subgraph.getFaceNum());
            Assert.assertEquals(4, subgraph.getBoundarySize());
            Set<Integer> vIDs = new HashSet<>();
            for (int j : clusterID[i]) vIDs.add(j);
            for (Vertex v : subgraph.getVertices()) {
                Assert.assertTrue(vIDs.contains(v.getID()));
            }
        }
    }

    @Test
    public void test_rDivision_4x4() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        int r = 6;
        FredDivider fd = new FredDivider(g);
        long time0 = System.currentTimeMillis();
        Set<Set<Vertex>> regions = fd.rDivision(r);
        long time1 = System.currentTimeMillis();
        System.out.printf("Time: [%dms]\n", time1 - time0);

        checkRDivisionResult(g, r, regions);
        // TODO: more specific test
    }
}
