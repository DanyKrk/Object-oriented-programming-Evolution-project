package agh.ics.oop;

import java.util.*;

public class MapSection {
    private AbstractWorldMap map;
    private Vector2d position;
//    private SortedSet<Animal> animalsSortedByEnergyDescending;
    private Grass grass;
    private List<IPopulationOfAnimalsObserver> populationOfAnimalsObservers;
    private List<IGrassExsistenceObserver> grassExistenceObservers;
    private List<Animal> animals;
    private boolean isInJungle;

    public MapSection(AbstractWorldMap map, Vector2d position){
        populationOfAnimalsObservers = Collections.synchronizedList(new ArrayList<>());
        grassExistenceObservers = Collections.synchronizedList(new ArrayList<>());
        this.map = map;
        this.position = position;
        this.animals = Collections.synchronizedList(new LinkedList<>());
//        this.animalsSortedByEnergyDescending = Collections.synchronizedSortedSet(new TreeSet<>(new AnimalReversedComparator()));
//        this.animalsSortedByEnergyDescending = Collections.synchronizedSortedSet(new TreeSet<>());
        this.grass = null;
        this.addPopulationOfAnimalsObserver(map);
        this.addGrassExistenceObserver(map);
        this.isInJungle = map.positionIsInJungle(position);
    }

    private void addGrassExistenceObserver(AbstractWorldMap map) {
        this.grassExistenceObservers.add(map);
        if(grassExistenceObservers.size() > 1){
            System.out.println("too many population observers");
        }
    }

    public boolean containsGrass() {
        return this.grass != null;
    }

    public void moveAnimals(){
//        Iterator<Animal> itr = animalsSortedByEnergyDescending.iterator();
//        while (itr.hasNext()) {
//            itr.next().move();
//        }
        Iterator<Animal> itr = animals.iterator();
        while (itr.hasNext()) {
            itr.next().move();
        }
    }

    public void removeDeadAnimals(){
//        long day = this.map.getDay();
//        Iterator<Animal> itr = animalsSortedByEnergyDescending.iterator();
//        while (itr.hasNext()) {
//            Animal animal = itr.next();
//            if(animal.getEnergy() < this.map.getMoveEnergy()){
//                this.decrementNumberOfAnimals();
//                animal.setDeathDay(day);
//                animalDied(animal, day);
//                itr.remove();
//            }
//        }
        int day = this.map.getDay();
        Iterator<Animal> itr = animals.iterator();
        while (itr.hasNext()) {
            Animal animal = itr.next();
            if(animal.getEnergy() < this.map.getMoveEnergy()){
                animal.die(day);
                itr.remove();
                animalDied(animal, day);
            }
        }
    }

    public void magicallyRemoveDeadAnimals(){
//        long day = this.map.getDay();
//        Iterator<Animal> itr = animalsSortedByEnergyDescending.iterator();
//        while (itr.hasNext()) {
//            Animal animal = itr.next();
//            if(animal.getEnergy() < this.map.getMoveEnergy()){
//                this.decrementNumberOfAnimals();
//                animal.setDeathDay(day);
//                animalDied(animal, day);
//                itr.remove();
//            }
//        }
        int day = this.map.getDay();
        Iterator<Animal> itr = animals.iterator();
        while (itr.hasNext()) {
            Animal animal = itr.next();
            if(animal.getEnergy() < this.map.getMoveEnergy()){
                animal.die(day);
                itr.remove();
                animalDied(animal, day);
                if(map.getNumberOfAnimals() == 5) {
                    map.useMagic();
                    return;
                }
            }
        }
    }

    private void animalDied(Animal animal, long day) {
        for (IPopulationOfAnimalsObserver observer : populationOfAnimalsObservers) {
            observer.animalDied(animal);
        }
    }

    private void animalWasBorn(Animal animal) {
        for (IPopulationOfAnimalsObserver observer : populationOfAnimalsObservers) {
            observer.animalWasBorn(animal);
        }
    }

