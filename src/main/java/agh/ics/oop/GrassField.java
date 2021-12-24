package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class GrassField extends AbstractWorldMap {
    private int maxGrassRange;

    public GrassField(int n){
        upperRightCorner = new Vector2d(Integer.MIN_VALUE, Integer.MIN_VALUE);
        lowerLeftCorner = new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE);
        drawer = new MapVisualizer(this);

        maxGrassRange = (int)Math.sqrt(n*10);
        for(int i = 0; i<n; i++){
            placeGrass();
        }
    }

    @Override public boolean canMoveTo(Vector2d position) {

        if (super.canMoveTo(position)) return true;

        if (this.objectAt(position) instanceof Grass) return true;

        return false;
    }

    @Override public IMapElement removeElement(Vector2d position){
        IMapElement removedElement = super.removeElement(position);

        if (removedElement instanceof Grass) {
            placeGrass();
            return removedElement;
        }
        return null;
    }

//    @Override public boolean positionChanged(Vector2d oldPosition, Vector2d newPosition){
//        IMapElement element = this.positionElementMap.remove(oldPosition);
//        if(element == null) return false;
//        if(this.positionElementMap.remove(newPosition) != null){
//            this.placeGrass();
//        }; //potential grass eating
//        return this.positionElementMap.put(newPosition, element) == element;
//    }

    private void placeGrass(){
        boolean placedFlag = false;
        while (!placedFlag){
            int x = (int) (Math.random() * maxGrassRange);
            int y = (int) (Math.random() * maxGrassRange);
            Vector2d positionProposition = new Vector2d(x,y);

            if (isOccupied(positionProposition)) continue;

            positionElementMap.put(positionProposition, new Grass(positionProposition));
            border.addPosition(positionProposition);
            placedFlag = true;
        }
    }
}
