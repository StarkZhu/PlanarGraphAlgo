import algorithms.RootFinder.*;
import algorithms.Separator.*;
import org.junit.*;
import selfdualgraph.*;

import java.io.FileNotFoundException;
import java.util.*;

public class test_Separator_LiptonTarjan extends test_Separator{

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

    @Test
    public void testLiptonTarjan(){
        SelfDualGraph g = readGraph("./input_data/test_graph_0.txt");
        g.flatten();
        g.triangulate();
        for (Vertex face : g.getFaces()) face.setWeight(1.0);
        LiptonTarjanSeparator liptonTarjan = new LiptonTarjanSeparator(g);

        Set<Vertex> separator = liptonTarjan.findSeparator(new SpecificIdRootFinder(0));
        verifyVertexSet(new int[]{0, 3, 5}, separator);
        Set<Vertex>[] subgraphs = liptonTarjan.findSubgraphs();
        verifyVertexSet(new int[]{0, 3, 5, 2}, subgraphs[0]);
        verifyVertexSet(new int[]{0, 3, 5, 1, 4}, subgraphs[1]);

        separator = liptonTarjan.findSeparator(new SpecificIdRootFinder(2));
        verifyVertexSet(new int[]{0, 3, 5}, separator);
        subgraphs = liptonTarjan.findSubgraphs();
        verifyVertexSet(new int[]{0, 3, 5, 2}, subgraphs[0]);
        verifyVertexSet(new int[]{0, 3, 5, 1, 4}, subgraphs[1]);

        separator = liptonTarjan.findSeparator(new SpecificIdRootFinder(5));
        verifyVertexSet(new int[]{0, 3, 5, 1}, separator);
        subgraphs = liptonTarjan.findSubgraphs();
        verifyVertexSet(new int[]{0, 3, 5, 1, 2}, subgraphs[0]);
        verifyVertexSet(new int[]{0, 3, 5, 1, 4}, subgraphs[1]);
    }

    @Test
    public void testOnGrid4x4() {
        SelfDualGraph g = readGraph("./test/benchmark_img_4x4.txt");
        g.flatten();
        g.triangulate();
        LiptonTarjanSeparator liptonTarjan = new LiptonTarjanSeparator(g);
        Set<Vertex> separator = liptonTarjan.findSeparator(new SpecificIdRootFinder(0));
        verifyVertexSet(new int[]{0, 5, 9, 14, 10, 6}, separator);
    }

}
