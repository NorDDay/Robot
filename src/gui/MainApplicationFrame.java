package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.xml.stream.Location;

import javafx.css.Size;
import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        loadFromFile();

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    saveAndClose();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
    private void addGame(){
        Robot robot = new Robot();
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow(robot);
        gameWindow.setSize(400,  400);
        gameWindow.setLocation(310,10);
        addWindow(gameWindow);

        CoordinateWindow coordWindow = new CoordinateWindow();
        coordWindow.setSize(400,  50);
        coordWindow.setLocation(310,310);
        robot.addObserver(coordWindow);
        addWindow(coordWindow);
    }
    private void loadFromFile(){
        File file  = new File("Windows.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<Robot> robots = new ArrayList<Robot>();
        while(sc.hasNextLine()){
            String tempScanner = sc.nextLine();
            if (tempScanner.equals("Протокол работы")){
                System.out.print(1);
                LogWindow logWindow = createLogWindow();
                logWindow.setLocation(sc.nextInt(), sc.nextInt());
                logWindow.setSize(sc.nextInt(), sc.nextInt());
                addWindow(logWindow);
            }
            else if (tempScanner.equals("Игровое поле")){
                Robot robot = new Robot();
                robots.add(robot);
                GameWindow gameWindow = new GameWindow(robot);
                gameWindow.setLocation(sc.nextInt(), sc.nextInt());
                gameWindow.setSize(sc.nextInt(), sc.nextInt());
                addWindow(gameWindow);
            }
        }
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int i=0;
        while(sc.hasNextLine()) {
            String tempScanner = sc.nextLine();
            if (tempScanner.equals("Координаты робота")) {
                CoordinateWindow coordWindow = new CoordinateWindow();
                coordWindow.setLocation(sc.nextInt(), sc.nextInt());
                coordWindow.setSize(sc.nextInt(), sc.nextInt());
                if(i<robots.size())
                    robots.get(i).addObserver(coordWindow);
                i++;
                addWindow(coordWindow);
            }
        }
    }

    private void saveAndClose() throws IOException {
        Object[] options = {"Да",
                "Нет"};
        int n = JOptionPane.showOptionDialog(new JFrame(),
                "Вы действительно хотите выйти?",
                "Выход",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n==0) {
            FileWriter windows = new FileWriter("Windows.txt", false);
            for (JInternalFrame e : desktopPane.getAllFrames()) {
                windows.write(e.getTitle() + '\n');
                windows.write(String.valueOf(e.getLocation().x) + " ");
                windows.write(String.valueOf(e.getLocation().y) + '\n');
                windows.write(String.valueOf(e.getSize().width) + '\n');
                windows.write(String.valueOf(e.getSize().height)+'\n');
            }
            windows.close();
            System.exit(0);
        }
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenu lookAndFeelMenu(){
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        return lookAndFeelMenu;
    }
    private JMenu testMenu(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }
        {
            JMenuItem addLogMessageItem = new JMenuItem("Добавить поле", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
               addGame();
            });
            testMenu.add(addLogMessageItem);
        }
        {
            JMenuItem addLogMessageItem = new JMenuItem("Выход", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                try {
                    saveAndClose();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(lookAndFeelMenu());
        menuBar.add(testMenu());
        return menuBar;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
