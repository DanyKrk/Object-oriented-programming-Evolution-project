package agh.ics.oop;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


public class Vector2dTest {
    Vector2d vector1 = new Vector2d(0, 1);
    Vector2d vector2 = new Vector2d(0, 1);
    Vector2d vector3 = new Vector2d(1, 0);
    Vector2d vector4 = new Vector2d(3, 4);



    @Test void equalsTest() {
        assertTrue(vector1.equals(vector1));
        assertTrue(vector1.equals(vector2));
        assertTrue(vector2.equals(vector1));
        assertFalse(vector1.equals(vector3));
        assertFalse(vector1.equals(vector4));
        assertFalse(vector4.equals(vector1));
        assertFalse(vector1.equals(1));
    }

    @Test void toStringTest() {
        assertEquals(vector1.toString(), "(0,1)");
        assertEquals(vector4.toString(), "(3,4)");
        assertNotEquals(vector1.toString(), "(1,0)");

    }

    @Test void precedesTest() {
        assertTrue(vector1.precedes(vector1));
        assertTrue(vector1.precedes(vector2));
        assertTrue(vector1.precedes(vector4));
        assertFalse(vector2.precedes(vector3));
        assertFalse(vector4.precedes(vector2));
    }

    @Test void followsTest() {
        assertTrue(vector1.follows(vector1));
        assertTrue(vector2.follows(vector1));
        assertTrue(vector4.follows(vector1));
        assertFalse(vector3.follows(vector2));
        assertFalse(vector2.follows(vector4));
    }

    @Test void upperRightTest() {
        assertEquals(vector1 ,vector1.upperRight(vector1));
        assertEquals(new Vector2d(1,1) ,vector1.upperRight(vector3));
        assertEquals(vector3.upperRight(vector1) ,vector1.upperRight(vector3));
        assertEquals(vector4, vector1.upperRight(vector4));
    }
    @Test void lowerLeftTest() {
        assertEquals(vector1 ,vector1.lowerLeft(vector1));
        assertEquals(new Vector2d(0, 0) ,vector1.lowerLeft(vector3));
        assertEquals(vector3.lowerLeft(vector1) ,vector1.lowerLeft(vector3));
        assertEquals(vector1, vector4.lowerLeft(vector1));
    }

    @Test void addTest() {
        assertEquals(new Vector2d(0, 2), vector1.add(vector1));
        assertEquals(new Vector2d(1, 1), vector1.add(vector3));
        assertEquals(vector3.add(vector1), vector1.add(vector3));
        assertEquals(new Vector2d(3, 5), vector1.add(vector4));
        assertNotEquals(new Vector2d(5, 3), vector4.add(vector1));

    }

    @Test void subtractTest() {
        assertEquals(new Vector2d(0, 0), vector1.subtract(vector1));
        assertEquals(new Vector2d(-3, -3), vector1.subtract(vector4));
        assertNotEquals(vector3.subtract(vector1), vector1.subtract(vector3));

    }

    @Test void oppositeTest() {
        assertEquals(new Vector2d(0, -1), vector1.opposite());
        assertEquals(vector1.opposite(), vector2.opposite());
        assertEquals(new Vector2d(-1, 0), vector3.opposite());
        assertEquals(new Vector2d(-3, -4), vector4.opposite());
        assertNotEquals(new Vector2d(-4, -3), vector4.opposite());
    }
}