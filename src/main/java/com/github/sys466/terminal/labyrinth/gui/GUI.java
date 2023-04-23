package com.github.sys466.terminal.labyrinth.gui;

import com.github.sys466.terminal.labyrinth.TerminalLabyrinthApp;
import com.github.sys466.terminal.labyrinth.system.ControlSystem;
import com.github.sys466.terminal.labyrinth.type.ControlType;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GUI extends JFrame {

    public static final int SCREEN_HEIGHT  = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public static final int SCREEN_WIDTH   = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

    private static final Robot ROBOT;

    private static final JTextPane VIEW_PORT        = new JTextPane();
    private static final JTextPane STAMINA_INFO     = new JTextPane();
    private static final JTextPane TIMER_INFO       = new JTextPane();
    private static final JTextPane CONTROL_INFO     = new JTextPane();
    private static final JTextPane DESCRIPTION_INFO = new JTextPane();
    private static final JTextPane CARD_INFO        = new JTextPane();

    static {
        try {
            ROBOT = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("error during robot initialization", e);
        }
    }

    public GUI() {
        super("terminal labyrinth by Dmitrii 'sys466' Efimenko");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLACK);
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null")
        );
        setLayout(null);
        initComponents();
        pack();
        setResizable(false);
        setVisible(true);

        GraphicsDevice device  = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        device.setFullScreenWindow(this);

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                VIEW_PORT.requestFocusInWindow();
            }

            @Override
            public void windowClosing(WindowEvent e) {}

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
    }

    public static void updateViewPort(String value) {
        VIEW_PORT.setText(value);
    }

    public static void updateStaminaInfo(String value) {
        STAMINA_INFO.setText(value);
    }

    public static void updateTimerInfo(String value) {
        TIMER_INFO.setText(value);
    }

    public static void updateControlInfo(String value) {
        CONTROL_INFO.setText(value);
    }

    public static void updateDescriptionInfo(String value) {
        DESCRIPTION_INFO.setText(value);
    }

    public static void updateCardInfo(String value) {
        CARD_INFO.setText(value);
    }

    public static void moveMouseToPosition(int positionY, int positionX) {
        ROBOT.mouseMove(positionX, positionY);
    }

    private void initComponents() {

        // Fonts
        Font viewPortFont = System.getProperty("os.name").matches("Windows.*")
                ? new Font("Consolas", Font.PLAIN,16)
                : new Font("Ubuntu Mono", Font.PLAIN,20);

        SimpleAttributeSet textCenterPosition = new SimpleAttributeSet();
        StyleConstants.setAlignment(textCenterPosition, StyleConstants.ALIGN_CENTER);
        VIEW_PORT.setParagraphAttributes(textCenterPosition, false);
        VIEW_PORT.setFont(viewPortFont);
        VIEW_PORT.setBackground(Color.BLACK);
        VIEW_PORT.setForeground(Color.WHITE);
        VIEW_PORT.setEditable(false);
        VIEW_PORT.setFocusable(true);
        VIEW_PORT.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W      -> ControlSystem.updateControlStatus(ControlType.UP, true);
                    case KeyEvent.VK_S      -> ControlSystem.updateControlStatus(ControlType.DOWN, true);
                    case KeyEvent.VK_A      -> ControlSystem.updateControlStatus(ControlType.LEFT, true);
                    case KeyEvent.VK_D      -> ControlSystem.updateControlStatus(ControlType.RIGHT, true);
                    case KeyEvent.VK_SHIFT  -> ControlSystem.updateControlStatus(ControlType.RUN, true);
                    case KeyEvent.VK_SPACE  -> TerminalLabyrinthApp.ENTER_LABYRINTH = true;
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W      -> ControlSystem.updateControlStatus(ControlType.UP, false);
                    case KeyEvent.VK_S      -> ControlSystem.updateControlStatus(ControlType.DOWN, false);
                    case KeyEvent.VK_A      -> ControlSystem.updateControlStatus(ControlType.LEFT, false);
                    case KeyEvent.VK_D      -> ControlSystem.updateControlStatus(ControlType.RIGHT, false);
                    case KeyEvent.VK_SHIFT  -> ControlSystem.updateControlStatus(ControlType.RUN, false);
                }
            }
        });

        JPanel techPanel = new JPanel();
        techPanel.setBounds(0, 0, SCREEN_WIDTH, 40);
        techPanel.setLayout(new GridLayout(1, 2, 0, 0));
        techPanel.setBackground(Color.DARK_GRAY);

        STAMINA_INFO.setParagraphAttributes(textCenterPosition, false);
        STAMINA_INFO.setBackground(Color.BLACK);
        STAMINA_INFO.setForeground(Color.WHITE);
        STAMINA_INFO.setEditable(false);

        TIMER_INFO.setParagraphAttributes(textCenterPosition, false);
        TIMER_INFO.setBackground(Color.BLACK);
        TIMER_INFO.setForeground(Color.WHITE);
        TIMER_INFO.setEditable(false);

        techPanel.add(STAMINA_INFO);
        techPanel.add(TIMER_INFO);

        JPanel viewPanel = new JPanel();
        viewPanel.setBounds(0, 40, SCREEN_WIDTH, SCREEN_HEIGHT - 160);
        viewPanel.setLayout(new BorderLayout());
        viewPanel.setBackground(Color.BLACK);
        viewPanel.add(VIEW_PORT);

        JPanel statusPanel = new JPanel();
        statusPanel.setBounds(0, SCREEN_HEIGHT - 120, SCREEN_WIDTH, 120);
        statusPanel.setLayout(new GridLayout(1, 3, 0, 0));
        statusPanel.setBackground(Color.DARK_GRAY);

        CONTROL_INFO.setParagraphAttributes(textCenterPosition, false);
        CONTROL_INFO.setBackground(Color.BLACK);
        CONTROL_INFO.setForeground(Color.WHITE);
        CONTROL_INFO.setEditable(false);

        DESCRIPTION_INFO.setParagraphAttributes(textCenterPosition, false);
        DESCRIPTION_INFO.setBackground(Color.BLACK);
        DESCRIPTION_INFO.setForeground(Color.WHITE);
        DESCRIPTION_INFO.setEditable(false);

        CARD_INFO.setParagraphAttributes(textCenterPosition, false);
        CARD_INFO.setBackground(Color.BLACK);
        CARD_INFO.setForeground(Color.WHITE);
        CARD_INFO.setEditable(false);

        statusPanel.add(CONTROL_INFO);
        statusPanel.add(DESCRIPTION_INFO);
        statusPanel.add(CARD_INFO);

        add(techPanel);
        add(viewPanel);
        add(statusPanel);
    }
}
