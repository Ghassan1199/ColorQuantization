package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Kmeans {

    public static File start(String originalImagePath, String newImagePath) {
        try {
            // Load the image
            File imageFile = new File(originalImagePath);
            BufferedImage image = ImageIO.read(imageFile);
            System.out.println("reading image is done");

            // Perform color quantization with k-means
            int k = 16;
            java.util.List<Color> colors = getColors(image);
            System.out.println("analays stage done");

            java.util.List<Color> centroids = getInitialCentroids(k, colors);
            boolean done = false;
            while (!done) {
                java.util.List<java.util.List<Color>> clusters = new ArrayList<>();
                for (int i = 0; i < k; i++) {
                    // add a k list in clusters list and that number ok k index we choose it
                    clusters.add(new ArrayList<>());
                }
                for (Color color : colors) {
                    int closestCentroidIndex = getClosestCentroidIndex(color, centroids);
                    clusters.get(closestCentroidIndex).add(color);
                }
                java.util.List<Color> newCentroids = new ArrayList<>();
                for (java.util.List<Color> cluster : clusters) {
                    if (cluster.size() == 0) {
                        newCentroids.add(centroids.get(clusters.indexOf(cluster)));
                    } else {
                        Color newCentroid = getAverageColor(cluster);
                        newCentroids.add(newCentroid);
                    }
                }
                done = true;
                for (int i = 0; i < k; i++) {
                    if (!newCentroids.get(i).equals(centroids.get(i))) {
                        done = false;
                        break;
                    }
                }
                centroids = newCentroids;
            }

            // Create a new image with quantized colors
            BufferedImage quantizedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color color = new Color(image.getRGB(x, y));
                    int closestCentroidIndex = getClosestCentroidIndex(color, centroids);
                    quantizedImage.setRGB(x, y, centroids.get(closestCentroidIndex).getRGB());
                }
            }

            // Save the quantized image to a file
            File outputImageFile = new File(newImagePath + "KMEANS" + imageFile.getName());
            ImageIO.write(quantizedImage, "png", outputImageFile);
            return outputImageFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static List<Color> getColors(BufferedImage image) {
        //  get colors for input image
        java.util.List<Color> colors = new ArrayList<>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                if (!colors.contains(color)) {
                    colors.add(color);
                }
            }
        }
        return colors;
    }

    private static List<Color> getInitialCentroids(int k, List<Color> colors) {// this function take a color list of input image and return a list  contain a clusterd colors by spicefic way
        java.util.List<Color> centroids = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            int randomIndex = (int) (Math.random() * colors.size()); /// we get a random number and * with colors list size for generate a cluster of color and store it in randomindex
            centroids.add(colors.get(randomIndex));//// using randomIndex variable to get color from colors list and after that we have a clusterd colors
        }
        return centroids;
    }

    private static int getClosestCentroidIndex(Color color, List<Color> centroids) {// we get a closest color for cussent color and that by comparing between all distance that we get between the source
        // color and all clustered colors and choose the smallest one
        int closestCentroidIndex = 0;
        double closestDistance = Double.MAX_VALUE;
        for (int i = 0; i < centroids.size(); i++) {
            double distance = getDistance(color, centroids.get(i));
            if (distance < closestDistance) {
                closestDistance = distance;
                closestCentroidIndex = i;

            }
        }
        return closestCentroidIndex;
    }

    private static double getDistance(Color c1, Color c2) {
        // get distance for colors in colors space
        double redDifference = c1.getRed() - c2.getRed();
        double greenDifference = c1.getGreen() - c2.getGreen();
        double blueDifference = c1.getBlue() - c2.getBlue();
        return Math.sqrt(redDifference * redDifference + greenDifference * greenDifference + blueDifference * blueDifference);
    }

    private static Color getAverageColor(List<Color> colors) {
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        for (Color color : colors) {
            redSum += color.getRed();
            greenSum += color.getGreen();
            blueSum += color.getBlue();
        }
        int redAverage = redSum / colors.size();
        int greenAverage = greenSum / colors.size();
        int blueAverage = blueSum / colors.size();
        return new Color(redAverage, greenAverage, blueAverage);
    }
}