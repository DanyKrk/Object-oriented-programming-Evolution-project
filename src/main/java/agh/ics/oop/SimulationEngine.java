//package agh.ics.oop;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static java.lang.System.out;
//
//public class SimulationEngine implements IEngine{
//
//    private MoveDirection[] directions;
//    private List<Animal> animals = new ArrayList<>();
//    int moveDelay;
//
//    public SimulationEngine(IWorldMap map, Vector2d[] positions, int moveDelay){
//
//        this.moveDelay = moveDelay;
//
//        for(Vector2d position: positions){
//            Animal animal = new Animal(map, position);
//            if(map.manuallyPlaceNewAnimal(animal)) {
//                animals.add(animal);
//            }
//        }
//    }
//    /**
//     * Move the animal on the map according to the provided move directions. Every
//     * n-th direction should be sent to the n-th animal on the map.
//     *
//     */
//    public void run(){
//        int ordersNumber = directions.length;
//        int animalsNumber = animals.size();
//
//
//        for(int i = 0; i<ordersNumber; i++){
//
//            synchronized (LockObject.INSTANCE) {
//                animals.get(i % animalsNumber).move(directions[i]);
//            }
//            try {
//                Thread.sleep(moveDelay);
//            } catch (InterruptedException e) {
//                out.println("The engine was stopped while running!");
//            }
//
//        }
//    }
//
//    public void setDirections(MoveDirection[] newDirections){
//        this.directions = newDirections;
//    }
//}
