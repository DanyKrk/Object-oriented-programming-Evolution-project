package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

public class BorderedMap extends AbstractWorldMap {

    public BorderedMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int numberOfStartingAnimals){
        super(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, numberOfStartingAnimals);
    }

    public boolean bordersRunaround(){
        return false;
    }
}
