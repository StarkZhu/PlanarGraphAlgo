package algorithms.RootFinder;

import selfdualgraph.*;
import java.util.*;

public abstract class RootFinder {

    public abstract Vertex selectRoot(Set<Vertex> vertices);

    /**
     * select a root VERTEX from a graph based on specific strategy
     * @return
     */
    public Vertex selectRootVertex(SelfDualGraph g) {
        return selectRoot(g.getVertices());
    }

    /**
     * select a root FACE from a graph based on specific strategy
     * @return
     */
    public Vertex selectRootFace(SelfDualGraph g) {
        return selectRoot(g.getFaces());
    }

}
