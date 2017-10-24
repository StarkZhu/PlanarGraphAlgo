package selfdualgraph;

/**
 * Created by qixinzhu on 10/23/17.
 */
public class Dart {
    public final int ID;
    private double weight, capacity;
    private Vertex tail, head, left, right;
    private Dart reverse, successor, predecessor, next, prev;

    public Dart(int ID, Vertex t, Vertex h) {
        this.ID = ID;
        tail = t;
        head = h;
    }

    public double getWeight() {
        return weight;
    }

    public double getCapacity() {
        return capacity;
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

    @Override
    public String toString() {
        return String.format("D<%d> (%s, %s)", ID, head, left);
    }
}
