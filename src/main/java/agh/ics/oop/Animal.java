package agh.ics.oop;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Animal extends AbstractWorldMapElement{

    private String northImageName = "src/main/resources/pig_up.png";
    private String southImageName = "src/main/resources/pig_down.png";
    private String eastImageName = "src/main/resources/pig_right.png";
    private String westImageName = "src/main/resources/pig_left.png";

    public final IWorldMap map;

    private MapDirection orientation = MapDirection.NORTH;

    private List<IPositionChangeObserver> observers = new ArrayList<>();

    public Animal(IWorldMap map, Vector2d initialPosition){
       super(initialPosition);
       this.map = map;
    }

    @Override public String toString() {
        switch(this.orientation){
            case NORTH:
                return "N";
            case EAST:
                return "E";
            case SOUTH:
                return "S";
            case WEST:
                return "W";
            default:
                return "Error!!!";
        }
    }

    public void move(MoveDirection direction){
        Vector2d newPosition = this.position;
        Vector2d oldPosition = this.position;

        switch (direction) {
            case RIGHT -> {
                this.orientation = orientation.next();
                positionChanged(oldPosition, newPosition);
                return;
            }
            case LEFT -> {
                this.orientation = orientation.previous();
                positionChanged(oldPosition,newPosition);
                return;
            }
            case FORWARD -> newPosition = this.position.add(orientation.toUnitVector());
            case BACKWARD -> newPosition = this.position.subtract(orientation.toUnitVector());
        }
        if ( !this.map.canMoveTo(newPosition)){
            return;
        }
        this.position = newPosition;
        positionChanged(oldPosition,newPosition);
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for(IPositionChangeObserver observer: observers){
            observer.positionChanged(oldPosition, newPosition);
        }
    }

    public String getImageName() throws FileNotFoundException {
        switch (this.orientation){
            case NORTH -> {
                if (northImageName.length() != 0) {
                    return this.northImageName;
                }
                else throw new FileNotFoundException("No image name for animal");
            }
            case EAST -> {
                if (eastImageName.length() != 0) {
                    return this.eastImageName;
                }
                else throw new FileNotFoundException("No image name for animal");
            }
            case WEST -> {
                if (westImageName.length() != 0) {
                    return this.westImageName;
                }
                else throw new FileNotFoundException("No image name for animal");
            }
            case SOUTH -> {
                if (southImageName.length() != 0) {
                    return this.southImageName;
                }
                else throw new FileNotFoundException("No image name for animal");
            }
            default -> {
                throw new FileNotFoundException("No image name for Animal");
            }
        }
    }

    @Override
    public String getLabel() {
        return "A" + this.getPosition().toString();
    }

}
