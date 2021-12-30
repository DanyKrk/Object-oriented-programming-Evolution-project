package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class SimulationEngine implements IEngine, IMagicEventObserver{

    boolean isMagical;

    int numberOfMagicalEvents;

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

        this.numberOfMagicalEvents = 0;

        this.map.addMagicEventObserver(this);
    }

    public void run(){
        while(!stopSignal) {
            synchronized (LockObject.INSTANCE) {
                if(isMagical && numberOfMagicalEvents < 3) map.magicallyRemoveDeadAnimals();
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

    public void magicHappened(AbstractWorldMap map){
        this.numberOfMagicalEvents += 1;
    }
}
