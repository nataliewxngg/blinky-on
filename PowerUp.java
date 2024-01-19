import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class PowerUp {
    private BufferedImage powerUp;
    private String powerUpType;
    private int x, y;

    public PowerUp(String powerUpType, int x, int y) {
        try {
            this.powerUp = ImageIO.read(new File("assets/" + powerUpType + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.powerUpType = powerUpType;
        this.x = x;
        this.y = y;
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

    public int getWidth() {
        return powerUp.getWidth();
    }

    public int getHeight() {
        return powerUp.getHeight();
    }
}
