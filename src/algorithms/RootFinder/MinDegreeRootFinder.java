package algorithms.RootFinder;

import selfdualgraph.Vertex;

import java.util.Set;

public class MinDegreeRootFinder extends RootFinder {
    @Override
    public Vertex selectRoot(Set<Vertex> vertices) {
        Vertex root = null;
        for (Vertex v : vertices) {
            if (root == null || v.getDegree() < root.getDegree()) {
                root = v;
            } else if (v.getDegree() == root.getDegree() && v.ID > root.ID) {
                root = v;
            }
        }
        return root;
    }
}
