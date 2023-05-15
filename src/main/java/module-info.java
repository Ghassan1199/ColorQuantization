module com.example.colorquantization {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens com.example.colorquantization to javafx.fxml;
    exports com.example.colorquantization;
}