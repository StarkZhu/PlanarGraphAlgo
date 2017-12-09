import algorithms.RootFinder.SpecificIdRootFinder;
import org.junit.*;
import selfdualgraph.*;


public class test_SelfDualGraph_mod_add extends test_SelfDualGraph_modification{
    @Test
    public void testAddTypicalEdge() {
        Dart tail = findDartByID(g, 4);
        Dart head = findDartByID(g, 9);
        g.addEdge(tail, head);
        Dart d = findDartByID(g, -1);
        Assert.assertEquals(1, d.getTail().getID());
        Assert.assertEquals(4, d.getHead().getID());
        Assert.assertEquals(6, d.getRight().getID());
        Assert.assertEquals(-1, d.getLeft().getID());
        Assert.assertEquals(9, d.getNext().getID());
        Assert.assertEquals(-1, d.getNext().getPrev().getID());
        Assert.assertEquals(0, d.getPrev().getID());
        Assert.assertEquals(-1, d.getPrev().getNext().getID());
        Assert.assertEquals(4, d.getSuccessor().getID());
        Assert.assertEquals(-1, d.getSuccessor().getPredecessor().getID());
        Assert.assertEquals(1, d.getPredecessor().getID());
        Assert.assertEquals(-1, d.getPredecessor().getSuccessor().getID());

        Assert.assertEquals(-2, d.getReverse().getID());
        Assert.assertEquals(4, d.getReverse().getTail().getID());
        Assert.assertEquals(1, d.getReverse().getHead().getID());
        Assert.assertEquals(-1, d.getReverse().getRight().getID());
        Assert.assertEquals(6, d.getReverse().getLeft().getID());
        Assert.assertEquals(4, d.getReverse().getNext().getID());
        Assert.assertEquals(-2, d.getReverse().getNext().getPrev().getID());
        Assert.assertEquals(11, d.getReverse().getPrev().getID());
        Assert.assertEquals(-2, d.getReverse().getPrev().getNext().getID());
        Assert.assertEquals(9, d.getReverse().getSuccessor().getID());
        Assert.assertEquals(-2, d.getReverse().getSuccessor().getPredecessor().getID());
        Assert.assertEquals(10, d.getReverse().getPredecessor().getID());
        Assert.assertEquals(-2, d.getReverse().getPredecessor().getSuccessor().getID());

        Assert.assertEquals(8, g.getFaceNum());
        Assert.assertEquals(6, head.getRight().getID());
        checkIncidentListOfFace(head.getRight(), new int[]{4, 0, 1});
        Assert.assertEquals(-1, tail.getRight().getID());
        checkIncidentListOfFace(tail.getRight(), new int[]{1, 3, 4});

        checkIncidentListOfVertex(d.getTail(), new int[]{0, 4, 3});
        checkIncidentListOfVertex(d.getHead(), new int[]{0, 5, 3, 1});
    }

