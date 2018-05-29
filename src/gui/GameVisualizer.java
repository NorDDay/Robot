package gui;

import barrier.AbstractBarrier;
import barrier.RectangleBarrier;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel
{
    private final Timer m_timer = initTimer();
    private ArrayList<AbstractRobot> robots;


    private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }
    

    
    public GameVisualizer(GameWindow win)
    {

        robots = win.RobotList;
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                for(AbstractRobot robot : robots) {
                    robot.onModelUpdateEvent();
                    robot.notifyObservers(robot);
                }
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(e.getButton() == 1) {
                    for(AbstractRobot robot : robots) {
                        robot.setTargetPosition(e.getPoint());
                        robot.createGraph();
                        repaint();
                    }
                }
                else if(e.getButton() == 3){
                    RectangleBarrier square = new RectangleBarrier(e.getPoint());
                    for(AbstractRobot robot : robots) {
                        robot.barriers.add(square);
                        robot.createGraph();
                    }
                }
                else{
                    double minDist=10000000;
                    int minI=-1;
                    for(AbstractRobot robot : robots) {
                        for (int i = 0; i < robot.barriers.size(); i++) {
                            if (robot.distance(e.getPoint(), robot.barriers.get(i).pos) < minDist) {
                                minDist = robot.distance(e.getPoint(), robot.barriers.get(i).pos);
                                minI = i;
                            }
                        }
                        if (minDist < 10)
                            robot.barriers.remove(minI);
                        robot.createGraph();
                    }
                }
            }
        });
        setDoubleBuffered(true);
    }

    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        for(AbstractRobot robot : robots) {
            drawRobot(g2d, round(robot.getPosition().x), round(robot.getPosition().y), robot.getDirection(), robot);
        }
        drawTarget(g2d, robots.get(0).getTargetPosition().x, robots.get(0).getTargetPosition().y);
        for(AbstractBarrier e : robots.get(0).barriers){
            drawRectangle(g2d, (RectangleBarrier) e);
        }
    }
    
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private void drawRobot(Graphics2D g, int x, int y, double direction, AbstractRobot robot)
    {

        int robotCenterX = round(robot.getPosition().x);
        int robotCenterY = round(robot.getPosition().y);

        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);

    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
    private void drawRectangle(Graphics2D g, RectangleBarrier square) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        Point center = square.pos;
        int size = square.size;
        g.setColor(Color.BLUE);
        g.fillRect(center.x - size / 2, center.y - size / 2, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(center.x - size / 2, center.y - size / 2, size, size);
    }
}
