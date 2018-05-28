package gui;

import java.awt.BorderLayout;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    public GameWindow(Robot robot)
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer(robot);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        JComboBox faceCombo = new JComboBox();
        faceCombo.addItem("Добавить робота");
        //faceCombo.addItem("SansSerif");
        panel.add(faceCombo,BorderLayout.PAGE_START);
        getContentPane().add(panel);
        pack();
    }
}
