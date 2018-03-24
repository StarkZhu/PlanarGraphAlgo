import org.junit.*;
import selfdualgraph.*;


public class test_SelfDualGraph_mod_delete extends test_SelfDualGraph_modification{
    @Test
    public void testDeleteLoop1() {
        Dart dart = findDartByID(g, 20);
        g.deleteEdge(dart);
        Assert.assertEquals(null, findDartByID(g, 20));
        Assert.assertEquals(null, findDartByID(g, 21));
        Vertex F = dart.getRight();
        Vertex V = dart.getHead();

        // check degree
        Assert.assertEquals(1, F.getID());
        Assert.assertEquals(4, F.getDegree());
        Assert.assertEquals(5, V.getID());
        Assert.assertEquals(4, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Assert.assertEquals(19, prev.getID());
        Assert.assertEquals(13, next.getID());
        Assert.assertEquals(13, prev.getNext().getID());
        Assert.assertEquals(19, next.getPrev().getID());
        Assert.assertEquals(18, next.getPredecessor().getID());
        Assert.assertEquals(13, prev.getReverse().getSuccessor().getID());

        // check incident list of F
        int[] boundaryVertexID = new int[]{3, 2, 5, 4};
        checkIncidentListOfFace(F, boundaryVertexID);


        // check incident list of V
        int[] incidentVertexID = new int[]{4, 0, 2, 2};
        checkIncidentListOfVertex(V, incidentVertexID);
    }

    @Test
    public void testDeleteLoopN() {
        g.deleteVertexSelfLoop(findVertexByID(g.getVertices(), 5));
        for (int i : new int[]{9, 13, 3}) {
            Dart dart = findDartByID(g, i);
            g.contractEdge(dart);
        }
        Vertex v0 = findVertexByID(g.getVertices(), 0);
        Assert.assertEquals(9, v0.getDegree());
        int[] expectedDid=new int[]{0, 7, 19, 17, 15, 14, 16, 18, 10};
        int i = 0;
        for (Dart d : v0.getIncidenceList()){
            Assert.assertEquals(expectedDid[i++], d.getID());
        }

        g.deleteLoop(findDartByID(g, 19));
        Assert.assertEquals(7, v0.getDegree());
        expectedDid=new int[]{0, 7, 17, 15, 14, 16, 10};
        i = 0;
        for (Dart d : v0.getIncidenceList()){
            Assert.assertEquals(expectedDid[i++], d.getID());
        }

        g.deleteLoop(findDartByID(g, 17));
        Assert.assertEquals(5, v0.getDegree());
        expectedDid=new int[]{0, 7, 15, 14, 10};
        i = 0;
        for (Dart d : v0.getIncidenceList()){
            Assert.assertEquals(expectedDid[i++], d.getID());
        }

        g.deleteLoop(findDartByID(g, 15));
        Assert.assertEquals(3, v0.getDegree());
        expectedDid=new int[]{0, 7, 10};
        i = 0;
        for (Dart d : v0.getIncidenceList()){
            Assert.assertEquals(expectedDid[i++], d.getID());
        }
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
        Assert.assertEquals(6, F.getID());
        Assert.assertEquals(5, F.getDegree());
        Assert.assertEquals(4, V.getID());
        Assert.assertEquals(2, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(14, prev.getID());
        Assert.assertEquals(12, next.getID());
        Assert.assertEquals(0, succ.getID());
        Assert.assertEquals(0, prev.getNext().getID());
        Assert.assertEquals(14, succ.getPrev().getID());
        Assert.assertEquals(11, next.getPrev().getID());
        Assert.assertEquals(12, next.getPrev().getNext().getID());

        Assert.assertEquals(15, succ.getPredecessor().getID());
        Assert.assertEquals(0, succ.getPredecessor().getSuccessor().getID());
        Assert.assertEquals(10, next.getPredecessor().getID());
        Assert.assertEquals(12, next.getPredecessor().getSuccessor().getID());

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
        Assert.assertEquals(1, F.getID());
        Assert.assertEquals(6, F.getDegree());
        Assert.assertEquals(5, V.getID());
        Assert.assertEquals(5, V.getDegree());

        // check local pointers
        Dart prev = dart.getPrev();
        Dart next = dart.getNext();
        Dart succ = dart.getSuccessor();
        Assert.assertEquals(20, prev.getID());
        Assert.assertEquals(10, next.getID());
        Assert.assertEquals(14, succ.getID());
        Assert.assertEquals(14, prev.getNext().getID());
        Assert.assertEquals(20, succ.getPrev().getID());
        Assert.assertEquals(8, next.getPrev().getID());
        Assert.assertEquals(10, next.getPrev().getNext().getID());

        Assert.assertEquals(21, succ.getPredecessor().getID());
        Assert.assertEquals(14, succ.getPredecessor().getSuccessor().getID());
        Assert.assertEquals(9, next.getPredecessor().getID());
        Assert.assertEquals(10, next.getPredecessor().getSuccessor().getID());

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
