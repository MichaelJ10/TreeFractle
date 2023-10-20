import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Color;

public class Lines {
    private Point2D p1, p2;
    private double strokeWidth;
    private int index;
    private Color color;

    public Lines(Point2D p1, Point2D p2, double strokeWidth, Color color, int index) {
        setP1(p1);
        setP2(p2);
        setStrokeWidth(strokeWidth);
        setColor(color);
    }

    public Lines(Point2D p1, Point2D p2, double strokeWidth, Color color) {
        setP1(p1);
        setP2(p2);
        setStrokeWidth(strokeWidth);
        setColor(color);
    }

    public Lines(Point2D p1, Point2D p2, int index) {
        this(p1, p2, 30, Color.WHITE, index);
    }

    public Lines(Point2D p1, Point2D p2) {
        this(p1, p2, 0);
    }

    public void setP1(Point2D point) {
        p1 = point;
    }

    public void setP2(Point2D point) {
        p2 = point;
    }

    public void setStrokeWidth(double width) {
        strokeWidth = width;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setIndex(int index) {
        this.index = index;
    } 

    public Point2D getP1() {
        return p1;
    }

    public Point2D getP2() {
        return p2;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public Color getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }

    public Line2D getLine() {
        return new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public double getAngle() {
        return Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
    }
}
