import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class PowerUp {
    private BufferedImage powerUp;
    private int x, y;

    public PowerUp(BufferedImage powerUp, int x, int y) {
        this.powerUp = powerUp;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g, int speed) {
        g.drawImage(this.powerUp, this.x, this.y, null);
        this.y += speed;
    }

    // Getters and Setters
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
