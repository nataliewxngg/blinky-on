import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Car {
    private BufferedImage car;
    private int x;
    private int y;
    private int speed;

    public Car(BufferedImage car, int x, int y, int speed) {
        this.car = car;
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void draw(Graphics g) {
        y += speed;
        g.drawImage(car, x, y, null);
    }

    // Getters and Setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void speedUp() {
        speed++;
    }

    public void move(int factor, String direction) {
        if (direction == "up")
            this.y -= factor;
        else if (direction == "left")
            this.x -= factor;
        else
            this.x += factor;
    }
}
