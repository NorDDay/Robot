package barrier;

import gui.Line;

import java.awt.*;
import java.util.ArrayList;

public abstract class AbstractBarrier {
    public Point pos = new Point();
    public int deltaDistance = 10;
    public int size = 10;

    public ArrayList<Point> verticies = new ArrayList<>();
    public ArrayList<Point> edges = new ArrayList<>();
    public ArrayList<Line> collisionPairs = new ArrayList<>();
}
