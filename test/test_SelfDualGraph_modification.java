import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;


public class test_SelfDualGraph_modification {
    private SelfDualGraph g;

    @Before
    /**
     * executed before each test
     */
    public void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./input_data/test_graph_0.txt");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
    }

    private Dart findDartByID(SelfDualGraph g, int dartID) {
        for (Vertex v : g.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                if (d.ID == dartID) {
                    return d;
                }
            }
        }
        //Assert.assertTrue(String.format("Graph does NOT contain dart with ID = %d", dartID), false);
        System.out.printf("Graph does NOT contain dart with ID = %d\n", dartID);
        return null;
    }

    private void checkIncidentListOfFace(Vertex F, int[] boundaryVertexID) {
        Assert.assertEquals(boundaryVertexID.length, F.getDegree());
        Dart d = F.getFirstDart();
        for (int i = 0; i < F.getDegree(); i++) {
            Assert.assertEquals(F.ID, d.getRight().ID);
            Assert.assertEquals(F.ID, d.getReverse().getLeft().ID);
            Assert.assertEquals(boundaryVertexID[i], d.getTail().ID);
            d = d.getNext();
        }
        Assert.assertEquals(boundaryVertexID[0], d.getTail().ID);
    }

    private void checkIncidentListOfVertex(Vertex V, int[] incidentVertexID) {
        Assert.assertEquals(incidentVertexID.length, V.getDegree());
        Dart d = V.getFirstDart();
        for (int i = 0; i < V.getDegree(); i++) {
            Assert.assertEquals(incidentVertexID[i], d.getHead().ID);
            d = d.getSuccessor();
        }
        Assert.assertEquals(incidentVertexID[0], d.getHead().ID);
    }

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

    @Test
    /**
     * test contracting a bridge on a vertex of degree 1
     * test deleting a self-loop on a vertex of degree 1
     */
    public void testMultipleDeleteContract() {
        Dart[] darts = new Dart[22];
        for (Vertex v : g.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                darts[d.ID] = d;
            }
        }
        g.deleteEdge(darts[16]);
        g.deleteEdge(darts[2]);
        g.deleteEdge(darts[0]);
        g.contractEdge(darts[4]);
        g.contractEdge(darts[15]);
        g.contractEdge(darts[11]);
        g.contractEdge(darts[13]);

        Assert.assertEquals(2, g.getVertexNum());
        Assert.assertEquals(4, g.getFaceNum());
        Dart d8 = findDartByID(g, 8);
        Vertex V = d8.getTail();
        Assert.assertEquals(5, V.ID);
        int[] incidentVertexID = new int[]{5, 2, 5, 5, 2, 5};
        checkIncidentListOfVertex(V, incidentVertexID);
        Vertex F = d8.getLeft();
        Assert.assertEquals(0, F.ID);
        int[] boundaryVertexID = new int[]{2, 5, 5};
        checkIncidentListOfFace(F, boundaryVertexID);

        g.deleteEdge(darts[9]);
        g.deleteEdge(darts[19]);
        Assert.assertEquals(2, g.getVertexNum());
        Assert.assertEquals(2, g.getFaceNum());
        incidentVertexID = new int[]{2, 5, 5};
        checkIncidentListOfVertex(V, incidentVertexID);
        Dart d21 = findDartByID(g, 21);
        F = d21.getLeft();
        Assert.assertEquals(1, F.ID);
        boundaryVertexID = new int[]{5, 2, 5};
        checkIncidentListOfFace(F, boundaryVertexID);

        g.contractEdge(darts[7]);
        Assert.assertEquals(1, g.getVertexNum());
        Assert.assertEquals(2, V.getDegree());
        Assert.assertEquals(2, g.getFaceNum());
        Assert.assertEquals(1, F.getDegree());

        g.deleteEdge(darts[20]);
        Assert.assertEquals(1, g.getVertexNum());
        Assert.assertEquals(0, V.getDegree());
        Assert.assertEquals(null, V.getFirstDart());
        Assert.assertEquals(1, g.getFaceNum());
        Assert.assertEquals(0, F.getDegree());
        Assert.assertEquals(null, F.getFirstDart());
    }

    @Test
    public void testFlattenOrigin() {
        g.flatten();
        Assert.assertEquals(null, findDartByID(g, 19));
        Assert.assertEquals(null, findDartByID(g, 21));

        Dart d13 = findDartByID(g, 13);
        Vertex V = d13.getTail();
        Assert.assertEquals(5, V.ID);
        Assert.assertEquals(3, V.getDegree());
        int[] incidentVertexID = new int[]{4, 0, 2};
        checkIncidentListOfVertex(V, incidentVertexID);

        Vertex F = d13.getRight();
        Assert.assertEquals(1, F.ID);
        Assert.assertEquals(4, F.getDegree());
        int[] boundaryVertexID = new int[]{3, 2, 5, 4};
        checkIncidentListOfFace(F, boundaryVertexID);
    }

    @Test
    public void testFlatten() {
        Dart[] darts = new Dart[22];
        for (Vertex v : g.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                darts[d.ID] = d;
            }
        }
        g.deleteEdge(darts[16]);
        g.deleteEdge(darts[2]);
        g.deleteEdge(darts[0]);
        g.contractEdge(darts[4]);
        g.contractEdge(darts[15]);
        g.contractEdge(darts[11]);
        g.contractEdge(darts[13]);

        g.flatten();
        Assert.assertEquals(null, findDartByID(g, 18));
        Assert.assertEquals(null, findDartByID(g, 8));
        Assert.assertEquals(null, findDartByID(g, 20));
        Dart d6 = findDartByID(g, 6);
        Assert.assertEquals(darts[6], d6);
        Vertex V = d6.getTail();
        Assert.assertEquals(2, g.getVertexNum());
        Assert.assertEquals(1, V.getDegree());
        int[] incidentVertexID = new int[]{2};
        checkIncidentListOfVertex(V, incidentVertexID);

        Vertex F = d6.getLeft();
        Assert.assertEquals(1, g.getFaceNum());
        Assert.assertEquals(2, F.getDegree());
        int[] boundaryVertexID = new int[]{5, 2};
        checkIncidentListOfFace(F, boundaryVertexID);
    }

}
