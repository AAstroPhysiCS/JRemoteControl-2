module JRemoteControl2 {
    requires javafx.swing;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.web;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires opencv;
    requires java.net.http;

    opens Server.Overlay;
}