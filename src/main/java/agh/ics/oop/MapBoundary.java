package agh.ics.oop;

import java.util.SortedSet;
import java.util.TreeSet;

public class MapBoundary implements IPositionChangeObserver{
    private SortedSet<Vector2d> positionsSortedByX = new TreeSet<Vector2d>(new Vector2dComparatorByX());


    private SortedSet<Vector2d> positionsSortedByY = new TreeSet<Vector2d>(new Vector2dComparatorByY());

    public boolean positionChanged(Vector2d oldPosition, Vector2d newPosition){
        if(!positionsSortedByX.remove(oldPosition)){return false;}
        if(!positionsSortedByX.add(newPosition)){return false;}

        if(!positionsSortedByY.remove(oldPosition)){return false;}
        if(!positionsSortedByY.add(newPosition)){return false;}
        return true;
    }

    public void addPosition(Vector2d position){
        positionsSortedByX.add(position);
        positionsSortedByY.add(position);
    }

    public void removePosition(Vector2d position){
        positionsSortedByX.remove(position);
        positionsSortedByY.remove(position);
    }

    public Vector2d getUpperRightCorner(){
        return positionsSortedByX.last().upperRight(positionsSortedByY.last());
    }


    public Vector2d getLowerLeftCorner() {
        return positionsSortedByX.first().lowerLeft(positionsSortedByY.first());

    }
}
