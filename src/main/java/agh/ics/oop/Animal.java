package agh.ics.oop;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Animal extends AbstractWorldMapElement implements Comparable{
    private final int numberOfGenes = 32;
    private final int genesRange = 8;
    private final MoveDirection[] moveDirections = {
            MoveDirection.FORWARD,
            MoveDirection.RIGHTFORWARD,
            MoveDirection.RIGHT,
            MoveDirection.RIGHTBACKWARD,
            MoveDirection.BACKWARD,
            MoveDirection.LEFTBACKWARD,
            MoveDirection.LEFT,
            MoveDirection.LEFTFORWARD
    };
    private final MapDirection[] mapDirections = {
            MapDirection.NORTH,
            MapDirection.NORTHEAST,
            MapDirection.EAST,
            MapDirection.SOUTHEAST,
            MapDirection.SOUTH,
            MapDirection.SOUTHWEST,
            MapDirection.WEST,
            MapDirection.NORTHWEST
    };

    private String northImageName = "src/main/resources/pig_up.png";
    private String southImageName = "src/main/resources/pig_down.png";
    private String eastImageName = "src/main/resources/pig_right.png";
    private String westImageName = "src/main/resources/pig_left.png";

    public final AbstractWorldMap map;

    private MapDirection orientation;

    private int energy;

    private int[] genes;

    private List<IPositionChangeObserver> observers = new ArrayList<>();



    public Animal(AbstractWorldMap map, Vector2d initialPosition){
       super(initialPosition);
       this.map = map;
       this.orientation = getRandomMapDirection();
       this.addObserver(this.map);
    }

    @Override public String toString() {
        return orientation.toString();
    }

    public void move(){
        Vector2d newPosition = this.position;
        Vector2d oldPosition = this.position;

        MoveDirection direction = getRandomMoveDirectionBasedOnGenes();

        switch (direction) {
            case RIGHTFORWARD -> {
                this.orientation = orientation.next();
                return;
            }
            case RIGHT -> {
                this.orientation = orientation.next().next();
                return;
            }
            case RIGHTBACKWARD -> {
                this.orientation = orientation.next().next().next();
                return;
            }
            case LEFTFORWARD -> {
                this.orientation = orientation.previous();
                return;
            }
            case LEFT -> {
                this.orientation = orientation.previous().previous();
                return;
            }
            case LEFTBACKWARD -> {
                this.orientation = orientation.previous().previous().previous();
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

    private MoveDirection getRandomMoveDirectionBasedOnGenes() {
        int directionID = genes[getRandomNumberFrom0ToN(numberOfGenes)];
        MoveDirection direction = moveDirections[directionID];
        return direction;
    }

    private MapDirection getRandomMapDirection() {
        int directionID = getRandomNumberFrom0ToN(genesRange);
        MapDirection direction = mapDirections[directionID];
        return direction;
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for(IPositionChangeObserver observer: observers){
            observer.positionChanged(this, oldPosition, newPosition);
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
            gene = getRandomNumberFrom0ToN(genesRange);
        }
        Arrays.sort(genes);
    }

    private int getRandomNumberFrom0ToN(int n) {
        return ((int)(Math.random() * 100)) % n;
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

    public int compareTo(Object o) {
        if (o instanceof Animal){
            int energyDiff = this.getEnergy() - ((Animal) o).getEnergy();
            if (energyDiff != 0){return energyDiff;}

            if(Math.random() - 0.5 > 0) return 1;
            else return -1;
            }
        return 0;
    }

    public boolean readyForReproduction(){
        return this.getEnergy() * 2 >= this.map.getStartEnergy();
    }

    public int extractEnergyForChild() {
        int energyForChild = (int) (this.getEnergy() * 0.25);
        this.setEnergy(this.getEnergy() - energyForChild);
        return energyForChild;
    }
}
