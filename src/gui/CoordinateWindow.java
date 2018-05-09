package gui;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

public class CoordinateWindow extends JInternalFrame implements Observer {
    private TextField m_coordinatesContent;
    private DecimalFormat format = new DecimalFormat("##.00");

    CoordinateWindow() {
        super("Координаты робота", true, true, true, true);
        m_coordinatesContent = new TextField("");
        m_coordinatesContent.setSize(463, 58);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_coordinatesContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void update(Observable o, Object arg) {
        Robot robot = (Robot) arg;
        m_coordinatesContent.setText("X: " + format.format(robot.getPosition().x)
                + "\tY: " + format.format(robot.getPosition().y));
    }
}