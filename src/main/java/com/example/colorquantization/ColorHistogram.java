package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorHistogram {
    // Define the number of histogram bins for each color channel
    private static final int numBins = 256;

    // Create a histogram for each color channel
    public static int[] redHistogram = new int[numBins];
    public static int[] greenHistogram = new int[numBins];
    public static int[] blueHistogram = new int[numBins];
    int maxRed = 0;
    int maxGreen = 0;
    int maxBlue = 0;
    int max = 0;

    public static void MakeColorHistogram(String inputImagePath) throws IOException, InterruptedException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                // Get the color values for the pixel at (x,y)
                Color pixel = new Color(inputImage.getRGB(x, y));
                int rgb = inputImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Add the color values to the histograms
                redHistogram[red]++;
                greenHistogram[green]++;
                blueHistogram[blue]++;
            }
        }
        for (int i = 0; i < redHistogram.length; i++) {
            redHistogram[i] /= 128;
        }
        for (int i = 0; i < greenHistogram.length; i++) {
            greenHistogram[i] /= 128;
        }
        for (int i = 0; i < blueHistogram.length; i++) {
            blueHistogram[i] /= 128;
        }

        // Find the maximum count in any of the histograms
        int maxCount = 0;
        for (int i = 0; i < 256; i++) {
            maxCount = Math.max(maxCount, Math.max(redHistogram[i], Math.max(greenHistogram[i], blueHistogram[i])));
        }

        // Create a new image to draw the histogram on
        BufferedImage histogramImage = new BufferedImage(768, maxCount, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = histogramImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, histogramImage.getWidth(), histogramImage.getHeight());

        // Draw the red histogram
        g2d.setColor(Color.RED);
        for (int i = 0; i < 256; i++) {
            int height = (int) ((double) redHistogram[i] / maxCount * histogramImage.getHeight());
            g2d.drawLine(i, histogramImage.getHeight() - height, i, histogramImage.getHeight());
        }

        // Draw the green histogram
        g2d.setColor(Color.GREEN);
        for (int i = 0; i < 256; i++) {
            int height = (int) ((double) greenHistogram[i] / maxCount * histogramImage.getHeight());
            g2d.drawLine(256 + i, histogramImage.getHeight() - height, 256 + i, histogramImage.getHeight());
        }

        // Draw the blue histogram
        g2d.setColor(Color.BLUE);
        for (int i = 0; i < 256; i++) {
            int height = (int) ((double) blueHistogram[i] / maxCount * histogramImage.getHeight());
            g2d.drawLine(512 + i, histogramImage.getHeight() - height, 512 + i, histogramImage.getHeight());
        }

        File file = new File("colorHistogram.png");
        ImageIO.write(histogramImage, "png", file);

        String[] commands = {
                "cmd.exe", "/c", "start", "\"DummyTitle\"", "\"" + file.getAbsolutePath() + "\""
        };
        Process p = Runtime.getRuntime().exec(commands);
        p.waitFor();


    }


}