package util;

import selfdualgraph.*;

import java.io.*;
import java.util.*;

public class CylingerGenerator {
    private SelfDualGraph g;

    public CylingerGenerator(SelfDualGraph g) {
        this.g = g;
    }

    /**
     * the same G is modified
     * to get a different graph, G need to be reloaded from text file, since it does not support deep copy yet
     *
     * @param magnitude 1 ~ 5
     * @return
     */
    public void generatCylinders(int magnitude) {
        int limit = (int) Math.pow(10, magnitude);
        Iterator<Vertex> it = g.getFaces().iterator();
        Vertex outerFace = it.next();
        while (outerFace.getID() != 0) outerFace = it.next();

        for (int i = 0; i < limit; i++) {
            for (int j=0; j<3; j++) {
                Vertex newV = g.addVertex(outerFace);
                Vertex minNeighbor = newV.getFirstDart().getHead();
                for (Dart d : newV.getIncidenceList()) {
                    if (d.getHead().getID() < minNeighbor.getID()) minNeighbor = d.getHead();
                }
                Dart newDart = minNeighbor.getFirstDart();
                for (Dart d : minNeighbor.getIncidenceList()) {
                    if (d.getID() < newDart.getID()) newDart = d;
                }
                outerFace = newDart.getLeft();

            }
            outerFace.setDart(outerFace.getFirstDart().getPrev());
            //System.out.println(outerFace.getID());
        }
        g.renumberIDs();
    }

    public static void main(String[] args) throws FileNotFoundException {
        for (int i=0; i< 5; i++) {
            SelfDualGraph g = new SelfDualGraph();
            g.buildGraph("./input_data/cylinder/0.txt");
            System.out.println(g.getFaceNum());
            System.out.println(g.getVertexNum());

            CylingerGenerator cg = new CylingerGenerator(g);
            cg.generatCylinders(i+1);
            g.saveToFile(String.format("./input_data/cylinder/%d.txt", i+1));
        }
    }
}
