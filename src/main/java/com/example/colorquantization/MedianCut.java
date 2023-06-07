package com.example.colorquantization;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MedianCut {

    public static File reduceColors(String originalImagePath, int targetColorCount,String newImagePath) throws IOException {

        File imageFile = new File(originalImagePath);
        BufferedImage image = ImageIO.read(imageFile);

        // Step 1: Get the list of pixels in the image
        int width = image.getWidth();
        int height = image.getHeight();
        List<int[]> pixelList = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                pixelList.add(new int[]{red, green, blue});
            }
        }

        // Step 2: Use median cut to create a palette of the desired size
        List<Color> palette = medianCut(pixelList, targetColorCount);

        // Step 3: Replace each pixel in the image with its closest color from the palette
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
        //  IndexColorModel colorModel = new IndexColorModel(8, palette.size(), convertColors(palette), 0, false, -1, DataBuffer.TYPE_BYTE);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                Color closestColor = palette.get(0);
                double closestDistance = getDistance(red, green, blue, closestColor.getRed(), closestColor.getGreen(), closestColor.getBlue());
                for (Color color : palette) {
                    double distance = getDistance(red, green, blue, color.getRed(), color.getGreen(), color.getBlue());
                    if (distance < closestDistance) {
                        closestColor = color;
                        closestDistance = distance;
                    }
                }
                result.setRGB(x, y, closestColor.getRGB());
            }
        }

        File outputImageFile = new File(newImagePath + "MEDIAN_CUT" + imageFile.getName());
        System.out.println("OUTPUT " + outputImageFile.getPath());
        ImageIO.write(result, "png", outputImageFile);

        return outputImageFile;
    }

    private static double getDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
        int dr = r1 - r2;
        int dg = g1 - g2;
        int db = b1 - b2;
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public static List<Color> medianCut(List<int[]> pixelList, int targetColorCount) {

        List<Color> palette = new ArrayList<>();

        // Step 1: Create a color cube that encloses all the pixels in the image
        int[] min = {255, 255, 255};
        int[] max = {0, 0, 0};
        for (int[] pixel : pixelList) {
            for (int i = 0; i < 3; i++) {
                min[i] = Math.min(min[i], pixel[i]);
                max[i] = Math.max(max[i], pixel[i]);
            }
        }
        ColorCube root = new ColorCube(min, max, pixelList);

        // Step 2: Create a list of color cubes to be split
        List<ColorCube> cubes = new ArrayList<>();
        cubes.add(root);

        // Step 3: Continue splitting color cubes until we have the desired number of cubes
        while (cubes.size() < targetColorCount) {
            // Find the color cube with the largest volume
            ColorCube maxVolumeCube = cubes.stream().max(Comparator.comparingDouble(ColorCube::getVolume)).get();

            // Split the color cube along its longest dimension
            ColorCube[] newCubes = maxVolumeCube.split();

            // Remove the original color cube from the list and add the new color cubes
            cubes.remove(maxVolumeCube);
            cubes.add(newCubes[0]);
            cubes.add(newCubes[1]);
        }

        // Step 4: Extract the color palette by averaging the colors within each color cube
        for (ColorCube cube : cubes) {
            palette.add(cube.getAverageColor());
        }

        return palette;
    }


    private record ColorCube(int[] min, int[] max, List<int[]> pixels) {

        public double getVolume() {
                double dx = max[0] - min[0] + 1;
                double dy = max[1] - min[1] + 1;
                double dz = max[2] - min[2] + 1;
                return dx * dy * dz;
            }

            public Color getAverageColor() {
                int red = 0;
                int green = 0;
                int blue = 0;
                int count = 0;
                if (pixels.isEmpty()) {
                    return Color.BLACK; // Return a default color if the cube has no pixels
                }
                for (int[] pixel : pixels) {
                    red += pixel[0];
                    green += pixel[1];
                    blue += pixel[2];
                    count++;
                }
                red /= count;
                green /= count;
                blue /= count;
                return new Color(red, green, blue);
            }

            public ColorCube[] split() {
                int splitIndex = 0;
                int splitDimension = 0;
                int maxExtent = -1;
                for (int i = 0; i < 3; i++) {
                    int extent = max[i] - min[i];
                    if (extent > maxExtent) {
                        maxExtent = extent;
                        splitDimension = i;
                    }
                }
                int target = min[splitDimension] + (max[splitDimension] - min[splitDimension]) / 2;
                for (int i = 0; i < pixels.size(); i++) {
                    if (pixels.get(i)[splitDimension] > target) {
                        splitIndex = i;
                        break;
                    }
                }
                ColorCube[] newCubes = new ColorCube[2];
                newCubes[0] = new ColorCube(min.clone(), max.clone(), pixels.subList(0, splitIndex));
                newCubes[1] = new ColorCube(min.clone(), max.clone(), pixels.subList(splitIndex, pixels.size()));
                newCubes[0].max[splitDimension] = target;
                newCubes[1].min[splitDimension] = target + 1;
                return newCubes;
            }


            private static Color getMedianColor(List<Color> colors, int maxRangeIndex) {
                int[] values = new int[colors.size()];
                for (int i = 0; i < colors.size(); i++) {
                    values[i] = colors.get(i).getRGB() >> (maxRangeIndex * 8) & 0xff;
                }
                Arrays.sort(values);
                int medianValue = values[values.length / 2];
                return new Color(medianValue << (maxRangeIndex
                        * 8), maxRangeIndex == 0 ? 0 : medianValue << 8, maxRangeIndex == 1 ? 0 : medianValue << 16);
            }

            private static Color findClosestColor(Color color, List<Color> colors) {
                Color closestColor = null;
                double closestDistance = Double.MAX_VALUE;
                for (Color candidateColor : colors) {
                    double distance = distance(color, candidateColor);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestColor = candidateColor;
                    }
                }
                return closestColor;
            }

            private static double distance(Color c1, Color c2) {
                double rmean = (c1.getRed() + c2.getRed()) / 2.0;
                double r = c1.getRed() - c2.getRed();
                double g = c1.getGreen() - c2.getGreen();
                double b = c1.getBlue() - c2.getBlue();
                double weightR = 2.0 + rmean / 256.0;
                double weightG = 4.0;
                double weightB = 2.0 + (255.0 - rmean) / 256.0;
                return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
            }
        }
}