package agh.ics.oop;

import agh.ics.oop.gui.App;
import javafx.application.Application;

import static java.lang.System.out;

public class World {

    public static void main(String[] args) {

        try {
            out.print("system wystartowal \n\n");
            Application.launch(App.class, args);
            out.print("system zakończyl dzialanie \n\n");

        } catch (IllegalArgumentException ex){
            out.println(ex);
        }
    }
}