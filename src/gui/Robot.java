package gui;

import barrier.AbstractBarrier;

import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;

public class Robot extends Observable {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;


    public ArrayList<AbstractBarrier> barriers = new ArrayList<>();
    public volatile ArrayList<Point> path = new ArrayList<>();
    public Robot(){

    }
    public PointDouble getPosition(){
        return new PointDouble(m_robotPositionX, m_robotPositionY);
    }

    public double getDirection(){
        return m_robotDirection;
    }

    public Point getTargetPosition(){
        return new Point(m_targetPositionX, m_targetPositionY);
    }


    protected void setTargetPosition(Point p)
    {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    public void createPath(){
        // gen path
    }

    public void createGraph(){
        ArrayList<ArrayList<Edge>> graph = new ArrayList<>();
        for(int i=0;i<barriers.size()*4 + 2;i++){
            graph.add(new ArrayList<>());
        }
        for(int i=0;i<barriers.size();i++){
            for(int j=0;j<barriers.get(i).edges.size();j++){
                graph.get(i*4+j+1).addAll(createEdges(barriers.get(i).edges.get(j)));
            }
        }
        //graph.get(0).add;
    }

    public ArrayList<Edge> createEdges(Point pos){
        ArrayList<Edge> edges = new ArrayList<>();
        for(int i=0;i<barriers.size();i++){
            for(int j=0;j<barriers.get(i).edges.size();j++){
                if(!(barriers.get(i).edges.get(j).x == pos.x && barriers.get(i).edges.get(j).y == pos.y)){
                    if(checkWay(new Line(barriers.get(i).edges.get(j), pos))){
                        edges.add(new Edge(i*4+j+1,distance(pos,barriers.get(i).edges.get(j) ),barriers.get(i).edges.get(j)));
                    }
                }
            }
        }
        return edges;
    }

    public boolean checkWay(Line line){
        for(int i=0;i<barriers.size();i++){
            for(int j=0;j<barriers.get(i).collisionPairs.size();j++){
                if(!intersect(line.a , line.b, barriers.get(i).collisionPairs.get(j).a,  barriers.get(i).collisionPairs.get(j).b)){
                    return false;
                }
            }
        }
        return true;
    }

    public int area (Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    public boolean intersect_1 (int a, int b, int c, int d) {
        if (a > b) {
            int temp = a;
            a = b;
            b = temp;
        }
        if (c > d) {
            int temp = c;
            c = d;
            d = temp;
        }
        return Math.max(a,c) <= Math.min(b,d);
    }

    public boolean intersect (Point a, Point b, Point c, Point d) {
        return intersect_1(a.x, b.x, c.x, d.x)
                && intersect_1(a.y, b.y, c.y, d.y)
                && area(a, b, c) * area(a, b, d) <= 0
                && area(c, d, a) * area(c, d, b) <= 0;
    }
    protected void onModelUpdateEvent()
    {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5)
        {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > m_robotDirection)
        {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < m_robotDirection)
        {
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(velocity, angularVelocity, 10);
        setChanged();
    }

    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection  + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
    private static double distance(Point a, Point b)
    {
        double diffX = a.x - b.x;
        double diffY = a.y - b.y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }
}
