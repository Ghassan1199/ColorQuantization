package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class ColorPalette {
    public static Color[] getColorPalette(String path) throws IOException {
        File imageFile = new File(path);
        BufferedImage reducedImage = ImageIO.read(imageFile);

        HashSet<Color> colorSet = new HashSet<>();

        // Loop through each pixel in the reduced image
        int width = reducedImage.getWidth();
        int height = reducedImage.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = reducedImage.getRGB(x, y);
                Color color = new Color(rgb);

                // Add the RGB value to the HashSet
                colorSet.add(color);
            }
        }

        // Convert the HashSet to an array
        Color[] colorArray = new Color[colorSet.size()];
        colorSet.toArray(colorArray);

        // Sort the array by RGB values
        Arrays.sort(colorArray, (a, b) -> Integer.compare(a.getRGB(), b.getRGB()));

        // Return the sorted array as the color palette
        return colorArray;
    }

}
