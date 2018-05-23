package gui;

import barrier.AbstractBarrier;

import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Stack;

public class Robot extends Observable {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.003;


    public ArrayList<AbstractBarrier> barriers = new ArrayList<>();
    public volatile ArrayList<Point> path = new ArrayList<>();
    public volatile int curIdPos;
    public Robot(){

    }
    public Robot(PointDouble point){
        m_robotPositionX = point.x;
        m_robotPositionY = point.y;
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


    public void createGraph(){
        ArrayList<ArrayList<Edge>> graph = new ArrayList<>();
        for(int i=0;i<barriers.size()*4 + 2;i++){
            graph.add(new ArrayList<>());
        }
        graph.get(0).addAll(createEdges(new Point((int)m_robotPositionX, (int)m_robotPositionY)));
        graph.get(barriers.size()*4+1).addAll(createEdges(new Point((int)m_targetPositionX, (int)m_targetPositionY)));
        for(int i=0;i<barriers.size();i++){
            for(int j=0;j<barriers.get(i).edges.size();j++){
                graph.get(i*4+j+1).addAll(createEdges(barriers.get(i).edges.get(j)));
            }
        }
        path = dijkstra(graph);
       // System.out.println(path.size());
        curIdPos=0;
    }

    private ArrayList<Point> dijkstra(ArrayList<ArrayList<Edge>> graph){
        int n= graph.size();
        int[] cost = new int[n];
        boolean[] vis = new boolean[n];
        int[] parent = new int[n];
        for(int i=0;i<n;i++) {
            cost[i] = 10000000;
            vis[i] = false;
            parent[i] = -1;
        }
        cost[0] = 0;
        for(int i=0;i<n;i++){
            int minCost = 10000001;
            int minV = -1;
            for(int j=0;j<n;j++){
                if(cost[j]<minCost && !vis[j]){
                    minCost = cost[j];
                    minV = j;
                }
            }
            vis[minV] = true;
            for(int j=0;j<graph.get(minV).size();j++){
                if(cost[minV] + graph.get(minV).get(j).cost< cost[graph.get(minV).get(j).to]){
                    cost[graph.get(minV).get(j).to] = cost[minV] + (int)graph.get(minV).get(j).cost;
                    parent[graph.get(minV).get(j).to] = minV;
                }
            }
        }
        Stack st = new Stack();
        int pos = n-1;
        st.push(new Point (m_targetPositionX, m_targetPositionY));
        while(pos != 0){
            if(parent[pos] == -1)
                return new ArrayList<>();
            pos = parent[pos];
            if(pos != 0)
                st.push(barriers.get((pos-1)/4).edges.get((pos-1)%4));
        }
        ArrayList<Point> path = new ArrayList<>();
        for(int i=0;i<st.size();i++){
            path.add((Point) st.pop());
        }
        return path;
    }

    public ArrayList<Edge> createEdges(Point pos){//список ребер из вершины
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
        if(!(m_robotPositionX == pos.x && m_robotPositionY == pos.y)) {
            if(checkWay(new Line(new Point((int)m_robotPositionX,(int) m_robotPositionY) , pos))){
                edges.add(new Edge(0,distance(pos,new Point((int)m_robotPositionX,(int) m_robotPositionY) ),new Point((int)m_robotPositionX,(int) m_robotPositionY)));
            }
        }
        if(!(m_targetPositionX == pos.x && m_targetPositionY == pos.y)) {
            if(checkWay(new Line(new Point((int)m_targetPositionX,(int) m_targetPositionY) , pos))){
                edges.add(new Edge(barriers.size()*4+1,distance(pos,new Point((int)m_targetPositionX,(int) m_targetPositionY) ),new Point((int)m_targetPositionX,(int) m_targetPositionY)));
            }
        }
        return edges;
    }

    public boolean checkWay(Line line){//проверка сущ ребра
        for(int i=0;i<barriers.size();i++){
            for(int j=0;j<barriers.get(i).collisionPairs.size();j++){
                if(intersect(line.a , line.b, barriers.get(i).collisionPairs.get(j).a,  barriers.get(i).collisionPairs.get(j).b)){
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
        if(path.size() == 0)
            return;
        double distance = distance(path.get(curIdPos).x, path.get(curIdPos).y,
                m_robotPositionX, m_robotPositionY);
        if (distance <1)
        {
            if(path.size() == curIdPos+1){
                createGraph();
                return;
            }
            curIdPos++;
        }
        else{
            if (lookingAtTarget()) {
                moveRobot(maxVelocity, 0, 10);
            } else {
                rotateRobot();
            }
        }
        setChanged();
    }
    private double angleFromRobot() {
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, path.get(curIdPos).x, path.get(curIdPos).y);
        return asNormalizedRadians(angleToTarget - m_robotDirection);
    }
    static double rounded(double num, int accuracy) { //default accuracy is 0, look at overload below
        num = Math.floor(num * Math.pow(10,accuracy));
        return num / Math.pow(10,accuracy);
    }
    private boolean lookingAtTarget()  { return rounded(angleFromRobot(), 1) == 0; }
    private void rotateRobot() {
        double angularVelocity;
        double angle = angleFromRobot();
        if (angle < Math.PI)
            angularVelocity = maxAngularVelocity; //turning left is closer
        else
            angularVelocity = -maxAngularVelocity; //turing right is closer
        moveRobot(0, angularVelocity,10);
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
    public static double distance(Point a, Point b)
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
