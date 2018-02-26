package selfdualgraph;

import java.io.*;
import java.util.*;

/**
 * reference: http://jeffe.cs.illinois.edu/teaching/topology17/chapters/02-planar-graphs.pdf
 * <p>
 * Self-dual data structure for planar graphs
 * is an overlay of the sorted incidence lists of primal graph G and its dual G*
 * <p>
 * Each vertex V, stores an incidence list of dart d, whose tail is V, in the counter-clockwise order
 * Each dual-vertex (face) F, stores an incidence list of dart d, whose right is F
 */

// TODO : change V's list to head(d) == V, be consistent with original definition

public class SelfDualGraph {
    private Set<Vertex> vertices;
    private Set<Vertex> faces;
    private Map<Integer, Vertex> idToVertex;
    private Map<Integer, Vertex> idToFace;
    private Set<Vertex> boundary;

    public SelfDualGraph(int V, int E, int F) {
        vertices = new HashSet<>(V);
        faces = new HashSet<>(F);
        boundary = new HashSet<>();
    }

    public SelfDualGraph() {
        this(10, 30, 10);
    }


    /**
     * Build self-dual planar graph from given file
     * file format see input_format.txt
     *
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
        for (int i = 0; i < V; i++) {
            String[] content = graphInput.nextLine().split("\\s+");
            int id = Integer.parseInt(content[0]);
            float coordX = Float.parseFloat(content[1]);
            float coordY = Float.parseFloat(content[2]);
            double weight = content.length > 3 ? Double.parseDouble(content[3]) : 1.0;
            verticesArr[id] = new Vertex(id, Vertex.VERTEX, coordX, coordY, weight);
            vertices.add(verticesArr[id]);
        }

        // read and create all darts
        for (int i = 0; i < E; i++) {
            String[] content = graphInput.nextLine().split("\\s+");
            int id = Integer.parseInt(content[0]);
            dartRev[id] = Integer.parseInt(content[1]);
            int t = Integer.parseInt(content[2]);
            int h = Integer.parseInt(content[3]);
            double weight = content.length > 4 ? Double.parseDouble(content[4]) : 1.0;
            double capacity = content.length > 5 ? Double.parseDouble(content[5]) : 1.0;
            dartsArr[id] = new Dart(id, verticesArr[t], verticesArr[h], weight, capacity);
            if (verticesArr[t].getDegree() == 0) {
                verticesArr[t].initDart(dartsArr[i]);
            }
        }

        // set all darts' reverse dart, check for inconsistency
        for (int i = 0; i < E; i++) {
            Dart rev = dartsArr[dartRev[i]];
            if (rev.getReverse() != null && rev.getReverse() != dartsArr[i])
                throw new RuntimeException("The other dart has a different reverse dart.");
            dartsArr[i].setReverse(rev);
        }

        // read all faces and set incident darts: next, prev, right
        for (int i = 0; i < F; i++) {
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
            for (int j = 1; j < degree; j++) {
                Dart next = dartsArr[Integer.parseInt(content[j + 2])];
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
                coordX = Float.parseFloat(content[degree + 2]);
                coordY = Float.parseFloat(content[degree + 3]);
            } else {
                coordX /= degree;
                coordY /= degree;
            }
            face.setCoordX(coordX);
            face.setCoordY(coordY);
            if (content.length > degree + 4) {
                face.setWeight(Double.parseDouble(content[degree + 4]));
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

    public boolean contains(Vertex v) {
        return vertices.contains(v) || faces.contains(v);
    }

    /**
     * delete an undirected edge, which performs the following actions:
     * (1) delete the given dart d and rev(d)
     * (2) merge the faces of left(d) and right(d), set all incidental darts' left() and right(), time O(degree)
     * (3) fix the pointers on neighboring darts of d and rev(d): next, prev, successor, predecessor
     *
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
     *
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
        // edge case: both left(d) and right(d) has degree 1 before deletion
        if (faceToKeep.getDegree() == 0) {
            // 0-degree face doesn't point to any incidental dart
            faceToKeep.setDart(null);
        } else if (faceToKeep.getFirstDart() == dart) {
            faceToKeep.setDart(dart.getNext());
        }
        Vertex v = dart.getTail();
        if (v.getDegree() == 0) {
            v.setDart(null);
        } else if (v.getFirstDart() == d || v.getFirstDart() == d.getReverse()) {
            v.setDart(dart.getPredecessor());
        }
    }

    /**
     * contract an undirected edge, which performs the following actions:
     * (1) delete the given dart d and rev(d)
     * (2) merge the vertices of head(d) and tail(d), set all incidental darts' head() and tail(), time O(degree)
     * (3) fix the pointers on neighboring darts of d and rev(d): next, prev, successor, predecessor
     *
     * @param d: user should make sure d is NOT be a self-loop, otherwise the contract operation is not well defined
     */
    public void contractEdge(Dart d) {
        if (d.getHead() == d.getTail()) {
            throw new RuntimeException("Contraction is not well defined on a self-loop dart!");
        }
        if (d.getLeft() == d.getRight()) {
            contractBridge(d);
            return;
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
        f = d.getRight();
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

    /**
     * contract a bridge, aka d.left == d.right
     *
     * @param d
     */
    private void contractBridge(Dart d) {
        Vertex vertexToKeep, vertexToDelete;
        if (d.getTail().getDegree() == 1) {
            vertexToDelete = d.getTail();
            vertexToKeep = d.getHead();
        } else {
            vertexToDelete = d.getHead();
            vertexToKeep = d.getTail();
        }
        vertices.remove(vertexToDelete);
        Dart dart = d.getHead() == vertexToDelete ? d : d.getReverse();
        //
        dart.getPredecessor().setSuccessor(dart.getSuccessor());
        dart.getSuccessor().setPredecessor(dart.getPredecessor());
        dart.getPrev().setNext(dart.getReverse().getNext());
        dart.getReverse().getNext().setPrev(dart.getPrev());
        //

        vertexToKeep.incrementDegree(-1);
        dart.getLeft().incrementDegree(-2);
        // edge case: both tail(d) and head(d) has degree 1 before deletion
        if (vertexToKeep.getDegree() == 0) {
            // 0-degree vertex doesn't point to any incidental dart
            vertexToKeep.setDart(null);
        } else if (vertexToKeep.getFirstDart() == dart) {
            vertexToKeep.setDart(dart.getSuccessor());
        }
        Vertex f = dart.getLeft();
        if (f.getDegree() == 0) {
            f.setDart(null);
        } else if (f.getFirstDart() == d || f.getFirstDart() == d.getReverse()) {
            f.setDart(dart.getPrev());  //??
        }
    }

    /**
     * delete all redundant edges from the graph without changing its minimum spanning tree
     * redundant: a loop or non-lightest (parallel) edge between its endpoints
     */
    public void flatten() {
        // delete all self-loop first
        for (Vertex v : vertices) {
            Set<Dart> toDelete = new HashSet<>();
            for (Dart d : v.getIncidenceList()) {
                if (d.getTail() == d.getHead() && !toDelete.contains(d.getReverse())) {
                    toDelete.add(d);
                }
            }
            for (Dart d : toDelete) {
                deleteLoop(d);
            }
        }
        // delete all parallel darts
        for (Vertex v : vertices) {
            if (v.getDegree() < 2) continue;
            Set<Dart> toDelete = new HashSet<>();
            Dart d = v.getFirstDart();
            Dart start = d;
            while (true) {
                Dart succ = d.getSuccessor();
                if (succ == start) break;
                if (succ.getHead() != d.getHead()) {
                    d = succ;
                    continue;
                }
                // TODO: fix bug for self-loop
                int tmp = 0;
                while (succ.getHead() == d.getHead() && succ != start) {
                    tmp++;
                    if (tmp >= 10000) {
                        System.out.println("bad");
                    }
                    if (succ.getWeight() >= d.getWeight()) {
                        toDelete.add(succ);
                        succ = succ.getSuccessor();
                    } else {
                        toDelete.add(d);
                        d = succ;
                        succ = succ.getSuccessor();
                    }
                }
                if (succ == start) break;
                d = succ;
            }
            for (Dart d_delete : toDelete) {
                deleteEdge(d_delete);
            }
        }
    }

    /**
     * given 2 darts, add a pair of darts between their tail vertices
     * divide the face to the right of the given 2 darts into 2 faces
     * worst case time complexity O(deg(new-face)), best performance when new-face is a triangle
     * [WARNING] this function does NOT support adding loops, as this is not needed in graph triangulation
     *
     * @param tail
     * @param head
     */
    public void addEdge(Dart tail, Dart head) {
        // verify given darts are on the same face
        if (tail.getRight() != head.getRight()) {
            throw new RuntimeException("Given darts must have the same face on their right, adding self-loop is NOT supported");
        }
        // create a new face and set its degree
        head.getRight().setDart(head);
        Vertex newFace = new Vertex(Vertex.FACE);
        this.faces.add(newFace);
        newFace.initDart(tail);
        tail.setRight(newFace);
        tail.getReverse().setLeft(newFace);
        int degreeInc = 0;
        Dart cur = tail;
        while (cur != head) {
            cur.setRight(newFace);
            cur.getReverse().setLeft(newFace);
            degreeInc++;
            cur = cur.getNext();
        }
        newFace.incrementDegree(degreeInc);
        head.getRight().incrementDegree(1 - degreeInc);
        // create a pair of new darts
        Dart d = new Dart(tail.getTail(), head.getTail());
        Dart rev = new Dart(head.getTail(), tail.getTail());
        d.setRight(head.getRight());
        d.setLeft(newFace);
        d.setReverse(rev);
        d.setPrev(tail.getPrev());
        d.setNext(head);
        d.setSuccessor(tail);
        d.setPredecessor(tail.getPredecessor());

        rev.setRight(newFace);
        rev.setLeft(head.getRight());
        rev.setReverse(d);
        rev.setPrev(head.getPrev());
        rev.setNext(tail);
        rev.setSuccessor(head);
        rev.setPredecessor(head.getPredecessor());

        tail.getPrev().setNext(d);
        tail.getPredecessor().setSuccessor(d);
        tail.setPredecessor(d);
        tail.setPrev(rev);

        head.getPrev().setNext(rev);
        head.getPredecessor().setSuccessor(rev);
        head.setPredecessor(rev);
        head.setPrev(d);

        tail.getTail().incrementDegree();
        head.getTail().incrementDegree();
    }

    /**
     * triangulate the entire graph
     * any face with degree more than 3 will be divided by adding edges between a selected vertex and all non-adjacent vertices
     */
    public void triangulate() {
        Set<Vertex> oldFaces = new HashSet<>(faces);
        for (Vertex face : oldFaces) {
            if (face.getDegree() <= 3) continue;
            Dart tail = face.getFirstDart();
            //if (tail.getHead().getDegree() == 1) tail = tail.getNext();
            Dart head = tail.getNext().getNext();
            while (head.getHead() != tail.getTail()) {
                addEdge(tail, head);
                tail = tail.getPredecessor();
                head = head.getNext();
            }
        }
    }

    /**
     * save the current planar graph to test file, for future read and re-use
     *
     * @param fileName
     * @throws FileNotFoundException
     */
    public void saveToFile(String fileName) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(fileName);
        int vNum = vertices.size();
        int fNum = faces.size();
        int eNum = 0;
        for (Vertex face : faces) {
            eNum += face.getDegree();
        }
        out.printf("%d %d %d\n", vNum, eNum, fNum);
        List<Vertex> vList = new ArrayList<>(vertices);
        vList.sort(null);

        for (Vertex v : vList) {
            out.printf("%d %f %f %f\n", v.getID(), v.getCoordX(), v.getCoordY(), v.getWeight());
        }
        for (Vertex v : vList) {
            for (Dart d : v.getIncidenceList()) {
                out.printf("%d %d %d %d %f %f\n", d.getID(), d.getReverse().getID(), d.getTail().getID(), d.getHead().getID(), d.getWeight(), d.getCapacity());
            }
        }

        List<Vertex> fList = new ArrayList<>(faces);
        fList.sort(null);
        for (Vertex f : fList) {
            StringBuilder sb = new StringBuilder(String.format("%d %d", f.getID(), f.getDegree()));
            for (Dart d : f.getIncidenceList()) {
                sb.append(String.format(" %d", d.getID()));
            }
            sb.append(String.format(" %f %f %f\n", f.getCoordX(), f.getCoordX(), f.getWeight()));
            out.print(sb.toString());
        }
        out.close();
    }

    /**
     * re-numbering all vertices, darts, faces IDs
     * keep all positive IDs, find the maximum
     * all negative IDs take absolute value and add previous maximum to it
     */
    public void renumberIDs() {
        int vID = 0;
        int dID = 0;
        for (Vertex v : vertices) {
            v.setID(vID++);
            for (Dart d : v.getIncidenceList()) {
                d.setID(dID++);
            }
        }
        int fID = 0;
        for (Vertex f : faces) {
            f.setID(fID++);
        }
    }

    /**
     * add a vertex on the given face, connect the new vertex to all vertices incidental to the face
     *
     * @param face
     */
    public Vertex addVertex(Vertex face) {
        Vertex vertex = new Vertex(Vertex.VERTEX);
        vertices.add(vertex);

        Dart d = face.getFirstDart();
        Dart out = new Dart(vertex, d.getTail());
        Dart in = new Dart(d.getTail(), vertex);
        vertex.initDart(out);
        face.incrementDegree(2);

        in.setReverse(out);
        in.setLeft(face);
        in.setRight(face);
        in.setPredecessor(d.getPredecessor());
        in.setSuccessor(d);
        in.setNext(out);
        in.setPrev(d.getPrev());

        out.setReverse(in);
        out.setLeft(face);
        out.setRight(face);
        out.setPredecessor(out);
        out.setPredecessor(out);
        out.setPrev(in);
        out.setNext(d);

        d.getTail().incrementDegree();
        d.getPredecessor().setSuccessor(in);
        d.setPredecessor(in);
        d.getPrev().setNext(in);
        d.setPrev(out);

        d = d.getNext();
        Dart tail = out;
        while (d != in) {
            addEdge(tail, d);
            d = d.getNext();
            tail = tail.getPredecessor();
        }
        return vertex;
    }

    public void resetAllToUnvisited() {
        // set every vertex, face, dart to be unvisited
        for (Vertex v : vertices) {
            v.setVisited(false);
            for (Dart d : v.getIncidenceList()) {
                d.setVisited(false);
            }
        }
        for (Vertex f : faces) {
            f.setVisited(false);
        }
    }

    /**
     * build subgraph by copy-construct all vertices, darts and faces
     * incidence list of vertices may be changed to exclude vertices not in subgraph
     *
     * @param subgraphV all vertices are object of this graph
     * @param separator
     * @return
     */
    public SelfDualGraph buildSubgraph(Set<Vertex> subgraphV, Set<Vertex> separator) {
        Set<Vertex> tmp = new HashSet<>(subgraphV);
        tmp.removeAll(separator);
        Set<Vertex> subB = separator;
        if (tmp.size() > 0) {
            Vertex src = tmp.iterator().next();
            subB = findBoundary(src, subgraphV, separator);
        }

        SelfDualGraph subgraph = new SelfDualGraph();
        // map old Vertice, Darts, Faces to new graph
        Map<Vertex, Vertex> vMap = new HashMap<>();
        Map<Vertex, Vertex> fMap = new HashMap<>();
        Map<Dart, Dart> dMap = new HashMap<>();
        for (Vertex v : subgraphV) {
            Vertex v2 = new Vertex(v);
            subgraph.vertices.add(v2);
            vMap.put(v, v2);
            for (Dart d : v.getIncidenceList()) {
                if (subgraphV.contains(d.getHead())) dMap.put(d, new Dart(d));
            }
        }
        for (Vertex f : faces) {
            if (isInSubgraph(f, dMap)) {
                Vertex f2 = new Vertex(f);
                f2.incrementDegree(f.getDegree());
                subgraph.faces.add(f2);
                fMap.put(f, f2);
            }
        }
        for (Vertex v : subB) subgraph.boundary.add(vMap.get(v));

        // set darts for vertices and faces
        for (Vertex v : vMap.keySet()) {
            Vertex v2 = vMap.get(v);
            Dart first = v.getFirstDart();
            while (!dMap.containsKey(first)) first = first.getSuccessor();
            v2.setDart(dMap.get(first));
        }
        for (Vertex f : fMap.keySet()) {
            Vertex f2 = fMap.get(f);
            Dart first = f.getFirstDart();
            while (!dMap.containsKey(first)) first = first.getNext();
            f2.setDart(dMap.get(first));
        }
        // set pointers for darts
        for (Dart d : dMap.keySet()) {
            Dart d2 = dMap.get(d);
            d2.setTail(vMap.get(d.getTail()));
            d2.setHead(vMap.get(d.getHead()));
            d2.setReverse(dMap.get(d.getReverse()));
            d2.getTail().incrementDegree();
            // successor & predecessor
            Dart succ = d.getSuccessor();
            while (!dMap.containsKey(succ)) succ = succ.getSuccessor();
            d2.setSuccessor(dMap.get(succ));
            Dart pred = d.getPredecessor();
            while (!dMap.containsKey(pred)) pred = pred.getPredecessor();
            d2.setPredecessor(dMap.get(pred));
        }
        // next & prev are easy when all succ and pred are set
        for (Dart d : dMap.keySet()) {
            Dart d2 = dMap.get(d);
            d2.setNext(d2.getReverse().getSuccessor());
            d2.setPrev(d2.getPredecessor().getReverse());
            d2.setLeft(fMap.getOrDefault(d.getLeft(), null));
            d2.setRight(fMap.getOrDefault(d.getRight(), null));
        }
        // examine all boundary darts and create boundary faces
        for (Dart d2 : dMap.values()) {
            if (d2.getRight() == null) {
                Vertex f = new Vertex(Vertex.FACE);
                subgraph.faces.add(f);
                f.setDart(d2);
                while (d2.getRight() == null) {
                    d2.setRight(f);
                    d2.getReverse().setLeft(f);
                    f.incrementDegree();
                    d2 = d2.getNext();
                }
            }
        }
        return subgraph;
    }

    private boolean isInSubgraph(Vertex face, Map<Dart, Dart> dMap) {
        if (face.type != Vertex.FACE) throw new RuntimeException("Func only applies to faces");
        for (Dart d : face.getIncidenceList()) {
            if (!dMap.containsKey(d)) return false;
        }
        return true;
    }

    public Set<Vertex> findBoundary(Vertex src, Set<Vertex> subgraph, Set<Vertex> separator) {
        if (src == null) throw new RuntimeException("Source vertex is NULL");
        if (separator.contains(src)) throw new RuntimeException("Source vertex is on the boundary.");
        for (Vertex v : subgraph) v.setVisited(false);
        Set<Vertex> boundary = new HashSet<>();
        Queue<Vertex> q = new LinkedList<>();
        q.add(src);
        src.setVisited(true);
        while (!q.isEmpty()) {
            Vertex v = q.poll();
            for (Dart d : v.getIncidenceList()) {
                Vertex u = d.getHead();
                if (!u.isVisited()) {
                    u.setVisited(true);
                    if (separator.contains(u)) {
                        boundary.add(u);
                    } else {
                        q.add(u);
                        if (this.boundary.contains(u)) boundary.add(u);
                    }
                }
            }
        }
        return boundary;
    }

    public int getBoundarySize() {
        return boundary.size();
    }

    public Set<Vertex> getBoundary() {
        return new HashSet<>(boundary);
    }

    public void addToBoundary(Set<Vertex> bVs) {
        boundary.addAll(bVs);
    }

    public Set<Vertex> getVerticesFromID(Set<Integer> ids) {
        if (idToVertex == null) {
            idToVertex = new HashMap<>();
            for (Vertex v : vertices) idToVertex.put(v.getID(), v);
        }
        Set<Vertex> vs = new HashSet<>();
        for (int i : ids) vs.add(idToVertex.get(i));
        return vs;
    }

    /**
     * Find all darts of V whose head is also on boundary
     *
     * @param v must be on graph's boundary
     * @return
     */
    public Set<Dart> vertexNeighborOnBoundary(Vertex v) {
        Set<Dart> darts = new HashSet<>();
        if (!boundary.contains(v)) return darts;

        Dart d = v.getFirstDart();
        if (boundary.contains(d.getHead())) darts.add(d);
        Dart d2 = d.getSuccessor();
        while (d2 != d) {
            if (boundary.contains(d2.getHead())) darts.add(d2);
            d2 = d2.getSuccessor();
        }
        return darts;
    }


    /**
     * Split vertex weight on boundary darts, then half-half to its left/right faces
     */
    public void assignWeightToBoundary_useDart() {
        for (Vertex f : getFaces()) f.setWeight(0);
        for (Vertex v : vertices) {
            if (!boundary.contains(v)) v.setWeight(0);
            else {  // v is on the boundary
                v.setWeight(1);
                Set<Dart> bDarts = vertexNeighborOnBoundary(v);
                for (Dart d : bDarts) {
                    Vertex left = d.getLeft();
                    left.setWeight(0.5 / bDarts.size() + left.getWeight());
                    Vertex right = d.getRight();
                    right.setWeight(0.5 / bDarts.size() + right.getWeight());
                }
            }
        }
    }

    /**
     * Split vertex weight on all neighboring faces
     */
    public void assignWeightToBoundary_useVertex() {
        for (Vertex f : getFaces()) f.setWeight(0);
        for (Vertex v : vertices) {
            if (!boundary.contains(v)) v.setWeight(0);
            else {  // v is on the boundary
                v.setWeight(1);
                for (Dart d : v.getIncidenceList()) {
                    Vertex left = d.getLeft();
                    left.setWeight(0.5 / v.getDegree() + left.getWeight());
                    Vertex right = d.getRight();
                    right.setWeight(0.5 / v.getDegree() + right.getWeight());
                }
            }
        }
    }

    // for debug only
    public static void main(String[] args) throws FileNotFoundException {
        /*
        SelfDualGraph g = new SelfDualGraph();
        g.buildGraph("./input_data/grids/5.txt");
        System.out.println(g.getFaceNum());
        System.out.println(g.getVertexNum());
        */
        System.out.println(-1 % 5);
    }

}
