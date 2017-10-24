import org.junit.*;
import selfdualgraph.SelfDualGraph;

import java.io.FileNotFoundException;

/**
 * Created by qixinzhu on 10/23/17.
 */
public class test_SelfDualGraph {
    private SelfDualGraph g;
    @Before
    public void readGraph() {
        g = new SelfDualGraph();
        try {
            g.buildGraph("./input_data/test_graph_0.txt");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testElementNum() {
        Assert.assertEquals(g.getVerticeNum(), 6);
        Assert.assertEquals(g.getFaseNum(), 7);
    }
}
