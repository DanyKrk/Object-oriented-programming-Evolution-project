package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class BorderedMap extends AbstractWorldMap {

    public BorderedMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio){
        super(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio);
    }

    public boolean bordersRunaround(){
        return false;
    }
}
