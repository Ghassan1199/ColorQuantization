package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorHistogram {
    // Define the number of histogram bins for each color channel
    private final int numBins = 256;

    // Create a histogram for each color channel
    public int[] redHistogram = new int[numBins];
    public int[] greenHistogram = new int[numBins];
    public int[] blueHistogram = new int[numBins];

    public int[] histogram = new int[numBins];

    ColorHistogram(String inputImagePath) throws IOException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                // Get the color values for the pixel at (x,y)
                int rgb = inputImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Add the color values to the histograms
                redHistogram[red]++;
                greenHistogram[green]++;
                blueHistogram[blue]++;

                int index = (red + green + blue) / 3;
                histogram[index]++;
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

    }

    public void MakeColorHistogram() throws IOException, InterruptedException {
        // Find the maximum count in any of the histograms
        int maxCount = 0;
        for (int i = 0; i < 256; i++) {
            maxCount = Math.max(maxCount, Math.max(redHistogram[i], Math.max(greenHistogram[i], blueHistogram[i])));
        }

        // Create a new image to draw the histogram on
        BufferedImage histogramImage = new BufferedImage(768, maxCount, BufferedImage.TYPE_BYTE_INDEXED);
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


    double calculateHistogramIntersection(ColorHistogram histogram1, ColorHistogram histogram2) {
        int redIntersection = 0;
        int greenInterSection = 0;
        int blueInterSection = 0;


        for (int i = 0; i < histogram1.greenHistogram.length; i++) {
            redIntersection += Math.min(histogram1.redHistogram[i], histogram2.redHistogram[i]);
        }
        for (int i = 0; i < histogram1.greenHistogram.length; i++) {
            blueInterSection += Math.min(histogram1.blueHistogram[i], histogram2.blueHistogram[i]);
        }
        for (int i = 0; i < histogram1.greenHistogram.length; i++) {
            greenInterSection += Math.min(histogram1.greenHistogram[i], histogram2.greenHistogram[i]);
        }

        int totalIntersection = redIntersection + blueInterSection + greenInterSection;

        return (double) totalIntersection / ((getTotalPixels(histogram1) + getTotalPixels(histogram2)) / 2);


    }


    private int getTotalPixels(ColorHistogram histogram) {
        int totalPixels = 0;

        for (int count : histogram.greenHistogram) {
            totalPixels += count;
        }
        for (int count : histogram.redHistogram) {
            totalPixels += count;
        }
        for (int count : histogram.blueHistogram) {
            totalPixels += count;
        }

        return totalPixels;
    }

    public double compareHistograms(ColorHistogram histogram1, ColorHistogram histogram2) {

        double result = 0;
        int sum = 0;

        for (int i = 0; i < histogram1.histogram.length; i++) {

            double a = histogram1.histogram[i] - histogram2.histogram[i];
            double b = histogram1.histogram[i] + histogram2.histogram[i];

            if (b > 0) {
                result += (a * a) / b;
            }
            sum += histogram1.histogram[i] + histogram2.histogram[i];

        }

        result = result / sum;

        result = (1 - result) * 100;

        return result / 100;

    }


}
