package algorithms.RootFinder;

import selfdualgraph.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RandomRootFinder extends RootFinder {
    @Override
    public Vertex selectRoot(Set<Vertex> vertices) {
        List<Vertex> list = new ArrayList<>(vertices);
        int idx = (int) (Math.random() * list.size());
        return list.get(idx);
    }
}
