package selfdualgraph;

import java.io.*;
import java.util.*;

/**
 * reference: http://jeffe.cs.illinois.edu/teaching/topology17/chapters/02-planar-graphs.pdf
 *
 * Self-dual data structure for planar graphs
 * is an overlay of the sorted incidence lists of primal graph G and its dual G*
 *
 * Each vertex V, stores an incidence list of dart d, whose tail is V, in the counter-clockwise order
 * Each dual-vertex (face) F, stores an incidence list of dart d, whose right is F
 */

// TODO: change V's list to head(d) == V, be consistent with original definition

public class SelfDualGraph {
    private Set<Vertex> vertices;
    private Set<Vertex> faces;

    public SelfDualGraph(int V, int E, int F) {
        vertices = new HashSet<>(V);
        faces = new HashSet<>(F);
    }

    public SelfDualGraph() {
        this(10,30,10);
    }


    /**
     * Build self-dual planar graph from given file
     * file format see input_format.txt
     * @param fileName
     * @throws FileNotFoundException
     */
    public void buildGraph(String fileName) throws FileNotFoundException {
        Scanner graphInput = new Scanner(new File(fileName));
        int V = graphInput.nextInt();
        int E = graphInput.nextInt();
        int F = graphInput.nextInt();
        graphInput.nextLine();

        Vertex[] verticesArr = new Vertex[V];
        Dart[] dartsArr = new Dart[E];
        int[] dartRev = new int[E];

        // read and create all vertices
        for (int i=0; i<V; i++) {
            String[] content = graphInput.nextLine().split("\\s+");
            int id = Integer.parseInt(content[0]);
            float coordX = Float.parseFloat(content[1]);
            float coordY = Float.parseFloat(content[2]);
            double weight = content.length > 3 ? Double.parseDouble(content[3]) : 1.0;
            verticesArr[i] = new Vertex(id, Vertex.VERTEX, coordX, coordY, weight);
            vertices.add(verticesArr[i]);
        }

        // read and create all darts
        for (int i=0; i<E; i++) {
            String[] content = graphInput.nextLine().split("\\s+");
            int id = Integer.parseInt(content[0]);
            dartRev[i] = Integer.parseInt(content[1]);
            int t = Integer.parseInt(content[2]);
            int h = Integer.parseInt(content[3]);
            double weight = content.length > 4 ? Double.parseDouble(content[4]) : 1.0;
            double capacity = content.length > 5 ? Double.parseDouble(content[5]) : 1.0;
            dartsArr[i] = new Dart(id, verticesArr[t], verticesArr[h], weight, capacity);
            if (verticesArr[t].getDegree() == 0) {
                verticesArr[t].addDart(dartsArr[i]);
            }
        }

        // set all darts' reverse dart, check for inconsistency
        for (int i=0; i<E; i++) {
            Dart rev = dartsArr[dartRev[i]];
            if (rev.getReverse() != null && rev.getReverse() != dartsArr[i])
                throw new RuntimeException("The other dart has a different reverse dart.");
            dartsArr[i].setReverse(rev);
        }

        // read all faces and set incident darts: next, prev, right
        for (int i=0; i<F; i++) {
            String[] content = graphInput.nextLine().split("\\s+");
            if (content.length < 3)
                throw new RuntimeException("Wrong format for face information.");
            Vertex face = new Vertex(Integer.parseInt(content[0]), Vertex.FACE);
            float coordX = 0, coordY = 0;
            int degree = Integer.parseInt(content[1]);
            Dart cur = dartsArr[Integer.parseInt(content[2])];
            coordX += cur.getTail().getCoordX();
            coordY += cur.getTail().getCoordY();
            face.addDart(cur);
            // read all incidental darts of given face
            for (int j=1; j<degree; j++) {
                Dart next = dartsArr[Integer.parseInt(content[j+2])];
                face.addDart(next);
                cur.setNext(next);
                next.setPrev(cur);
                cur.setRight(face);
                cur = next;
                coordX += cur.getTail().getCoordX();
                coordY += cur.getTail().getCoordY();
            }
            cur.setNext(face.getFirstDart());
            face.getFirstDart().setPrev(cur);
            cur.setRight(face);

            if (content.length > degree + 2) {
                // must provide coordY if coordX is provided
                if (content.length <= degree + 3)
                    throw new RuntimeException("Must provide coordY if coordX is provided.");
                coordX = Float.parseFloat(content[degree+2]);
                coordY = Float.parseFloat(content[degree+3]);
            } else {
                coordX /= degree;
                coordY /= degree;
            }
            face.setCoordX(coordX);
            face.setCoordY(coordY);
            if (content.length > degree + 4) {
                face.setWeight(Double.parseDouble(content[degree+4]));
            }
            faces.add(face);
        }

        // set successors, predecessors and left
        for (Vertex v : verticesArr) {
            assert v.getDegree() == 1;
            Dart cur = v.getFirstDart();
            Dart succ = cur.getReverse().getNext();
            while (succ != v.getFirstDart()) {
                cur.setSuccessor(succ);
                cur.setLeft(succ.getRight());
                succ.setPredecessor(cur);
                v.addDart(succ);
                cur = succ;
                succ = succ.getReverse().getNext();
            }
            cur.setSuccessor(succ);
            cur.setLeft(succ.getRight());
            succ.setPredecessor(cur);
        }
    }


    public int getVertexNum() {
        return vertices.size();
    }

    public int getFaceNum() {
        return faces.size();
    }

    public Set<Vertex> getVertices() {
        return new HashSet<>(vertices);
    }

    public Set<Vertex> getFaces() {
        return new HashSet<>(faces);
    }


    // for debug only
    public static void main(String[] args) throws FileNotFoundException {
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/test_graph_0.txt");
        Set<Vertex> vertices = g.getVertices();
        for (Vertex v :vertices) System.out.println(v);

        Set<Vertex> faces = g.getFaces();
        for (Vertex f :faces) System.out.println(f);

    }

}
