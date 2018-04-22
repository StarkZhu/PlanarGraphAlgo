import algorithms.RDivision.*;
import org.junit.*;
import selfdualgraph.*;
import util.RandomSubgraphGenerator;

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

    protected void generateLargeTextFile() throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/random/0.txt");
        RandomSubgraphGenerator rsg = new RandomSubgraphGenerator(g);
        rsg.generateRandomGraph(3);
        g.saveToFile(String.format("./test/large_rnd.txt"));
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
    public void test_rhoClustering_9x7() {
        SelfDualGraph g = readGraph("./test/grid_9x7.txt");
        FredDivider rd = new FredDivider(g);
        Map<Vertex, Set<Vertex>> vertexToCluster = rd.rhoClustering(20);
        Assert.assertEquals(g.getVertexNum(), vertexToCluster.size());

        Set<Vertex> visited = new HashSet<>();
        List<Vertex> overlap = new LinkedList<>();
        for (Set<Vertex> set : new HashSet<>(vertexToCluster.values())) {
            for (Vertex v : set) {
                if (!visited.contains(v)) visited.add(v);
                else overlap.add(v);
            }
        }
        Assert.assertEquals(0, overlap.size());
        Assert.assertEquals(g.getVertexNum(), visited.size());
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
            SelfDualGraph subgraph = (rd.expandRegion(region)).iterator().next();
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
    public void test_contract_expand() throws FileNotFoundException {
        generateLargeTextFile();
        SelfDualGraph g = readGraph("./test/large_rnd.txt");
        FredDivider fd = new FredDivider(g);
        int rho = (int) Math.sqrt(g.getVertexNum());
        Map<Vertex, Set<Vertex>> vertexToCluster = fd.rhoClustering(rho);
        SelfDualGraph contracted = fd.contractedGraph(new HashSet<>(vertexToCluster.values()));
        Set<SelfDualGraph> expandedSubgraphs = fd.expandRegion(contracted.getVertices());
        Assert.assertEquals(1, expandedSubgraphs.size());
        SelfDualGraph expandedG = expandedSubgraphs.iterator().next();

        Assert.assertEquals(g.getVertexNum(), expandedG.getVertexNum());
        Assert.assertEquals(g.getFaceNum(), expandedG.getFaceNum());
        List<Vertex> v0 = new ArrayList<>(g.getVertices());
        List<Vertex> v1 = new ArrayList<>(expandedG.getVertices());
        Collections.sort(v0);
        Collections.sort(v1);
        for (int i=0; i<v0.size(); i++) {
            Vertex vv0 = v0.get(i);
            Vertex vv1 = v1.get(i);
            Assert.assertEquals(vv0.getID(), vv1.getID());
            Assert.assertEquals(vv0.getDegree(), vv1.getDegree());
            List<Dart> list0 = vv0.getIncidenceList();
            List<Dart> list1 = vv1.getIncidenceList();
            Iterator<Dart> it0 = list0.iterator();
            Iterator<Dart> it1 = list1.iterator();
            while (it0.hasNext()) {
                Dart d0 = it0.next();
                Dart d1 = it1.next();
                Assert.assertEquals(d0.getID(), d1.getID());
                Assert.assertEquals(d0.getHead().getID(), d1.getHead().getID());
                Assert.assertEquals(d0.getReverse().getID(), d1.getReverse().getID());
                Assert.assertEquals(d0.getRight().getID(), d1.getRight().getID());
                Assert.assertEquals(d0.getLeft().getID(), d1.getLeft().getID());
                Assert.assertEquals(d0.getNext().getID(), d1.getNext().getID());
                Assert.assertEquals(d0.getPrev().getID(), d1.getPrev().getID());
                Assert.assertEquals(d0.getPredecessor().getID(), d1.getPredecessor().getID());
                Assert.assertEquals(d0.getNext().getID(), d1.getNext().getID());
            }
        }

        File tmpFile = new File("./test/large_rnd.txt");
        tmpFile.delete();
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
        int boundarySize = checkRDivisionResult(g, r, regions);
        Assert.assertEquals(0, boundarySize);

        r = 16;
        fd = new FredDivider(g);
        regions = fd.rDivision(r);
        Assert.assertEquals(1, regions.size());
        Assert.assertEquals(16, regions.iterator().next().size());
    }

    @Test
    public void test_9x7_r20() {
        SelfDualGraph g = readGraph("./test/grid_9x7.txt");
        FredDivider fd = new FredDivider(g);

        int r = 20;
        long time0 = System.currentTimeMillis();
        Set<Set<Vertex>> regions = fd.rDivision(r);
        long time1 = System.currentTimeMillis();
        System.out.printf("Time: [%dms]\n", time1 - time0);

        int boundarySize = checkRDivisionResult(g, r, regions);
        Assert.assertEquals(0, boundarySize);
    }
}
