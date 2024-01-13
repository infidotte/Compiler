module com.example.compiler {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.desktop;


    opens com.example.compiler to javafx.fxml;
    exports com.example.compiler;
}