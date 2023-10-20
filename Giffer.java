import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

/*
 * Giffer is a simple java class to make my life easier in creating gif images.
 * 
 * Usage :
 * There are two methods for creating gif images
 * To generate from files, just pass the array of filename Strings to this method
 * Giffer.generateFromFiles(String[] filenames, String output, int delay, boolean loop)
 * 
 * Or as an alternative you can use this method which accepts an array of BufferedImage
 * Giffer.generateFromBI(BufferedImage[] images, String output, int delay, boolean loop)
 * 
 * output is the name of the output file
 * delay is time between frames, accepts hundredth of a time. Yeah it's weird, blame Oracle
 * loop is the boolean for whether you want to make the image loopable.
 */

public abstract class Giffer {
    private final static NumberFormat PF = NumberFormat.getPercentInstance();

    // Generate gif from an array of filenames
    // Make the gif loopable if loop is true
    // Set the delay for each frame according to the delay (ms)
    // Use the name given in String output for output file
    public static void generateFromFiles(String[] filenames, String output, int delay, boolean loop)
            throws IIOException, IOException {
        ImageWriter gifWriter = getWriter();
        ImageOutputStream ios = getImageOutputStream(output);
        IIOMetadata metadata = getMetadata(gifWriter, delay, loop);

        gifWriter.setOutput(ios);
        gifWriter.prepareWriteSequence(null);
        for (int i = 0; i < filenames.length; i++) {
            BufferedImage img = ImageIO.read(new File(filenames[i]));
            IIOImage temp = new IIOImage(img, null, metadata);
            gifWriter.writeToSequence(temp, null);
            String str = "|";
            for (int j = 0; j < 25; j++)
                str += ((float) i / filenames.length * 25 < j ? " " : "#");
            str += "| (" + (i + 1) + " / " + filenames.length + ") (" + PF.format((double) (i + 1) / filenames.length) + ")\r";
            System.out.print(str);
        }
        System.out.println();
        gifWriter.endWriteSequence();
    }

    // Retrieve gif writer
    private static ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> itr = ImageIO.getImageWritersByFormatName("gif");
        if (itr.hasNext())
            return itr.next();

        throw new IIOException("GIF writer doesn't exist on this JVM!");
    }

    // Retrieve output stream from the given file name
    private static ImageOutputStream getImageOutputStream(String output) throws IOException {
        File outfile = new File(output);
        return ImageIO.createImageOutputStream(outfile);
    }

    // Prepare metadata from the user input, add the delays and make it loopable
    // based on the method parameters
    private static IIOMetadata getMetadata(ImageWriter writer, int delay, boolean loop)
            throws IIOInvalidTreeException {
        // Get the whole metadata tree node, the name is javax_imageio_gif_image_1.0
        // Not sure why I need the ImageTypeSpecifier, but it doesn't work without it
        ImageTypeSpecifier img_type = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
        IIOMetadata metadata = writer.getDefaultImageMetadata(img_type, null);
        String native_format = metadata.getNativeMetadataFormatName();
        IIOMetadataNode node_tree = (IIOMetadataNode) metadata.getAsTree(native_format);

        // Set the delay time you can see the format specification on this page
        // https://docs.oracle.com/javase/7/docs/api/javax/imageio/metadata/doc-files/gif_metadata.html
        IIOMetadataNode graphics_node = getNode("GraphicControlExtension", node_tree);
        graphics_node.setAttribute("delayTime", String.valueOf(delay));
        graphics_node.setAttribute("disposalMethod", "none");
        graphics_node.setAttribute("userInputFlag", "FALSE");

        if (loop)
            makeLoopy(node_tree);

        metadata.setFromTree(native_format, node_tree);

        return metadata;
    }

    // Add an extra Application Extension node if the user wants it to be loopable
    // I am not sure about this part, got the code from StackOverflow
    // TODO: Study about this
    private static void makeLoopy(IIOMetadataNode root) {
        IIOMetadataNode app_extensions = getNode("ApplicationExtensions", root);
        IIOMetadataNode app_node = getNode("ApplicationExtension", app_extensions);

        app_node.setAttribute("applicationID", "NETSCAPE");
        app_node.setAttribute("authenticationCode", "2.0");
        app_node.setUserObject(new byte[] { 0x1, (byte) (0 & 0xFF), (byte) ((0 >> 8) & 0xFF) });

        app_extensions.appendChild(app_node);
        root.appendChild(app_extensions);
    }

    // Retrieve the node with the name from the parent root node
    // Append the node if the node with the given name doesn't exist
    private static IIOMetadataNode getNode(String node_name, IIOMetadataNode root) {
        IIOMetadataNode node = null;

        for (int i = 0; i < root.getLength(); i++) {
            if (root.item(i).getNodeName().compareToIgnoreCase(node_name) == 0) {
                node = (IIOMetadataNode) root.item(i);
                return node;
            }
        }

        // Append the node with the given name if it doesn't exist
        node = new IIOMetadataNode(node_name);
        root.appendChild(node);

        return node;
    }

    public static void main(String[] args) {
        String[] arr = new String[720];
        for(int i = 0; i < 720; i++) {
            arr[i] = "Images/frame" + i + ".png";
        }
        File outputFile = new File("render.gif");
        try {
            outputFile.delete();
            outputFile.createNewFile();
            Giffer.generateFromFiles(arr, outputFile.getPath(), 21, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
