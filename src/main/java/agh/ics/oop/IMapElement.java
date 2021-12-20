package agh.ics.oop;

import java.io.FileNotFoundException;

public interface IMapElement {

    Vector2d getPosition();

    String toString();

    boolean isAt(Vector2d position);

    String getImageName() throws FileNotFoundException;

    String getLabel();
}
