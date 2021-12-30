package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class SimulationEngine implements IEngine{

    boolean isMagical;

    int moveDelay;

    AbstractWorldMap map;

    boolean stopSignal = false;

    public SimulationEngine(AbstractWorldMap map, int moveDelay, String evolutionRule){
        this.map = map;
        this.moveDelay = moveDelay;
        if(evolutionRule == "Normal"){
            isMagical = false;
        }
        else isMagical = true;
    }
    /**
     * Move the animal on the map according to the provided move directions. Every
     * n-th direction should be sent to the n-th animal on the map.
     *
     */
    public void run(){
        while(!stopSignal) {
            synchronized (LockObject.INSTANCE) {

                if(isMagical) map.magicallyRemoveDeadAnimals();
                else map.removeDeadAnimals();
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
