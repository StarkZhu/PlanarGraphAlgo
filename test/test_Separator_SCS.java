import algorithms.RootFinder.*;
import algorithms.Separator.*;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import org.junit.*;
import selfdualgraph.*;

import java.util.*;

public class test_Separator_SCS extends test_Separator{
    @Test
    public void test_SCS_sampleG() {
        g.flatten();
        g.triangulate();
        
        Separator sp = new SimpleCycleSeparator(g);
        Set<Vertex> separator = sp.findSeparator(null, new SpecificIdRootFinder(2), null);
    }
}
