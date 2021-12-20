package agh.ics.oop;

import java.io.FileNotFoundException;

public class Grass extends AbstractWorldMapElement{

    private String imageName = "src/main/resources/grass.png";

    public Grass(Vector2d position){super(position);}

    @Override public String toString(){
        return "*";
    }

    public String getImageName() throws FileNotFoundException {
        if (imageName.length() != 0) {
            return this.imageName;
        }
        else throw new FileNotFoundException("No image name for grass");
    }

    @Override
    public String getLabel() {
        return "Grass";
    }
}
