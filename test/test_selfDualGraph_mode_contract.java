import org.junit.*;
import selfdualgraph.*;


public class test_selfDualGraph_mode_contract extends test_SelfDualGraph_modification{

    @Test
    public void testContractFirstEdgeOfVertex() {
        Dart dart = findDartByID(g, 8);
        g.contractEdge(dart);
        Assert.assertEquals(null, findDartByID(g, 9));
        Assert.assertEquals(null, findDartByID(g, 8));
        Vertex F = dart.getRight();
        Vertex V = dart.getTail();

        // check degree
        Assert.assertEquals(5, F.ID);
        Assert.assertEquals(2, F.getDegree());
        Assert.assertEquals(0, V.ID);
        Assert.assertEquals(5, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(14, prev.ID);
        Assert.assertEquals(12, next.ID);
        Assert.assertEquals(0, succ.ID);
        Assert.assertEquals(12, prev.getNext().ID);
        Assert.assertEquals(14, next.getPrev().ID);
        Assert.assertEquals(11, succ.getPrev().ID);
        Assert.assertEquals(0, succ.getPrev().getNext().ID);

        Assert.assertEquals(10, succ.getPredecessor().ID);
        Assert.assertEquals(0, succ.getPredecessor().getSuccessor().ID);
        Assert.assertEquals(15, next.getPredecessor().ID);
        Assert.assertEquals(12, next.getPredecessor().getSuccessor().ID);

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
        Assert.assertEquals(1, F.ID);
        Assert.assertEquals(4, F.getDegree());
        Assert.assertEquals(5, V.ID);
        Assert.assertEquals(7, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(20, prev.ID);
        Assert.assertEquals(10, next.ID);
        Assert.assertEquals(14, succ.ID);
        Assert.assertEquals(10, prev.getNext().ID);
        Assert.assertEquals(20, next.getPrev().ID);
        Assert.assertEquals(8, succ.getPrev().ID);
        Assert.assertEquals(14, succ.getPrev().getNext().ID);

        Assert.assertEquals(9, succ.getPredecessor().ID);
        Assert.assertEquals(14, succ.getPredecessor().getSuccessor().ID);
        Assert.assertEquals(21, next.getPredecessor().ID);
        Assert.assertEquals(10, next.getPredecessor().getSuccessor().ID);

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
        Assert.assertEquals(2, F.ID);
        Assert.assertEquals(1, F.getDegree());
        Assert.assertEquals(5, V.ID);
        Assert.assertEquals(8, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(18, prev.ID);
        Assert.assertEquals(18, next.ID);
        Assert.assertEquals(3, succ.ID);
        Assert.assertEquals(18, prev.getNext().ID);
        Assert.assertEquals(18, next.getPrev().ID);
        Assert.assertEquals(15, succ.getPrev().ID);
        Assert.assertEquals(3, succ.getPrev().getNext().ID);

        Assert.assertEquals(14, succ.getPredecessor().ID);
        Assert.assertEquals(3, succ.getPredecessor().getSuccessor().ID);
        Assert.assertEquals(19, next.getPredecessor().ID);
        Assert.assertEquals(18, next.getPredecessor().getSuccessor().ID);

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
        Assert.assertEquals(6, F.ID);
        Assert.assertEquals(4, F.getDegree());
        Assert.assertEquals(3, V.ID);
        Assert.assertEquals(2, V.getDegree());

        // check local pointers
        Dart prev = dart.getReverse().getPrev();
        Dart next = dart.getNext();
        Assert.assertEquals(7, prev.ID);
        Assert.assertEquals(11, next.ID);
        Assert.assertEquals(11, prev.getNext().ID);
        Assert.assertEquals(7, next.getPrev().ID);
        Assert.assertEquals(6, next.getPredecessor().ID);
        Assert.assertEquals(11, prev.getReverse().getSuccessor().ID);

        // check incident list of F
        int[] boundaryVertexID = new int[]{4, 0, 2, 3};
        checkIncidentListOfFace(F, boundaryVertexID);

        // check incident list of V
        int[] incidentVertexID = new int[]{4, 2};
        checkIncidentListOfVertex(V, incidentVertexID);
    }
}
