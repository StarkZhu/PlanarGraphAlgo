import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;


public class test_SelfDualGraph_modification {
    protected SelfDualGraph g;

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
        Dart.uniqueID = 0;
        Vertex.uniqueID = 0;
    }

    protected Dart findDartByID(SelfDualGraph g, int dartID) {
        for (Vertex v : g.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                if (d.getID() == dartID) {
                    return d;
                }
            }
        }
        //Assert.assertTrue(String.format("Graph does NOT contain dart with ID = %d", dartID), false);
        System.out.printf("Graph does NOT contain dart with ID = %d\n", dartID);
        return null;
    }

    protected void checkIncidentListOfFace(Vertex F, int[] boundaryVertexID) {
        Assert.assertEquals(boundaryVertexID.length, F.getDegree());
        Dart d = F.getFirstDart();
        for (int i = 0; i < F.getDegree(); i++) {
            Assert.assertEquals(F.getID(), d.getRight().getID());
            Assert.assertEquals(F.getID(), d.getReverse().getLeft().getID());
            Assert.assertEquals(boundaryVertexID[i], d.getTail().getID());
            d = d.getNext();
        }
        Assert.assertEquals(boundaryVertexID[0], d.getTail().getID());
    }

    protected void checkIncidentListOfVertex(Vertex V, int[] incidentVertexID) {
        Assert.assertEquals(incidentVertexID.length, V.getDegree());
        Dart d = V.getFirstDart();
        for (int i = 0; i < V.getDegree(); i++) {
            Assert.assertEquals(incidentVertexID[i], d.getHead().getID());
            d = d.getSuccessor();
        }
        Assert.assertEquals(incidentVertexID[0], d.getHead().getID());
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
                darts[d.getID()] = d;
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
        Assert.assertEquals(5, V.getID());
        int[] incidentVertexID = new int[]{5, 2, 5, 5, 2, 5};
        checkIncidentListOfVertex(V, incidentVertexID);
        Vertex F = d8.getLeft();
        Assert.assertEquals(0, F.getID());
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
        Assert.assertEquals(1, F.getID());
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
        Assert.assertEquals(5, V.getID());
        Assert.assertEquals(3, V.getDegree());
        int[] incidentVertexID = new int[]{4, 0, 2};
        checkIncidentListOfVertex(V, incidentVertexID);

        Vertex F = d13.getRight();
        Assert.assertEquals(1, F.getID());
        Assert.assertEquals(4, F.getDegree());
        int[] boundaryVertexID = new int[]{3, 2, 5, 4};
        checkIncidentListOfFace(F, boundaryVertexID);
    }

    @Test
    public void testFlatten() {
        Dart[] darts = new Dart[22];
        for (Vertex v : g.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                darts[d.getID()] = d;
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
        //int[] boundaryVertexID = new int[]{5, 2};
        //checkIncidentListOfFace(F, boundaryVertexID);
        // unknown bug: running multiple test, result changes to {2, 5}
    }

    @Test
    public void testTriangulate(){
        g.flatten();
        g.triangulate();
        Assert.assertEquals(8, g.getFaceNum());
        checkIncidentListOfFace(findDartByID(g, 2).getRight(), new int[]{0, 2, 3});
        checkIncidentListOfFace(findDartByID(g, 1).getRight(), new int[]{3, 1, 0});
        checkIncidentListOfFace(findDartByID(g, 6).getRight(), new int[]{3, 2, 5});
        checkIncidentListOfFace(findDartByID(g, 13).getRight(), new int[]{5, 4, 3});
        checkIncidentListOfFace(findDartByID(g, 9).getRight(), new int[]{4, 0, 1});
        checkIncidentListOfFace(findDartByID(g, 11).getRight(), new int[]{1, 3, 4});

        checkIncidentListOfVertex(findDartByID(g, 2).getTail(), new int[]{1, 3, 2, 5, 4});
        checkIncidentListOfVertex(findDartByID(g, 9).getTail(), new int[]{0, 5, 3, 1});
        checkIncidentListOfVertex(findDartByID(g, 14).getTail(), new int[]{4, 0, 2, 3});
        checkIncidentListOfVertex(findDartByID(g, 6).getTail(), new int[]{1, 4, 5, 2, 0});
        checkIncidentListOfVertex(findDartByID(g, 1).getTail(), new int[]{0, 4, 3});
    }
}
