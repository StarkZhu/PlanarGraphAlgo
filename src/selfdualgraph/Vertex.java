package selfdualgraph;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by qixinzhu on 10/23/17.
 */
public class Vertex {
    //static variable
    public static String VERTEX = "V";
    public static String FACE = "F";

    private final String type;
    private final int ID;
    private final float coordX, coordY;
    private double weight, cost;
    private List<Dart> incidenceList;

    public Vertex(int ID, String type, float coordX, float coordY, double weight, double cost) {
        this.ID = ID;
        this.type = type;
        this.coordX = coordX;
        this.coordY = coordY;
        this.weight = weight;
        this.cost = cost;
        incidenceList = new LinkedList<>();
    }

    public Vertex(int ID, String type) {
        this(ID, type, -1, -1, 0, 0);
    }

    public Vertex(int ID, String type, float coordX, float coordY) {
        this(ID, type, coordX, coordY, 0, 0);
    }

    public float getCoordX() {
        return coordX;
    }

    public float getCoordY() {
        return coordY;
    }

    public double getWeight() {
        return weight;
    }

    public double getCost() {
        return cost;
    }

    public void addDart(Dart d) {
        incidenceList.add(d);
    }

    public List<Dart> getIncidenceList() {
        return new LinkedList<>(incidenceList);
    }

    public int getDegree() {
        return incidenceList.size();
    }

    public Dart getFirstDart() {
        return incidenceList.get(0);
    }

    @Override
    public String toString() {
        return String.format("%s<%d> ", type, ID);
    }
}
