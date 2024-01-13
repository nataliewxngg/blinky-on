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
import java.util.Queue;

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
    BufferedImage gameOver;
    BufferedImage instructions;
    BufferedImage about;
    BufferedImage highscores;
    BufferedImage store;
    BufferedImage pause;
    BufferedImage arrow;
    BufferedImage coin;
    BufferedImage steeringWheel;

    Font font = new Font("Press Start 2P", Font.PLAIN, 20);
    Font smallFont = new Font("Press Start 2P", Font.PLAIN, 13);

    Random random = new Random();

    ArrayList<BufferedImage> cars = new ArrayList<>();
    ArrayList<BufferedImage> smallCars = new ArrayList<>();
    char[] status = new char[12];
    int[] costs = new int[12];
    int[] handlings = new int[12];
    Car player;

    Map<Integer, ArrayList<Integer>> arrowStates = new HashMap<>();
    int arrowState = 1;

    Map<Integer, Integer> speeds = new HashMap<>(); // speed in relation to score

    LinkedList<Car> enemies = new LinkedList<>();

    int bgHeight;
    int y = 0; // reference point
    int speed = 1;

    int FPS = 60;
    int screenWidth = 500;
    int screenHeight = 650;

    Boolean leftPressed = false;
    Boolean rightPressed = false;

    int score = 0;

    int coins;
    LinkedList<Coin> availableCoins = new LinkedList<>();

    int playerIndex;
    int selected;
    Boolean boughtFailed = false;

    public void resetVars() {
        player = new Car(cars.get(playerIndex), 250, 700, 0);
        enemies.clear();
        availableCoins.clear();
        speed = 1;
        arrowState = 1;
        score = 0;

        rightPressed = leftPressed = false;
    }

    public Boolean coinCollision() {
        Coin coin;
        for (int i = 0; i < availableCoins.size(); i++) {
            coin = availableCoins.get(i);
            if (player.getX() < coin.getX() + Coin.getWidth() &&
                    player.getX() + player.getCar().getWidth() > coin.getX() &&
                    player.getY() < coin.getY() + Coin.getHeight() &&
                    player.getY() + player.getCar().getHeight() > coin.getY()) {
                coins++;
                availableCoins.remove(i);
                return true;
            }
        }
        return false;
    }

    public static String getHighScores(String fileName) {
        int count = 1;
        String s;
        String output = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader("highscores.txt"));
            while ((s = in.readLine()) != null && count < 11) {
                output += count + ") " + s + "\n";
                count++;
            }
            if (output.equals(""))
                output = "   YOU HAVE NO\n     EXISTING\n  HIGHSCORES YET!";
            in.close();
        } catch (IOException e) {
            System.out.println("highscores.txt is missing!");
        }
        return output;
    }

    public void updateStats() {
        try {
            PrintWriter out = new PrintWriter(new File("stats.txt"));

            out.println(coins);
            out.println(playerIndex);

            for (int i = 0; i < 36; i++) {
                if (i < 12)
                    out.println(status[i]);
                else if (i < 24)
                    out.println(costs[i - 12]);
                else if (i < 36)
                    out.println(handlings[i - 24]);
            }

            out.close();
        } catch (IOException e) {
            System.out.println("stats.txt is missing!");
        }
    }

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
            gameOver = ImageIO.read(new File("assets/game-over.png"));
            instructions = ImageIO.read(new File("assets/instructions.png"));
            about = ImageIO.read(new File("assets/about.png"));
            highscores = ImageIO.read(new File("assets/highscores.png"));
            store = ImageIO.read(new File("assets/store.png"));
            pause = ImageIO.read(new File("assets/pause.png"));
            coin = ImageIO.read(new File("assets/coin.png"));
            steeringWheel = ImageIO.read(new File("assets/steering-wheel.png"));

            bgHeight = bg.getHeight();

            cars.add(ImageIO.read(new File("assets/cars/rusty-white-van.png")));
            cars.add(ImageIO.read(new File("assets/cars/white-van.png")));
            cars.add(ImageIO.read(new File("assets/cars/blue-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/red-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/yellow-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/gray-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/yellow-cab.png")));
            cars.add(ImageIO.read(new File("assets/cars/white-truck.png")));
            cars.add(ImageIO.read(new File("assets/cars/red-truck.png")));
            cars.add(ImageIO.read(new File("assets/cars/school-bus.png")));
            cars.add(ImageIO.read(new File("assets/cars/red-car-with-white-stripes.png")));
            cars.add(ImageIO.read(new File("assets/cars/police-car.png")));

            smallCars.add(ImageIO.read(new File("assets/cars/small/white-truck.png")));
            smallCars.add(ImageIO.read(new File("assets/cars/small/red-truck.png")));
            smallCars.add(ImageIO.read(new File("assets/cars/small/school-bus.png")));

        } catch (IOException e) {
            e.printStackTrace();
        }

        arrowStates.put(1, new ArrayList<>(Arrays.asList(200, 262))); // 1-play
        arrowStates.put(2, new ArrayList<>(Arrays.asList(200, 280))); // 2-shop
        arrowStates.put(3, new ArrayList<>(Arrays.asList(140, 298))); // 3-instructions
        arrowStates.put(4, new ArrayList<>(Arrays.asList(190, 316))); // 4-about
        arrowStates.put(5, new ArrayList<>(Arrays.asList(150, 334))); // 5-highscores

        // initial gradual acceleration
        speeds.put(10, 2);
        speeds.put(30, 3);
        speeds.put(60, 4);
        speeds.put(100, 5);
        speeds.put(160, 6);
        speeds.put(300, 7);
        speeds.put(600, 8);
        speeds.put(1000, 9);

        try {
            BufferedReader in = new BufferedReader(new FileReader("stats.txt"));
            coins = Integer.parseInt(in.readLine());
            playerIndex = Integer.parseInt(in.readLine());
            for (int i = 0; i < 36; i++) {
                if (i < 12)
                    status[i] = in.readLine().charAt(0);
                else if (i < 24) {
                    costs[i - 12] = Integer.parseInt(in.readLine());
                    System.out.println(costs[i - 12]);
                } else if (i < 36)
                    handlings[i - 24] = Integer.parseInt(in.readLine());
            }
            in.close();
        } catch (IOException e) {
            System.out.println("stats.txt is missing!");
        }
        player = new Car(cars.get(playerIndex), 250, 700, 0);
        selected = playerIndex;
    }

    public void update() {
        // update stuff
        if (state != 7 && state != 6) {
            y += speed;
            if (y < -bgHeight)
                y = 0;
            else if (y > bgHeight)
                y = 0;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(font);
        g.setColor(Color.WHITE);

        g.drawImage(bg, 0, y, null);
        g.drawImage(bg, 0, y + bgHeight, null);
        g.drawImage(bg, 0, y - bgHeight, null);

        player.draw(g, false);

        g.drawImage(coin, 390, 10, null);
        g.drawString(coins + "", 420, 34);

        if (state != 6 && state != 7) {
            // player's ultimate, coolio introduction!!!!
            if (player.getY() > 500) {
                player.move(2, "up");
            } else if (player.getY() > 460) {
                player.move(1, "up");
            } else if (player.getY() > 415 && (playerIndex == 7 || playerIndex == 8 || playerIndex == 9))
                player.move(1, "up");
            // player's movement
            if (leftPressed && player.getX() > 85)
                player.move(handlings[playerIndex], "left");
            if (rightPressed && player.getX() < 330)
                player.move(handlings[playerIndex], "right");

            if (state != 5 && state != 1)
                g.drawImage(arrow, arrowStates.get(arrowState).get(0), arrowStates.get(arrowState).get(1), null);
        }
        if (state == 0) { // 0 - Menu
            g.drawImage(menu, 0, 0, null);
        } else if (state == 1) { // 1 - Store/MarketPlace
            g.setFont(smallFont);
            g.drawImage(store, 0, 0, null);

            if (selected != 7 && selected != 8 && selected != 9)
                g.drawImage(cars.get(selected), 160, 380, null);
            else // if player selects these, move up the y position
                g.drawImage(smallCars.get(selected - 7), 170, 365, null);

            if (status[selected] == 'e') {
                g.drawString("Equipped!", 200, 350);
            } else if (status[selected] == 'u') {
                g.drawString("Unlocked!", 200, 350);
            } else
                g.drawString("Locked!", 210, 350);

            if (boughtFailed) {
                g.drawString("You don't", 240, 430);
                g.drawString("have", 240, 445);
                g.drawString("enough", 240, 460);
                g.drawString("coins!", 240, 475);
            } else {
                g.drawImage(coin, 240, 420, null);
                g.drawString(Integer.toString(costs[selected]), 275, 440);
                g.drawImage(steeringWheel, 240, 450, null);
                g.drawString(handlings[selected] + "", 275, 470);
            }

        } else if (state == 2) { // 2 - Past HighScores
            g.drawImage(highscores, 0, 0, null);
            g.setFont(smallFont);
            String s = getHighScores("highscores.txt");
            int n = 0;
            for (String x : s.split("\n")) {
                if (n == 85)
                    n = 450;
                if (n > 85) {
                    g.drawString(x, 270, n);
                    n += 17;
                } else {
                    g.drawString(x, 130, 450 + n);
                    n += 17;
                }
            }
        } else if (state == 3) { // 3 - Instructions
            g.drawImage(instructions, 0, 0, null);
        } else if (state == 4) { // 4 - About
            g.drawImage(about, 0, 0, null);
        } else if (state == 5) { // 5 - In-Game
            // player collision
            if (player.collides(enemies)) {
                try {
                    BufferedReader in = new BufferedReader(new FileReader("highscores.txt"));
                    String s;
                    Queue<Integer> scores = new PriorityQueue<>(10, Collections.reverseOrder());
                    while ((s = in.readLine()) != null) {
                        scores.add(Integer.parseInt(s));
                    }
                    scores.add(score);
                    in.close();

                    PrintWriter out = new PrintWriter(new File("highscores.txt"));
                    for (int j = 0; j < 10; j++) {
                        if (!scores.isEmpty())
                            out.println(scores.remove());
                    }
                    out.close();
                } catch (IOException e) {
                    System.out.println("highscores.txt missing!");
                }

                state = 7;
            }

            // coin collision
            coinCollision();

            // spawn new cars
            if (score > 10) {
                int rand = random.nextInt(101); // random int from 0-100
                int rand1;
                int[] xCar = { 90, 170, 250, 330 };

                if (rand == 59) {
                    do {
                        rand = random.nextInt(4);
                        rand1 = random.nextInt(4);
                    } while (rand1 == 0);

                    Car newCar = new Car(cars.get(random.nextInt(cars.size())), xCar[rand], -250, rand1);

                    if (!newCar.collides(enemies))
                        enemies.add(newCar);
                    else
                        System.out.println("Collision occured while spawning!");
                } else if (rand == 10) {
                    rand = random.nextInt(4);
                    availableCoins.add(new Coin(xCar[rand] + 20, -200));
                }
            }

            // display all coins
            for (int i = 0; i < availableCoins.size(); i++) {
                if (availableCoins.get(i).getY() > 650) {
                    availableCoins.remove(i);
                } else {
                    availableCoins.get(i).draw(g, speed);
                }
            }

            // display all "enemies"
            for (int i = 0; i < enemies.size(); i++) {
                if (enemies.get(i).getY() > 650) {
                    enemies.remove(i);
                } else {
                    g.drawRect(enemies.get(i).getX(), enemies.get(i).getY(),
                            enemies.get(i).getCar().getWidth(), enemies.get(i).getCar().getHeight());
                    enemies.get(i).draw(g, false);
                }
            }

            score++;
            for (int i = 0; i < speeds.size(); i++) {
                if (speeds.containsKey(score)) {
                    speed = speeds.get(score);
                }
            }

            g.drawString("score: " + Integer.toString(score), 20, 40);
            updateStats(); // continuously update coins

        } else if (state == 6 || state == 7) {
            for (int i = 0; i < availableCoins.size(); i++) {
                availableCoins.get(i).draw(g, 0);
            }
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).draw(g, true);
            }
            if (state == 6) {
                g.drawImage(pause, 0, 0, null);
                g.drawString("score: " + Integer.toString(score), 20, 40);
            } else {
                g.drawImage(gameOver, 0, 0, null);
            }
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
                else if (arrowState == 2)
                    state = 1;
                else if (arrowState == 3)
                    state = 3;
                else if (arrowState == 4)
                    state = 4;
                else if (arrowState == 5)
                    state = 2;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = true;
        } else if (state == 1) { // 1 - Store/MarketPlace
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                boughtFailed = false;
                selected = playerIndex;
                state = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                // check for equip
                if (status[selected] != 'l') {
                    boughtFailed = false;
                    playerIndex = selected;
                    player = new Car(cars.get(playerIndex), player.getX(), player.getY(), 0);
                    for (int i = 0; i < 12; i++) {
                        if (status[i] == 'e')
                            status[i] = 'u';
                    }
                    status[selected] = 'e';
                    if (selected == 7 || selected == 8 || selected == 9)
                        player.setY(415);
                    else
                        player.setY(460);
                } else { // check for buy
                    if (coins >= costs[selected]) {
                        status[selected] = 'u';
                        coins -= costs[selected];
                    } else {
                        boughtFailed = true;
                    }
                }
                updateStats();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                boughtFailed = false;
                if (!(selected == 0))
                    selected--;
                else
                    selected = 11;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                boughtFailed = false;
                if (!(selected == 11))
                    selected++;
                else
                    selected = 0;
            }
            System.out.println(coins);
        } else if (state == 2 || state == 3 || state == 4) { // 3 - Instructions
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
                state = 0;
            else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = true;
        } else if (state == 5) { // 5 - In-Game
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = true;
            else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                state = 6;
        } else if (state == 6) { // 6 - Pause
            if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                resetVars();
                state = 5;
            } else if (e.getKeyChar() == 'm' || e.getKeyChar() == 'M') {
                resetVars();
                state = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                state = 5;
        } else if (state == 7) { // 7 - Game Over
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                resetVars();
                state = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                resetVars();
                state = 5;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (state == 0 || state == 2 || state == 3 || state == 4) { // 0 - Menu
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = false;
        } else if (state == 1) { // 1 - Store/MarketPlace

        } else if (state == 5) { // 5 - In-Game
            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                leftPressed = false;
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                rightPressed = false;
        } else if (state == 6) { // 6 - Pause

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