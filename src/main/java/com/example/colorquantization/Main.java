package com.example.colorquantization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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


public class Main extends Application {

    enum Algorithms {
        K_MEANS,
        MEDIAN_CUT

    }

    final String originalPath = "C:\\Users\\Ghassan\\Desktop\\Collage\\Media\\ColorQuantization\\src\\main\\resources\\com\\example\\colorquantization\\pics\\originals\\";
    final String editedPath = "C:\\Users\\Ghassan\\Desktop\\Collage\\Media\\ColorQuantization\\src\\main\\resources\\com\\example\\colorquantization\\pics\\Edited\\";
    final float screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    final float screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    Image selectedImage;
    Image newImage;
    File newImageFile;
    Scene scene;
    Button addImageButton, applyButton, saveImageButton;
    ImageView original, edited;
    ChoiceBox<Algorithms> choiceBox;


    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        scene = new Scene(fxmlLoader.load(), screenWidth / 2, screenHeight / 2);

        original = (ImageView) scene.lookup("#original");
        edited = (ImageView) scene.lookup("#edited");
        addImageButton = (Button) scene.lookup("#addImage");
        applyButton = (Button) scene.lookup("#applyButton");
        saveImageButton = (Button) scene.lookup("#saveImageButton");
        choiceBox = (ChoiceBox<Algorithms>) scene.lookup("#choiceBox");

        choiceBox.getItems().add(Algorithms.K_MEANS);
        choiceBox.getItems().add(Algorithms.MEDIAN_CUT);


        stage.setTitle("Color Quantization");
        stage.setScene(scene);
        stage.show();

        addImageButton.setOnAction(selectImageAction(stage));
        applyButton.setOnAction(applyAlgorithm());
        saveImageButton.setOnAction(saveNewImage(stage));

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
            switch (a) {
                case K_MEANS -> {
                    newImageFile = Kmeans.start(selectedImage.getUrl().substring(5), editedPath + Algorithms.K_MEANS.name() + "\\");
                    assert newImageFile != null;
                    newImage = new Image("file:" + newImageFile.getPath());
                }
                case MEDIAN_CUT -> {
                    try {
                        File newImageFile = MedianCut.reduceColors(selectedImage.getUrl().substring(5), 5, editedPath + Algorithms.MEDIAN_CUT.name() + "\\");
                        newImage = new Image("file:" + newImageFile.getPath());

                    } catch (IOException x) {
                        x.printStackTrace();
                    }
                }
                default -> {
                }
            }
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
            BufferedImage image = ImageIO.read(file);
            ImageIO.write(image, "png", new File(originalPath + "OG" + file.getName()));

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }


}