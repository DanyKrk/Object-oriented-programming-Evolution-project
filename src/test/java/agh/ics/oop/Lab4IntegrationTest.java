package agh.ics.oop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Lab4IntegrationTest {
    @Test void animalsMovementTest1(){
        String[] arguments = {"f", "b", "r" ,"l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"};
        MoveDirection[] directions = new OptionsParser().parse(arguments);
        IWorldMap map = new RectangularMap(10, 5);
        Vector2d[] positions = { new Vector2d(2,2), new Vector2d(3,4) };
        IEngine engine = new SimulationEngine(map, positions, 0);
        engine.setDirections(directions);
        engine.run();

        Vector2d expPosition1 = new Vector2d(2,0);
        MapDirection expOrientation1 = MapDirection.SOUTH;
        assertNotEquals(null, map.objectAt(expPosition1));
        assertEquals(expOrientation1.toString(), map.objectAt(expPosition1).toString());

        Vector2d expPosition2 = new Vector2d(3,4);
        MapDirection expOrientation2 = MapDirection.NORTH;
        assertNotEquals(null, map.objectAt(expPosition2));
        assertEquals(expOrientation2.toString(), map.objectAt(expPosition2).toString());
    }

    @Test void placementExceptionTest1() {
        assertThrows(IllegalArgumentException.class, () -> {
        String[] arguments = {"f", "r" ,"l", "f", "f", "forward", "backward", "f", "f", "f", "l", "f", "b", "f", "f", "right", "f", "f", "f", "forward", "left", "r", "l", "f", "f", "f"};
        MoveDirection[] directions = new OptionsParser().parse(arguments);
        IWorldMap map = new RectangularMap(5, 5);
        Vector2d[] positions = { new Vector2d(2,2), new Vector2d(2,2), new Vector2d(11,11), new Vector2d(0,0), new Vector2d(-1,2), new Vector2d(3,4), new Vector2d(3,5), new Vector2d(5,1) };
        IEngine engine = new SimulationEngine(map, positions, 0);
        engine.setDirections(directions);

        assertNotEquals(null, map.objectAt(new Vector2d(2,2)));
        assertNotEquals(null, map.objectAt(new Vector2d(0,0)));
        assertNotEquals(null, map.objectAt(new Vector2d(3,4)));

        engine.run();

        Vector2d expPosition1 = new Vector2d(3,1);
        MapDirection expOrientation1 = MapDirection.SOUTH;
        assertNotEquals(null, map.objectAt(expPosition1));
        assertEquals(expOrientation1.toString(), map.objectAt(expPosition1).toString());

        Vector2d expPosition2 = new Vector2d(1,2);
        MapDirection expOrientation2 = MapDirection.WEST;
        assertNotEquals(null, map.objectAt(expPosition2));
        assertEquals(expOrientation2.toString(), map.objectAt(expPosition2).toString());

        Vector2d expPosition3 = new Vector2d(0,3);
        MapDirection expOrientation3= MapDirection.SOUTH;
        assertNotEquals(null, map.objectAt(expPosition3));
        assertEquals(expOrientation3.toString(), map.objectAt(expPosition3).toString());
        });
    }

    @Test void placementExceptionTest2() {
        assertThrows(IllegalArgumentException.class, () -> {
            String[] arguments = {"f", "r" ,"l", "f", "f", "forward", "backward", "f", "f", "f", "l", "f", "b", "f", "f", "right", "f", "f", "f", "forward", "left", "r", "l", "f", "f", "f"};
            MoveDirection[] directions = new OptionsParser().parse(arguments);
            IWorldMap map = new RectangularMap(5, 5);
            Vector2d[] positions = { new Vector2d(11,11), new Vector2d(0,0), new Vector2d(-1,2), new Vector2d(3,4), new Vector2d(3,5), new Vector2d(5,1) };
            IEngine engine = new SimulationEngine(map, positions, 0);
            engine.setDirections(directions);

            assertNotEquals(null, map.objectAt(new Vector2d(2,2)));
            assertNotEquals(null, map.objectAt(new Vector2d(0,0)));
            assertNotEquals(null, map.objectAt(new Vector2d(3,4)));

            engine.run();

            Vector2d expPosition1 = new Vector2d(3,1);
            MapDirection expOrientation1 = MapDirection.SOUTH;
            assertNotEquals(null, map.objectAt(expPosition1));
            assertEquals(expOrientation1.toString(), map.objectAt(expPosition1).toString());

            Vector2d expPosition2 = new Vector2d(1,2);
            MapDirection expOrientation2 = MapDirection.WEST;
            assertNotEquals(null, map.objectAt(expPosition2));
            assertEquals(expOrientation2.toString(), map.objectAt(expPosition2).toString());

            Vector2d expPosition3 = new Vector2d(0,3);
            MapDirection expOrientation3= MapDirection.SOUTH;
            assertNotEquals(null, map.objectAt(expPosition3));
            assertEquals(expOrientation3.toString(), map.objectAt(expPosition3).toString());
        });
    }

    @Test void animalsMovementTest2(){
        String[] arguments = {"f", "r" ,"l", "f", "f", "forward", "backward", "f", "f", "f", "l", "f", "b", "f", "f", "right", "f", "f", "f", "forward", "left", "r", "l", "f", "f", "f"};
        MoveDirection[] directions = new OptionsParser().parse(arguments);
        IWorldMap map = new RectangularMap(5, 5);
        Vector2d[] positions = { new Vector2d(2,2), new Vector2d(0,0), new Vector2d(3,4)};
        IEngine engine = new SimulationEngine(map, positions, 0);
        engine.setDirections(directions);

        assertNotEquals(null, map.objectAt(new Vector2d(2,2)));
        assertNotEquals(null, map.objectAt(new Vector2d(0,0)));
        assertNotEquals(null, map.objectAt(new Vector2d(3,4)));

        engine.run();

        Vector2d expPosition1 = new Vector2d(3,1);
        MapDirection expOrientation1 = MapDirection.SOUTH;
        assertNotEquals(null, map.objectAt(expPosition1));
        assertEquals(expOrientation1.toString(), map.objectAt(expPosition1).toString());

        Vector2d expPosition2 = new Vector2d(1,2);
        MapDirection expOrientation2 = MapDirection.WEST;
        assertNotEquals(null, map.objectAt(expPosition2));
        assertEquals(expOrientation2.toString(), map.objectAt(expPosition2).toString());

        Vector2d expPosition3 = new Vector2d(0,3);
        MapDirection expOrientation3= MapDirection.SOUTH;
        assertNotEquals(null, map.objectAt(expPosition3));
        assertEquals(expOrientation3.toString(), map.objectAt(expPosition3).toString());
    }
}
