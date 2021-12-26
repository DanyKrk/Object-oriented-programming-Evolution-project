package agh.ics.oop;

public interface IPopulationOfAnimalsObserver {
    void incrementNumberOfAnimals();
    void decrementNumberOfAnimals();
    void animalDied(Animal animal, long day);
    void animalWasBorn(Animal animal);
}
