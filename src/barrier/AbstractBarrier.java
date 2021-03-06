package barrier;

import gui.Line;

import java.awt.*;
import java.util.ArrayList;

public abstract class AbstractBarrier {
    public Point pos = new Point();
    public int deltaDistance = 20;
    public int size = 40;

    public ArrayList<Point> verticies = new ArrayList<>();
    public ArrayList<Point> edges = new ArrayList<>();
    public ArrayList<Line> collisionPairs = new ArrayList<>();
}
