import algorithms.RootFinder.*;
import algorithms.Separator.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class test_LiptonTarjan {

    public SelfDualGraph readGraph(String fileName) {
        SelfDualGraph g = new SelfDualGraph();
        try {
            g.buildGraph(fileName);
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
        Dart.uniqueID = 0;
        Vertex.uniqueID = 0;
        return g;
    }

    public void verifySeparator(int[] expectedVerticies, Set<Vertex> separator) {
        Assert.assertEquals(expectedVerticies.length, separator.size());
        Set<Integer> expectedID = new HashSet<>();
        for (int i : expectedVerticies) expectedID.add(i);
        for (Vertex v : separator) {
            Assert.assertTrue(expectedID.contains(v.getID()));
        }
    }

    @Test
    public void testLiptonTarjan(){
        SelfDualGraph g = readGraph("./input_data/test_graph_0.txt");
        g.flatten();
        g.triangulate();
        for (Vertex face : g.getFaces()) face.setWeight(1.0);
        LiptonTarjanSeparator liptonTarjan = new LiptonTarjanSeparator();

        Set<Vertex> separator = liptonTarjan.findSeparator(g, new SpecificIdRootFinder(0));
        verifySeparator(new int[]{0, 3, 5}, separator);

        separator = liptonTarjan.findSeparator(g, new SpecificIdRootFinder(2));
        verifySeparator(new int[]{0, 3, 5}, separator);

        separator = liptonTarjan.findSeparator(g, new SpecificIdRootFinder(5));
        verifySeparator(new int[]{0, 3, 5, 1}, separator);
    }

    @Test
    public void testOnGrid4x4() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        g.flatten();
        g.triangulate();
        LiptonTarjanSeparator liptonTarjan = new LiptonTarjanSeparator();
        Set<Vertex> separator = liptonTarjan.findSeparator(g, new SpecificIdRootFinder(0));
        verifySeparator(new int[]{0, 5, 9, 14, 10, 6}, separator);
    }

}