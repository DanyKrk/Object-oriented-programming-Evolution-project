package agh.ics.oop;

import java.util.Comparator;

public class Vector2dComparatorByX implements Comparator<Vector2d> {
    public int compare(Vector2d a, Vector2d b){
        int xDiff = a.x - b.x;
        if (xDiff != 0){return xDiff;}

        int yDiff = a.y - b.y;
        return yDiff;
    }
}
