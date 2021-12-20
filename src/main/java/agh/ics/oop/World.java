package agh.ics.oop;

import agh.ics.oop.gui.App;
import javafx.application.Application;

import static java.lang.System.out;
import static java.lang.System.setOut;

public class World {

    public static void main(String[] args) {

        try {
            out.print("system wystartowal \n\n");

//            String[] arguments = {"f", "b", "r", "l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"};
//            MoveDirection[] directions = new OptionsParser().parse(arguments);
//            AbstractWorldMap map = new GrassField(10);
//            Vector2d[] positions = {new Vector2d(2, 2), new Vector2d(3, 4)};
//            IEngine engine = new SimulationEngine(directions, map, positions);
//            out.print(map);
//            engine.run();
//            out.print(map);
//
//        String[] arguments = {"f", "b", "r" ,"l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"};
//        MoveDirection[] directions = new OptionsParser().parse(arguments);
//        IWorldMap map = new RectangularMap(3,3);
//        Vector2d[] positions = { new Vector2d(2,2), new Vector2d(3,4) };
//        IEngine engine = new SimulationEngine(directions, map, positions);
//        out.print(map.toString());

            Application.launch(App.class, args);
            out.print("system zakończyl dzialanie \n\n");

        } catch (IllegalArgumentException ex){
            out.println(ex);
        }
    }
}