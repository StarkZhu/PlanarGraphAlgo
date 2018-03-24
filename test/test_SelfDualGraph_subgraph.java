import algorithms.RootFinder.SpecificIdRootFinder;
import org.junit.*;
import selfdualgraph.*;

import java.util.*;

public class test_SelfDualGraph_subgraph extends test_SelfDualGraph_modification {

    public void verifyVertexSet(int[] expectedVerticies, Set<Vertex> vertices) {
        Assert.assertEquals(expectedVerticies.length, vertices.size());
        Set<Integer> expectedID = new HashSet<>();
        for (int i : expectedVerticies) expectedID.add(i);
        for (Vertex v : vertices) {
            Assert.assertTrue(expectedID.contains(v.getID()));
        }
    }

    @Test
    public void testFindBoundary_1() {
        Set<Vertex> subg = findVertexSetByIDs(g.getVertices(), new int[]{0, 1, 2, 3, 4});
        Set<Vertex> boundary = findVertexSetByIDs(g.getVertices(), new int[]{0, 2, 3, 4, 5});
        Vertex src = (new SpecificIdRootFinder(1)).selectRootVertex(g);
        Set<Vertex> ans = g.findBoundary(src, subg, boundary);
        verifyVertexSet(new int[]{0, 3}, ans);
    }

    @Test
    public void testFindBoundary_5() {
        Set<Vertex> subg = findVertexSetByIDs(g.getVertices(), new int[]{0, 2, 3, 4, 5});
        Set<Vertex> boundary = findVertexSetByIDs(g.getVertices(), new int[]{0, 2, 3, 4});
        Vertex src = (new SpecificIdRootFinder(5)).selectRootVertex(g);
        Set<Vertex> ans = g.findBoundary(src, subg, boundary);
        verifyVertexSet(new int[]{0, 2, 4}, ans);
    }

    @Test
    public void testSubgraph_origin() {
        Set<Vertex> subg = g.getVertices();
        Set<Vertex> separator = new HashSet<>();
        SelfDualGraph subgraph = g.buildSubgraph(subg, separator);
        Assert.assertEquals(6, subgraph.getVertexNum());
        Assert.assertEquals(7, subgraph.getFaceNum());
        Assert.assertEquals(0, subgraph.getBoundary().size());
        for (Vertex f : subgraph.getFaces()) {
            Vertex f0 = findVertexByID(g.getFaces(), f.getID());
            Assert.assertTrue(f0 != null);
            Assert.assertTrue(f0 != f);
            Assert.assertEquals(f0.type, f.type);
            Assert.assertEquals(f0.getDegree(), f.getDegree());
            Assert.assertEquals(f0.getFirstDart().getID(), f.getFirstDart().getID());
        }

        for (Vertex v : subgraph.getVertices()) {
            Vertex v0 = findVertexByID(g.getVertices(), v.getID());
            Assert.assertTrue(v0 != null);
            Assert.assertTrue(v0 != v);
            Assert.assertEquals(v0.type, v.type);
            Assert.assertEquals(v0.getDegree(), v.getDegree());
            Assert.assertEquals(v0.getFirstDart().getID(), v.getFirstDart().getID());
            for (Dart d : v.getIncidenceList()) {
                Dart d0 = findDartByID(g, d.getID());
                Assert.assertTrue(d0 != null);
                Assert.assertTrue(d0 != d);
                Assert.assertEquals(d0.getTail().getID(), d.getTail().getID());
                Assert.assertEquals(d0.getHead().getID(), d.getHead().getID());
                Assert.assertEquals(d0.getReverse().getID(), d.getReverse().getID());
                Assert.assertEquals(d0.getLeft().getID(), d.getLeft().getID());
                Assert.assertEquals(d0.getRight().getID(), d.getRight().getID());
                Assert.assertEquals(d0.getNext().getID(), d.getNext().getID());
                Assert.assertEquals(d0.getPrev().getID(), d.getPrev().getID());
                Assert.assertEquals(d0.getSuccessor().getID(), d.getSuccessor().getID());
                Assert.assertEquals(d0.getPredecessor().getID(), d.getPredecessor().getID());
            }
        }
    }


