import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;

/**
 * Created by qixinzhu on 10/23/17.
 */
public class test_SelfDualGraph {
    private SelfDualGraph g;
    @Before
    public void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./input_data/test_graph_0.txt");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testElementNum() {
        Assert.assertEquals(6, g.getVerticeNum());
        Assert.assertEquals(7, g.getFaseNum());
    }
    @Test
    public void testDegree() {
        int[] vertexDegree = new int[] {4, 2, 4, 3, 3, 6};
        int[] faceDegree = new int[] {4, 5, 2, 1, 3, 3, 4};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(vertexDegree[v.ID], v.getDegree());
        }
        for (Vertex f : g.getFaces()) {
            Assert.assertEquals(faceDegree[f.ID], f.getDegree());
        }
    }

    @Test
    public void testReverse() {
        for (Vertex v : g.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                Assert.assertEquals(1, Math.abs(d.ID - d.getReverse().ID));
            }
        }
    }

    @Test
    public void testDartNextPrevTailHead() {
        int[][] boundaryVertexIDs = new int[][]{
                {0, 2, 3, 1, 0},
                {3, 2, 5, 5, 4, 3},
                {5, 2, 5},
                {5, 5},
                {5, 2, 0, 5},
                {4, 5, 0, 4},
                {4, 0, 1, 3, 4}
        };
        for (Vertex face : g.getFaces()) {
            Dart d = face.getFirstDart();
            Assert.assertEquals(boundaryVertexIDs[face.ID].length-1, face.getDegree());
            Assert.assertEquals(boundaryVertexIDs[face.ID][0], d.getTail().ID);
            Assert.assertEquals(boundaryVertexIDs[face.ID][0], d.getPrev().getHead().ID);
            for (int i=0; i<face.getDegree(); i++) {
                Assert.assertEquals(boundaryVertexIDs[face.ID][i+1], d.getHead().ID);
                d = d.getNext();
                Assert.assertEquals(boundaryVertexIDs[face.ID][i], d.getPrev().getTail().ID);
            }
        }
    }

    @Test
    public void testDartLeftRight() {
        int[][] leftFaceIDs = new int[][] {
                {4, 1, 6, 6},
                {0, 2, 3, 5, 6},
                {1, 4},
                {1},
                {2, 0, 5},
                {1, 4, 6},
                {5, 0, 0, 1}
        };
        for (Vertex face : g.getFaces()) {
            int i = 0;
            for (Dart d : face.getIncidenceList()) {
                Assert.assertEquals(face, d.getRight());
                Assert.assertEquals(leftFaceIDs[face.ID][i++], d.getLeft().ID);
            }
        }
    }

    @Test
    public void testDartSuccPred() {
        for (Vertex v : g.getVertices()) {
            Dart cur = v.getFirstDart();
            for (Dart d : v.getIncidenceList()) {
                Assert.assertEquals(d, cur);
                cur = cur.getSuccessor();
                Assert.assertEquals(d, cur.getPredecessor());
            }
            Assert.assertEquals(v.getFirstDart(), cur);
        }
    }
}
