module com.example.itsspotifylove {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;


    opens com.example.itsspotifylove to javafx.fxml;
    exports com.example.itsspotifylove;


}