package com.example.colorquantization;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainController {

    Image originalPhoto;
    @FXML
    private GridPane gridPane;
    @FXML
    private ImageView ogPhoto;
    @FXML
    private VBox vBox;


    @FXML
    protected void selectPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "\\Images"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));


        try {
            File file = fileChooser.showOpenDialog(new Stage());
            if (file == null) return;
            originalPhoto = new Image("file:" + file.getPath());
            ogPhoto.setImage(originalPhoto);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    protected void search() throws IOException {
        if (originalPhoto == null) {
            System.out.println("select a photo first");
            return;
        }
        ArrayList<ImageView> Images = new ArrayList<>();
        List<String> imagesPath = new ArrayList<>();
        File folder = new File(Main.ImagesPath);

        List<File> listOfFiles = new ArrayList<>(List.of(Objects.requireNonNull(folder.listFiles())));
        for (File file : listOfFiles) {
            imagesPath.add(file.getName());
        }

        System.out.println(originalPhoto.getUrl());
        BufferedImage target = ImageIO.read(new File(originalPhoto.getUrl().substring(5)));

        for (String path : imagesPath) {
            File image = new File(folder.getPath() + "\\" + path);
            BufferedImage im = ImageIO.read(image);
            double similarity = compareImages(im, target);
            similarity = getTwoDigits(similarity);
            System.out.println(path);
            System.out.println("Similarity: " + similarity);
            System.out.println("-----------------------------");
            if (similarity > 0.4) {
                vBox.getChildren().add(new Text("Similarity: " + similarity));
                ImageView img= new ImageView(new Image("file:" + folder.getPath() + "\\" + path));
                img.setFitHeight(200);
                img.setFitWidth(500);
                vBox.getChildren().add(img);
            }

        }
    }

    public double compareImages(BufferedImage image1, BufferedImage image2) {

        int[] histogram1 = MakeColorHistogram(image1);
        int[] histogram2 = MakeColorHistogram(image2);

        return calculateHistogramIntersection(histogram1, histogram2);
    }

    static int[] MakeColorHistogram(BufferedImage image) {
        int[] histogram = new int[256];

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int index = (red + green + blue) / 3;


                histogram[index]++;
            }
        }

        return histogram;
    }


    private double calculateHistogramIntersection(int[] histogram1, int[] histogram2) {
        int totalIntersection = 0;

        for (int i = 0; i < histogram1.length; i++) {
            totalIntersection += Math.min(histogram1[i], histogram2[i]);
        }


        return (double) totalIntersection / getTotalPixels(histogram1);
    }

    private int getTotalPixels(int[] histogram) {
        int totalPixels = 0;

        for (int count : histogram) {
            totalPixels += count;
        }

        return totalPixels;
    }


    private static Double getTwoDigits(Double input) {
        String result = String.format("%.2f", input);
        return Double.parseDouble(result);

    }


}