    @Test
    public void testSubgraph_13() {
        Set<Vertex> subg = findVertexSetByIDs(g.getVertices(), new int[]{0, 1, 2, 3, 4});
        Set<Vertex> separator = findVertexSetByIDs(g.getVertices(), new int[]{0, 2, 4});

        SelfDualGraph subgraph = g.buildSubgraph(subg, separator);
        Assert.assertEquals(5, subgraph.getVertexNum());
        Assert.assertEquals(3, subgraph.getFaceNum());
        verifyVertexSet(new int[]{0, 2, 4}, subgraph.getBoundary());

        int[][] vIncidList = new int[][]{{1, 2, 4}, {0, 3}, {0, 3}, {1, 4, 2}, {0, 3}};
        for (Vertex v : subgraph.getVertices()) {
            checkIncidentListOfVertex(v, vIncidList[v.getID()]);
        }
        int[][] fIncidList = new int[][]{{0, 2, 3, 1}, {}, {}, {}, {}, {}, {4, 0, 1, 3}};
        for (Vertex f : subgraph.getFaces()) {
            if (f.getID() >= 0) checkIncidentListOfFace(f, fIncidList[f.getID()]);
        }
        Vertex ff = (new SpecificIdRootFinder(-1)).selectRootFace(subgraph);
        Assert.assertEquals(4, ff.getDegree());
        checkNewFace(ff, new int[]{0, 4, 3, 2});
    }

    @Test
    public void testSubgraph_13_2() {
        Set<Vertex> subg1 = findVertexSetByIDs(g.getVertices(), new int[]{0, 1, 2, 3, 4});
        Set<Vertex> separator1 = findVertexSetByIDs(g.getVertices(), new int[]{0, 2, 4});
        SelfDualGraph subgraph1 = g.buildSubgraph(subg1, separator1);

        Set<Vertex> subg2 = findVertexSetByIDs(subgraph1.getVertices(), new int[]{0, 2, 3, 4});
        Set<Vertex> separator2 = findVertexSetByIDs(subgraph1.getVertices(), new int[]{0, 2, 3, 4}); // {0, 3}
        SelfDualGraph subgraph = subgraph1.buildSubgraph(subg2, separator2);

        Assert.assertEquals(4, subgraph.getVertexNum());
        Assert.assertEquals(2, subgraph.getFaceNum());
        verifyVertexSet(new int[]{0, 2, 3, 4}, subgraph.getBoundary());

        int[][] vIncidList = new int[][]{{2, 4}, {}, {0, 3}, {4, 2}, {0, 3}};
        for (Vertex v : subgraph.getVertices()) {
            checkIncidentListOfVertex(v, vIncidList[v.getID()]);
        }
        Vertex ff1 = (new SpecificIdRootFinder(-1)).selectRootFace(subgraph);
        Assert.assertEquals(4, ff1.getDegree());
        checkNewFace(ff1, new int[]{0, 4, 3, 2});

        Vertex ff2 = (new SpecificIdRootFinder(-2)).selectRootFace(subgraph);
        Assert.assertEquals(4, ff2.getDegree());
        checkNewFace(ff2, new int[]{0, 2, 3, 4});
    }

    @Test
    public void testSubgraph_13_14() {
        Set<Vertex> subg1 = findVertexSetByIDs(g.getVertices(), new int[]{0, 1, 2, 3, 4});
        Set<Vertex> separator1 = findVertexSetByIDs(g.getVertices(), new int[]{0, 2, 4});
        SelfDualGraph subgraph1 = g.buildSubgraph(subg1, separator1);

        Set<Vertex> subg2 = findVertexSetByIDs(subgraph1.getVertices(), new int[]{0, 1, 3, 4});
        Set<Vertex> separator2 = findVertexSetByIDs(subgraph1.getVertices(), new int[]{0, 3, 4});
        SelfDualGraph subgraph = subgraph1.buildSubgraph(subg2, separator2);

        Assert.assertEquals(4, subgraph.getVertexNum());
        Assert.assertEquals(2, subgraph.getFaceNum());
        verifyVertexSet(new int[]{3, 0}, subgraph.getBoundary());

        int[][] vIncidList = new int[][]{{1, 4}, {0, 3}, {}, {1, 4}, {0, 3}};
        for (Vertex v : subgraph.getVertices()) {
            checkIncidentListOfVertex(v, vIncidList[v.getID()]);
        }
        Vertex ff2 = (new SpecificIdRootFinder(-2)).selectRootFace(subgraph);
        Assert.assertEquals(4, ff2.getDegree());
        checkNewFace(ff2, new int[]{4, 3, 1, 0});

        Vertex ff6 = (new SpecificIdRootFinder(6)).selectRootFace(subgraph);
        Assert.assertEquals(4, ff6.getDegree());
        checkNewFace(ff6, new int[]{4, 0, 1, 3});
    }

