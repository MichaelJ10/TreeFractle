import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Canvas2D extends Canvas {
    private BufferedImage buffer;
    private Dimension resolution;

    public Canvas2D(int width, int height, Color Color) {
        setSize(width, height);
        setResolution(width, height);
        setBackground(Color);
    }

    public void setResolution(int width, int height) {
        resolution = new Dimension(width, height);
    }

    public Dimension getResolution() {
        return resolution;
    }

    @Override
    public Graphics2D getGraphics() {
        buffer = new BufferedImage((int) (float) resolution.getWidth(), (int) (float) resolution.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D CTX = (Graphics2D) buffer.getGraphics();
        CTX.setColor(getBackground());
        CTX.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        return CTX;
    }

    public void draw() {
        super.getGraphics().drawImage(buffer, 0, 0, buffer.getWidth(), buffer.getHeight(), null);
    }
}
