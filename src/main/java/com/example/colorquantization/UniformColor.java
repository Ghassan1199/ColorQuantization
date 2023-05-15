package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UniformColor {

    public static File start(String originalImagePath, int targetColor, String newImagePath) throws IOException {
        File imageFile = new File(originalImagePath);
        BufferedImage image = ImageIO.read(imageFile);
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        // Build a list of distinct colors in the image
        List<Color> colors = new ArrayList<>();
        for (int pixel : pixels) {
            Color color = new Color(pixel);
            if (!colors.contains(color)) {
                colors.add(color);
            }
        }

        // If the number of colors in the image is less than or equal to the target number,
        // just return the original image
        if (colors.size() <= targetColor) {
            System.out.println("The Image is still the same \n" +
                    "enter more numbers ");
            return imageFile;
        }

        // Divide the color space into a fixed number of regions
        int regionSize = 256 / targetColor;
        List<Color> palette = new ArrayList<>();
        for (int r = 0; r < 256; r += regionSize) {
            for (int g = 0; g < 256; g += regionSize) {
                for (int b = 0; b < 256; b += regionSize) {
                    Color color = new Color(r, g, b);
                    palette.add(color);
                }
            }
        }

        // Find the nearest color in the palette for each pixel in the image
        int[] quantizedPixels = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            Color color = new Color(pixels[i]);
            Color nearest = findNearestColor(color, palette);
            quantizedPixels[i] = nearest.getRGB();
        }

        // Create a new image with the quantized colors
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        result.setRGB(0, 0, width, height, quantizedPixels, 0, width);

        File outputImageFile = new File(newImagePath + "UNIFORM_COLOR" + imageFile.getName());
        System.out.println("OUTPUT " + outputImageFile.getPath());
        ImageIO.write(result, "png", outputImageFile);
        
        return outputImageFile;
    }

    private static Color findNearestColor(Color color, List<Color> palette) {
        double minDistance = Double.MAX_VALUE;
        Color nearest = null;
        for (Color candidate : palette) {
            double distance = colorDistance(color, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    private static double colorDistance(Color c1, Color c2) {
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }
}