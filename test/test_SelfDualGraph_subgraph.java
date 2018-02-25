import algorithms.RootFinder.SpecificIdRootFinder;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class test_SelfDualGraph_subgraph extends test_SelfDualGraph_modification {

    protected Set<Vertex> findVertices(int[] IDs) {
        Set<Vertex> vertices = new HashSet<>();
        Set<Integer> vIDs = new HashSet<>();
        for (int i : IDs) vIDs.add(i);
        for (Vertex v : g.getVertices()) {
            if (vIDs.contains(v.getID())) vertices.add(v);
        }
        return vertices;
    }

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
        Set<Vertex> subg = findVertices(new int[]{0, 1, 2, 3, 4});
        Set<Vertex> boundary = findVertices(new int[]{0, 2, 3, 4, 5});
        Vertex src = (new SpecificIdRootFinder(1)).selectRootVertex(g);
        Set<Vertex> ans = g.findBoundary(src, subg, boundary);
        verifyVertexSet(new int[]{0, 3}, ans);
    }

    @Test
    public void testFindBoundary_5() {
        Set<Vertex> subg = findVertices(new int[]{0, 2, 3, 4, 5});
        Set<Vertex> boundary = findVertices(new int[]{0, 2, 3, 4});
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
        Set<Vertex> subg = findVertices(new int[]{0, 1, 2, 3, 4});
        Set<Vertex> separator = findVertices(new int[]{0, 2, 4});

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
}