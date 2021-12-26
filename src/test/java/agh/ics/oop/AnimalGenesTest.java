package agh.ics.oop;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnimalGenesTest {
    int numberOfGenes = 32;
    IWorldMap testMap = new BorderedMap(10,10);
    Vector2d testPosition1 = new Vector2d(0,0);
    Vector2d testPosition2 = new Vector2d(1,0);
    Vector2d testPosition3 = new Vector2d(0,1);
    int strongerParentEnergy = 150;
    int weakerParentEnergy = 50;

    @Test void GeneTest1(){
        Animal parent1 = new Animal(testMap, testPosition1);
        Animal parent2 = new Animal(testMap, testPosition2);
        Animal child = new Animal(testMap, testPosition3);
        int[] testGenes1 = new int[numberOfGenes];
        int[] testGenes2 = new int[numberOfGenes];
        for (int i = 0; i < numberOfGenes; i++){
            testGenes1[i] = 0;
            testGenes2[i] = 1;
        }
        parent1.setGenotype(testGenes1);
        parent2.setGenotype(testGenes2);

        parent1.setEnergy(strongerParentEnergy);
        parent2.setEnergy(weakerParentEnergy);

        child.setGenesBasedOnParents(parent2,parent1);

        int[] expectedChildGenes = new int[numberOfGenes];

        for (int i = 0; i < 24; i++){
            expectedChildGenes[i] = 0;
        }
        for (int i = 24; i < 32; i++){
            expectedChildGenes[i] = 1;
        }
        assertArrayEquals(expectedChildGenes, child.getGenotype());
    }

}
