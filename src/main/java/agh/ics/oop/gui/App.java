package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.util.Map;


public class App extends Application implements IPositionChangeObserver{

    final int columnWidth = 50;
    final int rowHeight = 50;
    final int elementSize = 30;
    final int moveDelay = 300;
    final AbstractWorldMap map = new GrassField(10);
    Vector2d[] positions = {new Vector2d(2, 2), new Vector2d(3, 4)};

    GridPane gridPane = new GridPane();
    Thread engineThread;
    IEngine engine;
    Button startButton;
    TextField argsInput;
    VBox inputVBox;
    HBox fullScene;
    Scene scene;


    @Override
    public void init(){
        engine = new SimulationEngine( map, positions, moveDelay);
        engineThread = new Thread(engine);
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        try {
            fillGridPane();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        prepareScene();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void prepareScene() {
        startButton = new Button("Start");
        argsInput = new TextField();
        inputVBox = new VBox(10, argsInput, startButton);
        inputVBox.setAlignment(Pos.CENTER);
        fullScene = new HBox(50, inputVBox, gridPane);
        scene = new Scene(fullScene, 800, 600);

        startButton.setOnAction(event -> {
            String argument = argsInput.getText();
            String[] arguments = argument.split(" ");
            MoveDirection[] directions = new OptionsParser().parse(arguments);


            engineThread.interrupt();
            engine.setDirections(directions);
            engineThread = new Thread(engine);
            engineThread.start();

        });
    }


    public void fillGridPane() throws FileNotFoundException {
        gridPane.setGridLinesVisible(true);
        Vector2d upperRightCorner = this.map.getUpperRightCorner();
        Vector2d lowerLeftCorner = this.map.getLowerLeftCorner();

        int width = upperRightCorner.getX() - lowerLeftCorner.getX() + 2; //bo odejmowanie i dodatkowe kolumna/wiersz na indeksy
        int height = upperRightCorner.getY() - lowerLeftCorner.getY() + 2;


        addRowsAndColumnsToGridPane(width, height);

        addRowAndColumnDescriptionsToGridPane(upperRightCorner, lowerLeftCorner, width, height);

        synchronized (LockObject.INSTANCE) {
            addIMapElementsToGridPane(upperRightCorner, lowerLeftCorner);
        }
    }

    private void updateGridPane() throws FileNotFoundException {

        gridPane.setGridLinesVisible(false);
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        this.gridPane.getChildren().clear();

        Vector2d upperRightCorner = this.map.getUpperRightCorner();
        Vector2d lowerLeftCorner = this.map.getLowerLeftCorner();

        int width = upperRightCorner.getX() - lowerLeftCorner.getX() + 2; //bo odejmowanie i dodatkowe kolumna/wiersz na indeksy
        int height = upperRightCorner.getY() - lowerLeftCorner.getY() + 2;


        addRowsAndColumnsToGridPane(width, height);

        addRowAndColumnDescriptionsToGridPane(upperRightCorner, lowerLeftCorner, width, height);

        synchronized (LockObject.INSTANCE) {
            updateIMapElementsAtGridPane(upperRightCorner, lowerLeftCorner);
        }

        gridPane.setGridLinesVisible(true);
    }

    @Override
    public boolean positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
        Platform.runLater(() -> {
            try {
                updateGridPane();
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        });
        return true;
    }

    private void addIMapElementsToGridPane(Vector2d upperRightCorner, Vector2d lowerLeftCorner) throws FileNotFoundException {
        Map<Vector2d, IMapElement> positionElementMap = map.getPositionElementMap();

        for(IMapElement element: positionElementMap.values()) {
            if(element instanceof Animal){
                ((Animal) element).addObserver(this);
            }

            addGuiElementBoxToGridPane(upperRightCorner, lowerLeftCorner, element);

        }
    }

    private void addRowAndColumnDescriptionsToGridPane(Vector2d upperRightCorner, Vector2d lowerLeftCorner, int width, int height) {
        Label description = new Label("y/x");
        this.gridPane.add(description, 0, 0);
        this.gridPane.setHalignment(description, HPos.CENTER);

        for(int i = 0; i < height - 1 ; i++){
            Label number = new Label(String.valueOf(upperRightCorner.getY() - i));
            this.gridPane.add(number, 0, i+1);
            this.gridPane.setHalignment(number, HPos.CENTER);
        }

        for(int i = 0; i < width - 1; i++){
            Label number = new Label(String.valueOf(lowerLeftCorner.getX() + i));
            this.gridPane.add(number, i+1, 0);
            this.gridPane.setHalignment(number, HPos.CENTER);
        }
    }

    private void addRowsAndColumnsToGridPane(int width, int height) {
        for(int i = 0; i < width; i++){
            ColumnConstraints column = new ColumnConstraints(columnWidth);
            this.gridPane.getColumnConstraints().add(column);
        }

        for(int i = 0; i < height; i++){
            RowConstraints row = new RowConstraints(rowHeight);
            this.gridPane.getRowConstraints().add(row);
        }
    }




    private void updateIMapElementsAtGridPane(Vector2d upperRightCorner, Vector2d lowerLeftCorner) throws FileNotFoundException {
        Map<Vector2d, IMapElement> positionElementMap = map.getPositionElementMap();

        for(IMapElement element: positionElementMap.values()) {
            addGuiElementBoxToGridPane(upperRightCorner, lowerLeftCorner, element);
        }
    }

    private void addGuiElementBoxToGridPane(Vector2d upperRightCorner, Vector2d lowerLeftCorner, IMapElement element) throws FileNotFoundException {
        Vector2d elementPosition = element.getPosition();
        int elementX = elementPosition.getX();
        int elementY = elementPosition.getY();

        Label label = new Label(element.toString());
        GuiElementBox box = new GuiElementBox(element, elementSize);
        VBox GuiElementVBox = box.getVbox();
        this.gridPane.add(GuiElementVBox, elementX - lowerLeftCorner.getX() + 1, upperRightCorner.getY() - elementY + 1);
        this.gridPane.setHalignment(label, HPos.CENTER);
    }
}
