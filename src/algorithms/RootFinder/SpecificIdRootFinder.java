package algorithms.RootFinder;

import selfdualgraph.SelfDualGraph;
import selfdualgraph.Vertex;

import java.util.Set;

public  class SpecificIdRootFinder extends RootFinder {
    private int rootID;

    public SpecificIdRootFinder(int id) {
        rootID = id;
    }

    public SpecificIdRootFinder() {
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
