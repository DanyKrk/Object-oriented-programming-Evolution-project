package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class BorderedMap extends AbstractWorldMap {

    public BorderedMap(int width, int height){
        super(width, height);
    }

    public boolean bordersRunaround(){
        return false;
    }
}
