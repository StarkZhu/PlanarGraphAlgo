package selfdualgraph;

import java.io.*;
import java.util.*;

/**
 * Created by qixinzhu on 10/23/17.
 */
public class SelfDualGraph {
    private Set<Vertex> vertices;
    private Set<Vertex> faces;
    //private Set<Dart> edges;

    public SelfDualGraph(int V, int E, int F) {
        vertices = new HashSet<>(V);
        faces = new HashSet<>(F);
        //edges = new HashSet<>(E);
    }

    public SelfDualGraph() {
        this(10,30,10);
    }

    public void buildGraph(String fileName) throws FileNotFoundException {
        Scanner graphInput = new Scanner(new File(fileName));
        int V = graphInput.nextInt();
        int E = graphInput.nextInt();
        int F = graphInput.nextInt();
        assert V + F - E == 2;

        Vertex[] verticesArr = new Vertex[V];
        Dart[] dartsArr = new Dart[E];
        int[] dartRev = new int[E];

        // read and create all vertices
        for (int i=0; i<V; i++) {
            int id = graphInput.nextInt();
            float coordX = graphInput.nextFloat();
            float coordY = graphInput.nextFloat();
            verticesArr[i] = new Vertex(id, Vertex.VERTEX, coordX, coordY);
            vertices.add(verticesArr[i]);
        }

        // read and create all darts
        for (int i=0; i<E; i++) {
            int id = graphInput.nextInt();
            dartRev[i] = graphInput.nextInt();
            int t = graphInput.nextInt();
            int h = graphInput.nextInt();
            dartsArr[i] = new Dart(id, verticesArr[t], verticesArr[h]);
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
        graphInput.nextLine();

        // read all faces and set incident darts: next, prev, right
        for (int i=0; i<F; i++) {
            String[] content = graphInput.nextLine().split("\\s+");
            assert content.length >= 2;
            Vertex face = new Vertex(Integer.parseInt(content[0]), Vertex.FACE);
            Dart cur = dartsArr[Integer.parseInt(content[1])];
            face.addDart(cur);
            for (int j=2; j<content.length; j++) {
                Dart next = dartsArr[Integer.parseInt(content[j])];
                face.addDart(next);
                cur.setNext(next);
                next.setPrev(cur);
                cur.setRight(face);
                cur = next;
            }
            cur.setNext(face.getFirstDart());
            face.getFirstDart().setPrev(cur);
            cur.setRight(face);
            faces.add(face);
        }

        // set successors, predecessors and left
        for (Vertex v : verticesArr) {
            System.out.println(v);
            assert v.getDegree() == 1;
            Dart cur = v.getFirstDart();
            Dart succ = cur.getPrev().getReverse();
            while (succ != v.getFirstDart()) {
                cur.setSuccessor(succ);
                cur.setLeft(succ.getRight());
                succ.setPredecessor(cur);
                v.addDart(succ);
                cur = succ;
                succ = succ.getPrev().getReverse();
            }
            cur.setSuccessor(succ);
            cur.setLeft(succ.getRight());
            succ.setPredecessor(cur);
        }
    }

    public int getVerticeNum() {
        return vertices.size();
    }

    public int getFaseNum() {
        return faces.size();
    }

    public Set<Vertex> getVertices() {
        return new HashSet<>(vertices);
    }

    public Set<Vertex> getFaces() {
        return new HashSet<>(faces);
    }


    public static void main(String[] args) throws FileNotFoundException {
        // for debug only
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/test_graph_0.txt");
        Set<Vertex> vertices = g.getVertices();
        for (Vertex v :vertices) System.out.println(v);

        Set<Vertex> faces = g.getFaces();
        for (Vertex f :faces) System.out.println(f);
    }
}
