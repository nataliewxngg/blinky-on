import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class PowerUp {

    // instance variables + data encapsulation
    private BufferedImage powerUp;
    private String powerUpType;
    private int x, y, width, height;

    // DESCRIPTION: the CONSTRUCTOR method - utilized to create new PowerUp objects
    // and initialize its instance variables
    public PowerUp(String powerUpType, int x, int y) { // PARAMETERS:
                                                       // 1. the powerup type (ie. "dash", "doubleCoins")
                                                       // 2. x and y positions of the powerup object

        // initalizes the bufferedimage of this powerup objects according to the powerup
        // type provided
        try {
            this.powerUp = ImageIO.read(new File("assets/" + powerUpType + ".png"));
        } catch (IOException e) { // if png is missing, let the user know in the terminal
            e.printStackTrace();
        }

        this.powerUpType = powerUpType;
        this.x = x;
        this.y = y;
        this.width = this.powerUp.getWidth();
        this.height = this.powerUp.getHeight();

        // RETURNS: none (constructors do not return any value)
    }

    // DESCRIPTION: returns true if the car provided in the parameter collides
    // with this (this.) PowerUp object
    public boolean collides(Car car) { // PARAMETERS: a Car object to check collision with
        return car.getX() < this.x + this.width && car.getX() + car.getCar().getWidth() > this.x
                && car.getY() < this.y + this.height &&
                car.getY() + car.getCar().getHeight() > this.y;

        // RETURNS: true if the car provided in the parameter collides with this PowerUp
        // object, false otherwise
    }

    // DESCRIPTION: draws/renders this powerup's bufferedimage onto the Graphics
    // provided - moreover modifies its y position (to move downwards) unless the
    // boolean stop is true
    public void draw(Graphics g, int speed, Boolean stop) {

        // if not intended to be static, adjust this powerup's y position (add by
        // provided speed)
        if (!stop)
            this.y += speed;

        g.drawImage(this.powerUp, this.x, this.y, null);

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

    public String getPowerUpType() {
        return this.powerUpType;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
