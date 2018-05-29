package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.xml.stream.Location;

import barrier.AbstractBarrier;
import barrier.RectangleBarrier;
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
    private ArrayList<Robot> robots = new ArrayList<>();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        //initRobotClass();
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
    private void initRobotClass(){
        MyClassLoader loader = new MyClassLoader();
        File folder = new File("C:\\\\Users\\patan\\desktop\\git\\Robot\\src\\classLoad\\");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                try {
                      Class cl = loader.findClass(file.getAbsolutePath());
                      ArrayList<ArrayList<Edge>> test = new ArrayList<ArrayList<Edge>>();
                      //Method[] method = cl.getMethods();
                      Method method = cl.getMethod("findPath", new Class[] { test.getClass() });
                  //  System.out.println(method[0].toString());
                    //Выполняем метод m1. Нельзя забывать про метод newInstance(), если метод динамический.
//                    method.invoke(cl.newInstance(), new Object[] {test});
                }
                catch (Exception e){}
                /*catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }*/
            }
        }

    }
    private void addGame(){
        Robot robot = new Robot();
        robots.add(robot);
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        ArrayList<AbstractRobot> temp = new ArrayList<>();
        temp.add(robot);
        GameWindow gameWindow = new GameWindow(temp);
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
        File maps  = new File("Maps.txt");
        Scanner sc1 = null;
        try {
            sc1 = new Scanner(maps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(sc1.hasNextLine()){
            String tempScanner = sc1.nextLine();
            int k = -1;
            for(String s : tempScanner.split(";")){
                if(s.length()>0){
                    ArrayList<Double> t = new ArrayList<>();
                    for(String num : s.split(" ")){
                        t.add(Double.parseDouble(num));
                    }
                    if(k == -1){
                        Robot robot = new Robot(new PointDouble(t.get(0), t.get(1)));
                        robots.add(robot);
                        k = robots.size()-1;
                    }
                    else{
                        robots.get(k).barriers.add( new RectangleBarrier(
                                new PointDouble(t.get(0), t.get(1))
                        ));
                    }
                }
            }
        }
        File file  = new File("Windows.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<Robot> robots = new ArrayList<Robot>();
        int k=0;
        while(sc.hasNextLine()){
            String tempScanner = sc.nextLine();
            if (tempScanner.equals("Протокол работы")){
                LogWindow logWindow = createLogWindow();
                logWindow.setLocation(sc.nextInt(), sc.nextInt());
                logWindow.setSize(sc.nextInt(), sc.nextInt());
                addWindow(logWindow);
            }
            else if (tempScanner.equals("Игровое поле")){
                Robot robot = this.robots.get(k);
                k++;
                this.robots.add(robot);
                robots.add(robot);
                ArrayList<AbstractRobot> temp = new ArrayList<>();
                temp.add(robot);
                GameWindow gameWindow = new GameWindow(temp);
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
            FileWriter maps = new FileWriter("Maps.txt", false);
            int i=0;
            for (JInternalFrame e : desktopPane.getAllFrames()) {
                windows.write(e.getTitle() + '\n');
                if(e.getTitle().equals("Игровое поле")){
                    maps.write(robots.get(i).getPosition().x+" "+robots.get(i).getPosition().y+";");
                    for(int j=0;j<robots.get(i).barriers.size();j++){
                        maps.write(robots.get(i).barriers.get(j).pos.x+" "+robots.get(i).barriers.get(j).pos.y+";");
                    }
                    maps.write('\n');
                    i++;
                }
                windows.write(String.valueOf(e.getLocation().x) + " ");
                windows.write(String.valueOf(e.getLocation().y) + '\n');
                windows.write(String.valueOf(e.getSize().width) + '\n');
                windows.write(String.valueOf(e.getSize().height)+'\n');
            }
            maps.close();
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
