package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    Scene startScene;
    Scene mainScene;

    GridPane borderedMapGridPane;
    GridPane runaroundMapGridPane;

    Thread borderedMapEngineThread;
    IEngine borderedMapEngine;

    Thread runaroundMapEngineThread;
    IEngine runaroundMapEngine;

    private Button startButton;
    private VBox borderedMapVBox;
    private VBox runaroundMapVBox;
    private Button borderedMapStartButton;
    private Button runaroundMapStartButton;
    private HBox fullScene;
    private VBox inputVBox;
    private Stage primaryStage;
    private Button borderedMapStopButton;
    private Button runaroundMapStopButton;
    private HBox borderedMapActionHBox;
    private HBox runaroundMapActionHBox;
    private HBox borderedMapChartHBox;
    private HBox runaroundMapChartHBox;

    private XYChart.Series<Number, Number> borderedMapNumberOfLivingAnimalsSeries;
    private LineChart<Number, Number> borderedMapNumberOfLivingAnimalsChart;
    private XYChart.Series<Number, Number> runaroundMapNumberOfLivingAnimalsSeries;
    private LineChart<Number, Number> runaroundMapNumberOfLivingAnimalsChart;
    private XYChart.Series<Number, Number> borderedMapNumberOfGrassesSeries;
    private LineChart<Number, Number> borderedMapNumberOfGrassesChart;
    private XYChart.Series<Number, Number> runaroundMapNumberOfGrassesSeries;
    private LineChart<Number, Number> runaroundMapNumberOfGrassesChart;
    private XYChart.Series<Number, Number> borderedMapAverageEnergyOfLivingAnimalsSeries;
    private LineChart<Number, Number> borderedMapAverageEnergyOfLivingAnimalsChart;
    private XYChart.Series<Number, Number> runaroundMapAverageEnergyOfLivingAnimalsSeries;
    private LineChart<Number, Number> runaroundMapAverageEnergyOfLivingAnimalsChart;
    private XYChart.Series<Number, Number> borderedMapAverageLifespanOfDeadAnimalsSeries;
    private LineChart<Number, Number> borderedMapAverageLifespanOfDeadAnimalsChart;
    private XYChart.Series<Number, Number> runaroundMapAverageLifespanOfDeadAnimalsSeries;
    private LineChart<Number, Number> runaroundMapAverageLifespanOfDeadAnimalsChart;
    private XYChart.Series<Number, Number> borderedMapAverageNumberOfChildrenOfLivingAnimalsSeries;
    private LineChart<Number, Number> borderedMapAverageNumberOfChildrenOfLivingAnimalsChart;
    private XYChart.Series<Number, Number> runaroundMapAverageNumberOfChildrenOfLivingAnimalsSeries;
    private LineChart<Number, Number> runaroundMapAverageNumberOfChildrenOfLivingAnimalsChart;
    private HBox borderedMapChartHBox1;
    private HBox runaroundMapChartHBox1;
    private HBox borderedMapChartHBox2;
    private HBox runaroundMapChartHBox2;
    private VBox borderedMapChartVBox;
    private VBox runaroundMapChartVBox;
    private Label borderedMapDominatingGenotypeLabel;
    private Label runaroundMapDominatingGenotypeLabel;
    private Label borderedMapLabel;
    private Label runaroundMapLabel;
    private Button borderedMapSaveStatisticsButton;
    private Button borderedMapShowMapStatisticsButton;
    private Button borderedMapShowTrackedAnimalStatisticsButton;
    private Button runaroundMapSaveStatisticsButton;
    private Button runaroundMapShowMapStatisticsButton;
    private Button runaroundMapShowTrackedAnimalStatisticsButton;
    private HBox borderedMapStartStopButtonsHBox;
    private HBox runaroundMapStartStopButtonsHBox;
    private HBox borderedMapShowStatisticsButtonsHBox;
    private HBox runaroundMapShowStatisticsButtonsHBox;
    private Label borderedMapTrackedAnimalGenotypeLabel;
    private Label borderedMapTrackedAnimalNumberOfChildrenLabel;
    private Label borderedMapTrackedAnimalNumberOfDescendantsLabel;
    private Label borderedMapTrackedAnimalDeathDayLabel;
    private Label runaroundMapTrackedAnimalGenotypeLabel;
    private Label runaroundMapTrackedAnimalNumberOfChildrenLabel;
    private Label runaroundMapTrackedAnimalNumberOfDescendantsLabel;
    private Label runaroundMapTrackedAnimalDeathDayLabel;
    private Animal borderedMapTrackedAnimal = null;
    private Animal runaroundMapTrackedAnimal = null;

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
        Spinner<Integer> moveDelaySpinner = new Spinner<Integer>(10, Integer.MAX_VALUE, 300, 25);
        HBox moveDelayInputHBox = getIntegerSpinnerHBox(moveDelaySpinner, "Move delay: ");

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

        Spinner<Integer> numberOfStartingAnimalsSpinner = new Spinner<Integer>(1, Integer.MAX_VALUE, 30, 10);
        HBox numberOfStartingAnimalsInputHBox = getIntegerSpinnerHBox(numberOfStartingAnimalsSpinner, "Number of starting animals: ");

        Spinner<Double> jungleRatioSpinner = new Spinner<Double>(0.0, 1.0, 0.25, 0.1);
        HBox jungleRatioInputHBox = getDoubleSpinnerHBox(jungleRatioSpinner, "Jungle ratio");

        startButton = new Button("Start");

        startButton.setOnAction(event -> {
            moveDelay = moveDelaySpinner.getValue();
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


        inputVBox = new VBox(10, moveDelayInputHBox, mapWidthInputHBox, mapHeightInputHBox, startEnergyInputHBox, moveEnergyInputHBox,
                plantEnergyInputHBox, jungleRatioInputHBox, numberOfStartingAnimalsInputHBox, startButton);
        inputVBox.setAlignment(Pos.CENTER);

        Scene startScene = new Scene(inputVBox, sceneWidth, sceneHeight);
        return startScene;
    }

    private void adjustElementSize() {
        this.elementSize = Integer.min(rowHeight, columnWidth) - 20;
        if (elementSize <= 15) this.elementSize = 15;
    }

    private void adjustRowHeight() {
        rowHeight = (sceneHeight - 150) / (2  * (mapHeight + 1));
//        rowHeight = (sceneHeight - 150) / ( mapHeight);
    }

    private void adjustColumnWidth() {
        columnWidth = (sceneWidth - 150) / (2 * (mapWidth + 1));
//        columnWidth = (sceneWidth - 150) / (mapWidth);
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

        borderedMapLabel = new Label("Bordered map");
        runaroundMapLabel = new Label("Runaround map");

        runaroundMapGridPane = new GridPane();
        borderedMapGridPane = new GridPane();
        fillGridPane(runaroundMapGridPane, runaroundMap);
        fillGridPane(borderedMapGridPane, borderedMap);

        borderedMapStartButton = new Button("Start bordered map simulation");
        runaroundMapStartButton = new Button("Start runaround map simulation");

        borderedMapStopButton = new Button("Stop bordered map simulation");
        runaroundMapStopButton = new Button("Stop runaround map simulation");

        borderedMapStartStopButtonsHBox = new HBox(borderedMapStartButton);
        runaroundMapStartStopButtonsHBox = new HBox(runaroundMapStartButton);

        borderedMapShowMapStatisticsButton = new Button("Show map statistics");
        borderedMapShowTrackedAnimalStatisticsButton = new Button("Show tracked animal statistics");

        runaroundMapShowMapStatisticsButton = new Button("Show map statistics");
        runaroundMapShowTrackedAnimalStatisticsButton = new Button("Show tracked animal statistics");

        borderedMapShowStatisticsButtonsHBox = new HBox(borderedMapShowTrackedAnimalStatisticsButton);
        runaroundMapShowStatisticsButtonsHBox = new HBox(runaroundMapShowTrackedAnimalStatisticsButton);

        borderedMapSaveStatisticsButton = new Button("Save statistics to file");
        runaroundMapSaveStatisticsButton = new Button("Save statistics to file");

        borderedMapActionHBox = new HBox(50, borderedMapStartStopButtonsHBox, borderedMapShowStatisticsButtonsHBox, borderedMapSaveStatisticsButton);
        runaroundMapActionHBox = new HBox(50, runaroundMapStartStopButtonsHBox, runaroundMapShowStatisticsButtonsHBox, runaroundMapSaveStatisticsButton);

        borderedMapNumberOfLivingAnimalsSeries = createSeries("Living animals");
        borderedMapNumberOfLivingAnimalsChart = createChart("Day", "Animals", "Number of living animals", borderedMapNumberOfLivingAnimalsSeries);

        runaroundMapNumberOfLivingAnimalsSeries = createSeries("Living animals");
        runaroundMapNumberOfLivingAnimalsChart = createChart("Day", "Animals", "Number of living animals", runaroundMapNumberOfLivingAnimalsSeries);

        borderedMapNumberOfGrassesSeries = createSeries("Grasses");
        borderedMapNumberOfGrassesChart = createChart("Day", "Grasses", "Number of grasses", borderedMapNumberOfGrassesSeries);

        runaroundMapNumberOfGrassesSeries = createSeries("Grasses");
        runaroundMapNumberOfGrassesChart = createChart("Day", "Grasses", "Number of grasses", runaroundMapNumberOfGrassesSeries);

        borderedMapAverageEnergyOfLivingAnimalsSeries = createSeries("Average energy of living animals");
        borderedMapAverageEnergyOfLivingAnimalsChart = createChart("Day", "Average energy", "Average energy of living animals", borderedMapAverageEnergyOfLivingAnimalsSeries);

        runaroundMapAverageEnergyOfLivingAnimalsSeries = createSeries("Average energy of living animals");
        runaroundMapAverageEnergyOfLivingAnimalsChart = createChart("Day", "Average energy", "Average energy of living animals", runaroundMapAverageEnergyOfLivingAnimalsSeries);

        borderedMapAverageLifespanOfDeadAnimalsSeries = createSeries("Average lifespan of dead animals");
        borderedMapAverageLifespanOfDeadAnimalsChart = createChart("Day", "Average lifespan", "Average lifespan of dead animals", borderedMapAverageLifespanOfDeadAnimalsSeries);

        runaroundMapAverageLifespanOfDeadAnimalsSeries = createSeries("Average lifespan of dead animals");
        runaroundMapAverageLifespanOfDeadAnimalsChart = createChart("Day", "Average lifespan", "Average lifespan of dead animals", runaroundMapAverageLifespanOfDeadAnimalsSeries);

        borderedMapAverageNumberOfChildrenOfLivingAnimalsSeries = createSeries("Average number of children of living animals");
        borderedMapAverageNumberOfChildrenOfLivingAnimalsChart = createChart("Day", "Average number of children", "Average number of children of living animals", borderedMapAverageNumberOfChildrenOfLivingAnimalsSeries);

        runaroundMapAverageNumberOfChildrenOfLivingAnimalsSeries = createSeries("Average number of children of living animals");
        runaroundMapAverageNumberOfChildrenOfLivingAnimalsChart = createChart("Day", "Average number of children", "Average number of children of living animals", runaroundMapAverageNumberOfChildrenOfLivingAnimalsSeries);

        borderedMapTrackedAnimalGenotypeLabel = new Label("Tracked animal genotype: - ");
        borderedMapTrackedAnimalNumberOfChildrenLabel = new Label("Tracked animal number of children: - ");
        borderedMapTrackedAnimalNumberOfDescendantsLabel = new Label("Tracked animal number of descendants: - ");
        borderedMapTrackedAnimalDeathDayLabel = new Label("Tracked animal day of death: - ");
        runaroundMapTrackedAnimalGenotypeLabel = new Label("Tracked animal genotype: - ");
        runaroundMapTrackedAnimalNumberOfChildrenLabel = new Label("Tracked animal number of children: - ");
        runaroundMapTrackedAnimalNumberOfDescendantsLabel = new Label("Tracked animal number of descendants: - ");
        runaroundMapTrackedAnimalDeathDayLabel = new Label("Tracked animal day of death: - ");

        borderedMapChartHBox1 = new HBox(10, borderedMapNumberOfLivingAnimalsChart, borderedMapNumberOfGrassesChart,
                borderedMapAverageEnergyOfLivingAnimalsChart);
        runaroundMapChartHBox1 = new HBox(10, runaroundMapNumberOfLivingAnimalsChart, runaroundMapNumberOfGrassesChart,
                runaroundMapAverageEnergyOfLivingAnimalsChart);

        borderedMapChartHBox2 = new HBox(10, borderedMapAverageLifespanOfDeadAnimalsChart, borderedMapAverageNumberOfChildrenOfLivingAnimalsChart);
        runaroundMapChartHBox2 = new HBox(10, runaroundMapAverageLifespanOfDeadAnimalsChart, runaroundMapAverageNumberOfChildrenOfLivingAnimalsChart);

        borderedMapChartVBox = new VBox(10,  borderedMapChartHBox1,  borderedMapChartHBox2);
        runaroundMapChartVBox = new VBox(10, runaroundMapChartHBox1, runaroundMapChartHBox2);

        borderedMapDominatingGenotypeLabel = new Label("Dominating genotype: " + Arrays.toString(borderedMap.getDominatingGenotype()));
        runaroundMapDominatingGenotypeLabel = new Label("Dominating genotype: " + Arrays.toString(runaroundMap.getDominatingGenotype()));

        borderedMapChartVBox = new VBox(10, borderedMapChartVBox, borderedMapDominatingGenotypeLabel);
        runaroundMapChartVBox = new VBox(10, runaroundMapChartVBox, runaroundMapDominatingGenotypeLabel);

        borderedMapVBox = new VBox(10, borderedMapLabel, borderedMapGridPane, borderedMapActionHBox, borderedMapChartVBox);
        runaroundMapVBox = new VBox(10, runaroundMapLabel, runaroundMapGridPane, runaroundMapActionHBox, runaroundMapChartVBox);



        fullScene = new HBox(50, borderedMapVBox, runaroundMapVBox);


//        setStartButtonActions(borderedMapStartButton, borderedMapEngineThread, borderedMapStartStopButtonsHBox, borderedMapStopButton);
//        setStartButtonActions(runaroundMapStartButton, runaroundMapEngineThread, runaroundMapStartStopButtonsHBox, runaroundMapStopButton);

        borderedMapStartButton.setOnAction(event -> {
            borderedMapEngineThread = new Thread(borderedMapEngine);
            borderedMapEngineThread.start();
            borderedMapStartStopButtonsHBox.getChildren().clear();
            borderedMapStartStopButtonsHBox.getChildren().add(borderedMapStopButton);
            borderedMapActionHBox.getChildren().remove(borderedMapSaveStatisticsButton);
        });

        runaroundMapStartButton.setOnAction(event -> {
            runaroundMapEngineThread = new Thread(runaroundMapEngine);
            runaroundMapEngineThread.start();
            runaroundMapStartStopButtonsHBox.getChildren().clear();
            runaroundMapStartStopButtonsHBox.getChildren().add(runaroundMapStopButton);
            runaroundMapActionHBox.getChildren().remove(runaroundMapSaveStatisticsButton);
        });

        borderedMapStopButton.setOnAction(event -> {
            borderedMapEngineThread.stop();
            borderedMapEngineThread = new Thread(borderedMapEngine);
            borderedMapStartStopButtonsHBox.getChildren().clear();
            borderedMapStartStopButtonsHBox.getChildren().add(borderedMapStartButton);
            borderedMapActionHBox.getChildren().add(borderedMapSaveStatisticsButton);
        });

        runaroundMapStopButton.setOnAction(event -> {
            runaroundMapEngineThread.stop();
            runaroundMapEngineThread = new Thread(runaroundMapEngine);
            runaroundMapStartStopButtonsHBox.getChildren().clear();
            runaroundMapStartStopButtonsHBox.getChildren().add(runaroundMapStartButton);
            runaroundMapActionHBox.getChildren().add(runaroundMapSaveStatisticsButton);
        });

        setShowTrackedAnimalStatisticsButtonActions(borderedMapShowTrackedAnimalStatisticsButton, borderedMapShowStatisticsButtonsHBox, borderedMapShowMapStatisticsButton,
                borderedMapChartVBox, borderedMapTrackedAnimalGenotypeLabel, borderedMapTrackedAnimalNumberOfChildrenLabel,
                borderedMapTrackedAnimalNumberOfDescendantsLabel, borderedMapTrackedAnimalDeathDayLabel);

        setShowTrackedAnimalStatisticsButtonActions(runaroundMapShowTrackedAnimalStatisticsButton, runaroundMapShowStatisticsButtonsHBox, runaroundMapShowMapStatisticsButton,
                runaroundMapChartVBox, runaroundMapTrackedAnimalGenotypeLabel, runaroundMapTrackedAnimalNumberOfChildrenLabel,
                runaroundMapTrackedAnimalNumberOfDescendantsLabel, runaroundMapTrackedAnimalDeathDayLabel);

        setShowMapStatisticsButtonActions(runaroundMapShowMapStatisticsButton, runaroundMapShowStatisticsButtonsHBox, runaroundMapShowTrackedAnimalStatisticsButton,
                runaroundMapChartVBox, runaroundMapChartHBox1, runaroundMapChartHBox2, runaroundMapDominatingGenotypeLabel);

        setShowMapStatisticsButtonActions(borderedMapShowMapStatisticsButton, borderedMapShowStatisticsButtonsHBox, borderedMapShowTrackedAnimalStatisticsButton,
                borderedMapChartVBox, borderedMapChartHBox1, borderedMapChartHBox2, borderedMapDominatingGenotypeLabel);

        Scene mainScene = new Scene(fullScene, sceneWidth, sceneHeight);
        return mainScene;
    }

//    private void setStartButtonActions(Button startButton, Thread engineThread, HBox ancestorHBox, Button stopButton) {
//        startButton.setOnAction(event -> {
//            engineThread.start();
//            ancestorHBox.getChildren().clear();
//            ancestorHBox.getChildren().add(stopButton);
//        });
//    }

    private void setShowTrackedAnimalStatisticsButtonActions(Button showTrackedAnimalStatisticsButton, HBox ancestorHBox, Button showMapStatisticsButton,
                                                             VBox chartBox, Label trackedAnimalGenomeLabel, Label trackedAnimalNumberOfChildrenLabel,
                                                             Label trackedAnimalNumberOfDescendantsLabel, Label trackedAnimalDeathDayLabel){

        showTrackedAnimalStatisticsButton.setOnAction(event -> {
            ancestorHBox.getChildren().clear();
            ancestorHBox.getChildren().add(showMapStatisticsButton);

            chartBox.getChildren().clear();
            chartBox.getChildren().addAll(trackedAnimalGenomeLabel, trackedAnimalNumberOfChildrenLabel, trackedAnimalNumberOfDescendantsLabel, trackedAnimalDeathDayLabel);
        });
    }

    private void setShowMapStatisticsButtonActions(Button showMapStatisticsButton, HBox ancestorHBox, Button showTrackedAnimalStatisticsButton, VBox chartBox,
                                                   HBox chartHBox1, HBox chartHBox2, Label dominatingGenotypeLabel){
        showMapStatisticsButton.setOnAction(event -> {
            ancestorHBox.getChildren().clear();
            ancestorHBox.getChildren().add(showTrackedAnimalStatisticsButton);

            chartBox.getChildren().clear();
            chartBox.getChildren().addAll(chartHBox1, chartHBox2, dominatingGenotypeLabel);
        });

    }

    // code based on tutorial from:
    // https://levelup.gitconnected.com/realtime-charts-with-javafx-ed33c46b9c8d
    public LineChart<Number, Number> createChart(String xAxsisLabel, String yAxisLabel, String chartTitle, XYChart.Series<Number, Number> series){
        final NumberAxis xAxis = new NumberAxis(); // we are gonna plot against time
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxsisLabel);
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel(yAxisLabel);
        yAxis.setAnimated(false); // axis animations are removed

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(chartTitle);
        lineChart.setAnimated(false); // disable animations

        lineChart.getData().add(series);

        return lineChart;
    }

    public XYChart.Series<Number, Number> createSeries(String seriesName){
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(seriesName);
        return series;
    }

    public void addToSeries(XYChart.Series<Number, Number> series, Number xValue, Number yValue){
        series.getData().add(new XYChart.Data<>(xValue, yValue));
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

    public void changeTrackedAnimal(Animal animal){
        this.setTrackedAnimal(animal);
        this.updateTrackedAnimalStatistics(animal);
    }

    private void setTrackedAnimal(Animal animal){
        if(animal.getMap().bordersRunaround()){
            this.runaroundMapTrackedAnimal = animal;
        }
        else{
            this.borderedMapTrackedAnimal = animal;
        }
    }

    private void updateTrackedAnimalStatistics(Animal animal){
        if(animal == null) return;
        if(animal == borderedMapTrackedAnimal) {
            borderedMapTrackedAnimalGenotypeLabel.setText("Tracked animal genotype: " + Arrays.toString(borderedMapTrackedAnimal.getGenotype()));
            borderedMapTrackedAnimalNumberOfChildrenLabel.setText("Tracked animal number of children: " + borderedMapTrackedAnimal.getNumberOfChildren());
            borderedMapTrackedAnimalNumberOfDescendantsLabel.setText("Tracked animal number of descendants: " + + borderedMapTrackedAnimal.getNumberOfDescendants());
            borderedMapTrackedAnimalDeathDayLabel.setText("Tracked animal day of death: " + borderedMapTrackedAnimal.getDeathDay());
        }
        if(animal == runaroundMapTrackedAnimal){
            runaroundMapTrackedAnimalGenotypeLabel.setText("Tracked animal genotype: " + Arrays.toString(runaroundMapTrackedAnimal.getGenotype()));
            runaroundMapTrackedAnimalNumberOfChildrenLabel.setText("Tracked animal number of children: " + runaroundMapTrackedAnimal.getNumberOfChildren());
            runaroundMapTrackedAnimalNumberOfDescendantsLabel.setText("Tracked animal number of descendants: " + + runaroundMapTrackedAnimal.getNumberOfDescendants());
            runaroundMapTrackedAnimalDeathDayLabel.setText("Tracked animal day of death: " + runaroundMapTrackedAnimal.getDeathDay());
        }
    }

    public void fillGridPane(GridPane gridPane, IWorldMap map) throws FileNotFoundException {
        gridPane.setGridLinesVisible(true);
        Vector2d upperRightCorner = map.getUpperRightCorner();
        Vector2d lowerLeftCorner = map.getLowerLeftCorner();

        int width = upperRightCorner.getX() - lowerLeftCorner.getX() + 2; //bo odejmowanie i dodatkowe kolumna/wiersz na indeksy
        int height = upperRightCorner.getY() - lowerLeftCorner.getY() + 2;


        addRowsAndColumnsToGridPane(gridPane, width, height);

        addRowAndColumnDescriptionsToGridPane(gridPane, upperRightCorner, lowerLeftCorner, width, height);


            addAbstractWorldMapElementsToGridPane(gridPane, map, upperRightCorner, lowerLeftCorner);
    }


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
        GuiElementBox box = new GuiElementBox(this, element, elementSize);
        VBox GuiElementVBox = box.getVbox();
        gridPane.add(GuiElementVBox, elementX - lowerLeftCorner.getX() + 1, upperRightCorner.getY() - elementY + 1);
        gridPane.setHalignment(label, HPos.CENTER);
    }

    @Override
    public void dayPassed(AbstractWorldMap map) {
        Platform.runLater(() -> {
            GridPane gridPane;
            XYChart.Series<Number, Number> numberOfLivingAnimalsSeries;
            XYChart.Series<Number, Number> numberOfGrassesSeries;
            XYChart.Series<Number, Number> averageEnergyOfLivingAnimalsSeries;
            XYChart.Series<Number, Number> averageLifespanOfDeadAnimalsSeries;
            XYChart.Series<Number, Number> averageNumberOfChildrenOfLivingAnimalsSeries;
            Label dominatingGenotype;
            Animal trackedAnimal;

            try {
                if (map.bordersRunaround()){
                    gridPane = runaroundMapGridPane;
                    numberOfLivingAnimalsSeries = runaroundMapNumberOfLivingAnimalsSeries;
                    numberOfGrassesSeries = runaroundMapNumberOfGrassesSeries;
                    averageEnergyOfLivingAnimalsSeries = runaroundMapAverageEnergyOfLivingAnimalsSeries;
                    averageLifespanOfDeadAnimalsSeries = runaroundMapAverageLifespanOfDeadAnimalsSeries;
                    averageNumberOfChildrenOfLivingAnimalsSeries = runaroundMapAverageNumberOfChildrenOfLivingAnimalsSeries;
                    dominatingGenotype = runaroundMapDominatingGenotypeLabel;
                    trackedAnimal = runaroundMapTrackedAnimal;
                }
                else {
                    gridPane = borderedMapGridPane;
                    numberOfLivingAnimalsSeries = borderedMapNumberOfLivingAnimalsSeries;
                    numberOfGrassesSeries = borderedMapNumberOfGrassesSeries;
                    averageEnergyOfLivingAnimalsSeries = borderedMapAverageEnergyOfLivingAnimalsSeries;
                    averageLifespanOfDeadAnimalsSeries = borderedMapAverageLifespanOfDeadAnimalsSeries;
                    averageNumberOfChildrenOfLivingAnimalsSeries = borderedMapAverageNumberOfChildrenOfLivingAnimalsSeries;
                    dominatingGenotype = borderedMapDominatingGenotypeLabel;
                    trackedAnimal = borderedMapTrackedAnimal;
                }

                synchronized (LockObject.INSTANCE) {
                    updateGridPane(gridPane, map);
                }

                updateSeries(map, numberOfLivingAnimalsSeries, numberOfGrassesSeries, averageEnergyOfLivingAnimalsSeries,
                        averageLifespanOfDeadAnimalsSeries, averageNumberOfChildrenOfLivingAnimalsSeries);

                dominatingGenotype.setText("Dominating genotype: " + Arrays.toString(map.getDominatingGenotype()));

                updateTrackedAnimalStatistics(trackedAnimal);


            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
        });
    }

    private void updateSeries(AbstractWorldMap map,
                              XYChart.Series<Number, Number> numberOfLivingAnimalsSeries,
                              XYChart.Series<Number, Number> numberOfGrassesSeries,
                              XYChart.Series<Number, Number> averageEnergyOfLivingAnimalsSeries,
                              XYChart.Series<Number, Number> averageLifespanOfDeadAnimalsSeries,
                              XYChart.Series<Number, Number> averageNumberOfChildrenOfLivingAnimalsSeries){

        int day = map.getDay();
        int numberOfLivingAnimals = map.getNumberOfAnimals();
        int numberOfGrasses = map.getNumberOfGrasses();
        double averageEnergyOfLivingAnimals = map.getAverageEnergyOfLivingAnimals();
        double averageLifespanOfDeadAnimals = map.getAverageLifespanOfDeadAnimals();
        double averageNumberOfChildrenOfLivingAnimals = map.getAverageNumberOfChildrenOfLivingAnimals();

        addToSeries(numberOfLivingAnimalsSeries, day, numberOfLivingAnimals);
        addToSeries(numberOfGrassesSeries, day, numberOfGrasses);
        addToSeries(averageEnergyOfLivingAnimalsSeries, day, averageEnergyOfLivingAnimals);
        addToSeries(averageLifespanOfDeadAnimalsSeries, day, averageLifespanOfDeadAnimals);
        addToSeries(averageNumberOfChildrenOfLivingAnimalsSeries, day, averageNumberOfChildrenOfLivingAnimals);
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

    public void saveStatisticsToFile(AbstractWorldMap map){
        List numberOfLivingAnimalsList;
        List numberOfGrassesList;
        List averageEnergyOfLivingAnimalsList;
        List averageLifespanOfDeadAnimalsList;
        List averageNumberOfChildrenOfLivingAnimalsList;

        if(map.bordersRunaround()){
            numberOfLivingAnimalsList = borderedMapNumberOfLivingAnimalsSeries.getData();
        }
        List<String[]> dataLines = new ArrayList<>();
        for(int i = 0; i < )

    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated() throws IOException {
        File csvOutputFile = new File("statistics/outputfile.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        assertTrue(csvOutputFile.exists());
    }
}
