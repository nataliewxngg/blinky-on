import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Car {
    private BufferedImage car;
    private int x;
    private int y;
    private Boolean isPlayer; // use later on to move all cars that are !isPlayer by y coord

    public Car(BufferedImage car, int x, int y, Boolean isPlayer) {
        this.car = car;
        this.x = x;
        this.y = y;
        this.isPlayer = isPlayer;
    }

    public void draw(Graphics g) {
        g.drawImage(car, x, y, null);
    }

    // Getters and Setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
