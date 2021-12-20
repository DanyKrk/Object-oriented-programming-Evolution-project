package agh.ics.oop;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {

    protected Vector2d upperRightCorner;
    protected Vector2d lowerLeftCorner;
//    protected List<IMapElement> elements = new ArrayList<>();
    protected Map<Vector2d, IMapElement> positionElementMap= new LinkedHashMap<>();
    protected MapVisualizer drawer;
    protected MapBoundary border = new MapBoundary();

    public boolean canMoveTo(Vector2d position){
        if (this.isOccupied(position)) return false;
        return true;
    }

    public boolean place(Animal animal){
        Vector2d destination = animal.getPosition();

        if (canMoveTo(destination)) {
            positionElementMap.put(destination, animal);
            animal.addObserver(this);
            animal.addObserver(border);
            border.addPosition(destination);
            return true;
        }
        else {
            throw new IllegalArgumentException("Animal can not be placed on: " + destination);
        }
    }

    public boolean isOccupied(Vector2d position){
        return positionElementMap.containsKey(position);
    }

    public Object objectAt(Vector2d position){
        return this.positionElementMap.get(position);
    }

    public IMapElement removeElement(Vector2d position){
        IMapElement element = this.positionElementMap.remove(position);
        if(element != null){ border.removePosition(position);}
        return element;
    }

    public String toString(){
        prepareCorners();

        return drawer.draw(lowerLeftCorner, upperRightCorner);
    }

    protected void prepareCorners(){
        upperRightCorner = border.getUpperRightCorner();
        lowerLeftCorner = border.getLowerLeftCorner();
    }

    public boolean positionChanged(Vector2d oldPosition, Vector2d newPosition){
        IMapElement element = this.positionElementMap.remove(oldPosition);
        if(element == null) return false;
        return this.positionElementMap.put(newPosition, element) == element;
    }

    public Vector2d getUpperRightCorner(){
        return this.border.getUpperRightCorner();
    }

    public Vector2d getLowerLeftCorner(){
        return this.border.getLowerLeftCorner();
    }

    public Map<Vector2d, IMapElement> getPositionElementMap(){
        return this.positionElementMap;
    }
}
