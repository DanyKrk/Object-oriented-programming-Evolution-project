package agh.ics.oop;

public class RunaroundMap extends AbstractWorldMap{
    public RunaroundMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int numberOfStartingAnimals) {
        super(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, numberOfStartingAnimals);
    }
    public boolean bordersRunaround(){
        return true;
    }
}
