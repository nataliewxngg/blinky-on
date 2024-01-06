// Natalie Wong
// Due Sunday, January 21, 2024

// Blinky-on
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Main extends JPanel implements KeyListener, Runnable {

    // Global Variables
    int state = 0; // 0 - Menu
                   // 1 - Store/MarketPlace
                   // 2 - Past HighScores
                   // 3 - Instructions
                   // 4 - About
                   // 5 - In-Game
                   // 6 - Pause
                   // 7 - Game Over

    JFrame frame;
    Thread thread;

    BufferedImage bg;
    BufferedImage menu;
    BufferedImage arrow;
    BufferedImage arrowRight;

    BufferedImage wow;

    Map<Integer, ArrayList<Integer>> arrowStates = new HashMap<>();
    int arrowState = 1;

    int bgHeight;
    int y = 0; // reference point
    double speed = 1;

    int FPS = 60;
    int screenWidth = 500;
    int screenHeight = 650;

    public Main() {
        // JPanel default settings
        setPreferredSize(new Dimension(screenWidth, screenHeight));

        // Starting the thread
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        initialize();
        while (true) {
            update();
            repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize() {
        // setups before the game starts running
        try {
            menu = ImageIO.read(new File("assets/menu.png"));
            bg = ImageIO.read(new File("assets/bg.png"));
            arrow = ImageIO.read(new File("assets/arrow.png"));

            arrowStates.put(1, new ArrayList<>(Arrays.asList(200, 262))); // play
            arrowStates.put(2, new ArrayList<>(Arrays.asList(200, 280))); // shop
            arrowStates.put(3, new ArrayList<>(Arrays.asList(140, 298))); // instructions
            arrowStates.put(4, new ArrayList<>(Arrays.asList(190, 316))); // about
            arrowStates.put(5, new ArrayList<>(Arrays.asList(150, 334))); // highscores

            bgHeight = bg.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // update stuff
        y += speed;
        if (y < -bgHeight)
            y = 0;
        else if (y > bgHeight)
            y = 0;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Font font = new Font("Press Start 2P", Font.PLAIN, 25);
        g.setFont(font);
        g.setColor(Color.WHITE);

        if (state == 0) { // 0 - Menu
            g.drawImage(bg, 0, y, null);
            g.drawImage(bg, 0, y + bgHeight, null);
            g.drawImage(bg, 0, y - bgHeight, null);

            g.drawImage(menu, 0, 0, null);
            g.drawImage(arrow, arrowStates.get(arrowState).get(0), arrowStates.get(arrowState).get(1), null);

            // caaaaaaaaaaaaaaaar

        } else if (state == 1) { // 1 - Store/MarketPlace

        } else if (state == 2) { // 2 - Past HighScores

        } else if (state == 3) { // 3 - Instructions

        } else if (state == 4) { // 4 - About

        } else if (state == 5) { // 5 - In-Game
            g.drawImage(bg, 0, y, null);
            g.drawImage(bg, 0, y + bgHeight, null);
            g.drawImage(bg, 0, y - bgHeight, null);
        } else if (state == 6) { // 6 - Pause

        } else if (state == 7) { // 7 - Game Over

        }
    }

    public void keyPressed(KeyEvent e) {
        if (state == 0) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN)
                if (arrowState == 5)
                    arrowState = 1;
                else
                    arrowState++;
            else if (e.getKeyCode() == KeyEvent.VK_UP)
                if (arrowState == 1)
                    arrowState = 5;
                else
                    arrowState--;
            else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                // edit this later
                System.out.println("edit!");
        }
    }

    public static void main(String[] args) throws IOException {
        // Font
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(
                    Font.createFont(Font.TRUETYPE_FONT, new File("assets/PressStart2P-Regular.ttf")));
        } catch (IOException | FontFormatException e) {
            System.out.println("Something wrong with the font!");
        }

        JFrame frame = new JFrame("Blinky-ON");
        Main panel = new Main();

        frame.add(panel);
        frame.addKeyListener(panel);

        frame.setVisible(true);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {

    }
}