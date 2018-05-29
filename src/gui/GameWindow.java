package gui;

import javafx.scene.control.ComboBox;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    public ArrayList<Robot> RobotList;

    public GameWindow(ArrayList<Robot> robots){
        super("Игровое поле", true, true, true, true);
        RobotList = robots;
        m_visualizer = new GameVisualizer(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        JComboBox faceCombo = new JComboBox();
        faceCombo.addItem("Добавить робота");
        faceCombo.addItem("Дейкстра");
        faceCombo.addItemListener (new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(faceCombo.getSelectedIndex());
                addRobot();
               // faceCombo.setSelectedIndex(0);
            }
        });
        panel.add(faceCombo,BorderLayout.PAGE_START);
        getContentPane().add(panel);
        pack();
    }
    public void addRobot(){
        Robot temp = new Robot();
        temp.barriers = RobotList.get(0).barriers;
        RobotList.add(temp);
    }
}
