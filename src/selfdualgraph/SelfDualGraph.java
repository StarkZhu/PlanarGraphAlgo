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
                verticesArr[t].initDart(dartsArr[i]);
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
            face.initDart(cur);
            // read all incidental darts of given face
            for (int j=1; j<degree; j++) {
                Dart next = dartsArr[Integer.parseInt(content[j+2])];
                face.incrementDegree();
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
                v.incrementDegree();
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

    /**
     * delete an undirected edge, which performs the following actions:
     * (1) delete the given dart d and rev(d)
     * (2) merge the faces of left(d) and right(d), set all incidental darts' left() and right(), time O(degree)
     * (3) fix the pointers on neighboring darts of d and rev(d): next, prev, successor, predecessor
     * @param d: user should make sure d is NOT be a bridge, otherwise G will be disconnected
     */
    public void deleteEdge(Dart d) {
        if (d.getHead() == d.getTail()) {
            deleteLoop(d);
            return;
        }
        // delete the face with less degree
        Vertex faceToKeep, faceToDelete;
        if (d.getLeft().getDegree() > d.getRight().getDegree()) {
            faceToKeep = d.getLeft();
            faceToDelete = d.getRight();
        } else {
            faceToKeep = d.getRight();
            faceToDelete = d.getLeft();
        }
        // merge 2 faces
        for (Dart dart : faceToDelete.getIncidenceList()) {
            dart.setRight(faceToKeep);
            dart.getReverse().setLeft(faceToKeep);
        }
        faces.remove(faceToDelete);
        faceToKeep.incrementDegree(faceToDelete.getDegree() - 2);
        d.getTail().incrementDegree(-1);
        d.getHead().incrementDegree(-1);
        // may need to update the first dart pointed by a vertex/face
        if (faceToKeep.getFirstDart() == d || faceToKeep.getFirstDart() == d.getReverse()) {
            faceToKeep.setDart(d.getNext());
        }
        Vertex v = d.getTail();
        if (v.getFirstDart() == d) {
            v.setDart(d.getSuccessor());
        }
        v = d.getHead();
        if (v.getFirstDart() == d.getReverse()) {
            v.setDart(d.getNext());
        }
        // re-direct all pointers
        d.getPrev().setNext(d.getReverse().getNext());
        d.getNext().setPrev(d.getReverse().getPrev());
        d.getPredecessor().setSuccessor(d.getSuccessor());
        d.getSuccessor().setPredecessor(d.getPredecessor());

        d.getReverse().getPrev().setNext(d.getNext());
        d.getReverse().getNext().setPrev(d.getPrev());
        d.getReverse().getPredecessor().setSuccessor(d.getReverse().getSuccessor());
        d.getReverse().getSuccessor().setPredecessor(d.getReverse().getPredecessor());
    }

    /**
     * delete a self-loop, aka d.tail == d.head
     * @param d
     */
    private void deleteLoop(Dart d) {
        Vertex faceToKeep, faceToDelete;
        if (d.getLeft().getDegree() == 1) {
            faceToDelete = d.getLeft();
            faceToKeep = d.getRight();
        } else {
            faceToDelete = d.getRight();
            faceToKeep = d.getLeft();
        }
        faces.remove(faceToDelete);
        Dart dart = d.getLeft() == faceToDelete ? d : d.getReverse();
        dart.getPrev().setNext(dart.getNext());
        dart.getNext().setPrev(dart.getPrev());
        dart.getPredecessor().setSuccessor(dart.getReverse().getSuccessor());
        dart.getReverse().getSuccessor().setPredecessor(dart.getPredecessor());

        faceToKeep.incrementDegree(-1);
        dart.getTail().incrementDegree(-2);
        if (faceToKeep.getFirstDart() == dart) {
            faceToKeep.setDart(dart.getNext());
        }
        Vertex v = dart.getTail();
        if (v.getFirstDart() == d || v.getFirstDart() == d.getReverse()) {
            v.setDart(dart.getSuccessor());
        }
    }

    /**
     * contract an undirected edge, which performs the following actions:
     * (1) delete the given dart d and rev(d)
     * (2) merge the vertices of head(d) and tail(d), set all incidental darts' head() and tail(), time O(degree)
     * (3) fix the pointers on neighboring darts of d and rev(d): next, prev, successor, predecessor
     * @param d: user should make sure d is NOT be a self-loop, otherwise the contract operation is not well defined
     */
    public void contractEdge(Dart d) {
        if (d.getHead() == d.getTail()) {
            throw new RuntimeException("Contraction is not well defined on a self-loop dart!");
        }
        // delete the vertex with less degree
        Vertex vertexToKeep, vertexToDelete;
        if (d.getTail().getDegree() > d.getHead().getDegree()) {
            vertexToKeep = d.getTail();
            vertexToDelete = d.getHead();
        } else {
            vertexToKeep = d.getHead();
            vertexToDelete = d.getTail();
        }
        // merge 2 vertices
        for (Dart dart : vertexToDelete.getIncidenceList()) {
            //if (dart == d || dart == d.getReverse()) continue;
            dart.setTail(vertexToKeep);
            dart.getReverse().setHead(vertexToKeep);
        }
        vertices.remove(vertexToDelete);
        vertexToKeep.incrementDegree(vertexToDelete.getDegree() - 2);
        d.getLeft().incrementDegree(-1);
        d.getRight().incrementDegree(-1);
        // may need to update the first dart pointed by a vertex/face
        if (vertexToKeep.getFirstDart() == d || vertexToKeep.getFirstDart() == d.getReverse()) {
            vertexToKeep.setDart(d.getSuccessor());
        }
        Vertex f = d.getLeft();
        if (f.getFirstDart() == d.getReverse()) {
            f.setDart(d.getSuccessor());
        }
        f = d.getHead();
        if (f.getFirstDart() == d) {
            f.setDart(d.getNext());
        }
        // re-direct all pointers
        d.getPrev().setNext(d.getNext());
        d.getNext().setPrev(d.getPrev());
        d.getPredecessor().setSuccessor(d.getReverse().getSuccessor());
        d.getSuccessor().setPredecessor(d.getReverse().getPredecessor());

        d.getReverse().getPrev().setNext(d.getReverse().getNext());
        d.getReverse().getNext().setPrev(d.getReverse().getPrev());
        d.getReverse().getPredecessor().setSuccessor(d.getSuccessor());
        d.getReverse().getSuccessor().setPredecessor(d.getPredecessor());
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