    public void reproduction(){
//        if (this.animalsSortedByEnergyDescending.size() < 2) return;
//
//        Animal parent1 = animalsSortedByEnergyDescending.first();
//        animalsSortedByEnergyDescending.remove(parent1);
//        Animal parent2 = animalsSortedByEnergyDescending.first();
//        animalsSortedByEnergyDescending.remove(parent2);
//
//        if (parent1.readyForReproduction() && parent2.readyForReproduction()){
//            parent1.incrementNumberOfChildren();
//            parent2.incrementNumberOfChildren();
//            Animal child = new Animal(this.map, this.position, this.map.getDay());
//            child.setGenesBasedOnParents(parent1, parent2);
//            child.setEnergy(parent1.extractEnergyForChild() + parent2.extractEnergyForChild());
//            if(parent1.isTracked()){
//                linkTrackedAncestorWithDescendant(parent1, child);
//            }
//            if(parent2.isTracked()){
//                linkTrackedAncestorWithDescendant(parent2, child);
//            }
//            if(parent1.notifiesAncestor()){
//                linkTrackedAncestorWithDescendant(parent1.getTrackedAncestor(), child);
//            }
//            if(parent2.notifiesAncestor()){
//                linkTrackedAncestorWithDescendant(parent2.getTrackedAncestor(), child);
//            }
//            this.placeAnimal(child);
//            this.incrementNumberOfAnimals();
//            animalWasBorn(child);
//        }
//        animalsSortedByEnergyDescending.add(parent1);
//        animalsSortedByEnergyDescending.add(parent2);
        if (this.animals.size() < 2) return;

        Animal parent1 = this.getAnimalWithHighestEnergy();
        animals.remove(parent1);
        Animal parent2 = this.getAnimalWithHighestEnergy();
        animals.remove(parent2);

        if (parent1.readyForReproduction() && parent2.readyForReproduction()){
            parent1.incrementNumberOfChildren();
            parent2.incrementNumberOfChildren();
            int childEnergy = parent1.extractEnergyForChild() + parent2.extractEnergyForChild();
            Animal child = new Animal(this.map, this.position, this.map.getDay(), childEnergy, parent1, parent2);
            if(parent1.isTracked()){
                linkTrackedAncestorWithDescendant(parent1, child);
            }
            if(parent2.isTracked()){
                linkTrackedAncestorWithDescendant(parent2, child);
            }
            if(parent1.notifiesAncestor()){
                linkTrackedAncestorWithDescendant(parent1.getTrackedAncestor(), child);
            }
            if(parent2.notifiesAncestor()){
                linkTrackedAncestorWithDescendant(parent2.getTrackedAncestor(), child);
            }
            this.placeAnimal(child);
            animalWasBorn(child);
        }
        animals.add(parent1);
        animals.add(parent2);
    }

    private Animal getAnimalWithHighestEnergy() {
        int maxEnergy = 0;
        Animal animalWithMaxEnergy = null;
        for (Animal animal:animals){
            if(animal.getEnergy() >= maxEnergy){
                maxEnergy = animal.getEnergy();
                animalWithMaxEnergy = animal;
            }
        }
        return animalWithMaxEnergy;
    }

    public Vector2d getPosition(){
        return this.position;
    }
    private void linkTrackedAncestorWithDescendant(Animal trackedAncestor, Animal descendant) {
        trackedAncestor.addDescendant(descendant);
        descendant.startNotifyingTrackedAncestor(trackedAncestor);
    }

    public void grassEating(){
//        if (this.containsGrass && animalsSortedByEnergyDescending.size() > 0){
//            List<Animal> eatingAnimals = new ArrayList<Animal>();
//            Animal firstAnimal = animalsSortedByEnergyDescending.first();
//            animalsSortedByEnergyDescending.remove(firstAnimal);
//            eatingAnimals.add(firstAnimal);
//            int maxEnergy = firstAnimal.getEnergy();
//
//            Iterator<Animal> itr = animalsSortedByEnergyDescending.iterator();
//            while (itr.hasNext()) {
//                Animal analyzedAnimal = itr.next();
//                if(analyzedAnimal.getEnergy() == maxEnergy) {
//                    eatingAnimals.add(analyzedAnimal);
//                    itr.remove();
//                    continue;
//                }
//                break;
//            }
//
//            int plantEnergy = this.map.getPlantEnergy();
//            int numberOfEatingAnimals = eatingAnimals.size();
//            int numberOfAdditionalPoints = plantEnergy % numberOfEatingAnimals;
//            int universalPointsFromEating = plantEnergy/numberOfEatingAnimals;
//
//            for(Animal animal:eatingAnimals){
//                animal.setEnergy(maxEnergy + universalPointsFromEating);
//            }
//            for(int i = 0; i<numberOfAdditionalPoints; i++){
//                Animal animal = eatingAnimals.get(i);
//                animal.setEnergy(animal.getEnergy() + 1);
//            }
//
//            grassEaten(this.position);
//        }
        if (this.containsGrass() && this.containsAnimal()){
            List<Animal> eatingAnimals = new ArrayList<Animal>();
            Animal firstAnimal = getAnimalWithHighestEnergy();
            animals.remove(firstAnimal);
            eatingAnimals.add(firstAnimal);
            int maxEnergy = firstAnimal.getEnergy();

            Iterator<Animal> itr = animals.iterator();
            while (itr.hasNext()) {
                Animal analyzedAnimal = itr.next();
                if(analyzedAnimal.getEnergy() == maxEnergy) {
                    eatingAnimals.add(analyzedAnimal);
                    itr.remove();
                    continue;
                }
                break;
            }

            int plantEnergy = this.map.getPlantEnergy();
            int numberOfEatingAnimals = eatingAnimals.size();
            int numberOfAdditionalPoints = plantEnergy % numberOfEatingAnimals;
            int universalPointsFromEating = plantEnergy/numberOfEatingAnimals;

            for(Animal animal:eatingAnimals){
                animal.setEnergy(maxEnergy + universalPointsFromEating);
            }
            for(int i = 0; i<numberOfAdditionalPoints; i++){
                Animal animal = eatingAnimals.get(i);
                animal.setEnergy(animal.getEnergy() + 1);
            }
            animals.add(firstAnimal);
            grassEaten(this.position);
        }
    }

