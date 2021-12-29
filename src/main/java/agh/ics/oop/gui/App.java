package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.util.Map;


public class App extends Application implements IDayObserver{

    int sceneWidth = 1300;
    int sceneHeight = 800;
    int columnWidth = 50;
    int rowHeight = 50;
    int elementSize = 30;
    int moveDelay = 1000;

    private int mapWidth;
    private int mapHeight;
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;
    private int numberOfStartingAnimals;
    private Double jungleRatio;
    private AbstractWorldMap borderedMap;
    private AbstractWorldMap runaroundMap;
//    Vector2d[] positions = {new Vector2d(2, 2), new Vector2d(3, 4)};

    Scene startScene;
    Scene mainScene;

    GridPane borderedMapGridPane;
    GridPane runaroundMapGridPane;

    Thread borderedMapEngineThread;
    IEngine borderedMapEngine;

    Thread runaroundMapEngineThread;
    IEngine runaroundMapEngine;

//    TextField argsInput;
//    VBox inputVBox;
//    HBox fullScene;
//    Scene scene;
    private Button startButton;
    private VBox borderedMapVBox;
    private VBox runaroundMapVBox;
    private Button borderedMapStartButton;
    private Button runaroundMapStartButton;
    private HBox fullScene;
    private VBox inputVBox;
    private Stage primaryStage;
    private HBox borderedMapStartButtonHBox;
    private HBox borderedMapStopButtonHBox;
    private Button borderedMapStopButton;
    private Button runaroundMapStopButton;
    private HBox borderedMapStartStopButtonHBox;
    private HBox runaroundMapStartStopButtonHBox;
    private HBox runaroundMapStartButtonHBox;
    private HBox runaroundMapStopButtonHBox;

//    @Override
//    public void init(){
////        engine = new SimulationEngine( map, positions, moveDelay);
////        engineThread = new Thread(engine);
//    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        this.primaryStage = primaryStage;
        startScene = getStartScene();
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    private Scene getStartScene() {
        Spinner<Integer> mapWidthSpinner = new Spinner<Integer>(1, Integer.MAX_VALUE, 15, 1);
        HBox mapWidthInputHBox = getIntegerSpinnerHBox(mapWidthSpinner, "Map width: ");

        Spinner<Integer> mapHeightSpinner = new Spinner<Integer>(1, Integer.MAX_VALUE, 15, 1);
        HBox mapHeightInputHBox = getIntegerSpinnerHBox(mapHeightSpinner, "Map height: ");

        Spinner<Integer> startEnergySpinner = new Spinner<Integer>(1, Integer.MAX_VALUE, 100, 10);
        HBox startEnergyInputHBox = getIntegerSpinnerHBox(startEnergySpinner, "Start energy: ");

        Spinner<Integer> moveEnergySpinner = new Spinner<Integer>(1, Integer.MAX_VALUE, 3, 1);
        HBox moveEnergyInputHBox = getIntegerSpinnerHBox(moveEnergySpinner, "Move energy: ");

        Spinner<Integer> plantEnergySpinner = new Spinner<Integer>(1, Integer.MAX_VALUE, 100, 100);
        HBox plantEnergyInputHBox = getIntegerSpinnerHBox(plantEnergySpinner, "Plant energy: ");

        Spinner<Integer> numberOfStartingAnimalsSpinner = new Spinner<Integer>(10, Integer.MAX_VALUE, 30, 10);
        HBox numberOfStartingAnimalsInputHBox = getIntegerSpinnerHBox(numberOfStartingAnimalsSpinner, "Number of starting animals: ");

        Spinner<Double> jungleRatioSpinner = new Spinner<Double>(0.0, 1.0, 0.25, 0.1);
        HBox jungleRatioInputHBox = getDoubleSpinnerHBox(jungleRatioSpinner, "Jungle ratio");

        startButton = new Button("Start");

        startButton.setOnAction(event -> {
            mapWidth = mapWidthSpinner.getValue();
            mapHeight = mapHeightSpinner.getValue();
            startEnergy = startEnergySpinner.getValue();
            moveEnergy = moveEnergySpinner.getValue();
            plantEnergy = plantEnergySpinner.getValue();
            jungleRatio = jungleRatioSpinner.getValue();
            numberOfStartingAnimals = numberOfStartingAnimalsSpinner.getValue();

            adjustColumnWidth();
            adjustRowHeight();
            adjustElementSize();

            try {
                mainScene = getMainScene();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            primaryStage.setScene(mainScene);
            primaryStage.show();
        });

        inputVBox = new VBox(10, mapWidthInputHBox, mapHeightInputHBox, startEnergyInputHBox, moveEnergyInputHBox,
                plantEnergyInputHBox, jungleRatioInputHBox, numberOfStartingAnimalsInputHBox, startButton);
        inputVBox.setAlignment(Pos.CENTER);

        Scene startScene = new Scene(inputVBox, sceneWidth, sceneHeight);
        return startScene;
    }

    private void adjustElementSize() {
        this.elementSize = Integer.min(rowHeight, columnWidth) - 20;
        if (elementSize < 0) this.elementSize = 1;
    }

    private void adjustRowHeight() {
        rowHeight = (sceneHeight - 150) / (2  * mapHeight);
        rowHeight = (sceneHeight - 150) / ( mapHeight);
    }

    private void adjustColumnWidth() {
        columnWidth = (sceneWidth - 150) / (2 * mapWidth);
        columnWidth = (sceneWidth - 150) / (mapWidth);
    }

    private Scene getMainScene() throws FileNotFoundException {
        runaroundMap = new RunaroundMap(mapWidth, mapHeight, startEnergy, moveEnergy, plantEnergy, jungleRatio, numberOfStartingAnimals);
        borderedMap = new BorderedMap(mapWidth, mapHeight, startEnergy, moveEnergy, plantEnergy, jungleRatio, numberOfStartingAnimals);
        runaroundMap.addDayObserver(this);
        borderedMap.addDayObserver(this);

        borderedMapEngine = new SimulationEngine(borderedMap, moveDelay);
        borderedMapEngineThread = new Thread(borderedMapEngine);
        runaroundMapEngine = new SimulationEngine(runaroundMap, moveDelay);
        runaroundMapEngineThread = new Thread(runaroundMapEngine);

        runaroundMapGridPane = new GridPane();
        borderedMapGridPane = new GridPane();
        fillGridPane(runaroundMapGridPane, runaroundMap);
        fillGridPane(borderedMapGridPane, borderedMap);
        borderedMapStartButton = new Button("Start bordered map simulation");
        runaroundMapStartButton = new Button("Start runaround map simulation");

        borderedMapStopButton = new Button("Stop bordered map simulation");
        runaroundMapStopButton = new Button("Stop runaround map simulation");

        borderedMapStartButtonHBox = new HBox(50, borderedMapStartButton);
        runaroundMapStartButtonHBox = new HBox(50, runaroundMapStartButton);

        borderedMapStopButtonHBox = new HBox(50, borderedMapStopButton);
        runaroundMapStopButtonHBox = new HBox(50, runaroundMapStopButton);

        borderedMapStartStopButtonHBox = borderedMapStartButtonHBox;
        runaroundMapStartStopButtonHBox = runaroundMapStartButtonHBox;


        borderedMapVBox = new VBox(10, borderedMapGridPane, borderedMapStartStopButtonHBox);
        runaroundMapVBox = new VBox(10, runaroundMapGridPane, runaroundMapStartStopButtonHBox);
        fullScene = new HBox(50, borderedMapVBox, runaroundMapVBox);
        Scene mainScene = new Scene(fullScene, sceneWidth, sceneHeight);



        borderedMapStartButton.setOnAction(event -> {
            borderedMapEngineThread.start();
            borderedMapStartStopButtonHBox = borderedMapStopButtonHBox;
//            engineThread.interrupt();
//            engine.setDirections(directions);
//            engineThread = new Thread(engine);
//            engineThread.start();

        });

        return mainScene;
    }

    private HBox getDoubleSpinnerHBox(Spinner<Double> spinner, String labelContent) {
        spinner.setEditable(true);
        Label label = new Label(labelContent);
        HBox hBox = new HBox(10, label, spinner);
        return hBox;
    }

    private HBox getIntegerSpinnerHBox(Spinner<Integer> spinner, String labelContent) {
        spinner.setEditable(true);
        Label label = new Label(labelContent);
        HBox hBox = new HBox(10, label, spinner);
        return hBox;
    }

//    private void prepareScene() {
//        startButton = new Button("Start");
//        argsInput = new TextField();
//        inputVBox = new VBox(10, argsInput, startButton);
//        inputVBox.setAlignment(Pos.CENTER);
//        fullScene = new HBox(50, inputVBox, gridPane);
//        scene = new Scene(fullScene, 800, 600);
//
//        startButton.setOnAction(event -> {
//            String argument = argsInput.getText();
//            String[] arguments = argument.split(" ");
//            MoveDirection[] directions = new OptionsParser().parse(arguments);
//
//
//            engineThread.interrupt();
//            engine.setDirections(directions);
//            engineThread = new Thread(engine);
//            engineThread.start();
//
//        });
//    }


    public void fillGridPane(GridPane gridPane, IWorldMap map) throws FileNotFoundException {
        gridPane.setGridLinesVisible(true);
        Vector2d upperRightCorner = map.getUpperRightCorner();
        Vector2d lowerLeftCorner = map.getLowerLeftCorner();

        int width = upperRightCorner.getX() - lowerLeftCorner.getX() + 2; //bo odejmowanie i dodatkowe kolumna/wiersz na indeksy
        int height = upperRightCorner.getY() - lowerLeftCorner.getY() + 2;


        addRowsAndColumnsToGridPane(gridPane, width, height);

        addRowAndColumnDescriptionsToGridPane(gridPane, upperRightCorner, lowerLeftCorner, width, height);

//        synchronized (LockObject.INSTANCE) {
            addAbstractWorldMapElementsToGridPane(gridPane, map, upperRightCorner, lowerLeftCorner);
//        }
    }

//    private void updateGridPane() throws FileNotFoundException {
//
//        gridPane.setGridLinesVisible(false);
//        gridPane.getColumnConstraints().clear();
//        gridPane.getRowConstraints().clear();
//        this.gridPane.getChildren().clear();
//
//        Vector2d upperRightCorner = this.map.getUpperRightCorner();
//        Vector2d lowerLeftCorner = this.map.getLowerLeftCorner();
//
//        int width = upperRightCorner.getX() - lowerLeftCorner.getX() + 2; //bo odejmowanie i dodatkowe kolumna/wiersz na indeksy
//        int height = upperRightCorner.getY() - lowerLeftCorner.getY() + 2;
//
//
//        addRowsAndColumnsToGridPane(width, height);
//
//        addRowAndColumnDescriptionsToGridPane(upperRightCorner, lowerLeftCorner, width, height);
//
//        synchronized (LockObject.INSTANCE) {
//            updateIMapElementsAtGridPane(upperRightCorner, lowerLeftCorner);
//        }
//
//        gridPane.setGridLinesVisible(true);
//    }
//
//    @Override
//    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
//        Platform.runLater(() -> {
//            try {
//                updateGridPane();
//            } catch (FileNotFoundException e) {
//                System.out.println(e);
//            }
//        });
//        return true;
//    }

    private void addAbstractWorldMapElementsToGridPane(GridPane gridPane, IWorldMap map, Vector2d upperRightCorner, Vector2d lowerLeftCorner) throws FileNotFoundException {
        Map<Vector2d, MapSection> positionSectionMap = map.getPositionSectionMap();

        for(MapSection section: positionSectionMap.values()) {
            Object objectAtSection = section.objectAt();
            if (objectAtSection == null) continue;
            if (! (objectAtSection instanceof IMapElement)) continue;
            IMapElement IMapElementAtSection = (IMapElement) objectAtSection;
            addGuiElementBoxToGridPane(gridPane, upperRightCorner, lowerLeftCorner, IMapElementAtSection);
        }
    }

    private void addRowAndColumnDescriptionsToGridPane(GridPane gridPane, Vector2d upperRightCorner, Vector2d lowerLeftCorner, int width, int height) {
        Label description = new Label("y/x");
        gridPane.add(description, 0, 0);
        gridPane.setHalignment(description, HPos.CENTER);

        for(int i = 0; i < height - 1 ; i++){
            Label number = new Label(String.valueOf(upperRightCorner.getY() - i));
            gridPane.add(number, 0, i+1);
            gridPane.setHalignment(number, HPos.CENTER);
        }

        for(int i = 0; i < width - 1; i++){
            Label number = new Label(String.valueOf(lowerLeftCorner.getX() + i));
            gridPane.add(number, i+1, 0);
            gridPane.setHalignment(number, HPos.CENTER);
        }
    }

    private void addRowsAndColumnsToGridPane(GridPane gridPane, int width, int height) {
        for(int i = 0; i < width; i++){
            ColumnConstraints column = new ColumnConstraints(columnWidth);
            gridPane.getColumnConstraints().add(column);
        }

        for(int i = 0; i < height; i++){
            RowConstraints row = new RowConstraints(rowHeight);
            gridPane.getRowConstraints().add(row);
        }
    }




//    private void updateIMapElementsAtGridPane(Vector2d upperRightCorner, Vector2d lowerLeftCorner) throws FileNotFoundException {
//        Map<Vector2d, IMapElement> positionElementMap = map.getPositionElementMap();
//
//        for(IMapElement element: positionElementMap.values()) {
//            addGuiElementBoxToGridPane(upperRightCorner, lowerLeftCorner, element);
//        }
//    }

    private void addGuiElementBoxToGridPane(GridPane gridPane, Vector2d upperRightCorner, Vector2d lowerLeftCorner, IMapElement element) throws FileNotFoundException {
        Vector2d elementPosition = element.getPosition();
        int elementX = elementPosition.getX();
        int elementY = elementPosition.getY();

        Label label = new Label(element.toString());
        GuiElementBox box = new GuiElementBox(element, elementSize);
        VBox GuiElementVBox = box.getVbox();
        gridPane.add(GuiElementVBox, elementX - lowerLeftCorner.getX() + 1, upperRightCorner.getY() - elementY + 1);
        gridPane.setHalignment(label, HPos.CENTER);
    }

    @Override
    public void dayPassed(AbstractWorldMap map) {
        Platform.runLater(() -> {
            try {
                GridPane gridPane;
                if (map.bordersRunaround()){
                    gridPane = runaroundMapGridPane;
                }
                else {
                    gridPane = borderedMapGridPane;
                }
                synchronized (LockObject.INSTANCE) {
                    updateGridPane(gridPane, map);
                }
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        });
    }

    private void updateGridPane(GridPane gridPane, AbstractWorldMap map) throws FileNotFoundException {
        gridPane.setGridLinesVisible(false);
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getChildren().clear();

        Vector2d upperRightCorner = map.getUpperRightCorner();
        Vector2d lowerLeftCorner = map.getLowerLeftCorner();

        int width = upperRightCorner.getX() - lowerLeftCorner.getX() + 2; //bo odejmowanie i dodatkowe kolumna/wiersz na indeksy
        int height = upperRightCorner.getY() - lowerLeftCorner.getY() + 2;


        addRowsAndColumnsToGridPane(gridPane, width, height);

        addRowAndColumnDescriptionsToGridPane(gridPane, upperRightCorner, lowerLeftCorner, width, height);

//        synchronized (LockObject.INSTANCE) {
            addAbstractWorldMapElementsToGridPane(gridPane, map, upperRightCorner, lowerLeftCorner);
//        }

        gridPane.setGridLinesVisible(true);
    }
}
