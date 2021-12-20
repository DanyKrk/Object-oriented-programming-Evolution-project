package agh.ics.oop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RectangularMapIWorldMapTest {
    @Test void canMoveToBorderTest(){
        IWorldMap map = new RectangularMap(10,5);

        assertTrue(map.canMoveTo(new Vector2d(9,4)));
        assertFalse(map.canMoveTo(new Vector2d(10,4)));
        assertFalse(map.canMoveTo(new Vector2d(9,5)));

        assertTrue(map.canMoveTo(new Vector2d(0,0)));
        assertFalse(map.canMoveTo(new Vector2d(0,-1)));
        assertFalse(map.canMoveTo(new Vector2d(-1,0)));
        assertFalse(map.canMoveTo(new Vector2d(-1,-1)));
        assertFalse(map.canMoveTo(new Vector2d(3,-3)));
        assertFalse(map.canMoveTo(new Vector2d(-2,4)));
        assertFalse(map.canMoveTo(new Vector2d(100,100)));
    }

    @Test void canMoveToAnimalTest(){
        IWorldMap map = new RectangularMap(10,10);
        map.place(new Animal(map,new Vector2d(0,0)));
        map.place(new Animal(map,new Vector2d(5,5)));

        assertFalse(map.canMoveTo(new Vector2d(0,0)));
        assertFalse(map.canMoveTo(new Vector2d(5,5)));
        assertTrue(map.canMoveTo(new Vector2d(2,2)));
    }

    @Test void placeAndIsOccupiedTest(){
        IWorldMap map = new RectangularMap(10,10);
        map.place(new Animal(map,new Vector2d(0,0)));
        map.place(new Animal(map,new Vector2d(5,5)));

        assertTrue(map.isOccupied(new Vector2d(0,0)));
        assertFalse(map.isOccupied(new Vector2d(0,1)));
        assertFalse(map.isOccupied(new Vector2d(-1,-1)));

        assertTrue(map.isOccupied(new Vector2d(5,5)));
        assertFalse(map.isOccupied(new Vector2d(5,4)));
        assertFalse(map.isOccupied(new Vector2d(4,5)));
    }

    @Test void objectAtTest() {
        IWorldMap map = new RectangularMap(10,10);
        Animal animal1 = new Animal(map,new Vector2d(0,0));
        Animal animal2 = new Animal(map,new Vector2d(5,5));
        map.place(animal1);
        map.place(animal2);

        assertNull(map.objectAt(new Vector2d(1,1)));
        assertNull(map.objectAt(new Vector2d(9,9)));
        assertNull(map.objectAt(new Vector2d(100,12)));

        assertNotNull(map.objectAt(new Vector2d(0,0)));
        assertNotNull(map.objectAt(new Vector2d(5,5)));

        assertEquals(animal1, map.objectAt(new Vector2d(0,0)));
        assertEquals(animal2, map.objectAt(new Vector2d(5,5)));
    }


}
