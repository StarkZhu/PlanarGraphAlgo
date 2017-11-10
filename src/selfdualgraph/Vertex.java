package selfdualgraph;

import java.util.LinkedList;
import java.util.List;

/**
 * Vertex is a vertex or a face in the primal graph, which is a face or a vertex in the dual graph
 * <p>
 * degree(V): the number of darts whose head is vertex V
 * degree(F): the number of darts whose right is face F
 */
public class Vertex {
    //static variable
    public static String VERTEX = "V";
    public static String FACE = "F";

    public final String type;
    public final int ID;
    private float coordX, coordY;
    private double weight;
    //List<Dart> incidenceList;   // package protected
    private boolean visited;
    private int degree;
    private Dart dart;  // points to an arbitrary dart with tail(d) = current vertex

    public Vertex(int ID, String type, float coordX, float coordY, double weight) {
        this.ID = ID;
        this.type = type;
        this.coordX = coordX;
        this.coordY = coordY;
        this.weight = weight;
        //incidenceList = new LinkedList<>();
        visited = false;
        degree = 0;
        dart = null;
    }

    public Vertex(int ID, String type) {
        this(ID, type, -1, -1, 1.0);
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

    public void setDart(Dart d) {
        this.dart = d;
        degree++;
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

    public void addDegree() {
        degree++;
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
