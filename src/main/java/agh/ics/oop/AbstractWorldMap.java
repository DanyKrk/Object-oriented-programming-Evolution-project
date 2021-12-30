package agh.ics.oop;

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
    private int numberOfFreePositionsInJungle;
    private int numberOfFreePositionsInSteppe;
    private int numberOfAnimals;
    protected Vector2d upperRightCorner;
    protected Vector2d lowerLeftCorner;
    private Vector2d jungleLowerLeftCorner;
    private Vector2d jungleUpperRightCorner;
    protected Map<Vector2d, MapSection> positionSectionMap;
    private Map<GenotypeMapKey, Integer> genotypeNumberOfOwnersMap;
    private int[] dominatingGenotype;
    private int numberOfAnimalsWithDominatingGenotype;
    private int energyOfLivingAnimals;
    private int numberOfDeadAnimals;
    private int day;
    private int lifespanOfDeadAnimals;
    private int numberOfChildrenOfLivingAnimals;
    private List<IDayObserver> dayObservers;
    private int numberOfStartingAnimals;
    private ArrayList<Animal> animalsThatDiedInADay;
    private boolean readyForRun;


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
        this.jungleLowerLeftCorner = calculateJungleLowerLeftCorner(width, height, jungleWidth, jungleHeight);
        this.jungleUpperRightCorner = calculateJungleUpperRightCorner(width, height, jungleWidth, jungleHeight);
        this.jungleSurfaceArea = jungleWidth * jungleHeight;
        this.steppeSurfaceArea = width * height - jungleSurfaceArea;
        this.numberOfAnimals = 0;
        this.numberOfGrassesInSteppe = 0;
        this.numberOfGrassesInJungle = 0;
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
            this.manuallyPlaceNewAnimal(position);
        }
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
    public boolean manuallyPlaceNewAnimal(Vector2d destination){
        Animal animal = new Animal(this, destination, this.getDay(), startEnergy);
        int[] genotype = animal.getGenotype();

        if (canMoveTo(destination)) {
            MapSection destinationSection = getSectionAtPosition(destination);
            destinationSection.placeAnimal(animal);
            this.numberOfAnimals += 1;
//            incrementNumberOfGenotypeOwners(genotype);
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


    public boolean canMoveTo(Vector2d position) {
        return position.precedes(upperRightCorner) && position.follows(lowerLeftCorner);
    }

    private Vector2d calculateJungleUpperRightCorner(int width, int height, int jungleWidth, int jungleHeight) {
        int widthMarginSize = this.width - jungleWidth;
        int leftMarginSize = widthMarginSize / 2;

        int heightMarginSize = this.height - jungleHeight;
        int bottomMarginSize = heightMarginSize / 2;

        return new Vector2d(leftMarginSize + jungleWidth - 1, bottomMarginSize + jungleHeight - 1);
    }

    private Vector2d calculateJungleLowerLeftCorner(int width, int height, int jungleWidth, int jungleHeight) {
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

    private void determineDominatingGenotype() {
        genotypeNumberOfOwnersMap = new HashMap<>();
        numberOfAnimalsWithDominatingGenotype = 0;
        for(MapSection section: this.positionSectionMap.values()){
            if(!section.containsAnimal()) continue;
            for(Animal animal : section.getLivingAnimals()){
                GenotypeMapKey key = new GenotypeMapKey(animal.getGenotype());
                genotypeNumberOfOwnersMap.put(key, genotypeNumberOfOwnersMap.getOrDefault(key,0) + 1);
            }
        }

        for(Map.Entry<GenotypeMapKey, Integer> entry : genotypeNumberOfOwnersMap.entrySet()) {
            int[] genotype = entry.getKey().getGenotype();
            Integer numberOfOwners = entry.getValue();

            if(numberOfOwners >= numberOfAnimalsWithDominatingGenotype){
                dominatingGenotype = genotype;
                numberOfAnimalsWithDominatingGenotype = numberOfOwners;
            }
        }
    }

    public Vector2d getRandomPosition() {
        int x = getRandomNumberFrom0ToN(width);
        int y = getRandomNumberFrom0ToN(height);
        return new Vector2d(x,y);
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

    public int getJungleWidth() {
        return jungleWidth;
    }

    public int getJungleHeight() {
        return jungleHeight;
    }

    private int getRandomNumberFrom0ToN(int n) {
        return ((int)(Math.random() * 100000)) % n;
    }

    public int getDay() {
        return this.day;
    }

    public int getNumberOfAnimals(){
        return this.numberOfAnimals;
    }

    public int getNumberOfGrasses(){
        return this.numberOfGrassesInJungle + this.numberOfGrassesInSteppe;
    }

    public int[] getDominatingGenotype(){
        this.determineDominatingGenotype();
        return this.dominatingGenotype;
    }

    public float getAverageEnergyOfLivingAnimals(){
        return energyOfLivingAnimals/(float)numberOfAnimals;
    }

    public float getAverageLifespanOfDeadAnimals(){
        if(numberOfDeadAnimals == 0) return 0;
        return lifespanOfDeadAnimals / (float)numberOfDeadAnimals;
    }

    public float getAverageNumberOfChildrenOfLivingAnimals(){
        return numberOfChildrenOfLivingAnimals / (float)numberOfAnimals;
    }

    private List<Animal> getLivingAnimals() {
        List<Animal> livingAnimals = new ArrayList<>();
        for(MapSection section: this.positionSectionMap.values()) {
            livingAnimals.addAll(section.getLivingAnimals());
        }
        return livingAnimals;
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

    public void setReadyForRun(){
        this.readyForRun = true;
    }

    public void setNotReadyForRun(){
        this.readyForRun = false;
    }

//    private void incrementNumberOfGenotypeOwners(int[] genotype) {
//        GenotypeMapKey key = new GenotypeMapKey(genotype);
//        this.genotypeNumberOfOwnersMap.put(key, genotypeNumberOfOwnersMap.getOrDefault(key,0) + 1);
//        int numberOfAnimalsWithGenotype = this.genotypeNumberOfOwnersMap.get(key);
//        if(numberOfAnimalsWithGenotype > numberOfAnimalsWithDominatingGenotype){
//            numberOfAnimalsWithDominatingGenotype = numberOfAnimalsWithGenotype;
//            dominatingGenotype = genotype;
//        }
//    }

//    private void decrementNumberOfGenotypeOwners(int[] genotype) {
//        GenotypeMapKey key = new GenotypeMapKey(genotype);
//        if(! this.genotypeNumberOfOwnersMap.containsKey(key)) {
//            throw new IllegalActionException("No such genotype to decrement number of owners");
//        }
//        if(genotypeNumberOfOwnersMap.get(key) == 0) {
//            throw new IllegalActionException("No such genotype to decrement number of owners");
//        }
//        this.genotypeNumberOfOwnersMap.put(key, genotypeNumberOfOwnersMap.get(key) - 1);
//        if(genotypeNumberOfOwnersMap.get(key) + 1 == numberOfAnimalsWithDominatingGenotype){
//            determineDominatingGenotype();
//        }
//        if(genotypeNumberOfOwnersMap.get(key) == 0) {
//            genotypeNumberOfOwnersMap.remove(key);
//        }
//    }

    public boolean isReadyForRun(){
        return this.readyForRun;
    }

    public boolean positionIsInJungle(Vector2d position){
        return (position.precedes(jungleUpperRightCorner) && position.follows(jungleLowerLeftCorner));
    }

    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition){
        MapSection oldPositionSection = this.getSectionAtPosition(oldPosition);
        oldPositionSection.removeAnimal(animal);
        MapSection newPositionSection = this.getSectionAtPosition(newPosition);
        newPositionSection.placeAnimal(animal);
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

    public void animalWasBorn(Animal animal){
//        this.incrementNumberOfGenotypeOwners(animal.getGenotype());
        this.numberOfAnimals += 1;
        this.numberOfChildrenOfLivingAnimals += 2;
    }

    public void animalDied(Animal animal){
        this.animalsThatDiedInADay.add(animal);
        this.lifespanOfDeadAnimals += animal.getDeathDay() - animal.getBirthDay();
//        this.decrementNumberOfGenotypeOwners(animal.getGenotype());
        this.numberOfAnimals -= 1;
        this.numberOfDeadAnimals += 1;
        this.energyOfLivingAnimals -= animal.getEnergy();

    }

    public void energyChanged(int difference){
        this.energyOfLivingAnimals += difference;
    }

    public void dayPassed() {
        this.day += 1;
        for(IDayObserver observer: dayObservers){
            observer.dayPassed(this);
        }
    }

    public void removeDeadAnimals() {
        animalsThatDiedInADay = new ArrayList<>();
        for(MapSection section: this.positionSectionMap.values()){
            section.removeDeadAnimals();
        }
        for(Animal animal: animalsThatDiedInADay){
            if(animal.getParent1() != null) {
                if (animal.getParent1().isAlive()) {
                    this.numberOfChildrenOfLivingAnimals -= 1;
                }
            }
            if(animal.getParent2() != null) {
                if (animal.getParent2().isAlive()) {
                    this.numberOfChildrenOfLivingAnimals -= 1;
                }
            }
        }
    }

    public void magicallyRemoveDeadAnimals() {
        animalsThatDiedInADay = new ArrayList<>();
        for(MapSection section: this.positionSectionMap.values()){
            section.removeDeadAnimals();
        }
        for(Animal animal: animalsThatDiedInADay){
            if(animal.getParent1() != null) {
                if (animal.getParent1().isAlive()) {
                    this.numberOfChildrenOfLivingAnimals -= 1;
                }
            }
            if(animal.getParent2() != null) {
                if (animal.getParent2().isAlive()) {
                    this.numberOfChildrenOfLivingAnimals -= 1;
                }
            }
        }
    }

    public void moveAnimals() {
        List<Animal> livingAnimals = getLivingAnimals();
        for(Animal animal: livingAnimals){
            animal.move();
        }

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

    public void spawnGrasses() {
        List<Vector2d> freePositionsInJungle = new ArrayList<>();
        List<Vector2d> freePositionsInSteppe = new ArrayList<>();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
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
        if(numberOfFreePositionsInSteppe > 0){
            Vector2d positionInSteppeToPlaceGrass = freePositionsInSteppe.get(getRandomNumberFrom0ToN(numberOfFreePositionsInSteppe));
            getSectionAtPosition(positionInSteppeToPlaceGrass).spawnGrass();
        }

        int numberOfFreePositionsInJungle = freePositionsInJungle.size();
        if(numberOfFreePositionsInJungle > 0) {
            Vector2d positionInJungleToPlaceGrass = freePositionsInJungle.get(getRandomNumberFrom0ToN(numberOfFreePositionsInJungle));
            getSectionAtPosition(positionInJungleToPlaceGrass).spawnGrass();
        }

    }

    public void addDayObserver(IDayObserver observer) {
        this.dayObservers.add(observer);
    }

}
