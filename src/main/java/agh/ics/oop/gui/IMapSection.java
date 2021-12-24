package agh.ics.oop.gui;

import agh.ics.oop.*;

import java.util.*;

public class IMapSection {
    private AbstractWorldMap map;
    private Vector2d position;
    private SortedSet<Animal> animalsSortedByEnergyDescending;
    private Grass grass;
    private boolean containsGrass;
    private List<INumberOfAnimalsObserver> numberOfAnimalsObservers = new ArrayList<>();
    private int numberOfAnimals;
    private List<IGrassExsistenceObserver> grassExistenceObservers = new ArrayList<>();

    public IMapSection(AbstractWorldMap map, Vector2d position){
        this.map = map;
        this.position = position;
        this.animalsSortedByEnergyDescending = Collections.synchronizedSortedSet(new TreeSet<Animal>(new AnimalReversedComparator()));
        this.grass = null;
        this.containsGrass = false;
        this.numberOfAnimals = 0;
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
        Iterator<Animal> itr = animalsSortedByEnergyDescending.iterator();
        while (itr.hasNext()) {
            if(itr.next().getEnergy() <= 0){
                itr.remove();
                decrementNumberOfAnimals();
            }
        }
    }

    public void reproduction(){
        if (this.animalsSortedByEnergyDescending.size() < 2) return;

        Animal parent1 = animalsSortedByEnergyDescending.first();
        animalsSortedByEnergyDescending.remove(parent1);
        Animal parent2 = animalsSortedByEnergyDescending.first();
        animalsSortedByEnergyDescending.remove(parent2);

        if (parent1.readyForReproduction() && parent2.readyForReproduction()){
            Animal child = new Animal(this.map, this.position);
            child.setGenesBasedOnParents(parent1, parent2);
            child.setEnergy(parent1.extractEnergyForChild() + parent2.extractEnergyForChild());
            this.map.placeNewAnimal(child);
            incrementNumberOfAnimals();
            this.placeAnimal(parent1);
            this.placeAnimal(parent2);
        }
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

    public boolean placeAnimal(Animal animal) {
        return this.animalsSortedByEnergyDescending.add(animal);
    }

    public Object objectAt() {
        if(animalsSortedByEnergyDescending.size() > 0){
            return animalsSortedByEnergyDescending.first();
        }
        else return this.grass;
    }

    public boolean removeAnimal(Animal animal) {
        return this.animalsSortedByEnergyDescending.remove(animal);
    }

    public void addNumberOfAnimalsObserver(INumberOfAnimalsObserver observer){
        this.numberOfAnimalsObservers.add(observer);
    }

    public void removeNumberOfAnimalsObserver(INumberOfAnimalsObserver observer){
        this.numberOfAnimalsObservers.remove(observer);
    }

    private void incrementNumberOfAnimals(){
        this.numberOfAnimals += 1;
        for(INumberOfAnimalsObserver observer: numberOfAnimalsObservers){
            observer.incrementNumberOfAnimals();
        }
    }
    private void decrementNumberOfAnimals() {
        this.numberOfAnimals -= 1;
        for (INumberOfAnimalsObserver observer : numberOfAnimalsObservers) {
            observer.decrementNumberOfAnimals();
        }
    }
    private void grassEaten(Vector2d position){
        this.grass = null;
        this.containsGrass = false;
        for(IGrassExsistenceObserver observer: grassExistenceObservers){
            observer.grassEaten(position);
        }
    }
    private void grassSpawned(Vector2d position){
        this.grass = new Grass(this.position);
        this.containsGrass = true;
        for(IGrassExsistenceObserver observer: grassExistenceObservers){
            observer.grassSpawned(position);
        }
    }
    public void spawnGrass(){
        this.grass = new Grass(this.position);
        this.containsGrass = true;
        grassSpawned(this.position);
    }
}