    @Test
    public void testSubgraph_13_1() {
        Set<Vertex> subg1 = findVertexSetByIDs(g.getVertices(), new int[]{0, 1, 2, 3, 4});
        Set<Vertex> separator1 = findVertexSetByIDs(g.getVertices(), new int[]{0, 2, 4});
        SelfDualGraph subgraph1 = g.buildSubgraph(subg1, separator1);

        Set<Vertex> subg2 = findVertexSetByIDs(subgraph1.getVertices(), new int[]{0, 1, 3});
        Set<Vertex> separator2 = findVertexSetByIDs(subgraph1.getVertices(), new int[]{0, 3});
        SelfDualGraph subgraph = subgraph1.buildSubgraph(subg2, separator2);

        Assert.assertEquals(3, subgraph.getVertexNum());
        Assert.assertEquals(1, subgraph.getFaceNum());
        verifyVertexSet(new int[]{3, 0}, subgraph.getBoundary());

        int[][] vIncidList = new int[][]{{1}, {0, 3}, {}, {1}};
        for (Vertex v : subgraph.getVertices()) {
            checkIncidentListOfVertex(v, vIncidList[v.getID()]);
        }
        Vertex ff2 = (new SpecificIdRootFinder(-2)).selectRootFace(subgraph);
        ff2.setDart(findDartByID(subgraph, 0));
        Assert.assertEquals(4, ff2.getDegree());
        checkNewFace(ff2, new int[]{0, 1, 3, 1});
    }

    private void checkNewFace(Vertex F, int[] boundaryVertexID) {
        Assert.assertEquals(boundaryVertexID.length, F.getDegree());
        Dart d = F.getFirstDart();
        int j = 0;
        while (d.getTail().getID() != boundaryVertexID[j]) {
            j++;
            if (j == F.getDegree()) Assert.assertTrue(false);
        }
        for (int i = 0; i < F.getDegree(); i++) {
            Assert.assertEquals(F.getID(), d.getRight().getID());
            Assert.assertEquals(F.getID(), d.getReverse().getLeft().getID());
            Assert.assertEquals(boundaryVertexID[(j + i) % F.getDegree()], d.getTail().getID());
            d = d.getNext();
        }
        Assert.assertEquals(boundaryVertexID[j], d.getTail().getID());
    }

    @Test
    public void test_vertexNeighborOnBoundary() {
        Set<Integer> ids = new HashSet<>(Arrays.asList(new Integer[]{0, 2, 4, 5}));
        Set<Vertex> boundary = g.getVerticesFromID(ids);
        for (Vertex v : boundary) Assert.assertTrue(ids.contains(v.getID()));

        g.addToBoundary(boundary);
        Assert.assertEquals(ids.size(), g.getBoundarySize());
        Integer[][] neighbors = new Integer[][]{{2, 4, 5}, {}, {0, 5, -1}, {}, {0, 5}, {0, 2, 4, 5, -1, -2}};
        for (int i = 0; i < 6; i++) {
            Set<Dart> darts = g.vertexNeighborOnBoundary(findVertexByID(g.getVertices(), i));
            Set<Integer> expected = new HashSet<>(Arrays.asList(neighbors[i]));
            Assert.assertEquals(expected.size(), darts.size());
            for (Dart d : darts) Assert.assertTrue(expected.contains(d.getHead().getID()));
        }
    }

    @Test
    public void test_assignWeightToBoundary_useDart() {
        Set<Integer> ids = new HashSet<>(Arrays.asList(new Integer[]{2, 3, 4, 5}));
        Set<Vertex> boundary = g.getVerticesFromID(ids);
        g.addToBoundary(boundary);
        g.assignWeightToBoundary_useDart();
        for (Vertex v : g.getVertices()) {
            if (ids.contains(v.getID())) Assert.assertEquals(1.0, v.getWeight(), 0.0001);
            else Assert.assertEquals(0, v.getWeight(), 0.0001);
        }

        double[] faceWeights = new double[]{5.0 / 12, 26.0 / 15, 8.0 / 15, 0.2, 4.0 / 15, 0.35, 0.5};
        for (Vertex f : g.getFaces()) {
            Assert.assertEquals(faceWeights[f.getID()], f.getWeight(), 0.0001);
        }
    }

    @Test
    public void test_assignWeightToBoundary_useVertex() {
        Set<Integer> ids = new HashSet<>(Arrays.asList(new Integer[]{2, 3, 4, 5}));
        Set<Vertex> boundary = g.getVerticesFromID(ids);
        g.addToBoundary(boundary);
        g.assignWeightToBoundary_useVertex();
        for (Vertex v : g.getVertices()) {
            if (ids.contains(v.getID())) Assert.assertEquals(1.0, v.getWeight(), 0.0001);
            else Assert.assertEquals(0, v.getWeight(), 0.0001);
        }

        double[] faceWeights = new double[]{7.0 / 12, 1.25, 5.0 / 12, 1.0 / 6, 5.0 / 12, 0.5, 2.0 / 3};
        for (Vertex f : g.getFaces()) {
            Assert.assertEquals(faceWeights[f.getID()], f.getWeight(), 0.0001);
        }
    }
}
