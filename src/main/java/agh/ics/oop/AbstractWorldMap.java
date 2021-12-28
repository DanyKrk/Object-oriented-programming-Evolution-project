package agh.ics.oop;

import agh.ics.oop.gui.App;

import java.util.*;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver, IPopulationOfAnimalsObserver,
        IGrassExsistenceObserver, IAnimalEnergyObserver{
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;
    private double jungleRatio;
    private int width;
    private int height;
    private int jungleWidth;
    private int jungleHeight;
    private int jungleSurfaceArea;
    private int steppeSurfaceArea;
    private int numberOfGrassesInSteppe;
    private int numberOfGrassesInJungle;
    private Vector2d[] freePositionsInJungle;
    private Vector2d[] freePositionsInSteppe;
    private int numberOfFreePositionsInJungle;
    private int numberOfFreePositionsInSteppe;
    private int numberOfAnimals;
    protected Vector2d upperRightCorner;
    protected Vector2d lowerLeftCorner;
    private Vector2d jungleLowerLeftCorner;
    private Vector2d jungleUpperRightCorner;
    protected Map<Vector2d, MapSection> positionSectionMap;
    private Map<int[], Integer> genotypeNumberOfOwnersMap;
    private int[] dominatingGenotype;
    private int numberOfAnimalsWithDominatingGenotype;
    private int energyOfLivingAnimals;
    private int numberOfDeadAnimals;
    private long day;
    private long lifespanOfDeadAnimals;
//    protected MapVisualizer drawer;
    private int numberOfChildrenOfLivingAnimals;
    private List<IDayObserver> dayObservers;
    private int numberOfStartingAnimals;


    public AbstractWorldMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int numberOfStartingAnimals){
        positionSectionMap = Collections.synchronizedMap(new HashMap<>());
        genotypeNumberOfOwnersMap = Collections.synchronizedMap(new HashMap<>());
        this.width = width;
        this.height = height;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.jungleRatio = jungleRatio;
        this.lowerLeftCorner = new Vector2d(0,0);
        this.upperRightCorner = new Vector2d(width - 1, height - 1);
        this.jungleWidth = calculateJungleWidth(width, jungleRatio);
        this.jungleHeight = calculateJungleHeight(height, jungleRatio);
        this.jungleLowerLeftCorner = getJungleLowerLeftCorner(width, height, jungleWidth, jungleHeight);
        this.jungleUpperRightCorner = getJungleUpperRightCorner(width, height, jungleWidth, jungleHeight);
        this.jungleSurfaceArea = jungleWidth * jungleHeight;
        this.steppeSurfaceArea = width * height - jungleSurfaceArea;
        this.numberOfAnimals = 0;
        this.numberOfGrassesInSteppe = 0;
        this.numberOfGrassesInJungle = 0;
        this.freePositionsInJungle = new Vector2d[jungleSurfaceArea];
        this.freePositionsInSteppe = new Vector2d[steppeSurfaceArea];
        fillFreePositionsInJungle();
        fillFreePositionsInSteppe();
        this.numberOfFreePositionsInJungle = jungleSurfaceArea;
        this.numberOfFreePositionsInSteppe = steppeSurfaceArea;
        this.numberOfAnimalsWithDominatingGenotype = 0;
        this.energyOfLivingAnimals = 0;
        this.day = 0;
        this.lifespanOfDeadAnimals = 0;
        this.dayObservers = Collections.synchronizedList(new ArrayList<>());
        this.numberOfStartingAnimals = numberOfStartingAnimals;
        addStartingAnimalsToMap();
    }

    private void addStartingAnimalsToMap() {
        for(int i = 0; i < numberOfStartingAnimals; i++){
            Vector2d position = this.getRandomPosition();
            Animal animal = new Animal(this, position, 0);
            this.manuallyPlaceNewAnimal(animal);
        }
    }

    private void fillFreePositionsInSteppe(){
        int counter = 0;
        for(int y = this.lowerLeftCorner.getY(); y <= this.upperRightCorner.getY(); y++){
            for(int x = this.lowerLeftCorner.getX(); x <= this.upperRightCorner.getX(); x++){
                Vector2d position = new Vector2d(x,y);
                if(this.positionIsInJungle(position)) continue;
                freePositionsInSteppe[counter] = position;
                counter++;
            }
        }
    }

    private void fillFreePositionsInJungle() {
        int counter = 0;
        for(int y = this.jungleLowerLeftCorner.getY(); y <= this.jungleUpperRightCorner.getY(); y++){
            for(int x = this.jungleLowerLeftCorner.getX(); x <= this.jungleUpperRightCorner.getX(); x++){
                freePositionsInJungle[counter] = new Vector2d(x,y);
                counter++;
            }
        }
    }

    private Vector2d getJungleUpperRightCorner(int width, int height, int jungleWidth, int jungleHeight) {
        int widthMarginSize = this.width - jungleWidth;
        int leftMarginSize = widthMarginSize / 2;

        int heightMarginSize = this.height - jungleHeight;
        int bottomMarginSize = heightMarginSize / 2;

        return new Vector2d(leftMarginSize + jungleWidth - 1, bottomMarginSize + jungleHeight - 1);
    }

    private Vector2d getJungleLowerLeftCorner(int width, int height, int jungleWidth, int jungleHeight) {
        int widthMarginSize = this.width - jungleWidth;
        int leftMarginSize = widthMarginSize / 2;

        int heightMarginSize = this.height - jungleHeight;
        int bottomMarginSize = heightMarginSize / 2;

        return new Vector2d(leftMarginSize, bottomMarginSize);
    }

    private int calculateJungleHeight(int height, double jungleRatio) {
        return (int) Math.round(height * jungleRatio);
    }

    private int calculateJungleWidth(int width, double jungleRatio) {
        return (int) Math.round(width * jungleRatio);
    }

    public int getJungleWidth() {
        return jungleWidth;
    }

    public int getJungleHeight() {
        return jungleHeight;
    }

    private MapSection getSectionAtPosition(Vector2d position){
        MapSection sectionAtPosition;
        if (this.positionSectionMap.containsKey(position)){
            sectionAtPosition = positionSectionMap.get(position);
        }
        else{
            sectionAtPosition = new MapSection(this, position);
            positionSectionMap.put(position, sectionAtPosition);
        }
        return sectionAtPosition;
    }
    public boolean manuallyPlaceNewAnimal(Animal animal){
        Vector2d destination = animal.getPosition();
        int[] genotype = animal.getGenotype();

        if (canMoveTo(destination)) {
            MapSection destinationSection = getSectionAtPosition(destination);
            destinationSection.placeAnimal(animal);
            incrementNumberOfAnimals();
            incrementNumberOfGenotypeOwners(genotype);
            return true;
        }
        else {
            throw new IllegalArgumentException("Animal can not be placed on: " + destination);
        }
    }


    public Object objectAt(Vector2d position){
        if (this.positionSectionMap.containsKey(position)){
            MapSection positionSection = positionSectionMap.get(position);
            return positionSection.objectAt();
        }
        else{
           return null;
        }
    }


