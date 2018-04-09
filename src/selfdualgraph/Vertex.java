package selfdualgraph;

import java.util.LinkedList;
import java.util.List;

/**
 * Vertex is a vertex or a face in the primal graph, which is a face or a vertex in the dual graph
 * <p>
 * degree(V): the number of darts whose tail is vertex V
 * degree(F): the number of darts whose right is face F
 */
public class Vertex implements Comparable<Vertex>{
    //static variables
    public static String VERTEX = "V";
    public static String FACE = "F";
    public static int uniqueID = 0;

    public final String type;
    private int ID;
    private float coordX, coordY;
    private double weight, distance;
    private boolean visited;
    private int degree;
    private Dart dart;  // points to an arbitrary dart with tail(d) = current vertex

    public Vertex(int ID, String type, float coordX, float coordY, double weight) {
        this.ID = ID;
        this.type = type;
        this.coordX = coordX;
        this.coordY = coordY;
        this.weight = weight;
        visited = false;
        degree = 0;
        dart = null;
        distance = Double.MAX_VALUE;
    }

    public Vertex (Vertex other) {
        this.ID = other.ID;
        this.type = other.type;
        this.coordX = other.coordX;
        this.coordY = other.coordY;
        this.weight = other.weight;
        visited = false;
        degree = 0;
        dart = null;
        this.distance = other.distance;
    }

    @Override
    public int compareTo(Vertex other) {
        return this.ID - other.ID;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double d) {
        distance = d;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Vertex(int ID, String type) {
        this(ID, type, -1, -1, 1.0);
    }

    public Vertex(String type) {
        this(--uniqueID, type);
    }

    public float getCoordX() {
        return coordX;
    }

    public float getCoordY() {
        return coordY;
    }

    public void setCoordX(float coordX) {
        this.coordX = coordX;
    }

    public void setCoordY(float coordY) {
        this.coordY = coordY;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void initDart(Dart d) {
        setDart(d);
        degree++;
    }

    public void setDart(Dart d) {
        this.dart = d;
    }

    /**
     * O(degree) time
     *
     * @return
     */
    public List<Dart> getIncidenceList() {
        List<Dart> list = new LinkedList<>();
        Dart d = dart;

        for (int i = 0; i < degree; i++) {
            list.add(d);
            d = (type == VERTEX) ? d.getSuccessor() : d.getNext();
        }
        return list;
    }

    public int getDegree() {
        return degree;
    }

    public void incrementDegree() {
        incrementDegree(1);
    }

    public void incrementDegree(int n) {
        degree += n;
    }

    public Dart getFirstDart() {
        return dart;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visitedState) {
        visited = visitedState;
    }

    @Override
    public String toString() {
        return String.format("%s<%d>", type, ID);
    }
}
