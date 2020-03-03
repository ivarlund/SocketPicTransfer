import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Simple JPanel class to hold the image received from the Client class.
 *
 * @author Ivar Lund
 * ivlu1468
 * ivarnilslund@gmail.com
 */
public class Picture extends JPanel{

    private ObjectInputStream input;
    private BufferedImage img;

    /**
     * Class constructor. Paints a BufferedImage to this JPanel.

     * @param img   the image to be put at JPanel
     */
    public Picture(BufferedImage img)  {
        this.img = img;
    }

    /**
     * PaintComponent for rendering the image.
     */
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(img, 0, 0 ,this);
    }

}
