import org.junit.*;
import selfdualgraph.*;

import java.util.*;


public class test_selfDualGraph_mod_contract extends test_SelfDualGraph_modification {

    @Test
    public void testContractFirstEdgeOfVertex() {
        Dart dart = findDartByID(g, 8);
        g.contractEdge(dart);
        Assert.assertEquals(null, findDartByID(g, 9));
        Assert.assertEquals(null, findDartByID(g, 8));
        Vertex F = dart.getRight();
        Vertex V = dart.getTail();

        // check degree
        Assert.assertEquals(5, F.getID());
        Assert.assertEquals(2, F.getDegree());
        Assert.assertEquals(0, V.getID());
        Assert.assertEquals(5, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(14, prev.getID());
        Assert.assertEquals(12, next.getID());
        Assert.assertEquals(0, succ.getID());
        Assert.assertEquals(12, prev.getNext().getID());
        Assert.assertEquals(14, next.getPrev().getID());
        Assert.assertEquals(11, succ.getPrev().getID());
        Assert.assertEquals(0, succ.getPrev().getNext().getID());

        Assert.assertEquals(10, succ.getPredecessor().getID());
        Assert.assertEquals(0, succ.getPredecessor().getSuccessor().getID());
        Assert.assertEquals(15, next.getPredecessor().getID());
        Assert.assertEquals(12, next.getPredecessor().getSuccessor().getID());

        // check incident list of V
        int[] boundaryVertexID = new int[]{1, 2, 5, 5, 3};
        checkIncidentListOfVertex(V, boundaryVertexID);


        // check incident list of F
        int[] rightIncidentVertexID = new int[]{0, 5};
        checkIncidentListOfFace(dart.getRight(), rightIncidentVertexID);
        int[] leftIncidentVertexID = new int[]{0, 1, 3};
        checkIncidentListOfFace(dart.getLeft(), leftIncidentVertexID);
    }

    @Test
    public void testContractFirstEdgeOfFace() {
        Dart dart = findDartByID(g, 13);
        g.contractEdge(dart);
        Assert.assertEquals(null, findDartByID(g, 12));
        Assert.assertEquals(null, findDartByID(g, 13));
        Vertex F = dart.getRight();
        Vertex V = dart.getTail();

        // check degree
        Assert.assertEquals(1, F.getID());
        Assert.assertEquals(4, F.getDegree());
        Assert.assertEquals(5, V.getID());
        Assert.assertEquals(7, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(20, prev.getID());
        Assert.assertEquals(10, next.getID());
        Assert.assertEquals(14, succ.getID());
        Assert.assertEquals(10, prev.getNext().getID());
        Assert.assertEquals(20, next.getPrev().getID());
        Assert.assertEquals(8, succ.getPrev().getID());
        Assert.assertEquals(14, succ.getPrev().getNext().getID());

        Assert.assertEquals(9, succ.getPredecessor().getID());
        Assert.assertEquals(14, succ.getPredecessor().getSuccessor().getID());
        Assert.assertEquals(21, next.getPredecessor().getID());
        Assert.assertEquals(10, next.getPredecessor().getSuccessor().getID());

        // check incident list of V
        int[] boundaryVertexID = new int[]{0, 2, 2, 5, 5, 3, 0};
        checkIncidentListOfVertex(V, boundaryVertexID);


        // check incident list of F
        int[] rightIncidentVertexID = new int[]{3, 2, 5, 5};
        checkIncidentListOfFace(dart.getRight(), rightIncidentVertexID);
        int[] leftIncidentVertexID = new int[]{5, 0};
        checkIncidentListOfFace(dart.getLeft(), leftIncidentVertexID);
    }

    @Test
    public void testContractParallelEdge() {
        Dart dart = findDartByID(g, 17);
        g.contractEdge(dart);
        Assert.assertEquals(null, findDartByID(g, 16));
        Assert.assertEquals(null, findDartByID(g, 17));
        Vertex F = dart.getRight();
        Vertex V = dart.getTail();

        // check degree
        Assert.assertEquals(2, F.getID());
        Assert.assertEquals(1, F.getDegree());
        Assert.assertEquals(5, V.getID());
        Assert.assertEquals(8, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(18, prev.getID());
        Assert.assertEquals(18, next.getID());
        Assert.assertEquals(3, succ.getID());
        Assert.assertEquals(18, prev.getNext().getID());
        Assert.assertEquals(18, next.getPrev().getID());
        Assert.assertEquals(15, succ.getPrev().getID());
        Assert.assertEquals(3, succ.getPrev().getNext().getID());

        Assert.assertEquals(14, succ.getPredecessor().getID());
        Assert.assertEquals(3, succ.getPredecessor().getSuccessor().getID());
        Assert.assertEquals(19, next.getPredecessor().getID());
        Assert.assertEquals(18, next.getPredecessor().getSuccessor().getID());

        // check incident list of V
        int[] boundaryVertexID = new int[]{4, 0, 0, 3, 5, 5, 5, 5};
        checkIncidentListOfVertex(V, boundaryVertexID);


        // check incident list of F
        int[] rightIncidentVertexID = new int[]{5};
        checkIncidentListOfFace(dart.getRight(), rightIncidentVertexID);
        int[] leftIncidentVertexID = new int[]{5, 0};
        checkIncidentListOfFace(dart.getLeft(), leftIncidentVertexID);
    }

    @Test
    public void testContractBridge() {
        g.deleteEdge(findDartByID(g, 0));
        // now V<1> is a vertex of degree 1, D<4> is a bridge
        Dart dart = findDartByID(g, 4);
        g.contractEdge(dart);

        Assert.assertEquals(null, findDartByID(g, 4));
        Assert.assertEquals(null, findDartByID(g, 5));
        Vertex F = dart.getRight();
        Vertex V = dart.getHead();

        // check degree
        Assert.assertEquals(6, F.getID());
        Assert.assertEquals(4, F.getDegree());
        Assert.assertEquals(3, V.getID());
        Assert.assertEquals(2, V.getDegree());

        // check local pointers
        Dart prev = dart.getReverse().getPrev();
        Dart next = dart.getNext();
        Assert.assertEquals(7, prev.getID());
        Assert.assertEquals(11, next.getID());
        Assert.assertEquals(11, prev.getNext().getID());
        Assert.assertEquals(7, next.getPrev().getID());
        Assert.assertEquals(6, next.getPredecessor().getID());
        Assert.assertEquals(11, prev.getReverse().getSuccessor().getID());

        // check incident list of F
        int[] boundaryVertexID = new int[]{4, 0, 2, 3};
        checkIncidentListOfFace(F, boundaryVertexID);

        // check incident list of V
        int[] incidentVertexID = new int[]{4, 2};
        checkIncidentListOfVertex(V, incidentVertexID);
    }

    @Test
    public void testMergeConnectedPiece0() {
        Set<Vertex> piece = findVertexSetByID(g.getVertices(), new int[]{0, 2, 3, 4, 5});
        g.mergeConnectedPiece(piece);
        Assert.assertEquals(2, g.getVertexNum());
        Assert.assertEquals(2, g.getFaceNum());
        Vertex v1 = findVertexByID(g.getVertices(), 1);
        Assert.assertEquals(2, v1.getDegree());
        Vertex v = v1.getFirstDart().getHead();
        System.out.println("---");
        System.out.println(v);
        Assert.assertEquals(2, v.getDegree());
        for (Dart d: v.getIncidenceList()) {
            System.out.println(d);
            Assert.assertEquals(1, d.getHead().getID());
        }
    }

    @Test
    public void testMergeConnectedPiece1() {
        Set<Vertex> piece = findVertexSetByID(g.getVertices(), new int[]{0, 2, 4, 5});
        g.mergeConnectedPiece(piece);
        g.flatten();
        Assert.assertEquals(3, g.getVertexNum());
        Assert.assertEquals(2, g.getFaceNum());
        Vertex v1 = findVertexByID(g.getVertices(), 1);
        Assert.assertEquals(2, v1.getDegree());
        Vertex v3 = findVertexByID(g.getVertices(), 3);
        Assert.assertEquals(2, v3.getDegree());
    }
    @Test
    public void testMergeConnectedPiece2() {
        Set<Vertex> piece = findVertexSetByID(g.getVertices(), new int[]{2, 3, 4, 5});
        g.mergeConnectedPiece(piece);
        g.flatten();
        Assert.assertEquals(3, g.getVertexNum());
        Assert.assertEquals(2, g.getFaceNum());
        Vertex v1 = findVertexByID(g.getVertices(), 1);
        Assert.assertEquals(2, v1.getDegree());
        Vertex v0 = findVertexByID(g.getVertices(), 0);
        Assert.assertEquals(2, v0.getDegree());
    }

    @Test
    public void testMergeConnectedPiece3() {
        Set<Vertex> piece = findVertexSetByID(g.getVertices(), new int[]{0, 2, 5});
        g.mergeConnectedPiece(piece);
        g.flatten();
        Assert.assertEquals(4, g.getVertexNum());
        Assert.assertEquals(3, g.getFaceNum());
        Vertex v1 = findVertexByID(g.getVertices(), 1);
        Assert.assertEquals(2, v1.getDegree());
        Vertex v3 = findVertexByID(g.getVertices(), 3);
        Assert.assertEquals(3, v3.getDegree());
        Vertex v4 = findVertexByID(g.getVertices(), 4);
        Assert.assertEquals(2, v4.getDegree());
    }
}
