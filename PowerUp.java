import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class PowerUp {
    private BufferedImage powerUp;
    private String powerUpType;
    private int x, y, width, height;

    public PowerUp(String powerUpType, int x, int y) {
        try {
            this.powerUp = ImageIO.read(new File("assets/" + powerUpType + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.powerUpType = powerUpType;
        this.x = x;
        this.y = y;
        this.width = this.powerUp.getWidth();
        this.height = this.powerUp.getHeight();
    }

    // DESCRIPTION:
    // returns true if the car provided in the parameter collides
    // with this PowerUp object (this.)
    public boolean collides(Car car) { // PARAMETERS: a Car object to check collision with
        return car.getX() < this.x + this.width && car.getX() + car.getCar().getWidth() > this.x
                && car.getY() < this.y + this.height &&
                car.getY() + car.getCar().getHeight() > this.y;

        // RETURNS:
        // true if the car provided in the parameter collides with this PowerUp
        // object, false otherwise
    }

    public void draw(Graphics g, int speed, Boolean stop) {
        g.drawImage(this.powerUp, this.x, this.y, null);
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
