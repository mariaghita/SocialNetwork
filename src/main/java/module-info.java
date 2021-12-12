module socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens socialnetwork to javafx.fxml;
    exports socialnetwork;

    opens socialnetwork.controller to javafx.fxml;
    exports socialnetwork.controller;
}