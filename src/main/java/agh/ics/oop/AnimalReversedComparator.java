package agh.ics.oop;

import java.util.Comparator;

public class AnimalReversedComparator implements Comparator<Animal> {
    public int compare(Animal a, Animal b){
        if (a.equals(b)) return 0;
        return - a.compareTo(b);
    }
}
