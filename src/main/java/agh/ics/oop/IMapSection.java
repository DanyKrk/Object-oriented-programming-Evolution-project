package agh.ics.oop;

import java.util.*;

public class IMapSection {
    private AbstractWorldMap map;
    private Vector2d position;
    private SortedSet<Animal> animalsSortedByEnergyDescending;
    private Grass grass;
    private boolean containsGrass;
    private boolean containsAnimal;
    private List<IPopulationOfAnimalsObserver> populationOfAnimalsObservers = Collections.synchronizedList(new ArrayList<>());
    private int numberOfAnimals;
    private List<IGrassExsistenceObserver> grassExistenceObservers = Collections.synchronizedList(new ArrayList<>());

    public IMapSection(AbstractWorldMap map, Vector2d position){
        this.map = map;
        this.position = position;
        this.animalsSortedByEnergyDescending = Collections.synchronizedSortedSet(new TreeSet<Animal>(new AnimalReversedComparator()));
        this.grass = null;
        this.containsGrass = false;
        this.numberOfAnimals = 0;
        this.containsAnimal = false;
        this.addPopulationOfAnimalsObserver(map);
        this.addGrassExistenceObserver(map);
    }

    private void addGrassExistenceObserver(AbstractWorldMap map) {
        this.grassExistenceObservers.add(map);
    }

    public boolean containsGrass() {
        return containsGrass;
    }

    public void moveAnimals(){
        Iterator<Animal> itr = animalsSortedByEnergyDescending.iterator();
        while (itr.hasNext()) {
            itr.next().move();
        }
    }

    public void removeDeadAnimals(){
        long day = this.map.getDay();
        Iterator<Animal> itr = animalsSortedByEnergyDescending.iterator();
        while (itr.hasNext()) {
            Animal animal = itr.next();
            if(animal.getEnergy() < this.map.getMoveEnergy()){
                this.decrementNumberOfAnimals();
                animal.setDeathDay(day);
                animalDied(animal, day);
                itr.remove();
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
        if (this.animalsSortedByEnergyDescending.size() < 2) return;

        Animal parent1 = animalsSortedByEnergyDescending.first();
        animalsSortedByEnergyDescending.remove(parent1);
        Animal parent2 = animalsSortedByEnergyDescending.first();
        animalsSortedByEnergyDescending.remove(parent2);

        if (parent1.readyForReproduction() && parent2.readyForReproduction()){
            parent1.incrementNumberOfChildren();
            parent2.incrementNumberOfChildren();
            Animal child = new Animal(this.map, this.position, this.map.getDay());
            child.setGenesBasedOnParents(parent1, parent2);
            child.setEnergy(parent1.extractEnergyForChild() + parent2.extractEnergyForChild());
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
            this.incrementNumberOfAnimals();
            animalWasBorn(child);
        }
        animalsSortedByEnergyDescending.add(parent1);
        animalsSortedByEnergyDescending.add(parent2);
    }

    private void linkTrackedAncestorWithDescendant(Animal trackedAncestor, Animal descendant) {
        trackedAncestor.addDescendant(descendant);
        descendant.startNotifyingTrackedAncestor(trackedAncestor);
    }

    public void grassEating(){
        if (this.containsGrass){
            List<Animal> eatingAnimals = new ArrayList<Animal>();
            Animal firstAnimal = animalsSortedByEnergyDescending.first();
            animalsSortedByEnergyDescending.remove(firstAnimal);
            eatingAnimals.add(firstAnimal);
            int maxEnergy = firstAnimal.getEnergy();

            Iterator<Animal> itr = animalsSortedByEnergyDescending.iterator();
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

            grassEaten(this.position);
        }
    }

    public void placeAnimal(Animal animal) {
        boolean animalPlaced = this.animalsSortedByEnergyDescending.add(animal);
        if (animalPlaced) this.incrementNumberOfAnimals();
        else throw new IllegalActionException("Animal cannot be placed on" + this.position);
    }

    public Object objectAt() {
        if(animalsSortedByEnergyDescending.size() > 0){
            return animalsSortedByEnergyDescending.first();
        }
        else return this.grass;
    }

    public void removeAnimal(Animal animal) {
        boolean animalRemoved = this.animalsSortedByEnergyDescending.remove(animal);
        if (animalRemoved) this.decrementNumberOfAnimals();
        else throw new IllegalActionException("There is no Animal that you want to remove from" + this.position);
    }

    public void addPopulationOfAnimalsObserver(IPopulationOfAnimalsObserver observer){
        this.populationOfAnimalsObservers.add(observer);
    }

    public void removeNumberOfAnimalsObserver(IPopulationOfAnimalsObserver observer){
        this.populationOfAnimalsObservers.remove(observer);
    }

    private void incrementNumberOfAnimals(){
        this.numberOfAnimals += 1;
        if (this.numberOfAnimals > 0) this.containsAnimal = true;
    }
    private void decrementNumberOfAnimals() {
        this.numberOfAnimals -= 1;
        if(this.numberOfAnimals == 0) this.containsAnimal = false;
    }
    private void grassEaten(Vector2d position){
        this.grass = null;
        this.containsGrass = false;
        for(IGrassExsistenceObserver observer: grassExistenceObservers){
            observer.grassEaten(position);
        }
    }

    public void spawnGrass(){
        if(this.containsGrass || this.containsAnimal) {
            throw new IllegalActionException("Grass cannot be spawned at" + this.position);
        }
        this.grass = new Grass(this.position);
        this.containsGrass = true;
        for(IGrassExsistenceObserver observer: grassExistenceObservers){
            observer.grassSpawned(this.position);
        }
    }

    public List<Animal> getAnimalsWithGenotype(int[] dominatingGenotype) {
        List<Animal> animalsWithGenotype = new ArrayList<>();
        Iterator<Animal> itr = this.animalsSortedByEnergyDescending.iterator();
        while(itr.hasNext()){
            Animal animal = itr.next();
            if(animal.getGenotype() == dominatingGenotype){
                animalsWithGenotype.add(animal);
            }
        }
        return animalsWithGenotype;
    }
}
