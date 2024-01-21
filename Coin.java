import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.Graphics;
import javax.imageio.ImageIO;

public class Coin {

    // static variables + data encapsulation
    private static BufferedImage coin = null;

    // instance variables + data encapsulation
    private int x;
    private int y;

    // initializes the bufferedimage for the static variable coin, which must
    // utilize a try-catch statement as it may throw an IOException
    static {
        try {
            coin = ImageIO.read(new File("assets/coin.png"));
        } catch (IOException e) {
            System.out.println("coin.png not found.");
        }
    }

    // DESCRIPTION: the CONSTRUCTOR method - utilized to create new Coin objects and
    // initialize its instance variables
    public Coin(int x, int y) { // PARAMETERS: x and y positions of the coin object
        this.x = x;
        this.y = y;

        // RETURNS: none (constructors do not return any value)
    }

    // DESCRIPTION: returns true if the car provided in the parameter collides
    // with this (this.) Coin object
    public boolean collides(Car car) { // PARAMETERS: a Car object to check collision with
        return car.getX() < this.x + Coin.getWidth() && car.getX() + car.getCar().getWidth() > this.x
                && car.getY() < this.y + Coin.getHeight() &&
                car.getY() + car.getCar().getHeight() > this.y;

        // RETURNS: true if the car provided in the parameter collides with this Coin
        // object, false otherwise
    }

    // DESCRIPTION: draws/renders a coin bufferedimage onto the Graphics provided -
    // moreover modifies its y position (to move downwards) unless the boolean stop
    // is true
    public void draw(Graphics g, int speed, Boolean stop) {
        // if not intended to be static, adjust this coin's y position (add by
        // provided speed)
        if (!stop)
            this.y += speed;

        g.drawImage(coin, this.x, this.y, null);

        // RETURNS: none (void method)
    }

    // DESCRIPTION: getter methods - allows the files utilizing Coin objects
    // to access its private attributes
    // PARAMETERS: none
    // RETURNS: dependent on each attribute's data type
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
