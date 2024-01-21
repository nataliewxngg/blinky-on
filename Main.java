// Natalie Wong
// Due Sunday, January 21, 2024

// This program executes Blinky-ON utilizing graphics along with various classes,
// including Car.java, Coin.java, and PowerUp.java.

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
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
                   // 8 - Settings

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
    BufferedImage settings;
    BufferedImage arrow;
    BufferedImage coin;
    BufferedImage steeringWheel;
    BufferedImage dash;

    int FPS = 60;
    int screenWidth = 500;
    int screenHeight = 650;

    // utilized for the arrow in the main menu
    Map<Integer, ArrayList<Integer>> arrowStates = new TreeMap<>(); // key: arrow state (in ascending order)
                                                                    // (ie. 1-play, 2-shop, etc.)
                                                                    // value: the x and y coordinates of the arrow for
                                                                    // that specific arrow state
    int arrowState = 1;

    // stores the y positions for each state of the arrow in the settings
    int[] settingsArrowStates = { 435, 452, 470, 485, 502, 520 };
    int settingsArrowState = 1;
    Boolean keyListening = false;
    String hitboxes;
    String sfx;

    Font font = new Font("Press Start 2P", Font.PLAIN, 20);
    Font smallFont = new Font("Press Start 2P", Font.PLAIN, 13); // used for store/marketplace (state #1) and past
                                                                 // highscores (state #2)

    ArrayList<BufferedImage> cars = new ArrayList<>();
    ArrayList<BufferedImage> smallCars = new ArrayList<>(); // used for store/marketplace only

    // stores the status ('l'-locked, 'u'-unlocked, 'e'-equipped), costs, and
    // steering speeds of each car (in the store/marketplace)
    char[] status = new char[12];
    int[] costs = new int[12];
    int[] handlings = new int[12];

    Car player;
    int playerIndex;
    int selected;
    Boolean boughtFailed = false;

    int score = 0;
    int coins;

    // speed in relation to score
    Map<Integer, Integer> speeds = new HashMap<>(); // used to accelerate car in the commencement of the game

    // stores the car (enemies only), coin, and power-up objects present in the
    // current game - used to display
    LinkedList<Car> enemies = new LinkedList<>();
    LinkedList<Coin> gameCoins = new LinkedList<>();
    LinkedList<PowerUp> gamePowerUps = new LinkedList<>();

    // used for dynamic background
    int bgHeight;
    int y = 0;
    int speed = 1;

    // utilized for navigation
    Boolean leftPressed = false;
    Boolean rightPressed = false;
    Boolean upPressed = false;
    Boolean downPressed = false;

    int rightControl, leftControl, upControl, downControl;
    String rightControlText, leftControlText, upControlText, downControlText;

    Map<String, Boolean> powerUps = new HashMap<>(); // key: stores ALL the existing powerups
                                                     // value: a boolean to indicate whether or not the player has
                                                     // obtained or is utilizing them

    // used for powerups
    int doubleCoinsTime = 800;
    int invincibilityTime = 400;
    int dashTime = 100;
    int originalSpeed;

    // DESCRIPTION: resets all the variables necessary
    // (utilized to PREPARE FOR A NEW GAME or RETURN TO THE MAIN MENU)
    public void resetVars() { // PARAMETERS: none

        player = new Car(cars.get(playerIndex), 250, 700, 0); // resets player's position

        // clears all the enemies, coins, and powerups from the game
        enemies.clear();
        gameCoins.clear();
        gamePowerUps.clear();

        // sets all the powerups to FALSE
        powerUps.put("dash", false);
        powerUps.put("doubleCoins", false);
        powerUps.put("invincibility", false);

        player.setCarImage(cars.get(playerIndex));

        doubleCoinsTime = 800;
        invincibilityTime = 400;
        dashTime = 100;
        score = 0;
        speed = arrowState = 1;

        rightPressed = leftPressed = upPressed = downPressed = false;

        // RETURNS: none (void method)
    }

    // DESCRIPTION: plays or loops (dependent on the boolean parameter) an audio
    // file given its location in the directory
    public static void playMusic(String location, Boolean loop) { // PARAMETERS:
                                                                  // 1. audio file's address in directory
                                                                  // 2. boolean to indicate whether or not the audio
                                                                  // is intended to loop

        // utilizes the audio file path to determine and play its audio.
        // if loop is true, loop it continuously. otherwise, play it just once.
        try {
            File musicPath = new File(location);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            Clip clip1 = AudioSystem.getClip();
            clip1.open(audioInput);

            if (loop)
                clip1.loop(Clip.LOOP_CONTINUOUSLY);

            clip1.start();
        }
        // catch exceptions possibly thrown from initiating the audio file
        catch (IOException e) {
            System.out.println("Audio file not found!");
        } catch (UnsupportedAudioFileException n) {
            System.out.println(n);
        } catch (LineUnavailableException m) {
            System.out.println(m);
        }

        // RETURNS: none (void method)
    }

    //
    //
    //
    //
    //
    //
    // COMMENT HERE
    //
    //
    //
    public static String getSettings() {
        String out = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader("settings.txt"));
            String[] labels = { "STEER RIGHT  ", "STEER LEFT   ", "SPEED UP     ", "SLOW DOWN    " };

            for (int i = 0; i < 8; i++) {
                if (i < 4)
                    in.readLine();
                else {
                    out += labels[i - 4] + in.readLine() + "\n";
                }
            }

            out += "HITBOXES     " + in.readLine() + "\n";
            out += "SFX          " + in.readLine();

            in.close();
            return out;
        } catch (IOException e) {
            System.out.println("settings.txt is missing!");
        }
        return out;
    }

    public void updateSettings() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("settings.txt"));

            out.println(rightControl);
            out.println(leftControl);
            out.println(upControl);
            out.println(downControl);

            out.println(KeyEvent.getKeyText(rightControl));
            out.println(KeyEvent.getKeyText(leftControl));
            out.println(KeyEvent.getKeyText(upControl));
            out.println(KeyEvent.getKeyText(downControl));

            out.println(hitboxes);
            out.println(sfx);

            out.close();
        } catch (IOException e) {
            System.out.println("settings.txt is missing!");
        }
    }

    // DESCRIPTION: checks if player has collided with a coin and accumulates to
    // his/her currency if so. (+2 coins if the "double coins" power-up is active)
    public void coinCollision() { // PARAMETERS: none
        Coin coin;

        // collision checks the player with EVERY coin in the game.
        for (int i = 0; i < gameCoins.size(); i++) {
            coin = gameCoins.get(i);

            if (coin.collides(player)) {
                // plays coin sound effect if toggled in settings
                if (sfx.equals("On"))
                    playMusic("assets/coinSoundEffect.wav", false);

                // if collided, accumulate to his/her currency (+2 if "double coins" power-up is
                // active) and remove the Coin object from the game
                coins++;
                if (powerUps.get("doubleCoins"))
                    coins++;
                gameCoins.remove(i);
            }
        }
        // RETURNS: none (void method)
    }

    // DESCRIPTION: checks if player has collided with a power-up and
    // notes/acknowledges it in his/her current power-ups if so. (sets power-up to
    // true in powerUps map)
    public void powerUpsCollision() { // PARAMETERS: none
        PowerUp powerup;

        // collision checks the player with EVERY powerup in the game.
        for (int i = 0; i < gamePowerUps.size(); i++) {
            powerup = gamePowerUps.get(i);

            if (powerup.collides(player)) {
                // if collided, note/acknowledge it in his/her current power-ups
                // (by setting it to true in the powerUps map) and remove it from the game
                powerUps.put(powerup.getPowerUpType(), true);
                gamePowerUps.remove(i);
            }
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: returns a String of the top 10 highscores by accessing the
    // highscores.txt file
    public static String getHighScores() { // PARAMETERS: none
        int count = 1;
        String s;
        String output = "";

        // reads the highscores.txt file and utilizes it to format a string with the top
        // 10 past highscores labeled (ie. 1., 2., 3., etc.)
        try {
            BufferedReader in = new BufferedReader(new FileReader("highscores.txt"));

            // adds available highscore to string as long as
            // it is the first-tenth one
            while ((s = in.readLine()) != null && count < 11) {
                output += count + ") " + s + "\n";
                count++;
            }

            // if resultant string is empty, indicating that highscores.txt is empty,
            // replace the string with the statement "you have no existing highscores yet!"
            if (output.equals(""))
                output = "   YOU HAVE NO\n     EXISTING\n  HIGHSCORES YET!";
            in.close();
        } catch (IOException e) { // if highscores.txt is missing, let the user know in the terminal
            System.out.println("highscores.txt is missing!");
        }

        return output; // RETURNS: a String of the top 10 highscores
    }

    // DESCRIPTION: utilizes new score to update the highscores.txt file
    public static void saveScore(int score) { // PARAMETER: new score
        try {
            BufferedReader in = new BufferedReader(new FileReader("highscores.txt"));
            String s;
            ArrayList<Integer> scores = new ArrayList<>();

            // accumulate all the current highscores into an arraylist and add the new score
            // into it as well
            while ((s = in.readLine()) != null)
                scores.add(Integer.parseInt(s));
            scores.add(score);
            in.close();

            // sort the arraylist into ASCENDING order
            Collections.sort(scores);

            // rewrites the highscores.txt file with the new scores by extracting the
            // highest to lowest scores (last to first index)
            PrintWriter out = new PrintWriter(new File("highscores.txt"));
            for (int i = 0; i < 10; i++)
                if (!scores.isEmpty())
                    out.println(scores.remove(scores.size() - 1));
            out.close();
        } catch (IOException e) { // if highscores.txt is missing, let the user know in the terminal
            System.out.println("highscores.txt missing!");
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: spawns new cars, coins, or powerups depending on a random
    // number (unless player is utilizing their "dash" powerup - only spawn coins
    // then)
    public void spawnEntities() { // PARAMETERS: none
        Random random = new Random();
        int rand = random.nextInt(100);
        int[] xCar = { 90, 170, 250, 330 }; // x pos needed for each lane (accurate for cars)

        // if user is NOT dashing:
        if (dashTime == 100) {
            // if random # generated is 59, spawn a new car
            if (rand == 59) {
                int randSpeed;

                // generate a random # between 0-3 (inclusive) for both rand and randSpeed until
                // randSpeed != 0 (speed of enemy cars CANNOT be 0)
                do {
                    rand = random.nextInt(4); // for x pos
                    randSpeed = random.nextInt(4); // for speed of the car
                } while (randSpeed == 0);

                Car newCar = new Car(cars.get(random.nextInt(12)), xCar[rand], -250, randSpeed);
                // spawn new car if it doesn't collide with any existing enemy cars
                if (!newCar.collides(enemies))
                    enemies.add(newCar);
            }

            // if random # generated is 9, spawn a new coin
            else if (rand == 9) {
                rand = random.nextInt(4); // for x pos
                gameCoins.add(new Coin(xCar[rand] + 20, -200));
            }

            // generate a # between 0-399 (inclusive) and spawn a powerup only if it is ==59
            // (0-399 <- wider range = lower likelihood for powerups to be spawned)
            rand = random.nextInt(400);
            if (rand == 59) {
                rand = random.nextInt(2); // for powerup type

                ArrayList<String> powerUpTypes = new ArrayList<>(powerUps.keySet());
                String powerUpType = powerUpTypes.get(rand);

                rand = random.nextInt(4); // for x pos

                // spawn the new powerup only if
                // 1. the player does not already have or is utilizing it
                // 2. the amount of powerups displayed in the game is currently less than 1
                if (!powerUps.get(powerUpType) && gamePowerUps.size() < 1) {
                    gamePowerUps.add(new PowerUp(powerUpType, xCar[rand] + 15, -200));
                }
            }
        }
        // if the player IS dashing (not ending soon) and the random # generated is
        // between the exclusive range of 30-60, spawn a new coin
        else if (rand > 30 && rand < 60 && dashTime > 20) {
            rand = random.nextInt(4); // for x pos
            gameCoins.add(new Coin(xCar[rand] + 20, -200));
        }
        // RETURNS: none (void method)
    }

    // DESCRIPTION: displays all the coins, enemies, and powerups while
    // simultaneously removing the entities out of bounds (from the linkedlists)
    public void displayEntities(Graphics g) { // PARAMETERS: a Graphics component to render the entities on

        // displays the coins and removes the ones out of bounds
        for (int i = 0; i < gameCoins.size(); i++) {
            if (gameCoins.get(i).getY() > 650)
                gameCoins.remove(gameCoins.get(i));
            else
                gameCoins.get(i).draw(g, speed, false);
        }

        // displays the powerups and removes the ones out of bounds
        for (int i = 0; i < gamePowerUps.size(); i++) {
            if (gamePowerUps.get(i).getY() > 650) {
                gamePowerUps.remove(i);
            } else {
                gamePowerUps.get(i).draw(g, speed, false);
            }
        }

        // displays the enemeis and removes the ones out of bounds
        // (displays the hitboxes of the cars as well if hitboxes is toggled in
        // settings)
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).getY() > 700) {
                enemies.remove(i);
            } else {
                if (hitboxes.equals("On"))
                    g.drawRect(enemies.get(i).getX(), enemies.get(i).getY(),
                            enemies.get(i).getCar().getWidth(), enemies.get(i).getCar().getHeight());
                enemies.get(i).draw(g, false);
            }
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: updates the stats.txt file with, for instance, new amount of
    // coins.
    public void updateStats() { // PARAMETERS: none

        // rewrites the stats.txt file with present values
        try {
            PrintWriter out = new PrintWriter(new File("stats.txt"));

            out.println(coins);
            out.println(playerIndex);

            // updates each car's status, costs, and handlings (for the shop)
            for (int i = 0; i < 36; i++) {
                if (i < 12)
                    out.println(status[i]);
                else if (i < 24)
                    out.println(costs[i - 12]);
                else if (i < 36)
                    out.println(handlings[i - 24]);
            }

            out.close();
        } catch (IOException e) { // if stats.txt is missing, let the user know in the terminal
            System.out.println("stats.txt is missing!");
        }
    }

    // DESCRIPTION: initializes the JPanel
    public Main() { // PARAMETERS: none

        // JPanel default settings
        setPreferredSize(new Dimension(screenWidth, screenHeight));

        // starts the thread
        thread = new Thread(this);
        thread.start();

        // RETURNS: none (constructors don't return any value)
    }

    // DESCRIPTION: initializes all necessaary variables, updates the bg, repaints
    // components, and sets the FPS (utilized for thread)
    public void run() {
        initialize();

        // infinite loop to update bg and repaint components, as well as to set FPS
        while (true) {
            update();
            repaint();
            try {
                Thread.sleep(1000 / FPS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: initializes all necessary variables
    // (setups before the game starts running)
    public void initialize() { // PARAMETERS: none

        // initializes the font (Press Start 2P)
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(
                    Font.createFont(Font.TRUETYPE_FONT, new File("assets/PressStart2P-Regular.ttf")));
        } catch (IOException | FontFormatException e) { // displays exception message in terminal if an exception is
                                                        // thrown from initializing the font
            System.out.println(e);
        }

        // initializes all the bufferedimages (and bgHeight because that is dependent on
        // the bg bufferedimage)
        try {
            menu = ImageIO.read(new File("assets/menu.png"));
            bg = ImageIO.read(new File("assets/bg.png"));
            gameOver = ImageIO.read(new File("assets/game-over.png"));
            instructions = ImageIO.read(new File("assets/instructions.png"));
            about = ImageIO.read(new File("assets/about.png"));
            highscores = ImageIO.read(new File("assets/highscores.png"));
            store = ImageIO.read(new File("assets/store.png"));
            pause = ImageIO.read(new File("assets/pause.png"));
            settings = ImageIO.read(new File("assets/settings.png"));
            arrow = ImageIO.read(new File("assets/arrow.png"));
            coin = ImageIO.read(new File("assets/coin.png"));
            steeringWheel = ImageIO.read(new File("assets/steering-wheel.png"));
            dash = ImageIO.read(new File("assets/dash.png"));

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

            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-rusty-white-van.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-white-van.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-blue-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-red-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-yellow-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-gray-car.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-yellow-cab.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-white-truck.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-red-truck.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-school-bus.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-red-car-with-white-stripes.png")));
            cars.add(ImageIO.read(new File("assets/cars/transparent/transparent-police-car.png")));

            smallCars.add(ImageIO.read(new File("assets/cars/small/white-truck.png")));
            smallCars.add(ImageIO.read(new File("assets/cars/small/red-truck.png")));
            smallCars.add(ImageIO.read(new File("assets/cars/small/school-bus.png")));
        } catch (IOException e) { // prints stack trace if any image(s) is/are missing
            e.printStackTrace();
        }

        // initializes the x and y coords of the main menu arrow respective to its state
        arrowStates.put(1, new ArrayList<>(Arrays.asList(200, 262))); // 1-play
        arrowStates.put(2, new ArrayList<>(Arrays.asList(200, 280))); // 2-shop
        arrowStates.put(3, new ArrayList<>(Arrays.asList(140, 298))); // 3-instructions
        arrowStates.put(4, new ArrayList<>(Arrays.asList(190, 316))); // 4-about
        arrowStates.put(5, new ArrayList<>(Arrays.asList(150, 334))); // 5-highscores
        arrowStates.put(6, new ArrayList<>(Arrays.asList(170, 354))); // 6-settings

        // initializes the scores relative to the speed for gradual acceleration
        speeds.put(10, 2);
        speeds.put(20, 3);
        speeds.put(40, 4);
        speeds.put(60, 5);
        speeds.put(80, 6);

        // initializes the powerups and their "acquired"/"utilizing" statuses
        powerUps.put("dash", false);
        powerUps.put("doubleCoins", false);
        powerUps.put("invincibility", false);

        // reads info from stats.txt and initializes the # of coins, player's car, etc.
        try {
            BufferedReader in = new BufferedReader(new FileReader("stats.txt"));

            coins = Integer.parseInt(in.readLine());

            playerIndex = selected = Integer.parseInt(in.readLine());
            player = new Car(cars.get(playerIndex), 250, 700, 0);

            // intializes the statuses (locked, unlocked, equipped), costs, and steering
            // speeds of ALL cars
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
        } catch (IOException e) { // if stats.txt is missing, let the user know in the terminal
            System.out.println("stats.txt is missing!");
        }

        // reads info from settings.txt and initializes the right+left+up+down controls
        // and the activity of hitboxes + music
        try {
            BufferedReader in = new BufferedReader(new FileReader("settings.txt"));

            rightControl = Integer.parseInt(in.readLine());
            leftControl = Integer.parseInt(in.readLine());
            upControl = Integer.parseInt(in.readLine());
            downControl = Integer.parseInt(in.readLine());

            for (int i = 0; i < 4; i++)
                in.readLine();

            hitboxes = in.readLine();
            sfx = in.readLine();

            in.close();
        } catch (IOException e) { // if settings.txt is missing, let the user know in the terminal
            System.out.println("settings.txt is missing!");
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: updates the background - moves it relative to its speed
    public void update() { // PARAMETERS: none

        // given that the game is NOT in the PAUSE or GAMEOVER state,
        // move the background relative to its speed
        if (state != 7 && state != 6) {
            y += speed;
            if (y < -bgHeight)
                y = 0;
            else if (y > bgHeight)
                y = 0;
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: paints ALL of the graphics components on to the screen
    public void paintComponent(Graphics g) { // PARAMETERS: a graphics component for rendering
        super.paintComponent(g);

        // sets the font style and color
        g.setFont(font);
        g.setColor(Color.WHITE);

        // displays the background image (3 statements due to moving background)
        g.drawImage(bg, 0, y, null);
        g.drawImage(bg, 0, y + bgHeight, null);
        g.drawImage(bg, 0, y - bgHeight, null);

        // displays the player
        player.draw(g, false);

        // displays the coin symbol and amount (top right corner)
        g.drawImage(coin, 390, 10, null);
        g.drawString(coins + "", 420, 34);

        // given that the game is NOT in its PAUSE/GAMEOVER state,
        // move the player to its given position (intro animation) and enable player
        // movement
        if (state != 6 && state != 7) {

            // player's extremely cool introduction!!!
            // (moves up gradually until at desired y position - evident when game first
            // launches)
            if (player.getY() > 500) {
                player.move(2, "up");
            } else if (player.getY() > 460) {
                player.move(1, "up");
            } else if (player.getY() > 415 && (playerIndex == 7 || playerIndex == 8 || playerIndex == 9))
                player.move(1, "up");

            // move the player according to key presses (excluding speeding
            // up/slowing down - this is ONLY for in-game)
            if (leftPressed && player.getX() > 85)
                player.move(handlings[playerIndex], "left");
            if (rightPressed && player.getX() < 330)
                player.move(handlings[playerIndex], "right");

            // given that the game is NOT in its IN-GAME/STORE state, display the main menu
            // arrow
            if (state != 5 && state != 1)
                g.drawImage(arrow, arrowStates.get(arrowState).get(0), arrowStates.get(arrowState).get(1), null);
        }

        // display the main menu if state is 0
        if (state == 0) {
            g.drawImage(menu, 0, 0, null);
        }

        // display the marketplace and its varying components (dependent on key presses
        // and several other factors/variables) if state is 1
        else if (state == 1) {
            // sets and uses the smaller font
            g.setFont(smallFont);

            g.drawImage(store, 0, 0, null);

            // if the user is inspecting a large vehicle in the shop,
            // use the smaller images to save space
            if (selected != 7 && selected != 8 && selected != 9)
                g.drawImage(cars.get(selected), 160, 380, null);
            else
                g.drawImage(smallCars.get(selected - 7), 170, 365, null);

            // display the status of the car selected (locked, unlocked, equipped)
            if (status[selected] == 'e') {
                g.drawString("Equipped!", 200, 350);
            } else if (status[selected] == 'u') {
                g.drawString("Unlocked!", 200, 350);
            } else
                g.drawString("Locked!", 210, 350);

            // if user has recently failed to buy a car, display "you don't have enough
            // coins!" in the store
            // otherwise, display its cost and steering speed as usual
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

        }

        // display the past highscores if state is 2
        else if (state == 2) {
            g.drawImage(highscores, 0, 0, null);

            // sets and uses the smaller font
            g.setFont(smallFont);

            // gets a String of the top 10 past highscores and displays them using
            // appropriate formatting (improvised due to .printf's incompatibility with
            // .drawString())
            String s = getHighScores();
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
        }

        // displays the instructions if the state is 3
        else if (state == 3) {
            g.drawImage(instructions, 0, 0, null);
        }

        // displays the additional information (eg. developer, teacher, date) if the
        // state is 4
        else if (state == 4) {
            g.drawImage(about, 0, 0, null);
        }

        // displays, operates, and executes the game if state is 5
        else if (state == 5) {
            // if player collides with an enemy while NOT invincible, update highscores.txt
            // and transition to the GAMEOVER state
            if (invincibilityTime == 400)
                if (player.collides(enemies)) {
                    saveScore(score);
                    state = 7;
                }
            // ensure that enemies do NOT collide into eachother
            Car.enemyCollides(enemies);

            // checks for coin and powerups collision respectively
            coinCollision();
            powerUpsCollision();

            // spawns new enemies (cars), coins, and powerups
            spawnEntities();

            // displays the enemies, coins, and powerups
            // (and removes the entities out of bounds)
            displayEntities(g);

            // adjusts the score and the speed according to it
            // (by utilizing the map initialized for the scores relative to speeds earlier)
            score += speed;
            for (int i = 0; i < speeds.size(); i++) {
                if (speeds.containsKey(score)) { // if the current score corresponds to a particular speed, adjust the
                                                 // speed of the bg to its value
                    speed = speeds.get(score);
                }
            }

            // if the double coins powerup is currently active,
            // decrease its remaining time and display its 2x bar
            if (powerUps.get("doubleCoins")) {
                doubleCoinsTime--;

                // display at varying positions depending on if the invincibility powerup is
                // ALSO active
                if (powerUps.get("invincibility") && doubleCoinsTime > (invincibilityTime * 2)) {
                    g.drawString("2X COINS", 40, 530);
                    g.fillRect(50, 540, doubleCoinsTime / 2, 10);
                    g.drawRect(40, 535, 420, 20);
                } else {
                    g.drawString("2X COINS", 40, 590);
                    g.fillRect(50, 600, doubleCoinsTime / 2, 10);
                    g.drawRect(40, 595, 420, 20);
                }

                if (doubleCoinsTime == 0) { // if the double coins powerup has reached its time limit, set the "double
                                            // coins" powerup to inactive again and reset its remaining time
                    powerUps.put("doubleCoins", false);
                    doubleCoinsTime = 800;
                }
            }

            // if the player has obtained/is utilizing the "dash" powerup:
            if (powerUps.get("dash")) {
                // if the player has not yet initiated it, display the dash powerup by the
                // right-hand side
                if (dashTime == 100)
                    g.drawImage(dash, 450, 590, null);

                // if the player has initiated it, speed up the other components
                // in a quadratic pattern so that the animation appears smooth.

                // however, if the dash is over, reset the remaining dash time, set the speed of
                // the bg back to its original speed, and set it back to inactive.
                else {
                    if (dashTime == 0) { // dash ended
                        dashTime = 100;
                        speed = originalSpeed;
                        powerUps.put("dash", false);
                    } else { // dashing
                        if (dashTime > 50) {
                            speed++;
                            Car.speedUpOrDown(enemies, "up");
                        } else {
                            speed--;
                            Car.speedUpOrDown(enemies, "down");
                        }
                        dashTime--;
                    }
                }
            }

            // if the invincibility powerup is currently active,
            // decrease its remaining time and display its invinsibility bar
            if (powerUps.get("invincibility")) {
                invincibilityTime--;
                player.setCarImage(cars.get(playerIndex + 12));

                // display at varying positions depending on if the double
                // coins powerup is ALSO active
                if (powerUps.get("doubleCoins") && (invincibilityTime * 2) > doubleCoinsTime) {
                    g.drawString("INVINCIBILITY", 40, 530);
                    g.fillRect(50, 530, invincibilityTime, 10);
                    g.drawRect(40, 525, 420, 20);
                } else {
                    g.drawString("INVINCIBILITY", 40, 590);
                    g.fillRect(50, 600, invincibilityTime, 10);
                    g.drawRect(40, 595, 420, 20);
                }

                // alter between the transparent and default images of the player's car prior to
                // ending the invincibility powerup (flashing intended to warn the player)
                if ((invincibilityTime <= 100 && invincibilityTime >= 83)
                        || (invincibilityTime <= 66 && invincibilityTime >= 49)
                        || (invincibilityTime <= 32 && invincibilityTime >= 16))
                    player.setCarImage(cars.get(playerIndex));
                else if (invincibilityTime <= 83 && invincibilityTime >= 66
                        || (invincibilityTime <= 49 && invincibilityTime > 32)
                        || (invincibilityTime <= 16 && invincibilityTime > 0))
                    player.setCarImage(cars.get(playerIndex + 12));

                else if (invincibilityTime == 0) { // if the invincibility powerup has reached its time limit, set the
                    // "invincibility" powerup to inactive again, reset its remaining time,
                    // and revert back to the original car's bufferedimage
                    powerUps.put("invincibility", false);
                    invincibilityTime = 400;
                    player.setCarImage(cars.get(playerIndex));
                }
            }

            // display the score and continuously update the amount of coins in stats.txt
            g.drawString("score: " + Integer.toString(score), 20, 40);
            updateStats();

        }

        // displays the coins, powerups, and enemies (cars) if in pause/gameover state
        // (last parameter - true - indicates that they are STOPPED (static))
        else if (state == 6 || state == 7) {
            for (Coin coin : gameCoins)
                coin.draw(g, speed, true);
            for (PowerUp powerUp : gamePowerUps)
                powerUp.draw(g, speed, true);
            for (Car enemy : enemies) {
                if (hitboxes.equals("On"))
                    g.drawRect(enemy.getX(), enemy.getY(),
                            enemy.getCar().getWidth(), enemy.getCar().getHeight());
                enemy.draw(g, true);
            }

            // displays the pause menu and score if state is 6
            if (state == 6) {
                g.drawImage(pause, 0, 0, null);
                g.drawString("score: " + Integer.toString(score), 20, 40);
            }

            // displays the gameover screen if state is 7
            else {
                g.drawImage(gameOver, 0, 0, null);
            }
        }

        //
        //
        //
        //
        //
        //
        // COMMENT HERE
        //
        //
        //
        else if (state == 8) {
            g.drawImage(settings, 0, 0, null);
            g.setFont(smallFont);

            g.drawString(">", 115, settingsArrowStates[settingsArrowState - 1]);

            String s = getSettings();
            int n = 435;

            for (String x : s.split("\n")) {
                g.drawString(x, 140, n);
                n += 17;
            }

        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: detects key presses and adjusts variables accordingly
    public void keyPressed(KeyEvent e) { // PARAMETERS: a KeyEvent object that invokes when a key is pressed

        // if game is in state 0 - main menu:
        if (state == 0) {
            // if player pressed the DOWN ARROW, increase the arrow state UNLESS it is
            // already at its maximum (return back to first arrow state in this case)
            if (e.getKeyCode() == KeyEvent.VK_DOWN)
                if (arrowState == 6)
                    arrowState = 1;
                else
                    arrowState++;

            // if player pressed the UP ARROW, decrease the arrow state UNLESS it is
            // already at its minimum (return back to last arrow state in this case)
            else if (e.getKeyCode() == KeyEvent.VK_UP)
                if (arrowState == 1)
                    arrowState = 6;
                else
                    arrowState--;

            // if player pressed the enter key, enter the respective state depending on
            // where the arrow was pointed
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
                else if (arrowState == 6)
                    state = 8;
            }
            // if the player pressed the left or right control keys, update the
            // left/right pressed variables
            if (e.getKeyCode() == leftControl)
                leftPressed = true;
            if (e.getKeyCode() == rightControl)
                rightPressed = true;
        }

        // if game is in state 1 - store/marketplace:
        else if (state == 1) {
            // if player pressed the enter key, adjust necessary variables and return to
            // the main menu
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                boughtFailed = false;
                selected = playerIndex;
                state = 0;
            }

            // if the player presses the shift key, unlock or equip the current car OR
            // display the failed buying message (depending on respective situations) +
            // update stats.txt
            else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {

                // if current car is unlocked or equipped, enable the user to equip it and
                // update the car statuses. moreover, adjust the player's y position if the
                // newly selected car is a large vehicle.
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
                }

                // if current car is locked, check if the player can afford it.
                // if so, set its status to unlock. otherwise, set boughtfailed to true
                else { // check for buy
                    if (coins >= costs[selected]) {
                        status[selected] = 'u';
                        coins -= costs[selected];
                    } else {
                        boughtFailed = true;
                    }
                }

                updateStats(); // updates stats.txt
            }

            // if player presses the left or right arrow keys, navigate to and display the
            // next/previous car (and update the necessary variables)
            else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
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
        }

        // if game is in state 2/3/4/8 - highscores/instructions/about:
        else if (state == 2 || state == 3 || state == 4) {
            // if user presses enter, return to main menu
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
                state = 0;

            // if the player presses the left or right control keys, update the
            // left/right pressed variables
            if (e.getKeyCode() == leftControl)
                leftPressed = true;
            if (e.getKeyCode() == rightControl)
                rightPressed = true;
        }

        // if the user is in state 5 - in-game:
        else if (state == 5) {
            // if the player presses the left or right control keys, update the
            // left/right pressed variables
            if (e.getKeyCode() == leftControl)
                leftPressed = true;
            if (e.getKeyCode() == rightControl)
                rightPressed = true;

            // if the player presses the escape key, enter the pause menu
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                state = 6;

            // if the player activates the "dash" powerup, note the original speed and note
            // its initiation (by setting dashTime to 99)
            if (e.getKeyCode() == KeyEvent.VK_SHIFT && dashTime == 100 && powerUps.get("dash")) {
                dashTime = 99;
                originalSpeed = speed;
            }

        }

        // if the user is in state 6 - pause:
        else if (state == 6) {

            // if the user presses the 'R' key, reset the necessary variables and restart
            // the game
            if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                resetVars();
                state = 5;
            }

            // if the user presses the 'M' key, reset the necessary variables and return to
            // the main menu
            else if (e.getKeyChar() == 'm' || e.getKeyChar() == 'M') {
                resetVars();
                state = 0;
            }

            // if the user presses the enter key, return back to the game
            else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                state = 5;
        }

        // if the user is in state 7 - gameover:
        else if (state == 7) {

            // if the player presses the escape key, reset the neccessary variables and
            // return to the main menu
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                resetVars();
                state = 0;
            }

            // if the player presses the enter key, reset the necessary variables and
            // restart the game
            else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                resetVars();
                state = 5;
            }
        }
        //
        //
        //
        //
        //
        ///
        ///
        //
        //
        //
        //
        //
        else if (state == 8) {
            if (!keyListening)
                // if the user presses the enter key, return back to the main menu
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    state = 0;
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: detects key releases and adjusts variables accordingly
    public void keyReleased(KeyEvent e) { // PARAMETERS: a KeyEvent object that invokes when a key is released

        // if user is in state 0/2/3/4 - menu/highscores/instructions/about/in-game:
        if (state == 0 || state == 2 || state == 3 || state == 4 || state == 5) {

            // if the player releases the left or right control keys, update the
            // left/right pressed variables
            if (e.getKeyCode() == leftControl)
                leftPressed = false;
            if (e.getKeyCode() == rightControl)
                rightPressed = false;
        }

        // if user is in state 5 only - in-game
        if (state == 5) {

            // if the user releases the up/down arrow key,
            // speed up/slow down accordingly (since it's under keyReleased, the up/down
            // arrow keys must be TAPPED to alter speed)

            // ONLY APPLICABLE IF
            // 1. speed is less/greater than the maximum/minimum speed
            // 2. score is greater than 80 (players CANNOT adjust speed prior to this to
            // avoid unlogically quick acceleration)

            if (e.getKeyCode() == upControl) {
                if (speed < 11 && score >= 80) {
                    speed++;
                    Car.speedUpOrDown(enemies, "up");
                }
            }
            if (e.getKeyCode() == downControl) {
                if (speed > 5 && score >= 80) {
                    speed--;
                    Car.speedUpOrDown(enemies, "down");
                }
            }
        }
        //
        //
        //
        //
        //
        //
        // COMMENT HERE
        //
        //
        //
        else if (state == 8) {
            if (!keyListening) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (settingsArrowState == 6)
                        settingsArrowState = 1;
                    else
                        settingsArrowState++;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (settingsArrowState == 1)
                        settingsArrowState = 6;
                    else
                        settingsArrowState--;
                }
                if (e.getKeyCode() == KeyEvent.VK_CONTROL && (settingsArrowState >= 1 && settingsArrowState <= 4)) {
                    keyListening = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_SHIFT && settingsArrowState == 5) {
                    if (hitboxes.equals("On"))
                        hitboxes = "Off";
                    else
                        hitboxes = "On";
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT && settingsArrowState == 6) {
                    if (sfx.equals("On"))
                        sfx = "Off";
                    else
                        sfx = "On";
                }
            } else {
                if (settingsArrowState == 1) { // RIGHT
                    rightControl = e.getKeyCode();
                } else if (settingsArrowState == 2) {
                    leftControl = e.getKeyCode();
                } else if (settingsArrowState == 3) {
                    upControl = e.getKeyCode();
                } else if (settingsArrowState == 4) {
                    downControl = e.getKeyCode();
                }
                keyListening = false;
            }
            updateSettings();
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: initializes and manages the principal JFrame and panel
    public static void main(String[] args) { // PARAMETERS: String[] args not used
        playMusic("assets/bgMusic.wav", true);

        // initializes the JFrame and panel
        JFrame frame = new JFrame("Blinky-ON");
        Main panel = new Main();

        // adds Panel to JFrame, which is later implemented with KeyListener
        frame.add(panel);
        frame.addKeyListener(panel);

        // basic management of JFrame
        frame.setVisible(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminates program when x'd out
        frame.setResizable(false);

        // RETURNS: none (void method)
    }

    // DESCRIPTION: detects the typing of keys (for keylistener)
    public void keyTyped(KeyEvent e) { // PARAMETERS: a KeyEvent object that invokes when a key is typed
        // RETURNS: none (void method)
    }
}