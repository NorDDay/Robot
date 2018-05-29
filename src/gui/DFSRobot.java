package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class DFSRobot extends AbstractRobot {
    private ArrayList<ArrayList<Edge>> G;
    private boolean[] vis;
    private Stack ansStack;
    private int n;
    public DFSRobot(PointDouble point){
        m_robotPositionX = point.x;
        m_robotPositionY = point.y;
    }
    public DFSRobot(){

    }
    @Override
    public ArrayList<Point> findPath(ArrayList<ArrayList<Edge>> graph) {
        ansStack = new Stack();
        G = graph;
        n = graph.size();
        vis = new boolean[n];
        DFS(0);
        ArrayList<Point> path = new ArrayList<>();
        while(ansStack.size()>0){
            path.add((Point) ansStack.pop());
        }
        return path;
    }
    private boolean DFS(int pos){
        if(pos == n-1){
            ansStack.push(new Point(m_targetPositionX, m_targetPositionY));
            return true;
        }
        vis[pos] = true;
        for(int i=0;i<G.get(pos).size();i++){
            if(!vis[G.get(pos).get(i).to]){
                if( DFS(G.get(pos).get(i).to)){
                    ansStack.push(G.get(pos).get(i).pos);
                    return  true;
                }
            }
        }
        return false;
    }
}
