package agh.ics.oop;

public interface IWorldMap {

    boolean canMoveTo(Vector2d position);

    boolean manuallyPlaceNewAnimal(Animal animal);

    Object objectAt(Vector2d position);
}