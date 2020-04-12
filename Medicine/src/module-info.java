module Medicine {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;

    exports medNin.ui to javafx.fxml, javafx.graphics;

    opens medNin;
    opens medNin.ui to javafx.fxml, javafx.graphics;
}