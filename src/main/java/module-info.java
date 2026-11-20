/**
 * JavaFX Gallery – a showcase of the standard JavaFX controls.
 */
module name.ulbricht.javafx.gallery {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;
    requires java.prefs;

    exports name.ulbricht.javafx.gallery;
    exports name.ulbricht.javafx.gallery.catalog;
    exports name.ulbricht.javafx.gallery.shell;
    exports name.ulbricht.javafx.gallery.theme;
    exports name.ulbricht.javafx.gallery.settings;

    opens name.ulbricht.javafx.gallery.shell to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.basicinput to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.text to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.collections to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.layout to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.menus to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.dialogs to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.status to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.navigation to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.media to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.charts to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.shapes to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.styling to javafx.fxml;
    opens name.ulbricht.javafx.gallery.pages.data to javafx.fxml, javafx.base;
}
