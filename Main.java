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
    public static int state = 0; // 0 - Menu
                                 // 1 - Store/MarketPlace
                                 // 2 - Past HighScores
                                 // 3 - Instructions
                                 // 4 - About
                                 // 5 - In-Game
                                 // 6 - Pause
                                 // 7 - Game Over
    public static BufferedImage[] stateImages = new BufferedImage[7];

    public static JFrame frame;
    public static Thread thread;

    public static BufferedImage bg;
    public static int bgHeight;
    public static int y = 0; // reference point
    public static int speed = 1;

    public static int FPS = 144;
    public static int screenWidth = 500;
    public static int screenHeight = 650;

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
            bg = ImageIO.read(new File("assets/bg.png"));
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
        g.drawImage(stateImages[state], 0, 0, null);

        Font font = new Font("Press Start 2P", Font.PLAIN, 25);
        g.setFont(font);
        g.setColor(Color.WHITE);

        if (state == 5) {
            g.drawImage(bg, 0, y, null);
            g.drawImage(bg, 0, y + bgHeight, null);
            g.drawImage(bg, 0, y - bgHeight, null);
        }
    }

    public static void main(String[] args) throws IOException {
        // Load Images
        try {
        } catch (Exception e) {
            System.out.println("Something wrong with the image!");
        }

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
    }

    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }
}