package gui;

import javafx.scene.control.ComboBox;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    public ArrayList<AbstractRobot> RobotList;

    public GameWindow(ArrayList<AbstractRobot> robots){
        super("Игровое поле", true, true, true, true);
        RobotList = robots;
        m_visualizer = new GameVisualizer(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Добавить робота");
        JMenuItem item1 = new JMenuItem("Дейкстра", KeyEvent.VK_S);
        item1.addActionListener((event) -> {
            addRobot(new Robot());
        });
        menu.add(item1);
        JMenuItem item2 = new JMenuItem("DFS", KeyEvent.VK_S);
        item2.addActionListener((event) -> {
            addRobot(new DFSRobot());
        });
        menu.add(item2);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        getContentPane().add(panel);
        pack();
    }
    public void addRobot(AbstractRobot robot){
        AbstractRobot temp = robot;
        if(RobotList.size()>0)
            temp.barriers = RobotList.get(0).barriers;
        RobotList.add(temp);
    }
}
