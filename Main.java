import javax.swing.JFrame;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    Canvas2D canvas;
    Timer timer;

    private ActionListener ticker = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Graphics2D CTX = canvas.getGraphics();
            canvas.draw();
        }
    };

    public Main() {
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(50, 50);
        setBackground(Color.WHITE);

        canvas = new Canvas2D(400, 400, Color.WHITE);
        timer = new Timer(42, ticker);

        add(canvas);
        pack();
        start();
    }

    public void start() {
        timer.start();
    }

    public static void main(String[] args) {
        new Main();
    }
}