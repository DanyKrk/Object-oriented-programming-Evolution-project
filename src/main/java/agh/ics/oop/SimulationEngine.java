package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class SimulationEngine implements IEngine{

    int moveDelay;

    AbstractWorldMap map;

    boolean stopSignal = false;

    public SimulationEngine(AbstractWorldMap map, int moveDelay){
        this.map = map;
        this.moveDelay = moveDelay;


    }
    /**
     * Move the animal on the map according to the provided move directions. Every
     * n-th direction should be sent to the n-th animal on the map.
     *
     */
    public void run(){
        while(!stopSignal) {
            synchronized (LockObject.INSTANCE) {
                map.removeDeadAnimals();
                map.moveAnimals();
                map.grassEating();
                map.reproduction();
                map.spawnGrasses();
                map.dayPassed();
            }
                try {
                    Thread.sleep(moveDelay);
                } catch (InterruptedException e) {
                    out.println("The engine was stopped while running!");
                }
        }
    }

    public void stop(){
        this.stopSignal = true;
    }
}
