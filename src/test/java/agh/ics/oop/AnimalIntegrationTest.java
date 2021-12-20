package agh.ics.oop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnimalIntegrationTest {

    @Test void optionParserTest(){
        assertThrows(IllegalArgumentException.class, () -> {
                    String[] in1 = {"right", "a", "bc", "f", "forward"};
                    MoveDirection[] expOut1 = {MoveDirection.RIGHT, MoveDirection.FORWARD, MoveDirection.FORWARD};
                    MoveDirection[] pig_directions1 = OptionsParser.parse(in1);
                    assertArrayEquals(expOut1, pig_directions1);
                });

        assertThrows(IllegalArgumentException.class, () -> {
            String[] in2 = {"idz swinko","a","r","bc","aaa","left","l","backward","f", "forward","b","stoj"};
            MoveDirection[] expOut2 = {MoveDirection.RIGHT, MoveDirection.LEFT, MoveDirection.LEFT, MoveDirection.BACKWARD, MoveDirection.FORWARD, MoveDirection.FORWARD,MoveDirection.BACKWARD};
            MoveDirection[] pig_directions2 = OptionsParser.parse(in2);
            assertArrayEquals(expOut2, pig_directions2);
        });

        String[] in1 = {"right", "f", "forward"};
        MoveDirection[] expOut1 = {MoveDirection.RIGHT, MoveDirection.FORWARD, MoveDirection.FORWARD};
        MoveDirection[] pig_directions1 = OptionsParser.parse(in1);
        assertArrayEquals(expOut1, pig_directions1);

        String[] in2 = {"r","left","l","backward","f", "forward","b"};
        MoveDirection[] expOut2 = {MoveDirection.RIGHT, MoveDirection.LEFT, MoveDirection.LEFT, MoveDirection.BACKWARD, MoveDirection.FORWARD, MoveDirection.FORWARD,MoveDirection.BACKWARD};
        MoveDirection[] pig_directions2 = OptionsParser.parse(in2);
        assertArrayEquals(expOut2, pig_directions2);

    }

    @Test void positionTest() {

        IWorldMap map = new RectangularMap(10,10);
        Animal pig1 = new Animal(map, new Vector2d(2,2));
        String[] in1 = {"right", "f", "forward"};
        MoveDirection[] pig_directions1 = OptionsParser.parse(in1);
        Vector2d expPos1 = new Vector2d(4, 2);
        for (MoveDirection dir : pig_directions1) {
            pig1.move(dir);
        }
        assert (pig1.isAt(expPos1));


        Animal pig2 = new Animal(map, new Vector2d(2,2));
        String[] in2 = {"right", "f", "forward", "f", "f", "f", "f"};
        MoveDirection[] pig_directions2 = OptionsParser.parse(in2);
        Vector2d expPos2 = new Vector2d(4, 2);

        for (MoveDirection dir : pig_directions2) {
            pig2.move(dir);
        }
        assert (pig1.isAt(expPos2));
    }


    @Test void orientationAndPositionTest(){
        IWorldMap map = new RectangularMap(10,10);
        Animal pig1 = new Animal(map, new Vector2d(2,2));
        String[] in1 = {"right", "f", "forward"};
        MoveDirection[] pig_directions1 = OptionsParser.parse(in1);
        String expString1 = "E";
        for (MoveDirection dir : pig_directions1) {
            pig1.move(dir);
        }
        assertEquals(expString1, pig1.toString());

        Animal pig2 = new Animal(map, new Vector2d(2,2));
        String[] in2 = {"right", "r", "r", "f", "forward", "l", "b", "b"};
        MoveDirection[] pig_directions2 = OptionsParser.parse(in2);
        String expString2 = "S";
        for (MoveDirection dir : pig_directions2) {
            pig2.move(dir);
        }
        assertEquals(expString2, pig2.toString());
    }


}


