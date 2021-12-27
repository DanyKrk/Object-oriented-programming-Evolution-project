package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class SimulationEngine implements IEngine{

    int moveDelay;

    AbstractWorldMap map;

    public SimulationEngine(AbstractWorldMap map, int numberOfStartingAnimals, int moveDelay){

        this.moveDelay = moveDelay;

        for(int i = 0; i < numberOfStartingAnimals; i++){
            Vector2d position = map.getRandomPosition();
            Animal animal = new Animal(map, position, 0);
            map.manuallyPlaceNewAnimal(animal);
        }
    }
    /**
     * Move the animal on the map according to the provided move directions. Every
     * n-th direction should be sent to the n-th animal on the map.
     *
     */
    public void run(){
        map.removeDeadAnimals();
        map.moveAnimals();
        map.grassEating();
        map.reproduction();
        map.spawnGrassInJungle();
        map.spawnGrassInSteppe();
        map.dayPassed();

        try {
            Thread.sleep(moveDelay);
        } catch (InterruptedException e) {
            out.println("The engine was stopped while running!");
        }

    }
}
