import algorithms.RDivision.*;
import algorithms.RootFinder.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class test_RDivision_NlogN {
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

    public Set<Vertex> createBoundary(SelfDualGraph g, int[] IDs) {
        Set<Integer> vID = new HashSet<>();
        for (int i : IDs) vID.add(i);
        Set<Vertex> boundary = new HashSet<>();
        for (Vertex v : g.getVertices()) {
            if (vID.contains(v.getID())) boundary.add(v);
        }
        return boundary;
    }

    public void verifyVerticeID(SelfDualGraph graph, int[] ids) {
        verifyVerticeID(graph.getVertices(), ids);
    }

    public void verifyVerticeID(Set<Vertex> vertices, int[] ids) {
        Set<Integer> IDs = new HashSet<>();
        for (int i : ids) IDs.add(i);
        Assert.assertEquals(IDs.size(), vertices.size());
        for (Vertex v : vertices) {
            Assert.assertTrue(IDs.contains(v.getID()));
        }
    }

    public void verifyNewFace(SelfDualGraph graph, int[] incidentVertexID) {
        Vertex face = null;
        for (Vertex f : graph.getFaces()) if (f.getDegree() > 3) face = f;
        Assert.assertEquals(incidentVertexID.length, face.getDegree());
        int i = 0;
        for (Dart d : face.getIncidenceList()) {
            Assert.assertEquals(incidentVertexID[i++], d.getTail().getID());
        }
    }

    // TODO: hase I; phase II, integration test

    @Test
    public void test_phaseI_r14() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        RecursiveDivider rd = new RecursiveDivider(g);

        // phase I
        rd.phaseI(g, 14);
        Queue<SelfDualGraph> subgraphs = rd.getSubgraphsAfterPhaseI();
        Assert.assertEquals(2, subgraphs.size());
        while (!subgraphs.isEmpty()) {
            SelfDualGraph sub1 = subgraphs.poll();
            Assert.assertEquals(13, sub1.getVertexNum());
            Assert.assertEquals(15, sub1.getFaceNum());
            RootFinder rf = new SpecificIdRootFinder(0);
            try {
                rf.selectRootVertex(sub1);
                verifyVerticeID(sub1, new int[]{0, 3, 12, 1, 2, 4, 5, 7, 8, 11, 13, 14, 15});
                verifyNewFace(sub1, new int[]{1, 2, 7, 11, 15, 14, 13, 8, 4, 5});
            } catch (RuntimeException e) {
                verifyVerticeID(sub1, new int[]{6, 9, 10, 1, 2, 4, 5, 7, 8, 11, 13, 14, 15});
                verifyNewFace(sub1, new int[]{2, 1, 5, 4, 8, 13, 14, 15, 11, 7});
            }
        }
    }


    @Test
    public void test_phaseI_r10() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        RecursiveDivider rd = new RecursiveDivider(g);
        rd.phaseI(g, 10);

        List<SelfDualGraph> subgraphs = new ArrayList<>(rd.getSubgraphsAfterPhaseI());
        Assert.assertEquals(5, subgraphs.size());

        Collections.sort(subgraphs, new Comparator<SelfDualGraph>() {
            @Override
            public int compare(SelfDualGraph o1, SelfDualGraph o2) {
                if (o1.getVertexNum() != o2.getVertexNum()) return o1.getVertexNum() - o2.getVertexNum();
                int v1 = 0;
                for (Vertex v : o1.getVertices()) v1 += v.getID();
                int v2 = 0;
                for (Vertex v : o2.getVertices()) v2 += v.getID();
                return v1 - v2;
            }
        });

        int[][] subGVertices = new int[][]{
                {1, 2, 4, 5, 6, 7, 11},
                {2, 4, 8, 11, 13, 14, 15},
                {0, 1, 2, 3, 7, 11, 14, 15},
                {0, 1, 4, 5, 8, 12, 13, 14},
                {4, 5, 6, 8, 9, 10, 11, 13, 14, 15}};
        int[][] nonBoundaryVID = new int[][]{{}, {}, {3}, {12}, {9, 10}};
        for (int i = 0; i < 5; i++) {
            SelfDualGraph sub1 = subgraphs.get(i);
            verifyVerticeID(sub1, subGVertices[i]);
            Set<Vertex> nonBV = sub1.getVertices();
            nonBV.removeAll(sub1.getBoundary());
            Assert.assertEquals(nonBoundaryVID[i].length, nonBV.size());
            Set<Integer> vIDs = new HashSet<>();
            for (int j : nonBoundaryVID[i]) vIDs.add(j);
            for (Vertex v : nonBV) Assert.assertTrue(vIDs.contains(v.getID()));
        }
    }

    @Test
    public void test_phaseII_r14() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        RecursiveDivider rd = new RecursiveDivider(g);
        rd.phaseI(g, 14);

        // phase II, r=14
        rd.phaseII(14);
        Set<Set<Vertex>> regions = rd.getRegions();
        Assert.assertEquals(2, regions.size());
        RootFinder rf = new SpecificIdRootFinder(3);
        Vertex v3 = rf.selectRootVertex(g);
        for (Set<Vertex> region : regions) {
            if (region.contains(v3)) {
                verifyVerticeID(region, new int[]{0, 3, 12, 1, 2, 4, 5, 7, 8, 11, 13, 14, 15});
            } else {
                verifyVerticeID(region, new int[]{6, 9, 10, 1, 2, 4, 5, 7, 8, 11, 13, 14, 15});
            }
        }
    }

    @Test
    public void test_phaseII_r6() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        RecursiveDivider rd = new RecursiveDivider(g);
        rd.phaseI(g, 14);

        // phase II, r=14
        rd.phaseII(6);
        List<Set<Vertex>> regions = new ArrayList<>(rd.getRegions());
        Assert.assertEquals(5, regions.size());

        Collections.sort(regions, new Comparator<Set<Vertex>>() {
            @Override
            public int compare(Set<Vertex> o1, Set<Vertex> o2) {
                if (o1.size() != o2.size()) return o1.size() - o2.size();
                int v1 = 0;
                for (Vertex v : o1) v1 += v.getID();
                int v2 = 0;
                for (Vertex v : o2) v2 += v.getID();
                return v1 - v2;
            }
        });

        int[][] subGVertices = new int[][]{
                {1, 2, 4, 5, 6, 7, 11},
                {2, 4, 8, 11, 13, 14, 15},
                {0, 1, 2, 3, 7, 11, 14, 15},
                {0, 1, 4, 5, 8, 12, 13, 14},
                {4, 5, 6, 8, 9, 10, 11, 13, 14, 15}};

        for (int i = 0; i < 5; i++) {
            verifyVerticeID(regions.get(i), subGVertices[i]);
        }
    }

    @Test
    public void test_9x7_r15() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        //SelfDualGraph g = readGraph("./input_data/random/5.txt");
        RecursiveDivider rd = new RecursiveDivider(g);

        int r = 20;
        long time0 = System.currentTimeMillis();
        Set<Set<Vertex>> regions = rd.rDivision(r);
        long time1 = System.currentTimeMillis();
        System.out.printf("Time: [%dms]\n", time1 - time0);

        Set<Vertex> vertices = g.getVertices();
        Set<Vertex> visited = new HashSet<>();
        Set<Vertex> boundaries = new HashSet<>();
        // check vertex num
        for (Set<Vertex> region : regions) {
            Assert.assertTrue(region.size() <= r);
            for (Vertex v : region) {
                Assert.assertTrue(vertices.contains(v));
                if (visited.contains(v)) boundaries.add(v);
                Assert.assertEquals(1, v.getWeight(), 0.001);
                visited.add(v);
            }
        }
        Assert.assertEquals(g.getVertexNum(), visited.size());

        // check boundary size for each region
        for (Set<Vertex> region : regions) {
            int boundarySize = 0;
            for (Vertex v : region) {
                if (boundaries.contains(v)) boundarySize++;
            }
            Assert.assertTrue(boundarySize <= 4 * Math.sqrt(r));
        }
    }

}
