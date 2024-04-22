import java.util.ArrayList;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

public class Animation {
    private final static NumberFormat PF = NumberFormat.getPercentInstance();
    private double centerPointX, centerPointY, scale, startLength, angle, decrementer, startHue,
            startStroke, endstroke;
    private int numRepotitions, numFrames;
    private double frameRate;
    private boolean drawOnChange = false, outputToList = true, drawRendering = true, HD = false;
    private ArrayList<Branch> branches = new ArrayList<Branch>();
    private ArrayList<Image> frames = new ArrayList<Image>();
    private int numRenderedFrames = 0;
    private Canvas2D canvas;
    public Set set = new Set();
    public Get get = new Get();
    private double[] centerPointXArr, centerPointYArr, scaleArr, startLengthArr, angleArr, decrementerArr, startHueArr,
            startStrokeArr, endstrokeArr;
    private ArrayList<Branch> startBranches = new ArrayList<Branch>();

    public static enum Variable {
        centerPointX, centerPointY, scale, startLength, angle, decrementer, startHue, startStroke, endstroke
    }

    private long generate, draw;

    public Animation(Canvas2D canvas, double centerPointX, double centerPointY, double scale, int numStartBranches,
            double branchOffsetAngle, double startLength, double angle, double decrementer, int numRepotitions,
            double startHue, double startStroke, double endstroke) {
        set.centerPointX(centerPointX);
        set.centerPointY(centerPointY);
        set.scale(scale);
        set.startLength(startLength);
        set.angle(angle);
        set.decrementer(decrementer);
        set.numRepotitions(numRepotitions);
        set.startHue(startHue);
        set.startStroke(startStroke);
        set.endstroke(endstroke);
        set.canvas(canvas);
        File renderFile = new File("render.mp4");
        if(renderFile.exists()) renderFile.delete();
        File imagesFolder = new File("Images");
        if(!imagesFolder.isDirectory())
            imagesFolder.delete();
        if(!imagesFolder.exists())
            imagesFolder.mkdirs();
        for (File file : imagesFolder.listFiles()) 
            file.delete();
    }

