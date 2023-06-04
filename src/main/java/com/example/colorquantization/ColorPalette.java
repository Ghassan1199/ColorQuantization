package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ColorPalette {
    HashSet<Color> colorSet = new HashSet<>();
    ArrayList<Color> colorArray = new ArrayList<>();
    HashMapColor colorMap = new HashMapColor();
    int pixels;

    public ColorPalette(String path) throws IOException {
        File imageFile = new File(path);
        BufferedImage reducedImage = ImageIO.read(imageFile);

        // Loop through each pixel in the reduced image
        int width = reducedImage.getWidth();
        int height = reducedImage.getHeight();
        pixels = height * width;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = reducedImage.getRGB(x, y);
                Color color = new Color(rgb);
                if (colorMap.containsKey(color) && colorMap.get(color)!=null) {
                    int temp = colorMap.get(color) + 1;
                    colorMap.replace(color, temp);
                }else{
                    colorMap.put(color,1);
                }
                // Add the RGB value to the HashSet
                colorSet.add(color);
            }
        }
        // Convert the HashSet to an array
        colorArray.addAll(colorSet);
        // Sort the array by RGB values
        colorArray.sort((o1, o2) -> {
            float[] hsv1 = rgbToHsv(o1);
            float[] hsv2 = rgbToHsv(o2);

            // Compare the Hue values first
            if (hsv1[0] < hsv2[0]) {
                return -1;
            } else if (hsv1[0] > hsv2[0]) {
                return 1;
            }

            // If the Hue values are equal, compare the Saturation values
            if (hsv1[1] < hsv2[1]) {
                return -1;
            } else if (hsv1[1] > hsv2[1]) {
                return 1;
            }

            // If the Saturation values are equal, compare the Value/Brightness values
            if (hsv1[2] < hsv2[2]) {
                return -1;
            } else if (hsv1[2] > hsv2[2]) {
                return 1;
            }

            // If all HSV values are equal, consider the colors equal
            return 0;
        });
    }

    public void createColorPalette() throws IOException, InterruptedException {
        BufferedImage image = new BufferedImage(1280, 1280, BufferedImage.TYPE_BYTE_INDEXED);
        int x = 0;
        int rectWidth = 1280 / this.colorArray.size();
        int recHeight = 1280;

        for (Color color : this.colorArray) {
            for (int i = 0; i < rectWidth; i++) {
                for (int j = 0; j < recHeight; j++) {
                    image.setRGB(j, x + i, color.getRGB());
                }
            }
            x += rectWidth;

        }
        ImageIO.write(image, "png", new File("colorPalette.png"));
        File file = new File("colorPalette.png");
        String[] commands = {
                "cmd.exe", "/c", "start", "\"DummyTitle\"", "\"" + file.getAbsolutePath() + "\""
        };
        Process p = Runtime.getRuntime().exec(commands);
        p.waitFor();
    }

    static public double compareTwoImages(ColorPalette image1, ColorPalette image2) {
        int count = 0;
        for (int i = 0; i < image2.colorArray.size(); i++) {
            if (image1.colorSet.contains(image2.colorArray.get(i)))
                count++;
        }

        return (double) count / ((image1.colorArray.size() + image2.colorArray.size()) / 2);
    }

    static public double compareTwoImages(ColorPalette image1, ArrayList<Color> colors) {
        int count = 0;
        for (Color color : colors) {
            if (image1.colorSet.contains(color))
                count+=image1.colorMap.get(color);
        }

        return (double) count/ image1.pixels;
    }

    public static float[] rgbToHsv(Color rgb) {
        float[] hsv = new float[3];
        float[] hsb = Color.RGBtoHSB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), null);

        // Convert HSB (Hue, Saturation, Brightness) to HSV (Hue, Saturation, Value)
        hsv[0] = hsb[0] * 360;  // Hue in range 0-360
        hsv[1] = hsb[1] * 100;  // Saturation in range 0-100
        hsv[2] = hsb[2] * 100;  // Value in range 0-100

        return hsv;
    }


}
