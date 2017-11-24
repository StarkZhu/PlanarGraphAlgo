package algorithms;

import selfdualgraph.*;
import java.util.*;

public abstract class Separator {

    /**
     * linear-time to find a vertex such that w(v0) > alpha * totalW, and every child v of v0 has w(v) <= alpha * totalW
     * @param root
     * @param alpha in range (0, 1)
     * @param totalW the weight of the root
     * @return
     */
    public static Tree.TreeNode<Vertex> leafmostHeavyVertex(Tree.TreeNode<Vertex> root, double alpha, double totalW) {
        if (alpha <= 0 || alpha >= 1) {
            throw new RuntimeException("alpha must be in range (0, 1)");
        }
        for (Tree.TreeNode<Vertex> child : root.getChildren()) {
            if (child.getWeightSum() > alpha * totalW) {
                return leafmostHeavyVertex(child, alpha, totalW);
            }
        }
        return root;
    }


}
