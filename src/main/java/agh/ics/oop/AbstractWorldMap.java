package agh.ics.oop;

import agh.ics.oop.gui.IMapSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver, INumberOfAnimalsObserver, IGrassExsistenceObserver{
    private final int startEnergy = 15;
    private final int moveEnergy = 3;
    private final int plantEnergy = 10;
    private final float jungleRatio = 0.25f;
    private int width;
    private int height;
    private int jungleWidth;
    private int jungleHeight;
    private int jungleSurfaceArea;
    private int steppeSurfaceArea;
    private int numberOfGrassesInSteppe;
    private int numberOfGrassesInJungle;
    private int numberOfAnimals;
    protected Vector2d upperRightCorner;
    protected Vector2d lowerLeftCorner;
    private Vector2d jungleLowerLeftCorner;
    private Vector2d jungleUpperRightCorner;
//    protected List<IMapElement> elements = new ArrayList<>();
    protected Map<Vector2d, IMapSection> positionSectionMap = Collections.synchronizedMap(new HashMap<>());
    protected MapVisualizer drawer;

    public AbstractWorldMap(int width, int height){
        this.width = width;
        this.height = height;
        this.lowerLeftCorner = new Vector2d(0,0);
        this.upperRightCorner = new Vector2d(width - 1, height - 1);
        this.jungleWidth = getJungleWidth(width, jungleRatio);
        this.jungleHeight = getJungleHeight(height, jungleRatio);
        this.jungleLowerLeftCorner = getJungleLowerLeftCorner(width, height, jungleWidth, jungleHeight);
        this.jungleUpperRightCorner = getJungleUpperRightCorner(width, height, jungleWidth, jungleHeight);
        this.jungleSurfaceArea = jungleWidth * jungleHeight;
        this.steppeSurfaceArea = width * height - jungleSurfaceArea;
        this.numberOfAnimals = 0;
        this.numberOfGrassesInSteppe = 0;
        this.numberOfGrassesInJungle = 0;
        drawer = new MapVisualizer(this);
    }

    private Vector2d getJungleUpperRightCorner(int width, int height, int jungleWidth, int jungleHeight) {
        int widthMarginSize = this.width - jungleWidth;
        int leftMarginSize = widthMarginSize / 2;

        int heightMarginSize = this.height - jungleHeight;
        int bottomMarginSize = heightMarginSize / 2;

        return new Vector2d(leftMarginSize + jungleWidth, bottomMarginSize + jungleHeight);
    }

    private Vector2d getJungleLowerLeftCorner(int width, int height, int jungleWidth, int jungleHeight) {
        int widthMarginSize = this.width - jungleWidth;
        int leftMarginSize = widthMarginSize / 2;

        int heightMarginSize = this.height - jungleHeight;
        int bottomMarginSize = heightMarginSize / 2;

        return new Vector2d(leftMarginSize, bottomMarginSize);
    }

    private int getJungleHeight(int height, float jungleRatio) {
        return Math.round(height * jungleRatio);
    }

    private int getJungleWidth(int width, float jungleRatio) {
        return Math.round(width * jungleRatio);
    }

    private IMapSection getSectionAtPosition(Vector2d position){
        IMapSection sectionAtPosition;
        if (this.positionSectionMap.containsKey(position)){
            sectionAtPosition = positionSectionMap.get(position);
        }
        else{
            sectionAtPosition = new IMapSection(this, position);
            positionSectionMap.put(position, sectionAtPosition);
        }
        return sectionAtPosition;
    }
    public boolean placeNewAnimal(Animal animal){
        Vector2d destination = animal.getPosition();

        if (canMoveTo(destination)) {
            IMapSection destinationSection = this.getSectionAtPosition(destination);
            destinationSection.placeAnimal(animal);
            animal.addObserver(this);
            this.numberOfAnimals += 1;
            return true;
        }
        else {
            throw new IllegalArgumentException("Animal can not be placed on: " + destination);
        }
    }


    public Object objectAt(Vector2d position){
        if (this.positionSectionMap.containsKey(position)){
            IMapSection positionSection = positionSectionMap.get(position);
            return positionSection.objectAt();
        }
        else{
           return null;
        }
    }


    public String toString(){
        return drawer.draw(lowerLeftCorner, upperRightCorner);
    }

    public boolean positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition){
        IMapSection oldPositionSection = this.getSectionAtPosition(oldPosition);
        if(! oldPositionSection.removeAnimal(animal)) return false;
        IMapSection newPositionSection = this.getSectionAtPosition(newPosition);
        if(! newPositionSection.placeAnimal(animal)) return false;
        return true;
    }

    public Vector2d getUpperRightCorner(){
        return this.getUpperRightCorner();
    }

    public Vector2d getLowerLeftCorner(){
        return this.getLowerLeftCorner();
    }

    public Map<Vector2d, IMapSection> getPositionSectionMap(){
        return this.positionSectionMap;
    }

    public int getStartEnergy() {
        return this.startEnergy;
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public int getPlantEnergy() {
        return plantEnergy;
    }

    public float getJungleRatio() {
        return jungleRatio;
    }

    public void incrementNumberOfAnimals(){
        this.numberOfAnimals += 1;
    }

    public void decrementNumberOfAnimals(){
        this.numberOfAnimals -=1;
    }

    private boolean positionIsInJungle(Vector2d position){
        return (position.precedes(jungleUpperRightCorner) && position.follows(jungleLowerLeftCorner));
    }

    public void grassEaten(Vector2d position){
        if(this.positionIsInJungle(position)){
            numberOfGrassesInJungle -= 1;
        }
        else {
            numberOfGrassesInSteppe -= 1;
        }
    }
    public void grassSpawned(Vector2d position){
        if(this.positionIsInJungle(position)){
            numberOfGrassesInJungle += 1;
        }
        else {
            numberOfGrassesInSteppe += 1;
        }
    }
}
