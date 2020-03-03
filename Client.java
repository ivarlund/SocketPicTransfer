/**
 * Simple implementation of a program that allows the user to pick and send images over
 * a socket and receive images from same socket.
 *
 * @author Ivar Lund
 * ivarnilslund@gmail.com
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Main class. Creates a GUI for picking an image and sending it via a socket.
 */
public class Client extends JFrame implements Runnable {

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket socket;

    Thread t = new Thread(this);

    /**
     * Class constructor. Sets up GUI, socket and outputstream as well as listener thread.
     */
    public Client() {
        super("image Transciever");

        try {
            socket = new Socket("atlas.dsv.su.se", 4848);
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel top = new JPanel();

        JButton send = new JButton("Send");
        send.addActionListener(new sendListener());

        top.add(send);
        add(top, BorderLayout.NORTH);

        setVisible(true);
        setSize(720, 540);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        t.start();
    }

    /**
     * Lets the user pick an image from the computer. Converts the image to a byte array
     * and then to a Storage object and writes it to outputstream.
     */
    public void pickImage() {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
        int answer = jfc.showOpenDialog(this);
        try {
            if (answer != JFileChooser.APPROVE_OPTION) {
                return;
            } else {
                if (ImageIO.read(jfc.getSelectedFile()) == null) {
                    System.out.println("You have to pick an image!");
                    return;
                } else {
                    BufferedImage img = ImageIO.read(jfc.getSelectedFile());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(img, "png", baos);
                    byte[] b = baos.toByteArray();
                    sendImage(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a Storage object and writes it to the outputstream.
     *
     * @param b the array that is constructed to a Storage object.
     */
    public void sendImage(byte[] b) {
        Storage storage = new Storage(b);
        try {
            output.writeObject(storage);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ActionListener for 'Send' button.
     */
    private class sendListener implements ActionListener {

        /**
         * Method call for ActionListener
         */
        public void actionPerformed(ActionEvent e) {
            try {
                pickImage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Constructs a BufferedImage from a Storage object
     *
     * @param storage Storage object to become a BufferedImage
     */
    public void constructImage(Storage storage) {
        ByteArrayInputStream bais = new ByteArrayInputStream(storage.getData());
        try {
            BufferedImage img = ImageIO.read(bais);
            add(new Picture(img), BorderLayout.CENTER);
            repaint();
            revalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Listener for incoming objects from socket connection.
     */
    public void run() {
        while (true) {
            try {
                input = new ObjectInputStream(socket.getInputStream());
                Storage storage = (Storage) input.readObject();
                constructImage(storage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //Main method
    public static void main(String[] args) throws Exception {
        new Client();
    }

}
