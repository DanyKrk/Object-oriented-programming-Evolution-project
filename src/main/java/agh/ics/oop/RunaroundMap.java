package agh.ics.oop;

public class RunaroundMap extends AbstractWorldMap{
    public RunaroundMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio) {
        super(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio);
    }
    public boolean bordersRunaround(){
        return true;
    }
}
