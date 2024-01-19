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

    public static void enemyCollides(LinkedList<Car> cars) {
        for (int i = 0; i < cars.size(); i++) {
            for (int j = 0; j < cars.size(); j++) {
                // in-front
                if (cars.get(i).y > cars.get(j).y) {
                    if (cars.get(i).y - 500 < cars.get(j).y) {
                        if (cars.get(i).x == cars.get(j).x) // in-front
                            cars.get(i).speed = cars.get(j).speed;
                        else if (cars.get(i).x - 1 == cars.get(j).x || cars.get(j).x + 1 == cars.get(j).x) { // side-by-side
                            cars.get(i).speed = cars.get(j).speed + 1;
                        }
                    }
                }
            }
        }
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

    public int getSpeed() {
        return speed;
    }

    public void setY(int val) {
        this.y = val;
    }

    public void setSpeed(int val) {
        this.speed = val;
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
