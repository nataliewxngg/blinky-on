import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.Graphics;
import javax.imageio.ImageIO;

public class Coin {

    private static BufferedImage coin = null;
    private int x;
    private int y;

    static {
        try {
            coin = ImageIO.read(new File("assets/coin.png"));
        } catch (IOException e) {
            System.out.println("coin.png not found.");
        }
    }

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // DESCRIPTION:
    // returns true if the car provided in the parameter collides
    // with this Coin object (this.)
    public boolean collides(Car car) { // PARAMETERS: a Car object to check collision with
        return car.getX() < this.x + Coin.getWidth() && car.getX() + car.getCar().getWidth() > this.x
                && car.getY() < this.y + Coin.getHeight() &&
                car.getY() + car.getCar().getHeight() > this.y;

        // RETURNS:
        // true if the car provided in the parameter collides with this Coin
        // object, false otherwise
    }

    public void draw(Graphics g, int speed, Boolean stop) {
        g.drawImage(coin, this.x, this.y, null);
        if (!stop)
            this.y += speed;
    }

    // Getters and Setters
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public static int getWidth() {
        return coin.getWidth();
    }

    public static int getHeight() {
        return coin.getHeight();
    }
}
