package agh.ics.oop;

import java.util.Comparator;

public class AnimalReversedComparator implements Comparator<Animal> {
    public int compare(Animal a, Animal b){
        return - a.compareTo(b);
    }
}
