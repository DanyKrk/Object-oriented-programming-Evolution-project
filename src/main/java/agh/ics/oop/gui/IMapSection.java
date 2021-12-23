package agh.ics.oop.gui;

import agh.ics.oop.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class IMapSection {
    private IWorldMap map;

    private Vector2d position;

    private SortedSet<Animal> animalsSortedByEnergyDescending = Collections.synchronizedSortedSet(new TreeSet<Animal>(new AnimalReversedComparator()));

    private Grass grass = null;

    private boolean containsGrass = false;

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
        }
    }
}
