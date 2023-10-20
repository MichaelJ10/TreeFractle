import javax.swing.JFrame;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class Main extends JFrame {
    Canvas2D canvas;
    Timer timer;
    Animation animation;
    int frameIndex = FRAME_START;
    final static int FRAME_START = 0;

    private ActionListener ticker = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Graphics2D CTX = canvas.getGraphics();
            CTX.drawImage(animation.get.frames().get(frameIndex), 0, 0, null);
            frameIndex++;
            if(frameIndex >= animation.get.frames().size()) frameIndex = FRAME_START;
            if(frameIndex < FRAME_START) frameIndex = animation.get.frames().size() - 1;
            canvas.draw();
        }
    };

    public Main() {
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(50, 50);
        setBackground(Color.BLACK);

        canvas = new Canvas2D(889, 500, getBackground());
        canvas.setResolution(3840, 2160);
        timer = new Timer(42, ticker);

        add(canvas);
        pack();
        start();
    }

    public void start() {
        try{Thread.sleep(50);}
        catch(InterruptedException e) {e.printStackTrace();}
        animation = new Animation(canvas, canvas.getResolution().getWidth() / 2, canvas.getResolution().getHeight() / 2, 1.0, 6, 90, 200, 0, 0.707, 15, 0, 15, 1);
        animation.set.drawOnChange(true);
        animation.set.outputToList(false);
        animation.initAnimation(1000);
        animation.addAnimation(Animation.Variable.angle, 0, 500, 180, 0);
        animation.addAnimation(Animation.Variable.angle, 501, 1000, 0, 180);
        animation.animate();
        animation.render();
        if(animation.get.hasRenderFrames()) timer.start();
        else dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static void main(String[] args) {
        new Main();
    }
}