//    public String toString(){
//        return drawer.draw(lowerLeftCorner, upperRightCorner);
//    }

    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition){
        MapSection oldPositionSection = this.getSectionAtPosition(oldPosition);
        oldPositionSection.removeAnimal(animal);
        MapSection newPositionSection = this.getSectionAtPosition(newPosition);
        newPositionSection.placeAnimal(animal);
    }

    public Vector2d getUpperRightCorner(){
        return this.upperRightCorner;
    }

    public Vector2d getLowerLeftCorner(){
        return this.lowerLeftCorner;
    }

    public Map<Vector2d, MapSection> getPositionSectionMap(){
        return this.positionSectionMap;
    }

    public int getStartEnergy() {
        return this.startEnergy;
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public int getPlantEnergy() {
        return plantEnergy;
    }

    public double getJungleRatio() {
        return jungleRatio;
    }

    public void incrementNumberOfAnimals(){
        this.numberOfAnimals += 1;
    }

    public void decrementNumberOfAnimals(){
        this.numberOfAnimals -=1;
    }

    private boolean positionIsInJungle(Vector2d position){
        return (position.precedes(jungleUpperRightCorner) && position.follows(jungleLowerLeftCorner));
    }

    public void grassEaten(Vector2d position){
        if(this.positionIsInJungle(position)){
            numberOfGrassesInJungle -= 1;
        }
        else {
            numberOfGrassesInSteppe -= 1;
        }
    }
    public void grassSpawned(Vector2d position){
        if(this.positionIsInJungle(position)){
            numberOfGrassesInJungle += 1;
        }
        else {
            numberOfGrassesInSteppe += 1;
        }
    }

//    public void spawnGrassInJungle(){
//        Vector2d grassPosition = this.getFreePositionInJungle();
//        if (grassPosition == null) {
//            throw new IllegalActionException("There is no place for a new grass in Jungle!!!");
//        }
//        MapSection grassSection = this.getSectionAtPosition(grassPosition);
//        grassSection.spawnGrass();
//    }
//
//    public void spawnGrassInSteppe(){
//        Vector2d grassPosition = this.getFreePositionInSteppe();
//        if (grassPosition == null) {
//            throw new IllegalActionException("There is no place for a new grass in Steppe!!!");
//        }
//        MapSection grassSection = this.getSectionAtPosition(grassPosition);
//        grassSection.spawnGrass();
//    }

//    private Vector2d getFreePositionInSteppe() {
//        if (numberOfFreePositionsInSteppe == 0) return null;
//        int positionID = getRandomNumberFrom0ToN(numberOfFreePositionsInSteppe);
//        Vector2d freePosition = freePositionsInSteppe[positionID];
//        freePositionsInSteppe[positionID] = freePositionsInSteppe[numberOfFreePositionsInSteppe - 1];
//        numberOfFreePositionsInSteppe -= 1;
//        return freePosition;
//    }
//
//    private Vector2d getFreePositionInJungle() {
//        if (numberOfFreePositionsInJungle == 0) return null;
//        int positionID = getRandomNumberFrom0ToN(numberOfFreePositionsInJungle);
//        Vector2d freePosition = freePositionsInJungle[positionID];
//        freePositionsInJungle[positionID] = freePositionsInJungle[numberOfFreePositionsInJungle - 1];
//        numberOfFreePositionsInJungle -= 1;
//        return freePosition;
//    }

    private int getRandomNumberFrom0ToN(int n) {
        return ((int)(Math.random() * 100)) % n;
    }

    public void animalDied(Animal animal){
        this.lifespanOfDeadAnimals += animal.getDeathDay() - animal.getBirthDay();
        this.numberOfChildrenOfLivingAnimals -= animal.getNumberOfChildren();
        this.decrementNumberOfGenotypeOwners(animal.getGenotype());
        this.decrementNumberOfAnimals();
        this.incrementNumberOfDeadAnimals();
        this.changeNumberOfChildrenOfLivingAnimals(-animal.getNumberOfChildren());
    }

    private void changeNumberOfChildrenOfLivingAnimals(int i) {
        this.numberOfChildrenOfLivingAnimals += i;
    }

    private void incrementNumberOfDeadAnimals() {
        this.numberOfDeadAnimals += 1;
    }

    private void decrementNumberOfGenotypeOwners(int[] genotype) {
        if(! this.genotypeNumberOfOwnersMap.containsKey(genotype)) {
            throw new IllegalActionException("No such genotype to decrement number of owners");
        }
        if(genotypeNumberOfOwnersMap.get(genotype) == 0) {
            throw new IllegalActionException("No such genotype to decrement number of owners");
        }
        this.genotypeNumberOfOwnersMap.put(genotype, genotypeNumberOfOwnersMap.get(genotype) - 1);
        if(genotypeNumberOfOwnersMap.get(genotype) + 1 == numberOfAnimalsWithDominatingGenotype){
            determineDominatingGenotype();
        }
    }

    private void determineDominatingGenotype() {
        for(Map.Entry<int[], Integer> entry : genotypeNumberOfOwnersMap.entrySet()) {
            int[] genotype = entry.getKey();
            Integer numberOfOwners = entry.getValue();

            if(numberOfOwners > numberOfAnimalsWithDominatingGenotype){
                dominatingGenotype = genotype;
                numberOfAnimalsWithDominatingGenotype = numberOfOwners;
            }
        }
    }

    public void animalWasBorn(Animal animal){
        animal.addPositionChangeObserver(this);
        this.incrementNumberOfGenotypeOwners(animal.getGenotype());
        this.incrementNumberOfAnimals();
        this.changeNumberOfChildrenOfLivingAnimals(2);
    }


    private void incrementNumberOfGenotypeOwners(int[] genotype) {
        this.genotypeNumberOfOwnersMap.put(genotype, genotypeNumberOfOwnersMap.getOrDefault(genotype,0) + 1);
        int numberOfAnimalsWithGenotype = this.genotypeNumberOfOwnersMap.get(genotype);
        if(numberOfAnimalsWithGenotype > numberOfAnimalsWithDominatingGenotype){
            numberOfAnimalsWithDominatingGenotype = numberOfAnimalsWithGenotype;
            dominatingGenotype = genotype;
        }
    }

    public void energyChanged(int difference){
        this.energyOfLivingAnimals += difference;
    }

    public long getDay() {
        return this.day;
    }

    public int getNumberOfAnimals(){
        return this.numberOfAnimals;
    }

    public int getNumberOfGrasses(){
        return this.numberOfGrassesInJungle + this.numberOfGrassesInSteppe;
    }

    public int[] getDominatingGenotype(){
        return this.dominatingGenotype;
    }

    public float getAverageEnergyOfLivingAnimals(){
        return energyOfLivingAnimals/(float)numberOfAnimals;
    }

    public float getAverageLifespanOfDeadAnimals(){
        return lifespanOfDeadAnimals / (float)numberOfDeadAnimals;
    }

    public float getAverageNumberOfChildrenOfLivingAnimals(){
        return numberOfChildrenOfLivingAnimals / (float)numberOfAnimals;
    }

    public List<Animal> getAnimalsWithDominatingGenotype(){
        List<Animal> animalsWithDominatingGenotype = new ArrayList<>();
        for(MapSection section: this.positionSectionMap.values()){
            animalsWithDominatingGenotype.addAll(section.getAnimalsWithGenotype(this.dominatingGenotype));
        }
        return animalsWithDominatingGenotype;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean canMoveTo(Vector2d position) {
        return position.precedes(upperRightCorner) && position.follows(lowerLeftCorner);
    }

    public abstract boolean bordersRunaround();

    public Vector2d getRandomPosition() {
        int x = getRandomNumberFrom0ToN(width);
        int y = getRandomNumberFrom0ToN(height);
        return new Vector2d(x,y);
    }

    public void removeDeadAnimals() {
        for(MapSection section: this.positionSectionMap.values()){
            section.removeDeadAnimals();
        }
    }

    public void moveAnimals() {
//        for(MapSection section: this.positionSectionMap.values()){
//            section.moveAnimals();
//
//        }
        List<Animal> livingAnimals = getLivingAnimals();
        for(Animal animal: livingAnimals){
            animal.move();
        }

    }

    private List<Animal> getLivingAnimals() {
        List<Animal> livingAnimals = new ArrayList<>();
        for(MapSection section: this.positionSectionMap.values()) {
            livingAnimals.addAll(section.getLivingAnimals());
        }
        return livingAnimals;
    }

    public void grassEating() {
        for(MapSection section: this.positionSectionMap.values()){
            section.grassEating();
        }
    }

    public void reproduction() {
        for(MapSection section: this.positionSectionMap.values()){
            section.reproduction();
        }
    }

    public void dayPassed() {
        this.day += 1;
        for(IDayObserver observer: dayObservers){
            observer.dayPassed(this);
        }
    }

    public void addDayObserver(IDayObserver observer) {
        this.dayObservers.add(observer);
    }


    public void spawnGrasses() {
        List<Vector2d> freePositionsInJungle = new ArrayList<>();
        List<Vector2d> freePositionsInSteppe = new ArrayList<>();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < width; y++){
                Vector2d position = new Vector2d(x,y);
                if(this.positionIsInJungle(position)){
                    if(positionSectionMap.containsKey(position)){
                        MapSection mapSection = positionSectionMap.get(position);
                        if(mapSection.containsGrass() || mapSection.containsAnimal()) continue;
                    }
                    freePositionsInJungle.add(position);
                }
                else{
                    if(positionSectionMap.containsKey(position)){
                        MapSection mapSection = positionSectionMap.get(position);
                        if(mapSection.containsGrass() || mapSection.containsAnimal()) continue;
                    }
                    freePositionsInSteppe.add(position);
                }
            }
        }

        int numberOfFreePositionsInSteppe = freePositionsInSteppe.size();
        int numberOfFreePositionsInJungle = freePositionsInJungle.size();

        Vector2d positionInJungleToPlaceGrass = freePositionsInJungle.get(getRandomNumberFrom0ToN(numberOfFreePositionsInJungle));
        Vector2d positionInSteppeToPlaceGrass = freePositionsInSteppe.get(getRandomNumberFrom0ToN(numberOfFreePositionsInSteppe));

        getSectionAtPosition(positionInJungleToPlaceGrass).spawnGrass();
        getSectionAtPosition(positionInSteppeToPlaceGrass).spawnGrass();
    }
}