    @Test
    public void testAddParallelEdge() {
        Dart tail = findDartByID(g, 16);
        Dart head = findDartByID(g, 3);
        g.addEdge(tail, head);
        Dart d = findDartByID(g, -1);
        Assert.assertEquals(5, d.getTail().getID());
        Assert.assertEquals(2, d.getHead().getID());
        Assert.assertEquals(4, d.getRight().getID());
        Assert.assertEquals(-1, d.getLeft().getID());
        Assert.assertEquals(3, d.getNext().getID());
        Assert.assertEquals(-1, d.getNext().getPrev().getID());
        Assert.assertEquals(15, d.getPrev().getID());
        Assert.assertEquals(-1, d.getPrev().getNext().getID());
        Assert.assertEquals(16, d.getSuccessor().getID());
        Assert.assertEquals(-1, d.getSuccessor().getPredecessor().getID());
        Assert.assertEquals(14, d.getPredecessor().getID());
        Assert.assertEquals(-1, d.getPredecessor().getSuccessor().getID());

        Assert.assertEquals(-2, d.getReverse().getID());
        Assert.assertEquals(2, d.getReverse().getTail().getID());
        Assert.assertEquals(5, d.getReverse().getHead().getID());
        Assert.assertEquals(-1, d.getReverse().getRight().getID());
        Assert.assertEquals(4, d.getReverse().getLeft().getID());
        Assert.assertEquals(16, d.getReverse().getNext().getID());
        Assert.assertEquals(-2, d.getReverse().getNext().getPrev().getID());
        Assert.assertEquals(16, d.getReverse().getPrev().getID());
        Assert.assertEquals(-2, d.getReverse().getPrev().getNext().getID());
        Assert.assertEquals(3, d.getReverse().getSuccessor().getID());
        Assert.assertEquals(-2, d.getReverse().getSuccessor().getPredecessor().getID());
        Assert.assertEquals(17, d.getReverse().getPredecessor().getID());
        Assert.assertEquals(-2, d.getReverse().getPredecessor().getSuccessor().getID());

        Assert.assertEquals(8, g.getFaceNum());
        Assert.assertEquals(4, head.getRight().getID());
        checkIncidentListOfFace(head.getRight(), new int[]{2, 0, 5});
        Assert.assertEquals(-1, tail.getRight().getID());
        checkIncidentListOfFace(tail.getRight(), new int[]{5, 2});

        checkIncidentListOfVertex(d.getTail(), new int[]{4, 0, 2, 2, 2, 5, 5});
        checkIncidentListOfVertex(d.getHead(), new int[]{0, 3, 5, 5, 5});
    }

    @Test
    public void testAddMultipleEdges() {
        Dart tail1 = findDartByID(g, 13);
        Dart tail2 = findDartByID(g, 20);
        Dart tail3 = findDartByID(g, 19);
        Dart head = findDartByID(g, 6);
        g.addEdge(tail1, head);
        g.addEdge(tail2, head);
        g.addEdge(tail3, head);
        Dart d = findDartByID(g, -1);
        g.addEdge(d, findDartByID(g, -4));

        Assert.assertEquals(11, g.getFaceNum());

        Assert.assertEquals(1, head.getRight().getID());
        checkIncidentListOfFace(head.getRight(), new int[]{3, 2});
        Assert.assertEquals(-1, tail1.getRight().getID());
        checkIncidentListOfFace(tail1.getRight(), new int[]{5, 4, 3});
        Assert.assertEquals(-2, tail2.getRight().getID());
        checkIncidentListOfFace(tail2.getRight(), new int[]{3, 5, 5});
        Assert.assertEquals(-3, tail3.getRight().getID());
        checkIncidentListOfFace(tail3.getRight(), new int[]{2, 5, 3});
        Assert.assertEquals(-4, d.getRight().getID());
        checkIncidentListOfFace(d.getRight(), new int[]{5, 3});

        checkIncidentListOfVertex(d.getTail(), new int[]{4, 0, 2, 2, 3, 5, 5, 3, 3});
        checkIncidentListOfVertex(d.getHead(), new int[]{1, 4, 5, 5, 5, 2, 2});
    }

    @Test
    public void testAddVertex() {
        g.addVertex(findDartByID(g, 14).getRight());
        Assert.assertEquals(7, g.getVertexNum());
        Assert.assertEquals(9, g.getFaceNum());
        checkIncidentListOfVertex(findDartByID(g, 14).getTail(), new int[]{4, -1, 0, 2, 2, 5, 5});
        checkIncidentListOfVertex(findDartByID(g, 12).getTail(), new int[]{0, -1, 5, 3});
        checkIncidentListOfVertex(findDartByID(g, 8).getTail(), new int[]{1, 2, 5, -1, 4});

        Vertex v = new SpecificIdRootFinder(-1).selectRoot(g.getVertices());
        checkIncidentListOfVertex(v, new int[]{4, 0, 5});

        checkIncidentListOfFace(findDartByID(g, 12).getRight(), new int[]{-1, 4, 5});
        checkIncidentListOfFace(findDartByID(g, 14).getRight(), new int[]{-1, 5, 0});
        checkIncidentListOfFace(findDartByID(g, 8).getRight(), new int[]{0, 4, -1});
    }
}
