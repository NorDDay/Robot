package barrier;

import gui.Line;

import java.awt.*;

public class RectangleBarrier extends AbstractBarrier {
    public RectangleBarrier(Point position){
        pos = position;
        verticies.add(new Point(pos.x-size/2, pos.y-size/2));
        verticies.add(new Point(pos.x+size/2, pos.y-size/2));
        verticies.add(new Point(pos.x+size/2, pos.y+size/2));
        verticies.add(new Point(pos.x-size/2, pos.y+size/2));

        collisionPairs.add(new Line(verticies.get(0), verticies.get(1)));
        collisionPairs.add(new Line(verticies.get(1), verticies.get(2)));
        collisionPairs.add(new Line(verticies.get(2), verticies.get(3)));
        collisionPairs.add(new Line(verticies.get(3), verticies.get(0)));

        edges.add(new Point(verticies.get(0).x-deltaDistance/2, verticies.get(0).y-deltaDistance/2));
        edges.add(new Point(verticies.get(1).x+deltaDistance/2, verticies.get(1).y-deltaDistance/2));
        edges.add(new Point(verticies.get(2).x+deltaDistance/2, verticies.get(2).y+deltaDistance/2));
        edges.add(new Point(verticies.get(3).x-deltaDistance/2, verticies.get(3).y+deltaDistance/2));
    }
}
