package util;

import algorithms.RootFinder.*;
import algorithms.Separator.FundamentalCycleSeparator;
import algorithms.Separator.ModifiedFCS;
import algorithms.Separator.Separator;
import algorithms.SpanningTreeSolver.*;
import algorithms.TreeWeightAssigner.*;
import selfdualgraph.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GraphPlotter extends JFrame {
    // static variables
    public static double VERTEX_DIA = 0.25;
    public static double EDGE_WIDTH = 0.25;


    private SelfDualGraph graph;
    private double x_min, y_min, x_max, y_max;
    private double scale;
    private int w, h;

    public GraphPlotter(SelfDualGraph g) {
        this(g, 800, 800);
    }

    public GraphPlotter(SelfDualGraph g, int width, int height) {
        graph = g;
        w = width;
        h = height;
        setSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void findScale() {
        x_min = Integer.MAX_VALUE;
        y_min = Integer.MAX_VALUE;
        x_max = Integer.MIN_VALUE;
        y_max = Integer.MIN_VALUE;
        Set<Vertex> set = new HashSet<>(graph.getVertices());
        set.addAll(graph.getFaces());
        for (Vertex v : set) {
            x_min = Math.min(x_min, v.getCoordX());
            y_min = Math.min(y_min, v.getCoordY());
            x_max = Math.max(x_max, v.getCoordX());
            y_max = Math.max(y_max, v.getCoordY());
        }
        scale = Math.min(h / (y_max - y_min + 2), w / (x_max - x_min + 2));
        x_min--;
        y_min--;
    }

    private int[] getCoord(Vertex v, double offset) {
        int[] ans = new int[2];
        ans[0] = (int) ((v.getCoordX() - offset - x_min) * scale);
        ans[1] = (int) ((v.getCoordY() - offset - y_min) * scale);
        return ans;
    }

    private void drawVertices(Graphics g, Color c, Set<Vertex> vertices) {
        g.setColor(c);
        for (Vertex v : vertices) {
            int[] coord = getCoord(v, VERTEX_DIA / 2);
            int dia = (int) (VERTEX_DIA * scale);
            g.fillOval(coord[0], coord[1], dia, dia);
        }
    }

    private void drawEdges(Graphics g, Color c, Set<Dart> darts) {
        for (Dart d : darts) {
            drawDart(g, c, d);
        }
    }

    private void drawDart(Graphics g, Color c, Dart d) {
        Graphics2D gg = (Graphics2D) g;
        gg.setColor(c);
        gg.setStroke(new BasicStroke((int) (VERTEX_DIA * scale * EDGE_WIDTH)));
        int[] start = getCoord(d.getTail(), 0);
        int[] end = getCoord(d.getHead(), 0);
        gg.drawLine(start[0], start[1], end[0], end[1]);
    }

    private void drawPrimalGraph(Graphics g, Color c) {
        drawVertices(g, c, graph.getVertices());
        Set<Dart> darts = new HashSet<>();
        for (Vertex v : graph.getVertices()) {
            for (Dart d : v.getIncidenceList()) {
                if (!darts.contains(d) && !darts.contains(d.getReverse())) darts.add(d);
            }
        }
        drawEdges(g, c, darts);
    }

    private void drawPrimalTree(Graphics g, Tree primalTree) {
        Set<Dart> darts = new HashSet<>();
        Map<Vertex, Tree.TreeNode> nodes = primalTree.mapVertexToTreeNode(false);
        for (Tree.TreeNode node : nodes.values()) {
            if (node.getParentDart() != null) darts.add(node.getParentDart());
        }
        drawVertices(g, Color.blue, graph.getVertices());
        drawEdges(g, Color.blue, darts);
    }

    private void drawDualTree(Graphics g, Tree dualTree) {
        Set<Dart> darts = new HashSet<>();
        Map<Vertex, Tree.TreeNode> nodes = dualTree.mapVertexToTreeNode(false);
        for (Tree.TreeNode node : nodes.values()) {
            Dart d = node.getParentDart();
            if (d != null) {
                Dart dd = new Dart(d.getLeft(), d.getRight());
                darts.add(dd);
            }
        }
        drawVertices(g, Color.red, graph.getFaces());
        drawEdges(g, Color.red, darts);
    }

    public void paint(Graphics g) {
        findScale();

        drawPrimalGraph(g, Color.black);

        SpanningTreeSolver sts = new BFSsolver();
        RootFinder rf = new SpecificIdRootFinder(32);
        Tree[] trees = sts.buildTreeCoTree(graph, rf.selectRootVertex(graph), null);

        drawPrimalTree(g, trees[0]);
        drawDualTree(g, trees[1]);

        TreeWeightAssigner vertexCountTWA = new VertexCount();
        vertexCountTWA.calcWeightSum(trees[1].getRoot());
        FundamentalCycleSeparator sp = new ModifiedFCS(graph);
        Tree.TreeNode separatorNode = sp.leafmostHeavyVertex(
                trees[1].getRoot(), 1.0 / 3, trees[1].getRoot().getDescendantWeightSum());
        separatorNode = sp.chooseNode(separatorNode, trees[0], trees[1], 3);
        Dart uv = separatorNode.getParentDart();
        drawDart(g, Color.green, uv);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    SelfDualGraph g = new SelfDualGraph();
                    g.buildGraph("./input_data/cylinder/unsymm/1.txt");
                    new GraphPlotter(g);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