    public void render(double lengthSec) {
        frameRate = numFrames / lengthSec;
        if(!outputToList) {
            try {
                String[] command = {"ffmpeg", "-framerate", "" + frameRate, "-i", "Images/frame%d.jpg", "render.mp4"};

                Thread.sleep(100);
                
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(true);
            
                Process process = processBuilder.start();
                InputStream output = process.getInputStream();
                
                while(process.isAlive()) {
                    while (output.available() > 0) {
                        System.out.print((char) output.read());
                    }
                }
                
                Thread.sleep(100);
                
                for (File file : new File("Images").listFiles()) 
                    file.delete();
                
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateFrame() {
        Graphics2D CTX = canvas.getGraphics();
        AffineTransform transform = new AffineTransform();
        transform.translate((centerPointX - canvas.getResolution().getWidth() / 2) * scale + canvas.getResolution().getWidth() / 2, (centerPointY - canvas.getResolution().getHeight() / 2) * scale + canvas.getResolution().getHeight() / 2);
        transform.scale(scale, scale);
        CTX.transform(transform);
        generatebranches();
        long start = System.currentTimeMillis();
        for (int j = branches.size() - 1; j >= 0; j--) {
            Branch line = branches.get(j);
            CTX.setColor(line.getColor());
            if(j < startBranches.size()) CTX.setColor(calculateColor(-1));
            CTX.setStroke(new BasicStroke((int) (float) line.getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            CTX.draw(line.getLine());
        }
        generate += (System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        if (outputToList)
            frames.add(canvas.getImage());
        else {
            try {
                numRenderedFrames++;
                String str = "Images/frame" + numRenderedFrames + ".jpg";
                ImageIO.write((BufferedImage) canvas.getImage(), "jpg", new File(str));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        draw += (System.currentTimeMillis() - start);
        if(drawRendering) canvas.draw();
    }

    public void generatebranches() {
        int lastIteration = 0;
        double length = startLength;
        branches.clear();
        branches.addAll(startBranches);
        for (int i = 0; i < numRepotitions; i++) {
            int max = branches.size();
            for (int j = lastIteration; j < max; j++) {
                Branch line = branches.get(j);
                Point2D initialPos = line.getP2();
                branches.add(new Branch(initialPos, getPoint(initialPos, line.getAngle() + angle, length), map(i, 0, numRepotitions, startStroke, endstroke), calculateColor(i)));
                branches.add(new Branch(initialPos, getPoint(initialPos, line.getAngle() - angle, length), map(i, 0, numRepotitions, startStroke, endstroke), calculateColor(i)));
            }
            length *= decrementer;
            lastIteration = max;
        }
    }

    public boolean initAnimation(int frameCount) {
        if(!HD) frameCount /= 2;
        if(frameCount == 0) frameCount = 1;
        if(frameCount <= 0) return false;
        numFrames = frameCount;
        angleArr = new double[numFrames];
        centerPointXArr = new double[numFrames];
        centerPointYArr = new double[numFrames];
        decrementerArr = new double[numFrames];
        endstrokeArr = new double[numFrames];
        scaleArr = new double[numFrames];
        startHueArr = new double[numFrames];
        startLengthArr = new double[numFrames];
        startStrokeArr = new double[numFrames];
        for(int i = 0 ; i < numFrames; i++) {
            angleArr[i] = angle;
            centerPointXArr[i] = get.centerPointX();
            centerPointYArr[i] = get.centerPointY();
            decrementerArr[i] = get.decrementer();
            endstrokeArr[i] = get.decrementer();
            scaleArr[i] = get.scale();
            startHueArr[i] = get.startHue();
            startLengthArr[i] = get.startLength();
            startStrokeArr[i] = get.startStroke();
        }
        return true;
    }

    public void addAnimation(Variable attribute, int startFrame, int endFrame, double startValue, double endValue) {
        if(!HD) {
            startFrame /= 2;
            endFrame /= 2;
        }
        endFrame = Math.min(endFrame, numFrames);
        startFrame = Math.max(startFrame, 0);
        endFrame--;
        endFrame = Math.max(endFrame, 0);
        for(int i = startFrame; i <= endFrame; i++) {
            double value = map(i, startFrame, endFrame, startValue, endValue);
            switch(attribute) {
                case angle:
                    angleArr[i] = value;
                    break;
                case centerPointX:
                    centerPointXArr[i] = value;
                    break;
                case centerPointY:
                    centerPointYArr[i] = value;
                    break;
                case decrementer:
                    decrementerArr[i] = value;
                    break;
                case endstroke:
                    endstrokeArr[i] = value;
                    break;
                case scale:
                    scaleArr[i] = value;
                    break;
                case startHue:
                    startHueArr[i] = value;
                    break;
                case startLength:
                    startLengthArr[i] = value;
                    break;
                case startStroke:
                    startStrokeArr[i] = value;
                    break;
            }
        }
    }

    public void animate() {
        boolean hasDrawOnChange = drawOnChange;
        drawOnChange = false;
        long start = System.currentTimeMillis();
        for(int i = 0; i <= numFrames; i++) {
            String str = "|";
            for (int j = 0; j < 25; j++)
                str += (Math.floor((float) i / numFrames * 25) < j ? " " : "=");
            long elapsed = System.currentTimeMillis() - start;
            long millisPerFrame = (elapsed / (i + 1));
            long Queued = (numFrames - 1 - i);
            str += "| (" + (i + 1) + " / " + numFrames + ") (" + PF.format((double) (Math.min(i, numFrames)) / numFrames) + ") (" + getTime(Math.max(Queued * millisPerFrame, 0)) + ")             \r";
            System.out.print(str);
            if(i == numFrames)
                break;
            set.angle(angleArr[i]);
            set.centerPointX(centerPointXArr[i]);
            set.centerPointY(centerPointYArr[i]);
            set.decrementer(decrementerArr[i]);
            set.endstroke(endstrokeArr[i]);
            set.scale(scaleArr[i]);
            set.startHue(startHueArr[i]);
            set.startLength(startLengthArr[i]);
            set.startStroke(startStrokeArr[i]);
            generateFrame();
        }
        long totalTime = System.currentTimeMillis() - start;
        System.out.println("\nTotal Time: " + getTime(totalTime) + "\nGenerate Time: " + getTime(generate) + "\t" + PF.format(((double) generate) / totalTime) + "\nDraw Time: " + getTime(draw) + "\t\t" + PF.format(((double) draw) / totalTime));
        drawOnChange = hasDrawOnChange;
    }

    private static String getTime(long time) {
        long millis = time % 1000;
        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;
        long hour = (time / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
    }

    public void addBranch(Point2D point, double angle) {
        startBranches.add(new Branch(point, Math.toRadians(angle), -startLength, startStroke, calculateColor(-1)));
    }

    public void addBranch(double x, double y, double angle) {
        addBranch(new Point2D.Double(x, y), angle);
    }
    
    private Point2D getPoint(Point2D point, double angle, double distance) {
        return new Point2D.Double(point.getX() + distance * Math.cos(angle), point.getY() + distance * Math.sin(angle));
    }

    private double map(double x, double x1, double x2, double y1, double y2) {
        return (isEqualTo(x1, x2) ? y1 : (((y2 - y1) / (x2 - x1)) * (x - x1) + y1));
    }

    private static boolean isEqualTo(double num1, double num2) {
        return (Math.abs(num1 - num2) < 0.000001);
    }

    private Color calculateColor(int index) {
        return Color.getHSBColor((float) map((index + 2), 1, numRepotitions, startHue, startHue + 360) / 360f % 1, 1.0f, 1.0f);
    }

    public class Set {
        public void centerPointX(double centerPointX) {
            Animation.this.centerPointX = centerPointX;
            if (drawOnChange)
                generateFrame();
        }

        public void centerPointY(double centerPointY) {
            Animation.this.centerPointY = centerPointY;
            if (drawOnChange)
                generateFrame();
        }

        public void scale(double scale) {
            Animation.this.scale = scale;
            if (drawOnChange)
                generateFrame();
        }

        public void startLength(double startLength) {
            Animation.this.startLength = startLength;
            if (drawOnChange)
                generateFrame();
        }

        public void angle(double angle) {
            Animation.this.angle = Math.toRadians(angle);
            if (drawOnChange)
                generateFrame();
        }

        public void decrementer(double decrementer) {
            Animation.this.decrementer = decrementer;
            if (drawOnChange)
                generateFrame();
        }

        public void startHue(double startHue) {
            Animation.this.startHue = startHue;
            if (drawOnChange)
                generateFrame();
        }

        public void startStroke(double startStroke) {
            Animation.this.startStroke = startStroke;
            if (drawOnChange)
                generateFrame();
        }

        public void endstroke(double endstroke) {
            Animation.this.endstroke = endstroke;
            if (drawOnChange)
                generateFrame();
        }

        public void startBranches(int numStartBranches, int branchOffsetAngle) {
            double angle = Math.toRadians(branchOffsetAngle);
            for (int i = 0; i < numStartBranches; i++) {
                startBranches.add(new Branch(new Point2D.Double(0, 0), (angle + (2 * Math.PI * i / numStartBranches)), -startLength, startStroke, calculateColor(-1)));
            }
        }

        public void numRepotitions(int numRepotitions) {
            Animation.this.numRepotitions = numRepotitions;
            if (drawOnChange)
                generateFrame();
        }

        public void frames(ArrayList<Image> frames) {
            Animation.this.frames = frames;
            if (drawOnChange)
                generateFrame();
        }

        public void canvas(Canvas2D canvas) {
            Animation.this.canvas = canvas;
            if (drawOnChange)
                generateFrame();
        }

        public void drawOnChange(boolean drawOnChange) {
            Animation.this.drawOnChange = drawOnChange;
        }

        public void outputToList(boolean outputToList) {
            Animation.this.outputToList = outputToList;
        }

        public void drawRendering(boolean drawRendering) {
            Animation.this.drawRendering = drawRendering;
        }

        public void HD(boolean HD) {
            Animation.this.HD = HD;
        }
    }

    public class Get {
        public double centerPointX() {
            return centerPointX;
        }

        public double centerPointY() {
            return centerPointY;
        }

        public double scale() {
            return scale;
        }

        public double startLength() {
            return startLength;
        }

        public double angle() {
            return Math.toDegrees(angle);
        }

        public double decrementer() {
            return decrementer;
        }

        public double startHue() {
            return startHue;
        }

        public double startStroke() {
            return startStroke;
        }

        public double endstroke() {
            return endstroke;
        }

        public int numRepotitions() {
            return numRepotitions;
        }

        public ArrayList<Image> frames() {
            return frames;
        }

        public Canvas2D canvas() {
            return canvas;
        }

        public boolean hasRenderFrames() {
            return outputToList;
        }

        public boolean outputToList() {
            return outputToList;
        }

        public double frameRate() {
            return frameRate;
        }
    }
}
