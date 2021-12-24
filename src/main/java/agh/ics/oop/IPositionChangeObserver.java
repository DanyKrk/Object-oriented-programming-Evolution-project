package agh.ics.oop;

public interface IPositionChangeObserver {
    boolean positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition);
}
