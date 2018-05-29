package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class Robot extends  AbstractRobot {

    public Robot(){
        robotName = "Дейкстра";
    }
    public Robot(PointDouble point){
        m_robotPositionX = point.x;
        m_robotPositionY = point.y;
    }

    @Override
    public ArrayList<Point> findPath(ArrayList<ArrayList<Edge>> graph) {
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
        while(st.size()>0){
            path.add((Point) st.pop());
        }
        return path;
    }
}
