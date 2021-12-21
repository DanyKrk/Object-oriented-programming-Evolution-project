package agh.ics.oop;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Animal extends AbstractWorldMapElement{

    private final int numberOfGenes = 32;
    private final int genesRange = 8;

    private String northImageName = "src/main/resources/pig_up.png";
    private String southImageName = "src/main/resources/pig_down.png";
    private String eastImageName = "src/main/resources/pig_right.png";
    private String westImageName = "src/main/resources/pig_left.png";

    public final IWorldMap map;

    private MapDirection orientation = MapDirection.NORTH;

    private int energy;

    private int[] genes;

    private List<IPositionChangeObserver> observers = new ArrayList<>();



    public Animal(IWorldMap map, Vector2d initialPosition){
       super(initialPosition);
       this.map = map;
    }

    @Override public String toString() {
        return orientation.toString();
    }

    public void move(MoveDirection direction){
        Vector2d newPosition = this.position;
        Vector2d oldPosition = this.position;

        switch (direction) {
            case RIGHT -> {
                this.orientation = orientation.next();
                positionChanged(oldPosition, newPosition);
                return;
            }
            case LEFT -> {
                this.orientation = orientation.previous();
                positionChanged(oldPosition,newPosition);
                return;
            }
            case FORWARD -> newPosition = this.position.add(orientation.toUnitVector());
            case BACKWARD -> newPosition = this.position.subtract(orientation.toUnitVector());
        }
        if ( !this.map.canMoveTo(newPosition)){
            return;
        }
        this.position = newPosition;
        positionChanged(oldPosition,newPosition);
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for(IPositionChangeObserver observer: observers){
            observer.positionChanged(oldPosition, newPosition);
        }
    }

    public String getImageName() throws FileNotFoundException {
        switch (this.orientation){
            case NORTH -> {
                if (northImageName.length() != 0) {
                    return this.northImageName;
                }
                else throw new FileNotFoundException("No image name for animal");
            }
            case EAST -> {
                if (eastImageName.length() != 0) {
                    return this.eastImageName;
                }
                else throw new FileNotFoundException("No image name for animal");
            }
            case WEST -> {
                if (westImageName.length() != 0) {
                    return this.westImageName;
                }
                else throw new FileNotFoundException("No image name for animal");
            }
            case SOUTH -> {
                if (southImageName.length() != 0) {
                    return this.southImageName;
                }
                else throw new FileNotFoundException("No image name for animal");
            }
            default -> {
                throw new FileNotFoundException("No image name for Animal");
            }
        }
    }

    @Override
    public String getLabel() {
        return "A" + this.getPosition().toString();
    }

    public int getEnergy() {
        return energy;
    }

    public int[] getGenes() {
        return genes;
    }

    public void setRandomGenes(){
        this.genes = new int[numberOfGenes];
        for(int gene:genes){
            gene = ((int)(Math.random() * 100)) % genesRange;
        }
        Arrays.sort(genes);
    }

    public void setGenesBasedOnParents(Animal parent1, Animal parent2){
        this.genes = new int[numberOfGenes];
        Animal strongerParent;
        Animal weakerParent;

        if (parent1.getEnergy() >= parent2.getEnergy()){
            strongerParent = parent1;
            weakerParent = parent2;
        }
        else {
            strongerParent = parent2;
            weakerParent = parent1;
        }

        int[] strongerGenes = strongerParent.getGenes();
        int[] weakerGenes = weakerParent.getGenes();
        int strongerParentEnergy = strongerParent.getEnergy();
        int weakerParentEnergy = weakerParent.getEnergy();

        boolean takesLeftSideGenesFromStrongerParent = Math.random() < 0.5;

        if (takesLeftSideGenesFromStrongerParent){
            int borderID = (numberOfGenes - 1) * strongerParentEnergy / (strongerParentEnergy + weakerParentEnergy);
            for (int i = 0; i <= borderID; i++){
                this.genes[i] = strongerGenes[i];
            }
            for (int i = borderID + 1; i < numberOfGenes; i++){
                this.genes[i] = weakerGenes[i];
            }
        }
        else {
            int borderID = (numberOfGenes - 1) * weakerParentEnergy / (strongerParentEnergy + weakerParentEnergy);
            for (int i = 0; i <= borderID; i++){
                this.genes[i] = weakerGenes[i];
            }
            for (int i = borderID + 1; i < numberOfGenes; i++){
                this.genes[i] = strongerGenes[i];
            }
        }

        Arrays.sort(this.genes);
    }

    public void setGenes(int[] inputGenes){
        this.genes = Arrays.copyOf(inputGenes,numberOfGenes);
    }

    public void setEnergy(int inputEnergy){
        this.energy = inputEnergy;
    }
}
