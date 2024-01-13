import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

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

    public void draw(Graphics g, Boolean stop) {
        if (!stop)
            y += speed;
        g.drawImage(car, x, y, null);
    }

    public Boolean collides(LinkedList<Car> cars) {
        Car car;
        for (int i = 0; i < cars.size(); i++) {
            car = cars.get(i);

            if (this.x < car.x + car.car.getWidth() &&
                    this.x + this.car.getWidth() > car.x &&
                    this.y < car.y + car.car.getHeight() &&
                    this.y + this.car.getHeight() > car.y) {
                return true;
            }
        }
        return false;
    }

    // Getters and Setters
    public BufferedImage getCar() {
        return car;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int val) {
        this.y = val;
    }

    public void move(int factor, String direction) {
        if (direction == "up")
            this.y -= factor;
        else if (direction == "down")
            this.y += factor;
        else if (direction == "left")
            this.x -= factor;
        else
            this.x += factor;
    }
}
