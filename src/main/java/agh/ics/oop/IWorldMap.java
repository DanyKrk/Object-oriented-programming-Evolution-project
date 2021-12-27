package agh.ics.oop;

import java.util.Map;

public interface IWorldMap {

    boolean canMoveTo(Vector2d position);

    boolean manuallyPlaceNewAnimal(Animal animal);

    Object objectAt(Vector2d position);

    Map<Vector2d, MapSection> getPositionSectionMap();

    Vector2d getUpperRightCorner();

    Vector2d getLowerLeftCorner();
}