package com.example.colorquantization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class Main extends Application {

    static final String originalPath = System.getProperty("user.dir") + "\\src\\main\\resources\\com\\example\\colorquantization\\pics\\originals\\";
    static final String editedPath = System.getProperty("user.dir") + "\\src\\main\\resources\\com\\example\\colorquantization\\pics\\Edited\\";
    static final String ImagesPath = System.getProperty("user.dir") + "\\Images";
    final float screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    final float screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    enum Algorithms {
        K_MEANS,
        MEDIAN_CUT,
        UNIFORM_COLOR
    }

    BufferedImage oldImage;

    Image selectedImage;
    Image newImage;
    File newImageFile;
    Scene scene;
    TextField numberId;
    Button addImageButton, applyButton, saveImageButton, colorPaletteBtn, histogramBtn, searchBtn;
    ImageView original, edited;
    Parent root;
    ChoiceBox<Algorithms> choiceBox;


    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(System.getProperty("user.dir"));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        root = fxmlLoader.load();
        scene = new Scene(root, screenWidth / 2, screenHeight / 2);

        original = (ImageView) scene.lookup("#original");
        edited = (ImageView) scene.lookup("#edited");
        addImageButton = (Button) scene.lookup("#addImage");
        applyButton = (Button) scene.lookup("#applyButton");
        colorPaletteBtn = (Button) scene.lookup("#colorPaletteBtn");
        saveImageButton = (Button) scene.lookup("#saveImageButton");
        histogramBtn = (Button) scene.lookup("#histogramBtn");
        searchBtn = (Button) scene.lookup("#searchBtn");

        choiceBox = (ChoiceBox<Algorithms>) scene.lookup("#choiceBox");
        numberId = (TextField) scene.lookup("#numberId");

        choiceBox.getItems().add(Algorithms.K_MEANS);
        choiceBox.getItems().add(Algorithms.UNIFORM_COLOR);
        choiceBox.getItems().add(Algorithms.MEDIAN_CUT);


        stage.setTitle("Color Quantization");
        stage.setScene(scene);
        stage.show();

        addImageButton.setOnAction(selectImageAction(stage));
        applyButton.setOnAction(applyAlgorithm());
        saveImageButton.setOnAction(saveNewImage(stage));
        colorPaletteBtn.setOnAction(openColorPalette());
        histogramBtn.setOnAction(openColorHistogram());
        searchBtn.setOnAction(openSearchScreen());

    }

    private Action openSearchScreen() {
        return new Action(e -> {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(SearchController.class.getResource("search-view.fxml")));
                Stage stage = new Stage();
                stage.setTitle("My New Stage Title");
                stage.setScene(new Scene(root, 1280, 800));
                stage.show();
                // Hide this current window (if this is what you want)
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
    }

    private Action saveNewImage(Stage stage) {
        return new Action(e -> {
            try {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("choose the directory");
                File selectedDirectory = directoryChooser.showDialog(stage);
                if (newImageFile == null) return;

                BufferedImage image = ImageIO.read(newImageFile);
                ImageIO.write(image, "png", new File(selectedDirectory + newImageFile.getName()));

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private Action openColorHistogram() {
        return new Action(e -> {
            try {

                ColorHistogram colorHistogram = new ColorHistogram(newImageFile.getPath());
                colorHistogram.MakeColorHistogram();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private Action openColorPalette() {
        return new Action(e -> {
            try {
                ColorPalette colorPalette = new ColorPalette(newImageFile.getPath());
                colorPalette.createColorPalette();

            } catch (Exception s) {
                s.printStackTrace();
            }

        });
    }

    private Action applyAlgorithm() {
        return new Action(e -> {
            if (selectedImage == null) {
                System.out.println("Enter an image to apply an algorithim to it");
                return;
            }

            Algorithms a = choiceBox.getValue();

            if (a == null) {
                System.out.println("Select Algorithim");
                return;
            }
            if (Objects.equals(numberId.getText(), "")) {
                System.out.println("you need to add a targetColor");
                return;
            }

            int targetColor = Integer.parseInt(numberId.getText());

            if (targetColor < 1) {
                System.out.println("you need to enter a number more than 1");
                return;
            }

            String imgPath = selectedImage.getUrl().substring(5);
            long startTime = System.nanoTime();

            switch (a) {

                case K_MEANS -> {
                    newImageFile = Kmeans.start(imgPath, editedPath + Algorithms.K_MEANS.name() + "\\", targetColor);
                    assert newImageFile != null;
                    newImage = new Image("file:" + newImageFile.getPath());
                }

                case MEDIAN_CUT -> {
                    try {
                        newImageFile = MedianCut.reduceColors(imgPath, targetColor, editedPath + Algorithms.MEDIAN_CUT.name() + "\\");
                        newImage = new Image("file:" + newImageFile.getPath());

                    } catch (IOException x) {
                        x.printStackTrace();
                    }
                }

                case UNIFORM_COLOR -> {
                    try {
                        newImageFile = UniformColor.start(imgPath, targetColor, editedPath + Algorithms.UNIFORM_COLOR.name() + "\\");
                        newImage = new Image("file:" + newImageFile.getPath());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }
            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("Time to run  "+a+" is :" + elapsedTime/1000000000);

            edited.setImage(newImage);
        });
    }

    public static void main(String[] args) {
        launch();
    }

    public Action selectImageAction(Stage stage) {
        return new Action(e -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select an Image");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "\\Images"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

            try {
                File file = fileChooser.showOpenDialog(stage);
                if (file == null) return;
                selectedImage = new Image("file:" + file.getPath());
                saveOldImage(file);
                original.setImage(selectedImage);

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        });
    }

    public void saveOldImage(File file) {

        try {
            oldImage = ImageIO.read(file);
            ImageIO.write(oldImage, "png", new File(originalPath + "OG" + file.getName()));

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }


}