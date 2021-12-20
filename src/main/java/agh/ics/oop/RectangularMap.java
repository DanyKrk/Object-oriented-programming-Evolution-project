package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class RectangularMap  extends AbstractWorldMap {


    public RectangularMap(int width, int height){
        this.lowerLeftCorner = new Vector2d(0,0);
        this.upperRightCorner = new Vector2d(width - 1, height - 1);
        border.addPosition(upperRightCorner);
        border.addPosition(lowerLeftCorner);
        drawer = new MapVisualizer(this);
    }

    @Override public boolean canMoveTo(Vector2d position) {
        if (super.canMoveTo(position)) {
            return position.precedes(upperRightCorner) && position.follows(lowerLeftCorner);
        }
        return false;
    }
}
