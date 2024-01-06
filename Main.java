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

public class Main extends JPanel implements Runnable, KeyListener {

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

    Random random = new Random();

    ArrayList<BufferedImage> cars = new ArrayList<>();
    Car player;

    Map<Integer, ArrayList<Integer>> arrowStates = new HashMap<>();
    int arrowState = 1;

    LinkedList<Car> enemies = new LinkedList<>();

    int bgHeight;
    int y = 0; // reference point
    double speed = 1;
    Boolean speedUp = false;

    int FPS = 60;
    int screenWidth = 500;
    int screenHeight = 650;

    Boolean upPressed = false;
    Boolean downPressed = false;
    Boolean leftPressed = false;
    Boolean rightPressed = false;

    int score = 0;

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

            bgHeight = bg.getHeight();

            cars.add(ImageIO.read(new File("assets/cars/blue-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/gray-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/police-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/red-car-with-white-stripes.png")));
            cars.add(ImageIO.read(new File("assets/cars/red-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/red-truck.png")));
            cars.add(ImageIO.read(new File("assets/cars/rusty-white-van.png")));
            cars.add(ImageIO.read(new File("assets/cars/school-bus.png")));
            cars.add(ImageIO.read(new File("assets/cars/white-truck.png")));
            cars.add(ImageIO.read(new File("assets/cars/white-van.png")));
            cars.add(ImageIO.read(new File("assets/cars/yellow-cab.png")));
            cars.add(ImageIO.read(new File("assets/cars/yellow-car.png")));

        } catch (IOException e) {
            e.printStackTrace();
        }

        arrowStates.put(1, new ArrayList<>(Arrays.asList(200, 262))); // play
        arrowStates.put(2, new ArrayList<>(Arrays.asList(200, 280))); // shop
        arrowStates.put(3, new ArrayList<>(Arrays.asList(140, 298))); // instructions
        arrowStates.put(4, new ArrayList<>(Arrays.asList(190, 316))); // about
        arrowStates.put(5, new ArrayList<>(Arrays.asList(150, 334))); // highscores

        player = new Car(cars.get(0), 250, 700, 0);
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

            player.draw(g);
            if (player.getY() > 500) {
                player.move(2, "up");
            } else if (player.getY() > 460) {
                player.move(1, "up");
            }

            if (leftPressed && player.getX() > 85)
                player.move(2, "left");
            if (rightPressed && player.getX() < 330)
                player.move(2, "right");

        } else if (state == 1) { // 1 - Store/MarketPlace

        } else if (state == 2) { // 2 - Past HighScores

        } else if (state == 3) { // 3 - Instructions

        } else if (state == 4) { // 4 - About

        } else if (state == 5) { // 5 - In-Game
            g.drawImage(bg, 0, y, null);
            g.drawImage(bg, 0, y + bgHeight, null);
            g.drawImage(bg, 0, y - bgHeight, null);

            // player enter screen
            player.draw(g);
            if (player.getY() > 500) {
                player.move(2, "up");
            } else if (player.getY() > 460) {
                player.move(1, "up");
            }

            // movement
            if (leftPressed && player.getX() > 85)
                player.move(2, "left");
            if (rightPressed && player.getX() < 330)
                player.move(2, "right");

            // score and gradual speed ups - SORT IN MAP LATER?
            score++;
            if (score == 160) {
                speed = 6;
                speedUp = true;
            } else if (score == 100) {
                speed = 5;
                speedUp = true;
            } else if (score == 60) {
                speed = 4;
                speedUp = true;
            } else if (score == 30) {
                speed = 3;
                speedUp = true;
            } else if (score == 10) {
                speed = 2;
                speedUp = true;
            }

            // spawn new cars
            if (score > 10) {
                int rand = random.nextInt(151); // random int from 1-150
                int rand1;
                int[] x = { 90, 170, 250, 330 };

                if (rand == 59) {
                    do {
                        rand = random.nextInt(4);
                        rand1 = random.nextInt(4);
                    } while (rand1 == 0);

                    enemies.add(new Car(cars.get(random.nextInt(cars.size())), x[rand], -250, rand1));
                }
            }

            // display all "enemies"
            for (int i = 0; i < enemies.size(); i++) {
                if (enemies.get(i).getY() > 650) {
                    enemies.remove(i);
                } else {
                    if (speedUp) {
                        enemies.get(i).speedUp();
                    }
                    enemies.get(i).draw(g);
                }
            }
            speedUp = false;

        } else if (state == 6) { // 6 - Pause

        } else if (state == 7) { // 7 - Game Over

        }
    }

    public void keyPressed(KeyEvent e) {
        if (state == 0) { // 0 - Menu
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
            else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (arrowState == 1)
                    state = 5;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = true;
        } else if (state == 1) { // 1 - Store/MarketPlace

        } else if (state == 2) { // 2 - Past HighScores

        } else if (state == 3) { // 3 - Instructions

        } else if (state == 4) { // 4 - About

        } else if (state == 5) { // 5 - In-Game
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = true;
        } else if (state == 6) { // 6 - Pause

        } else if (state == 7) { // 7 - Game Over

        }
    }

    public void keyReleased(KeyEvent e) {
        if (state == 0) { // 0 - Menu
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = false;
        } else if (state == 1) { // 1 - Store/MarketPlace

        } else if (state == 2) { // 2 - Past HighScores

        } else if (state == 3) { // 3 - Instructions

        } else if (state == 4) { // 4 - About

        } else if (state == 5) { // 5 - In-Game
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = false;
        } else if (state == 6) { // 6 - Pause

        } else if (state == 7) { // 7 - Game Over

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
}