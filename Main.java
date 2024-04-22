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
    final static boolean HD = true;

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
        setVisible(!HD);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(50, 50);
        setBackground(Color.BLACK);

        canvas = new Canvas2D(889, 500, getBackground());
        if(HD) canvas.setResolution(3840, 2160);
        timer = new Timer(42, ticker);

        add(canvas);
        pack();
        start();
    }

    public void start() {
        try{Thread.sleep(50);}
        catch(InterruptedException e) {e.printStackTrace();}
        long start = System.currentTimeMillis();
        animation = new Animation(canvas, canvas.getResolution().getWidth() / 2, canvas.getResolution().getHeight() / 2, (HD ? 1.0 : 0.23), 6, 90, 200, 0, 0.707, (HD ? 15 : 12), 0, 15, 1);
        animation.set.drawOnChange(true);
        animation.set.outputToList(!HD);
        animation.set.HD(HD);

        animation.addBranch(0, 0, 330);
        animation.addBranch(0, 0, 270);
        animation.addBranch(0, 0, 210);
        animation.addBranch(0, 0, 150);
        animation.addBranch(0, 0, 90);
        animation.addBranch(0, 0, 30);
        
        animation.addBranch(1920, -1080, 315);
        animation.addBranch(-1920, -1080, 225);
        animation.addBranch(-1920, 1080, 135);
        animation.addBranch(1920, 1080, 45);

        animation.addBranch(-1200, -880, 90);
        animation.addBranch(1200, -880, 90);
        animation.addBranch(1200, 880, 270);
        animation.addBranch(-1200, 880, 270);

        animation.addBranch(-960, 0, 300);
        animation.addBranch(-960, 0, 240);
        animation.addBranch(-960, 0, 180);
        animation.addBranch(-960, 0, 120);
        animation.addBranch(-960, 0, 60);
        animation.addBranch(-960, 0, 0);

        animation.addBranch(960, 0, 300);
        animation.addBranch(960, 0, 240);
        animation.addBranch(960, 0, 180);
        animation.addBranch(960, 0, 120);
        animation.addBranch(960, 0, 60);
        animation.addBranch(960, 0, 0);

        animation.addBranch(-480, -1080, 270);
        animation.addBranch(-480, -1080, 180);
        animation.addBranch(-480, -1080, 90);
        animation.addBranch(-480, -1080, 0);

        animation.addBranch(480, -1080, 270);
        animation.addBranch(480, -1080, 180);
        animation.addBranch(480, -1080, 90);
        animation.addBranch(480, -1080, 0);

        animation.addBranch(-480, 1080, 270);
        animation.addBranch(-480, 1080, 180);
        animation.addBranch(-480, 1080, 90);
        animation.addBranch(-480, 1080, 0);

        animation.addBranch(480, 1080, 270);
        animation.addBranch(480, 1080, 180);
        animation.addBranch(480, 1080, 90);
        animation.addBranch(480, 1080, 0);

        animation.addBranch(-1920, 0, 270);
        animation.addBranch(-1920, 0, 210);
        animation.addBranch(-1920, 0, 150);
        animation.addBranch(-1920, 0, 90);

        animation.addBranch(1920, 0, 330);
        animation.addBranch(1920, 0, 270);
        animation.addBranch(1920, 0, 90);
        animation.addBranch(1920, 0, 30);

        animation.initAnimation(750);
        animation.addAnimation(Animation.Variable.angle, 0, 375, 180, 0);
        animation.addAnimation(Animation.Variable.angle, 375, 750, 0, 180);
        animation.addAnimation(Animation.Variable.startHue, 0, 750, 360, 0);
        animation.animate();
        if(!animation.get.outputToList()) setVisible(false);
        animation.render(15);
        if(animation.get.frameRate() > 0) timer.setDelay((int) Math.round(1000 / animation.get.frameRate()));
        System.out.println("\nTotal Time Spent: " + getTime(System.currentTimeMillis() - start));
        if(animation.get.hasRenderFrames()) timer.start();
        else dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private static String getTime(long time) {
        long millis = time % 1000;
        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;
        long hour = (time / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
    }

    public static void main(String[] args) {
        new Main();
    }
}