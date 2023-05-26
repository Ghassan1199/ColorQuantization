package com.example.colorquantization;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchController {

    Image originalPhoto;
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
    protected void searchUsingColorHistogram() throws IOException {
        if (originalPhoto == null) {
            System.out.println("select a photo first");
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("choose the directory");
        directoryChooser.setInitialDirectory(new File(Main.ImagesPath));
        File folder = directoryChooser.showDialog(new Stage());

        if (folder == null) {
            System.out.println("Choose a Directory ");
            return;
        }

        List<String> imagesPath = new ArrayList<>();

        List<File> listOfFiles = new ArrayList<>(List.of(Objects.requireNonNull(folder.listFiles())));
        for (File file : listOfFiles) {
            int index = file.getName().lastIndexOf('.');
            String extension = file.getName().substring(index + 1);
            if (extension.equals("png") || extension.equals("jpg")) {
                imagesPath.add(file.getName());
            }
        }

        System.out.println(originalPhoto.getUrl());

        for (String path : imagesPath) {
            File image = new File(folder.getPath() + "\\" + path);
            double similarity = compareImagesUsingHistogram(image.getPath(), originalPhoto.getUrl().substring(5));
            similarity = getTwoDigits(similarity);
            System.out.println(path);
            System.out.println("Similarity: " + similarity);
            System.out.println("-----------------------------");

            vBox.getChildren().add(new Text("Similarity: " + similarity));
            ImageView img = new ImageView(new Image("file:" + folder.getPath() + "\\" + path));
            img.setFitHeight(200);
            img.setFitWidth(500);
            vBox.getChildren().add(img);


        }

    }

    @FXML
    protected void searchUsingColorPalette() throws IOException {
        if (originalPhoto == null) {
            System.out.println("select a photo first");
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("choose the directory");
        directoryChooser.setInitialDirectory(new File(Main.ImagesPath));
        File folder = directoryChooser.showDialog(new Stage());

        if (folder == null) {
            System.out.println("Choose a Directory ");
            return;
        }

        List<String> imagesPath = new ArrayList<>();

        List<File> listOfFiles = new ArrayList<>(List.of(Objects.requireNonNull(folder.listFiles())));
        for (File file : listOfFiles) {
            int index = file.getName().lastIndexOf('.');
            String extension = file.getName().substring(index + 1);
            if (extension.equals("png") || extension.equals("jpg")) {
                imagesPath.add(file.getName());
            }
        }

        System.out.println(originalPhoto.getUrl());

        for (String path : imagesPath) {
            File image = new File(folder.getPath() + "\\" + path);
            double similarity = compareImagesUsingColorPalette(image.getPath(), originalPhoto.getUrl().substring(5));
            similarity = getTwoDigits(similarity);
            System.out.println(path);
            System.out.println("Similarity: " + similarity);
            System.out.println("-----------------------------");

            vBox.getChildren().add(new Text("Similarity: " + similarity));
            ImageView img = new ImageView(new Image("file:" + folder.getPath() + "\\" + path));
            img.setFitHeight(200);
            img.setFitWidth(500);
            vBox.getChildren().add(img);


        }

    }


    public double compareImagesUsingHistogram(String image1, String image2) throws IOException {

        ColorHistogram histogram1 = new ColorHistogram(image1);
        ColorHistogram histogram2 = new ColorHistogram(image2);
        return histogram2.calculateHistogramIntersection(histogram1, histogram2);

    }

    public double compareImagesUsingColorPalette(String image1, String image2) throws IOException {
        ColorPalette palette1 = new ColorPalette(image1);
        ColorPalette palette2 = new ColorPalette(image2);
        return ColorPalette.compareTwoImages(palette1, palette2);

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


    private static Double getTwoDigits(Double input) {
        String result = String.format("%.2f", input);
        return Double.parseDouble(result);

    }


}