package algorithms;

import selfdualgraph.*;
import java.util.*;

public abstract class RootFinder {

    public abstract Vertex selectRoot(Set<Vertex> vertices);

    /**
     * Call this static function to select a root VERTEX from a graph based on specific strategy
     * @param g
     * @param rf
     * @return
     */
    public static Vertex selectRootVertex(SelfDualGraph g, RootFinder rf) {
        return rf.selectRoot(g.getVertices());
    }

    /**
     * Call this static function to select a root FACE from a graph based on specific strategy
     * @param g
     * @param rf
     * @return
     */
    public static Vertex selectRootFace(SelfDualGraph g, RootFinder rf) {
        return rf.selectRoot(g.getFaces());
    }

    /**
     * Subclasses, implement different selection strategy
     */
    public static class RandomRoot extends RootFinder {
        @Override
        public Vertex selectRoot(Set<Vertex> vertices) {
            return vertices.iterator().next();
        }
    }

    public static class SpecificIdRoot extends RootFinder {
        private int rootID;

        public SpecificIdRoot(int id) {
            rootID = id;
        }

        public SpecificIdRoot() {
            this(0);
        }

        @Override
        public Vertex selectRoot(Set<Vertex> vertices) {
            for (Vertex v : vertices) {
                if (v.ID == rootID) return v;
            }
            throw new RuntimeException(String.format("No vertex has ID %d", rootID));
        }

    }

    public static class MaxDegreeRoot extends RootFinder {
        @Override
        public Vertex selectRoot(Set<Vertex> vertices) {
            Vertex root = null;
            for (Vertex v : vertices) {
                if (root == null || v.getDegree() > root.getDegree()) {
                    root = v;
                }
            }
            return root;
        }
    }

    public static class MinDegreeRoot extends RootFinder {
        @Override
        public Vertex selectRoot(Set<Vertex> vertices) {
            Vertex root = null;
            for (Vertex v : vertices) {
                if (root == null || v.getDegree() < root.getDegree()) {
                    root = v;
                }
            }
            return root;
        }
    }
}