    public void placeAnimal(Animal animal) {
//        boolean animalPlaced = this.animalsSortedByEnergyDescending.add(animal);
//        if (animalPlaced) this.incrementNumberOfAnimals();
//        else throw new IllegalActionException("Animal cannot be placed on" + this.position);
        if(this.animals.add(animal)) return;
        else throw new IllegalActionException("Animal cannot be placed on" + this.position);
    }

    public Object objectAt() {
//        if(animalsSortedByEnergyDescending.size() > 0){
//            return animalsSortedByEnergyDescending.first();
//        }
//        else return this.grass;
        if(animals.size() > 0){
            return getAnimalWithHighestEnergy();
        }
        else return this.grass;
    }

    public void removeAnimal(Animal animal) {
//        boolean animalRemoved = this.animalsSortedByEnergyDescending.remove(animal);
//        if (animalRemoved) this.decrementNumberOfAnimals();
//        else throw new IllegalActionException("There is no Animal that you want to remove from" + this.position);
        if(this.animals.remove(animal)) return;
        else throw new IllegalActionException("There is no Animal that you want to remove from" + this.position);
    }

    public void addPopulationOfAnimalsObserver(IPopulationOfAnimalsObserver observer){
        this.populationOfAnimalsObservers.add(observer);
    }

    public void removeNumberOfAnimalsObserver(IPopulationOfAnimalsObserver observer){
        this.populationOfAnimalsObservers.remove(observer);
    }

    private void grassEaten(Vector2d position){
        this.grass = null;
        for(IGrassExsistenceObserver observer: grassExistenceObservers){
            observer.grassEaten(position);
        }
    }

    public void spawnGrass(){
        if(this.containsGrass() || this.containsAnimal()) {
            throw new IllegalActionException("Grass cannot be spawned at" + this.position);
        }
        this.grass = new Grass(this.position);
        for(IGrassExsistenceObserver observer: grassExistenceObservers){
            observer.grassSpawned(this.position);
        }
    }

    public List<Animal> getAnimalsWithGenotype(int[] dominatingGenotype) {
//        List<Animal> animalsWithGenotype = new ArrayList<>();
//        Iterator<Animal> itr = this.animalsSortedByEnergyDescending.iterator();
//        while(itr.hasNext()){
//            Animal animal = itr.next();
//            if(animal.getGenotype() == dominatingGenotype){
//                animalsWithGenotype.add(animal);
//            }
//        }
//        return animalsWithGenotype;
        List<Animal> animalsWithGenotype = new ArrayList<>();
        Iterator<Animal> itr = this.animals.iterator();
        while(itr.hasNext()){
            Animal animal = itr.next();
            if(Arrays.equals(animal.getGenotype(), dominatingGenotype)){
                animalsWithGenotype.add(animal);
            }
        }
        return animalsWithGenotype;
    }

    public List<Animal> getLivingAnimals() {
//        List<Animal> livingAnimals = new ArrayList<>();
//        Iterator<Animal> itr = this.animalsSortedByEnergyDescending.iterator();
//        while(itr.hasNext()){
//            Animal animal = itr.next();
//            livingAnimals.add(animal);
//        }
//        return livingAnimals;
        List<Animal> livingAnimals = new ArrayList<>();
        Iterator<Animal> itr = this.animals.iterator();
        while(itr.hasNext()){
            Animal animal = itr.next();
            livingAnimals.add(animal);
        }
        return livingAnimals;
    }

    public int getNumberOfAnimals(){
        return this.animals.size();
    }

    public boolean containsAnimal() {
     return this.animals.size() > 0;
    }

    public boolean isInJungle(){
        return this.isInJungle;
    }
}
