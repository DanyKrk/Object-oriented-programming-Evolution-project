package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class BorderedMap extends AbstractWorldMap {


    public BorderedMap(int width, int height){
        super(width, height);
    }

    @Override public boolean canMoveTo(Vector2d position) {
        if (super.canMoveTo(position)) {
            return position.precedes(upperRightCorner) && position.follows(lowerLeftCorner);
        }
        return false;
    }
}
