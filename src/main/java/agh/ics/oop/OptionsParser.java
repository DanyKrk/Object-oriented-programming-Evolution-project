package agh.ics.oop;

public class OptionsParser {
    public static MoveDirection[] parse(String[] input){
        int resSize = 0;

        for(String el : input){
            switch (el) {
                case "f", "forward", "b", "backward", "r", "right", "l", "left" -> resSize += 1;
                default -> {
                }
            }
        }

        MoveDirection[] res = new MoveDirection[resSize];
        int i = 0;
        for(String el : input){
            switch (el) {
                case "f", "forward" -> {
                    res[i] = MoveDirection.FORWARD;
                    i += 1;
                }
                case "b", "backward" -> {
                    res[i] = MoveDirection.BACKWARD;
                    i += 1;
                }
                case "r", "right" -> {
                    res[i] = MoveDirection.RIGHT;
                    i += 1;
                }
                case "l", "left" -> {
                    res[i] = MoveDirection.LEFT;
                    i += 1;
                }
                default -> {
                    throw new IllegalArgumentException(el + " is not legal move specification");
                }
            }
        }
        return res;
    }
}
