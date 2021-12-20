package agh.ics.oop;

import java.util.Comparator;

public class Vector2dComparatorByY implements Comparator<Vector2d> {
    public int compare(Vector2d a, Vector2d b){
        int yDiff = a.y - b.y;
        if (yDiff != 0){return yDiff;}

        int xDiff = a.x - b.x;
        return xDiff;

    }
}
