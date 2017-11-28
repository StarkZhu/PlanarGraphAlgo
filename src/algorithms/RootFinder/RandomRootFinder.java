package algorithms.RootFinder;

import selfdualgraph.Vertex;

import java.util.Set;

public class RandomRootFinder extends RootFinder {
    @Override
    public Vertex selectRoot(Set<Vertex> vertices) {
        return vertices.iterator().next();
    }
}
