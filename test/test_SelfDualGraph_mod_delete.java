import org.junit.*;
import selfdualgraph.*;


public class test_SelfDualGraph_mod_delete extends test_SelfDualGraph_modification{
    @Test
    public void testDeleteLoop() {
        Dart dart = findDartByID(g, 20);
        g.deleteEdge(dart);
        Assert.assertEquals(null, findDartByID(g, 20));
        Assert.assertEquals(null, findDartByID(g, 21));
        Vertex F = dart.getRight();
        Vertex V = dart.getHead();

        // check degree
        Assert.assertEquals(1, F.ID);
        Assert.assertEquals(4, F.getDegree());
        Assert.assertEquals(5, V.ID);
        Assert.assertEquals(4, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Assert.assertEquals(19, prev.ID);
        Assert.assertEquals(13, next.ID);
        Assert.assertEquals(13, prev.getNext().ID);
        Assert.assertEquals(19, next.getPrev().ID);
        Assert.assertEquals(18, next.getPredecessor().ID);
        Assert.assertEquals(13, prev.getReverse().getSuccessor().ID);

        // check incident list of F
        int[] boundaryVertexID = new int[]{3, 2, 5, 4};
        checkIncidentListOfFace(F, boundaryVertexID);


        // check incident list of V
        int[] incidentVertexID = new int[]{4, 0, 2, 2};
        checkIncidentListOfVertex(V, incidentVertexID);
    }

    @Test
    public void testDeleteFirstEdgeOfFace() {
        Dart dart = findDartByID(g, 8);
        g.deleteEdge(dart);
        Assert.assertEquals(null, findDartByID(g, 8));
        Assert.assertEquals(null, findDartByID(g, 9));
        Vertex F = dart.getRight();
        Vertex V = dart.getHead();

        // check degree
        Assert.assertEquals(6, F.ID);
        Assert.assertEquals(5, F.getDegree());
        Assert.assertEquals(4, V.ID);
        Assert.assertEquals(2, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(14, prev.ID);
        Assert.assertEquals(12, next.ID);
        Assert.assertEquals(0, succ.ID);
        Assert.assertEquals(0, prev.getNext().ID);
        Assert.assertEquals(14, succ.getPrev().ID);
        Assert.assertEquals(11, next.getPrev().ID);
        Assert.assertEquals(12, next.getPrev().getNext().ID);

        Assert.assertEquals(15, succ.getPredecessor().ID);
        Assert.assertEquals(0, succ.getPredecessor().getSuccessor().ID);
        Assert.assertEquals(10, next.getPredecessor().ID);
        Assert.assertEquals(12, next.getPredecessor().getSuccessor().ID);

        // check incident list of F
        int[] boundaryVertexID = new int[]{4, 5, 0, 1, 3};
        checkIncidentListOfFace(F, boundaryVertexID);


        // check incident list of V
        int[] headIncidentVertexID = new int[]{5, 3};
        checkIncidentListOfVertex(V, headIncidentVertexID);
        int[] tailIncidentVertexID = new int[]{1, 2, 5};
        checkIncidentListOfVertex(dart.getTail(), tailIncidentVertexID);
    }

    @Test
    public void testDeleteFirstEdgeOfVertex() {
        Dart dart = findDartByID(g, 13);
        g.deleteEdge(dart);
        Assert.assertEquals(null, findDartByID(g, 12));
        Assert.assertEquals(null, findDartByID(g, 13));
        Vertex F = dart.getLeft();
        Vertex V = dart.getTail();

        // check degree
        Assert.assertEquals(1, F.ID);
        Assert.assertEquals(6, F.getDegree());
        Assert.assertEquals(5, V.ID);
        Assert.assertEquals(5, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(20, prev.ID);
        Assert.assertEquals(10, next.ID);
        Assert.assertEquals(14, succ.ID);
        Assert.assertEquals(14, prev.getNext().ID);
        Assert.assertEquals(20, succ.getPrev().ID);
        Assert.assertEquals(8, next.getPrev().ID);
        Assert.assertEquals(10, next.getPrev().getNext().ID);

        Assert.assertEquals(21, succ.getPredecessor().ID);
        Assert.assertEquals(14, succ.getPredecessor().getSuccessor().ID);
        Assert.assertEquals(9, next.getPredecessor().ID);
        Assert.assertEquals(10, next.getPredecessor().getSuccessor().ID);

        // check incident list of F
        int[] boundaryVertexID = new int[]{3, 2, 5, 5, 0, 4};
        checkIncidentListOfFace(F, boundaryVertexID);


        // check incident list of V
        int[] headIncidentVertexID = new int[]{0, 3};
        checkIncidentListOfVertex(dart.getHead(), headIncidentVertexID);
        int[] tailIncidentVertexID = new int[]{0, 2, 2, 5, 5};
        checkIncidentListOfVertex(dart.getTail(), tailIncidentVertexID);
    }
}
