package agh.ics.oop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GrassFieldIWorldMapTest {

    @Test void canMoveToAnimalTest(){
        IWorldMap map = new GrassField(10);
        map.manuallyPlaceNewAnimal(new Animal(map,new Vector2d(0,0)));
        map.manuallyPlaceNewAnimal(new Animal(map,new Vector2d(5,5)));

        assertFalse(map.canMoveTo(new Vector2d(0,0)));
        assertFalse(map.canMoveTo(new Vector2d(5,5)));
        assertTrue(map.canMoveTo(new Vector2d(1000,1000)));
        assertTrue(map.canMoveTo(new Vector2d(2,2)));
    }

    @Test void placeAndIsOccupiedTest(){
        IWorldMap map = new GrassField(10);
        map.manuallyPlaceNewAnimal(new Animal(map,new Vector2d(0,0)));
        map.manuallyPlaceNewAnimal(new Animal(map,new Vector2d(5,5)));

        assertTrue(map.isOccupied(new Vector2d(0,0)));
        assertFalse(map.isOccupied(new Vector2d(11,11)));
        assertFalse(map.isOccupied(new Vector2d(-1,-1)));

        assertTrue(map.isOccupied(new Vector2d(5,5)));
        assertFalse(map.isOccupied(new Vector2d(1000,1000)));
        assertFalse(map.isOccupied(new Vector2d(-1000,-1000)));
    }

    @Test void objectAtTest() {
        IWorldMap map = new GrassField(10);
        Animal animal1 = new Animal(map,new Vector2d(0,0));
        Animal animal2 = new Animal(map,new Vector2d(5,5));
        map.manuallyPlaceNewAnimal(animal1);
        map.manuallyPlaceNewAnimal(animal2);

        assertNull(map.objectAt(new Vector2d(-11,-1)));
        assertNull(map.objectAt(new Vector2d(-9,-9)));
        assertNull(map.objectAt(new Vector2d(100,12)));

        assertNotNull(map.objectAt(new Vector2d(0,0)));
        assertNotNull(map.objectAt(new Vector2d(5,5)));

        assertEquals(animal1, map.objectAt(new Vector2d(0,0)));
        assertEquals(animal2, map.objectAt(new Vector2d(5,5)));

    }
}
