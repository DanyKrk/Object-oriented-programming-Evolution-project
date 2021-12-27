package agh.ics.oop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractWorldMapJungleSteppeTest {
    int numberOfGenes = 32;
    AbstractWorldMap testMap = new BorderedMap(10,10, 10, 10, 10, 0.5);
    Vector2d testPosition1 = new Vector2d(0,0);
    Vector2d testPosition2 = new Vector2d(1,0);
    Vector2d testPosition3 = new Vector2d(0,1);
    int strongerParentEnergy = 150;
    int weakerParentEnergy = 50;

    @Test
    void JungleHeightAndWidthTest(){
        assertEquals(5, testMap.getJungleHeight());
        assertEquals(5, testMap.getJungleWidth());
    }

}
