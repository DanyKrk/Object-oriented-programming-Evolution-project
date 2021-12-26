package agh.ics.oop;

public interface IPopulationOfAnimalsObserver {
    void incrementNumberOfAnimals();
    void decrementNumberOfAnimals();
    void animalDied(Animal animal);
    void animalWasBorn(Animal animal);
}
