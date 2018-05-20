package gui;

import java.awt.*;

public class Edge {
    public int to;
    public double cost;
    public Point pos;
    public Edge(int t, double c, Point p){
        to = t;
        cost = c;
        pos = p;
    }
}
