package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class ColorPalette {
    HashSet<Color> colorSet = new HashSet<>();
    ArrayList<Color> colorArray = new ArrayList<>();

    File imageFile;

    public ColorPalette(String path) throws IOException {
        File imageFile = new File(path);
        BufferedImage reducedImage = ImageIO.read(imageFile);

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
        colorArray.addAll(colorSet);
        // Sort the array by RGB values
        colorArray.sort(Comparator.comparingInt(Color::getRGB));
        System.out.println(colorArray.size());
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
        for(int i=0;i<image1.colorArray.size()-1;i++){
            if(similarTo(image1.colorArray.get(i),image2.colorArray.get(i))){
                count++;
            }
        }
        return (double) count / Math.min(image1.colorArray.size(),image2.colorArray.size());
    }

    static boolean similarTo(Color c1,Color c2){
        double distance = (c1.getRed() - c2.getRed())*(c1.getRed() - c2.getRed()) + (c1.getGreen() - c2.getGreen())*(c1.getGreen() - c2.getGreen()) + (c1.getBlue() - c2.getBlue())*(c1.getBlue() - c2.getBlue());
        System.out.println(Math.sqrt(distance));

        if(Math.sqrt(distance)>150){
            return true;
        }else{
            return false;
        }
    }

}
