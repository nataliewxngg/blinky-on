import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

public class Car {

    // instance variables + data encapsulation
    private BufferedImage car;
    private int x;
    private int y;
    private int speed;

    // DESCRIPTION: the CONSTRUCTOR method - utilized to create new Car objects and
    // initialize its instance variables
    public Car(BufferedImage car, int x, int y, int speed) { // PARAMETERS:
                                                             // 1. the car's bufferedimage
                                                             // 2. x and y positions of the car object,
                                                             // as well as its speed
        this.car = car;
        this.x = x;
        this.y = y;
        this.speed = speed;

        // RETURNS: none (constructors do not return any value)
    }

    // DESCRIPTION: draws/renders this car object's bufferedimage onto the Graphics
    // provided - moreover modifies its y position (car moves downwards) unless
    // the boolean stop is true
    public void draw(Graphics g, Boolean stop) { // PARAMETERS:
                                                 // 1. a Graphics component to render the car on
                                                 // 2. a boolean to indicate whether or not the car object should be
                                                 // static (constant x,y pos)

        // if not intended to be static, adjust this car's y position (add by its speed)
        if (!stop)
            y += speed;

        g.drawImage(car, x, y, null);

        // RETURNS: none (void method)
    }

    // DESCRIPTION: checks if this Car object collides/collided with any of the Cars
    // in the provided linkedlist
    public Boolean collides(LinkedList<Car> cars) { // PARAMETERS: A linkedlist of cars to check for collision with
        Car car;

        // traverses through EACH Car object in the linkedlist - returns true if it
        // collides with this (this.) car
        for (int i = 0; i < cars.size(); i++) {
            car = cars.get(i);

            if (this.x < car.x + car.car.getWidth() &&
                    this.x + this.car.getWidth() > car.x &&
                    this.y < car.y + car.car.getHeight() &&
                    this.y + this.car.getHeight() > car.y) {
                return true; // RETURNS: true if this car collides with another car in the linkedlist
            }
        }
        return false; // RETURNS: false if this car does not collide with another car in the
                      // linkedlist
    }

    // DESCRIPTION: adjusts cars' speed if there are other cars ahead of it
    // (utilized to avoid collision in enemy cars)
    public static void enemyCollides(LinkedList<Car> cars) { // PARAMETERS: a linkedlist of cars

        // traverses through ALL the cars in the linkedlist and - for each car - checks
        // its proximity to EVERY other car included.

        // if its distance decreases to a certain measure (and if its not being compared
        // to itself), adjust its speed to match the speed of the car ahead.
        for (int i = 0; i < cars.size(); i++) {
            for (int j = 0; j < cars.size(); j++) {
                if (i != j && cars.get(i).y > cars.get(j).y && cars.get(i).y - 300 < cars.get(j).y
                        && cars.get(i).x == cars.get(j).x) {
                    cars.get(i).speed = cars.get(j).speed;
                }
            }
        }

        // RETURNS: none (void method)

    }

    // DESCRIPTION: adjusts a car's x/y position according to the adjustment factor
    // and direction given
    public void move(int factor, String direction) { // PARAMETERS: an adjustment factor and the direction (x or y) to
                                                     // modify

        // adjusts the x/y position by referring to the adjustment factor and direction
        if (direction == "up")
            this.y -= factor;
        else if (direction == "down")
            this.y += factor;
        else if (direction == "left")
            this.x -= factor;
        else
            this.x += factor;

        // RETURNS: none (void method)
    }

    // DESCRIPTION: increases/decreases the speed of a linkedlist of cars
    public static void speedUpOrDown(LinkedList<Car> cars, String upOrDown) { // PARAMETERS: a linkedlist of cars to
                                                                              // adjust speeds to, and a speed up/down
                                                                              // indication

        // increase the speed of every car in the linkedlist if upOrDown is "up".
        // otherwise, decrease it.
        if (upOrDown.equals("up")) {
            for (Car car : cars)
                car.speed++;
        } else {
            for (Car car : cars)
                car.speed--;
        }

        // RETURNS: none (void method)
    }

    // DESCRIPTION: getter methods - allows the files utilizing Car objects
    // to access its private attributes
    // PARAMETERS: none
    // RETURNS: dependent on each attribute's data type
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

    // DESCRIPTION: setter methods - allows the files utilizing Car objects
    // to access/edit its private attributes
    // PARAMETERS: none
    // RETURNS: none (void methods)
    public void setY(int val) {
        this.y = val;
    }

    public void setCarImage(BufferedImage newCar) {
        this.car = newCar;
    }
}
