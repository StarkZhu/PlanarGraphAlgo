package selfdualgraph;

/**
 * Dart is a directed edge
 * Each (tail_vertex, left_face) uniquely identify a dart
 *
 * tail & head: a dart leaves its tail vertex and enters its head vertex, d = tail -> head
 * reverse: rev(rev(d)) == d
 * left & right: face on the left/right side of a dart
 * successor & predecessor: the next dart leaving tail(d) in counter-clockwise/clockwise order after d
 * next & prev: the next dart after d in clockwise/counter-clockwise order around the boundary of right(d)
 */
public class Dart implements Comparable<Dart> {
    // static variables
    public static int uniqueID = 0;

    private int ID;
    private double weight, capacity;
    private Vertex tail, head, left, right;
    private Dart reverse, successor, predecessor, next, prev;
    private boolean visited;

    public Dart(int ID, Vertex t, Vertex h, double w, double c) {
        this.ID = ID;
        tail = t;
        head = h;
        weight = w;
        capacity = c;
        visited = false;
    }

    public Dart(Dart other) {
        ID = other.ID;
        weight = other.weight;
        capacity = other.capacity;
        visited = false;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Dart(Vertex t, Vertex h) {
        this(--uniqueID, t, h, 0.0, 0.0);
    }

    public double getWeight() {
        return weight;
    }

    public double getCapacity() {
        return capacity;
    }

    public boolean isVisited() {return visited;}

    public void setVisited(boolean visitedState) {
        visited = visitedState;
    }

    public Vertex getTail() {
        return tail;
    }

    public Vertex getHead() {
        return head;
    }

    public Vertex getLeft() {
        return left;
    }

    public Vertex getRight() {
        return right;
    }

    public Dart getReverse() {
        return reverse;
    }

    public Dart getSuccessor() {
        return successor;
    }

    public Dart getPredecessor() {
        return predecessor;
    }

    public Dart getNext() {
        return next;
    }

    public void setLeft(Vertex left) {
        this.left = left;
    }

    public void setRight(Vertex right) {
        this.right = right;
    }

    public void setReverse(Dart reverse) {
        this.reverse = reverse;
    }

    public void setSuccessor(Dart successor) {
        this.successor = successor;
    }

    public void setPredecessor(Dart predecessor) {
        this.predecessor = predecessor;
    }

    public void setNext(Dart next) {
        this.next = next;
    }

    public Dart getPrev() {
        return prev;
    }

    public void setPrev(Dart prev) {
        this.prev = prev;
    }

    public void setTail(Vertex tail) {
        this.tail = tail;
    }

    public void setHead(Vertex head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return String.format("D<%d> (%s, %s)", ID, head, left);
    }

    @Override
    public int compareTo(Dart other) {
        if (this.weight == other.weight) return this.ID - other.ID;
        else if (this.weight < other.weight) return -1;
        else return 1;
    }
}
