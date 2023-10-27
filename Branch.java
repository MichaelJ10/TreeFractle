import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Color;

public class Branch {
    private Point2D p1, p2;
    private double strokeWidth;
    private Color color;

    public Branch(Point2D p1, Point2D p2, double strokeWidth, Color color) {
        setP1(p1);
        setP2(p2);
        setStrokeWidth(strokeWidth);
        setColor(color);
    }

    public Branch(Point2D point, double angle, double length, double strokeWidth, Color color) {
        this(point, new Point2D.Double(length * Math.cos(angle) + point.getX(), length * Math.sin(angle) + point.getY()), strokeWidth, color);
    }

    public Branch(Point2D p1, Point2D p2) {
        this(p1, p2, 5, Color.WHITE);
    }

    public Branch(Point2D point, double angle, double length) {
        this(point, new Point2D.Double(length * Math.cos(angle) + point.getX(), length * Math.sin(angle) + point.getY()), 5, Color.WHITE);
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

    public Line2D getLine() {
        return new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public double getAngle() {
        return Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
    }
}
