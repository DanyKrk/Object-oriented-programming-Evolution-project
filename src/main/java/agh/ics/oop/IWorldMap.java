package agh.ics.oop;

import java.util.Map;

public interface IWorldMap {

    boolean canMoveTo(Vector2d position);

    boolean manuallyPlaceNewAnimal(Vector2d destination);

    Object objectAt(Vector2d position);

    Map<Vector2d, MapSection> getPositionSectionMap();

    Vector2d getUpperRightCorner();

    Vector2d getLowerLeftCorner();


    int getStartEnergy();

    boolean bordersRunaround();

    int getWidth();

    int getHeight();
}