import org.junit.*;
import selfdualgraph.*;

import java.io.*;
import java.util.*;

/**
 * Created by qixinzhu on 10/23/17.
 */
public class test_SelfDualGraph_basic {
    private static SelfDualGraph g;

    @BeforeClass
    /**
     * runs once before the entire test
     * multiple tests need to share the same computationally expensive setup code
     */
    public static void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./input_data/test_graph_0.txt");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
    }

    public Map<Integer, double[]>[] readTxtGraphFile(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        Map<Integer, double[]>[] maps = new HashMap[3];
        int vNum = in.nextInt();
        int dNum = in.nextInt();
        int fNum = in.nextInt();
        in.nextLine();
        maps[0] = new HashMap<>(vNum);
        for (int i = 0; i < vNum; i++) {
            String[] content = in.nextLine().split(" ");
            int ID = Integer.parseInt(content[0]);
            double[] values = new double[3];
            for (int j = 0; j < 3; j++) {
                if (j < content.length - 1) values[j] = Double.parseDouble(content[j + 1]);
                else values[j] = 1.0;
            }
            maps[0].put(ID, values);
        }

        maps[1] = new HashMap<>(dNum);
        for (int i = 0; i < dNum; i++) {
            String[] content = in.nextLine().split(" ");
            int ID = Integer.parseInt(content[0]);
            double[] values = new double[5];
            for (int j = 0; j < 5; j++) {
                if (j < content.length - 1) values[j] = Double.parseDouble(content[j + 1]);
                else values[j] = 1.0;
            }
            maps[1].put(ID, values);
        }

        maps[2] = new HashMap<>(fNum);
        for (int i = 0; i < fNum; i++) {
            String[] content = in.nextLine().split(" ");
            int ID = Integer.parseInt(content[0]);
            int deg = Integer.parseInt(content[1]);
            double[] values = new double[2 + deg];
            for (int j = 0; j < 1 + deg; j++) {
                values[j] = Double.parseDouble(content[j + 1]);
            }
            values[deg + 1] = content.length >= deg + 3 ? Double.parseDouble(content[deg + 4]) : 1.0;
            maps[2].put(ID, values);
        }
        return maps;
    }

    protected Vertex findVertexByID(Set<Vertex> vertices, int id) {
        for (Vertex v : vertices) {
            if (v.getID() == id) return v;
        }
        return null;
    }


    @Test
    public void testElementNum() {
        Assert.assertEquals(6, g.getVertexNum());
        Assert.assertEquals(7, g.getFaceNum());
    }

    @Test
    public void testDegree() {
        int[] vertexDegree = new int[]{4, 2, 4, 3, 3, 6};
        int[] faceDegree = new int[]{4, 5, 2, 1, 3, 3, 4};
        for (Vertex v : g.getVertices()) {
            Assert.assertEquals(vertexDegree[v.getID()], v.getDegree());
        }
        for (Vertex f : g.getFaces()) {
            Assert.assertEquals(faceDegree[f.getID()], f.getDegree());
        }
    }

    @Test
    public void testIncidenceListOfVertices() {
        int[][] neighbors = new int[][]{{1, 2, 5, 4},
                {0, 3},
                {0, 3, 5, 5},
                {1, 4, 2},
                {0, 5, 3},
                {4, 0, 2, 2, 5, 5}};
        for (int i = 0; i <= 5; i++) {
            Vertex v = findVertexByID(g.getVertices(), i);
            List<Dart> inciList = v.getIncidenceList();
            Assert.assertEquals(neighbors[i].length, inciList.size());
            int j = 0;
            for (Dart d : inciList) {
                Assert.assertEquals(neighbors[i][j], d.getHead().getID());
                j++;
            }
        }
    }

    @Test
    public void testIncidenceListOfFaces() {
        int[][] neighbors = new int[][]{{2, 3, 1, 0},
                {2, 5, 5, 4, 3},
                {2, 5},
                {5},
                {2, 0, 5},
                {5, 0, 4},
                {0, 1, 3, 4}};
        for (int i = 0; i <= 6; i++) {
            Vertex f = findVertexByID(g.getFaces(), i);
            List<Dart> inciList = f.getIncidenceList();
            Assert.assertEquals(neighbors[i].length, inciList.size());
            int j = 0;
            for (Dart d : inciList) {
                Assert.assertEquals(neighbors[i][j], d.getHead().getID());
                j++;
            }
        }
    }

    @Test
    public void testReverse() {
        for (Vertex v : g.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                Assert.assertEquals(1, Math.abs(d.getID() - d.getReverse().getID()));
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
            Assert.assertEquals(boundaryVertexIDs[face.getID()].length - 1, face.getDegree());
            Assert.assertEquals(boundaryVertexIDs[face.getID()][0], d.getTail().getID());
            Assert.assertEquals(boundaryVertexIDs[face.getID()][0], d.getPrev().getHead().getID());
            for (int i = 0; i < face.getDegree(); i++) {
                Assert.assertEquals(boundaryVertexIDs[face.getID()][i + 1], d.getHead().getID());
                d = d.getNext();
                Assert.assertEquals(boundaryVertexIDs[face.getID()][i], d.getPrev().getTail().getID());
            }
        }
    }

    @Test
    public void testDartLeftRight() {
        int[][] leftFaceIDs = new int[][]{
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
                Assert.assertEquals(leftFaceIDs[face.getID()][i++], d.getLeft().getID());
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

    @Test
    public void testSaveToFile() throws FileNotFoundException {
        g.saveToFile("./test/tmp_text.txt");
        Map<Integer, double[]>[] expect = readTxtGraphFile("./input_data/test_graph_0.txt");
        Map<Integer, double[]>[] actual = readTxtGraphFile("./test/tmp_text.txt");
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(expect[i].size(), actual[i].size());
            for (int key : expect[i].keySet()) {
                for (int j = 0; j < expect[i].get(key).length; j++) {
                    double val = expect[i].get(key)[j];
                    Assert.assertEquals(val, actual[i].get(key)[j], 0.001);
                }
            }
        }
        File tmpFile = new File("./test/tmp_text.txt");
        tmpFile.delete();
    }
}
