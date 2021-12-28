package agh.ics.oop.gui;

import agh.ics.oop.Grass;
import agh.ics.oop.IMapElement;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GuiElementBox{
    private VBox vbox;

    public GuiElementBox(IMapElement element, int elementSize) throws FileNotFoundException {
        Image image = new Image(new FileInputStream(element.getImageName()));

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(elementSize);
        imageView.setFitHeight(elementSize);

        Label label = new Label(element.getLabel());

//        vbox = new VBox(imageView, label);
        vbox = new VBox(imageView);
        vbox.setAlignment(Pos.BASELINE_CENTER);

    }

    public VBox getVbox() {
        return vbox;
    }
}
