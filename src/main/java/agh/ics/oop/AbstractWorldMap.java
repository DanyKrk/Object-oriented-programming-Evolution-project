package agh.ics.oop;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    private int width;
    private int height;
    protected Vector2d upperRightCorner;
    protected Vector2d lowerLeftCorner;
//    protected List<IMapElement> elements = new ArrayList<>();
    protected Map<Vector2d, IMapElement> positionElementMap= new LinkedHashMap<>();
    protected MapVisualizer drawer;

    public AbstractWorldMap(int width, int height){
        this.lowerLeftCorner = new Vector2d(0,0);
        this.upperRightCorner = new Vector2d(width - 1, height - 1);
        drawer = new MapVisualizer(this);
    }

    public boolean canMoveTo(Vector2d position){
        if (this.isOccupied(position)) return false;
        return true;
    }

    public boolean place(Animal animal){
        Vector2d destination = animal.getPosition();

        if (canMoveTo(destination)) {
            positionElementMap.put(destination, animal);
            animal.addObserver(this);
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
        return element;
    }

    public String toString(){
        return drawer.draw(lowerLeftCorner, upperRightCorner);
    }

    public boolean positionChanged(Vector2d oldPosition, Vector2d newPosition){
        IMapElement element = this.positionElementMap.remove(oldPosition);
        if(element == null) return false;
        return this.positionElementMap.put(newPosition, element) == element;
    }

    public Vector2d getUpperRightCorner(){
        return this.getUpperRightCorner();
    }

    public Vector2d getLowerLeftCorner(){
        return this.getLowerLeftCorner();
    }

    public Map<Vector2d, IMapElement> getPositionElementMap(){
        return this.positionElementMap;
    }
}
