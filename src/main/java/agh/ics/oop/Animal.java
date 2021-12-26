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
    private int[] genotype;
    private List<IPositionChangeObserver> positionChangeObservers = new ArrayList<>();
    private List<IAnimalEnergyObserver> energyObservers = new ArrayList<>();
    private long birthDay;
    private long deathDay;
    private int numberOfChildren;
    private boolean isTracked;
    private List<Animal> descendants;
    private boolean notifiesAncestor;
    private Animal trackedAncestor;


    public Animal(AbstractWorldMap map, Vector2d initialPosition, long birthDay){
       super(initialPosition);
       this.map = map;
       this.orientation = getRandomMapDirection();
       this.addPositionChangeObserver(this.map);
       this.addEnergyObserver(this.map);
       this.numberOfChildren = 0;
       this.isTracked = false;
       this.trackedAncestor = null;
    }

    private void addEnergyObserver(AbstractWorldMap map) {
        this.energyObservers.add(map);
    }

    @Override public String toString() {
        return orientation.toString();
    }

    public void move(){
        Vector2d newPosition = this.position;
        Vector2d oldPosition = this.position;

        MoveDirection direction = getRandomMoveDirectionBasedOnGenotype();

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

    private MoveDirection getRandomMoveDirectionBasedOnGenotype() {
        int directionID = genotype[getRandomNumberFrom0ToN(numberOfGenes)];
        MoveDirection direction = moveDirections[directionID];
        return direction;
    }

    private MapDirection getRandomMapDirection() {
        int directionID = getRandomNumberFrom0ToN(genesRange);
        MapDirection direction = mapDirections[directionID];
        return direction;
    }

    public void addPositionChangeObserver(IPositionChangeObserver observer){
        this.positionChangeObservers.add(observer);
    }

    void removeObserver(IPositionChangeObserver observer){
        this.positionChangeObservers.remove(observer);
    }

    void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for(IPositionChangeObserver observer: positionChangeObservers){
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

    public int[] getGenotype() {
        return genotype;
    }

    public void setRandomGenes(){
        this.genotype = new int[numberOfGenes];
        for(int gene: genotype){
            gene = getRandomNumberFrom0ToN(genesRange);
        }
        Arrays.sort(genotype);
    }

    private int getRandomNumberFrom0ToN(int n) {
        return ((int)(Math.random() * 100)) % n;
    }

    public void setGenesBasedOnParents(Animal parent1, Animal parent2){
        this.genotype = new int[numberOfGenes];
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

        int[] strongerGenes = strongerParent.getGenotype();
        int[] weakerGenes = weakerParent.getGenotype();
        int strongerParentEnergy = strongerParent.getEnergy();
        int weakerParentEnergy = weakerParent.getEnergy();

        boolean takesLeftSideGenesFromStrongerParent = Math.random() < 0.5;

        if (takesLeftSideGenesFromStrongerParent){
            int borderID = (numberOfGenes - 1) * strongerParentEnergy / (strongerParentEnergy + weakerParentEnergy);
            for (int i = 0; i <= borderID; i++){
                this.genotype[i] = strongerGenes[i];
            }
            for (int i = borderID + 1; i < numberOfGenes; i++){
                this.genotype[i] = weakerGenes[i];
            }
        }
        else {
            int borderID = (numberOfGenes - 1) * weakerParentEnergy / (strongerParentEnergy + weakerParentEnergy);
            for (int i = 0; i <= borderID; i++){
                this.genotype[i] = weakerGenes[i];
            }
            for (int i = borderID + 1; i < numberOfGenes; i++){
                this.genotype[i] = strongerGenes[i];
            }
        }

        Arrays.sort(this.genotype);
    }

    public void setGenotype(int[] inputGenes){
        this.genotype = Arrays.copyOf(inputGenes,numberOfGenes);
    }

    public void setEnergy(int inputEnergy){
        int oldEnergy = this.getEnergy();
        this.energyChanged(inputEnergy - oldEnergy);
        this.energy = inputEnergy;
    }

    private void energyChanged(int difference) {
        for(IAnimalEnergyObserver observer: energyObservers){
            observer.energyChanged(difference);
        }
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

    public long getBirthDay() {
        return this.birthDay;
    }

    public long getDeathDay() {
        return this.deathDay;
    }

    public void setDeathDay(long day) {
        this.deathDay = day;
    }

    public void incrementNumberOfChildren() {
        this.numberOfChildren += 1;
    }

    public int getNumberOfChildren() {
        return this.numberOfChildren;
    }

    public void startTracking(){
        this.isTracked = true;
        this.descendants = new ArrayList<>();
    }

    public void stopTracking(){
        this.isTracked = false;
        this.notifyDescendantsToForgetTrackedAncestor();
        this.descendants = null;
    }

    private void notifyDescendantsToForgetTrackedAncestor() {
        for(Animal descendant: descendants){
            descendant.forgetTrackedAncestor();
        }
    }

    private void forgetTrackedAncestor() {
        this.notifiesAncestor = false;
        this.trackedAncestor = null;
    }

    public void startNotifyingTrackedAncestor(Animal trackedAncestor){
        this.trackedAncestor = trackedAncestor;
        this.notifiesAncestor = true;
    }

    public boolean isTracked() {
        return this.isTracked;
    }

    public boolean notifiesAncestor(){
        return this.notifiesAncestor;
    }

    public void addDescendant(Animal child) {
        this.descendants.add(child);
    }

    public Animal getTrackedAncestor() {
        return trackedAncestor;
    }

    public int getNumberOfDescendants(){
        if (!this.isTracked()){
            throw new IllegalActionException("Trying to get number of descendants from not tracked Animal!!!");
        }
        else return this.descendants.size();
    }
}
