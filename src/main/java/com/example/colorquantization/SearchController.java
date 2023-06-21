package com.example.colorquantization;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class SearchController {

    private Image originalImage;
    private ImageView imageView;
    private Rectangle cropRectangle;
    private double rectangleOffsetX;
    private double rectangleOffsetY;
    private double mouseX;
    private double mouseY;

    private ListView<String> colorListView;
    private ColorPicker colorPicker;
    private Stage searchStage;
    private Stage cropStage;


    Image originalPhoto;
    ArrayList<Color> colorArrayList = new ArrayList<>();
    @FXML
    ListView<String> colors;
    @FXML
    private ImageView ogPhoto;
    @FXML
    private VBox vBox;

    private final ArrayList<Pair<ImageView, Double>> ImageViewList = new ArrayList<>();


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
        ImageViewList.clear();
        if (originalPhoto == null) {
            System.out.println("select a photo first");
            return;
        }

        File folder = openDirectory();

        if (folder == null) {
            System.out.println("Choose A Directory");
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
        File image1 = Kmeans.start(originalPhoto.getUrl().substring(5), Main.editedPath, 0);
        ogPhoto.setImage(new Image("file:" + image1.getPath()));
        for (String path : imagesPath) {
            File image2 = Kmeans.start(folder.getPath() + "\\" + path, Main.editedPath, 0);
            double similarity = compareImagesUsingHistogram(image2.getPath(), image1.getPath());
            similarity = getTwoDigits(similarity);
            System.out.println(path);
            System.out.println("Similarity: " + similarity);
            System.out.println("-----------------------------");

            ImageView img = new ImageView(new Image("file:" + image2.getPath()));
            img.setFitHeight(200);
            img.setFitWidth(500);
            img.setOnMouseClicked(e -> {
                String[] commands = {
                        "cmd.exe", "/c", "start", "\"DummyTitle\"", "\"" + folder.getPath() + "\\" + path + "\""
                };
                try {
                    Process p = Runtime.getRuntime().exec(commands);
                    p.waitFor();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            });
            ImageViewList.add(new Pair<>(img, similarity));
        }


        addImagesToVBox();

    }

    @FXML
    protected void searchUsingColorPalette() throws IOException {

        if (originalPhoto == null) {
            System.out.println("select a photo first");
            return;
        }

        File folder = openDirectory();

        if (folder == null) {
            System.out.println("Choose A Directory");
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
        File image1 = UniformColor.start(originalPhoto.getUrl().substring(5), Main.editedPath, 4);
        ogPhoto.setImage(new Image("file:" + image1.getPath()));
        for (String path : imagesPath) {
            File image2 = UniformColor.start(folder.getPath() + "\\" + path, Main.editedPath, 4);
            double similarity = compareImagesUsingColorPalette(image2.getPath(), image1.getPath());
            similarity = getTwoDigits(similarity);
            System.out.println(path);
            System.out.println("Similarity: " + similarity);
            System.out.println("-----------------------------");

            ImageView img = new ImageView(new Image("file:" + image2.getPath()));
            img.setFitHeight(200);
            img.setFitWidth(500);
            img.setOnMouseClicked(e -> {
                String[] commands = {
                        "cmd.exe", "/c", "start", "\"DummyTitle\"", "\"" + folder.getPath() + "\\" + path + "\""
                };
                try {
                    Process p = Runtime.getRuntime().exec(commands);
                    p.waitFor();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            });
            ImageViewList.add(new Pair<>(img, similarity));
        }


        addImagesToVBox();


    }


    public void searchUsingColor() throws IOException {

        searchStage.close();

        File folder = openDirectory();

        if (folder == null) {
            System.out.println("Choose A Directory");
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

        for (String path : imagesPath) {
            File image2 = UniformColor.start(folder.getPath() + "\\" + path, Main.editedPath, 4);
            double similarity = compareUsingColors(image2.getPath(), colorArrayList);
            similarity = getTwoDigits(similarity);
            System.out.println(path);
            System.out.println("Similarity: " + similarity);
            System.out.println("-----------------------------");
            if (similarity > 0.5) {
                ImageView img = new ImageView(new Image("file:" + folder.getPath() + "\\" + path));
                img.setFitHeight(200);
                img.setFitWidth(500);
                img.setOnMouseClicked(e -> {
                    String[] commands = {
                            "cmd.exe", "/c", "start", "\"DummyTitle\"", "\"" + folder.getPath() + "\\" + path + "\""
                    };
                    try {
                        Process p = Runtime.getRuntime().exec(commands);
                        p.waitFor();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                });
                ImageViewList.add(new Pair<>(img, similarity));
            }
        }

        colorArrayList.clear();

        addImagesToVBox();

    }

    @FXML
    protected void openSearchUsingColorWindow() {
        try {
            colorPicker = new ColorPicker();
            colorListView = new ListView<>();

            Button addButton = new Button("Add Color");
            addButton.setOnAction(e -> addColorToList());
            Button searchButton = new Button("Search");
            searchButton.setOnAction(e -> {
                try {
                    searchUsingColor();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            HBox controlBox = new HBox(colorPicker, addButton, searchButton);
            VBox root = new VBox(controlBox, colorListView);
            Scene scene = new Scene(root, 300, 200);

            searchStage = new Stage();
            searchStage.setTitle("Multi-Color Picker");
            searchStage.setScene(scene);
            searchStage.show();

        } catch (Exception e) {
            e.printStackTrace();

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

    public double compareUsingColors(String image1, ArrayList<Color> colors) throws IOException {
        ColorPalette palette1 = new ColorPalette(image1);
        return ColorPalette.compareColors(palette1, colors);
    }

    private static Double getTwoDigits(Double input) {
        String result = String.format("%.2f", input);
        return Double.parseDouble(result);

    }

    private void addColorToList() {
        String colorString = getColorString(colorPicker.getValue());

        colorListView.getItems().add(colorString);
        colors.getItems().add(colorString);
    }

    private String getColorString(javafx.scene.paint.Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        Color c = new Color(red, green, blue);
        colorArrayList.add(c);
        return String.format("RGB: %d, %d, %d", red, green, blue);
    }

    @FXML
    protected void cropImage() {

        cropStage = new Stage();
        BorderPane root = new BorderPane();
        imageView = new ImageView();
        cropRectangle = new Rectangle();

        Button cropButton = new Button("Crop");
        cropButton.setOnAction(e -> cropAndSaveImage(cropStage));

        HBox toolbar = new HBox(cropButton);
        root.setTop(toolbar);
        root.setCenter(imageView);

        root.getChildren().add(cropRectangle);

        Scene scene = new Scene(root, 800, 600);
        cropStage.setTitle("Image Crop");
        cropStage.setScene(scene);
        cropStage.show();

        loadImage(cropStage);
        setupCropRectangle();
        addMouseHandlers();

    }

    private void loadImage(Stage primaryStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "\\Images"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File imageFile = fileChooser.showOpenDialog(primaryStage);

        if (imageFile != null) {
            originalImage = new Image(Objects.requireNonNull(imageFile.toURI().toString()));
            imageView.setImage(originalImage);
            imageView.setFitWidth(originalImage.getWidth());
            imageView.setFitHeight(originalImage.getHeight());
        } else {
            primaryStage.close();
        }
    }

    private void setupCropRectangle() {
        cropRectangle.setStroke(javafx.scene.paint.Color.RED);
        cropRectangle.setStrokeWidth(2);
        cropRectangle.setFill(javafx.scene.paint.Color.rgb(255, 0, 0, 0.1));
        cropRectangle.setMouseTransparent(true);
    }

    private void addMouseHandlers() {
        imageView.setOnMousePressed(this::handleMousePressed);
        imageView.setOnMouseDragged(this::handleMouseDragged);
    }

    private void handleMousePressed(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();

        if (event.isSecondaryButtonDown()) {
            rectangleOffsetX = cropRectangle.getX() - mouseX;
            rectangleOffsetY = cropRectangle.getY() - mouseY;
        } else {
            cropRectangle.setX(mouseX);
            cropRectangle.setY(mouseY);
            cropRectangle.setWidth(0);
            cropRectangle.setHeight(0);
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        double newMouseX = event.getX();
        double newMouseY = event.getY();

        if (event.isSecondaryButtonDown()) {
            cropRectangle.setX(newMouseX + rectangleOffsetX);
            cropRectangle.setY(newMouseY + rectangleOffsetY);
        } else {
            double width = newMouseX - mouseX;
            double height = newMouseY - mouseY;

            cropRectangle.setWidth(width);
            cropRectangle.setHeight(height);
        }
    }

    private void cropAndSaveImage(Stage primaryStage) {
        double x = cropRectangle.getX();
        double y = cropRectangle.getY();
        double width = cropRectangle.getWidth();
        double height = cropRectangle.getHeight();

        if (width <= 0 || height <= 0 || x < 0 || y < 0 || x + width > originalImage.getWidth() ||
                y + height > originalImage.getHeight()) {
            return;
        }

        // Calculate the coordinates and dimensions for cropping
        int cropX = (int) Math.round(x);
        int cropY = (int) Math.round(y);
        int cropWidth = (int) Math.round(width);
        int cropHeight = (int) Math.round(height);

        // Create a new BufferedImage with the cropped region
        BufferedImage croppedImage = new BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_ARGB);

        for (int destY = 0, srcY = cropY; destY < cropHeight; destY++, srcY++) {
            for (int destX = 0, srcX = cropX; destX < cropWidth; destX++, srcX++) {
                javafx.scene.paint.Color color = originalImage.getPixelReader().getColor(srcX, srcY);
                int rgba = (int) (color.getRed() * 255) << 16 |
                        (int) (color.getGreen() * 255) << 8 |
                        (int) (color.getBlue() * 255) |
                        (int) (color.getOpacity() * 255) << 24;
                croppedImage.setRGB(destX, destY, rgba);
            }
        }

        // Show folder chooser dialog for selecting the output folder
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory == null) {
            return; // No directory selected, exit without saving
        }

        // Construct the output file path using the selected directory and a fixed filename
        String outputFileName = "cropped.png";
        File outputFile = new File(selectedDirectory, outputFileName);

        // Save the cropped image
        try {
            ImageIO.write(croppedImage, "png", outputFile);
            originalPhoto = new Image("file:" + outputFile.getPath());
            ogPhoto.setImage(originalPhoto);

        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.close();
    }

    public void addImagesToVBox() {
        vBox.getChildren().clear();
        ImageViewList.sort(new Comparator<Pair<ImageView, Double>>() {
            @Override
            public int compare(Pair<ImageView, Double> o1, Pair<ImageView, Double> o2) {
                if (o1.getValue() > o2.getValue())
                    return -1;
                else
                    return 1;
            }
        });

        for (Pair<ImageView, Double> imageViewDoublePair : ImageViewList) {
            if (imageViewDoublePair.getValue() > 0.5)
                vBox.getChildren().add(imageViewDoublePair.getKey());
        }
    }

    private File openDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("choose the directory");
        directoryChooser.setInitialDirectory(new File(Main.ImagesPath));
        return directoryChooser.showDialog(new Stage());
    }


}