package algorithms;

import selfdualgraph.*;
import java.util.*;

public abstract class Separator {

    public static Tree.TreeNode<Vertex> leafmostHeavyVertex(Tree<Vertex> tree, double alpha) {
        if (alpha <= 0 || alpha >= 1) {
            throw new RuntimeException("alpha must be in range (0, 1)");
        }
        return null;
    }


}
