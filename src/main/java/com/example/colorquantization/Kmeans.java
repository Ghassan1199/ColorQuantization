package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.example.colorquantization.UniformColor.resize;


public class Kmeans {

    public Kmeans(String s, String editedPath, int i) {
    }

    public static File start(String originalImagePath, String newImagePath, int k) {
        try {
            // Load the image
            File imageFile = new File(originalImagePath);
            BufferedImage image1 = ImageIO.read(imageFile);
            BufferedImage image = resize(image1, 1280, 720);
            // Perform color quantization with k-means
            List<Color> colors = new ArrayList<>(getHashColors(image));


            List<Color> centroids = getInitialCentroids(k, colors);
            boolean done = false;
            while (!done) {
                List<List<Color>> clusters = new ArrayList<>();
                if(k==0)
                    k=16;
                for (int i = 0; i < k; i++) {
                    // add a k list in clusters list and that number ok k index we choose it
                    clusters.add(new ArrayList<>());
                }
                for (Color color : colors) {
                    int closestCentroidIndex = getClosestCentroidIndex(color, centroids);
                    clusters.get(closestCentroidIndex).add(color);
                }

                List<Color> newCentroids = new ArrayList<>();
                for (List<Color> cluster : clusters) {
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
            BufferedImage quantizedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color color = new Color(image.getRGB(x, y));
                    int closestCentroidIndex = getClosestCentroidIndex(color, centroids);
                    quantizedImage.setRGB(x, y, centroids.get(closestCentroidIndex).getRGB());
                }
            }

            // Save the quantized image to a file
            File outputImageFile = new File(newImagePath + "KMEANS" + imageFile.getName());
            System.out.println("OUTPUT " + outputImageFile.getPath());
            ImageIO.write(quantizedImage, "png", outputImageFile);
            return outputImageFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    private static HashSet<Color> getHashColors(BufferedImage image) {
        //  get colors for input image
        HashSet<Color> colors = new HashSet<>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                colors.add(color);
            }
        }
        return colors;
    }

    private static List<Color> getInitialCentroids(int k, List<Color> colors) {
        List<Color> centroids = new ArrayList<>();
        if(k==0){
            // Manually specify the initial centroid colors
            Color[] fixedColors = new Color[] {
                    Color.RED,     // Example fixed centroid color 1
                    Color.GREEN,   // Example fixed centroid color 2
                    Color.BLUE ,    // Example fixed centroid color 3
                    Color.BLACK,
                    Color.CYAN,
                    Color.DARK_GRAY,
                    Color.LIGHT_GRAY,
                    Color.YELLOW,
                    Color.PINK,
                    Color.WHITE,
                    Color.ORANGE,
                    Color.MAGENTA,
                    Color.GRAY,
                    // Add more colors as needed
            };

            centroids.addAll(Arrays.asList(fixedColors));
        }else{
            List<Color> shuffledColors = new ArrayList<>(colors);
            Collections.shuffle(shuffledColors);

            for (int i = 0; i < k; i++) {
                centroids.add(shuffledColors.get(i));
            }
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