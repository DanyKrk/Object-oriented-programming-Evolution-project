package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class SimulationEngine implements IEngine{

    int moveDelay;

    AbstractWorldMap map;

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
        while(true) {
            synchronized (LockObject.INSTANCE) {
                map.removeDeadAnimals();
                map.moveAnimals();
                map.grassEating();
                map.reproduction();
//                map.spawnGrassInJungle();
//                map.spawnGrassInSteppe();
                map.spawnGrasses();
                map.dayPassed();
            }
                long day = map.getDay();
                if(day == 100){
                    out.println("100 dzien");
                }
                try {
                    Thread.sleep(moveDelay);
                } catch (InterruptedException e) {
                    out.println("The engine was stopped while running!");
                }
        }
    }
}
