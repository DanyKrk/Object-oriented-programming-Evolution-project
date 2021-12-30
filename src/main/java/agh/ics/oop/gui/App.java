package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
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


public class App extends Application implements IDayObserver, IMagicEventObserver{

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
    private Button borderedMapShowAnimalsWithDominatingGenotypeButton;
    private Button runaroundMapShowAnimalsWithDominatingGenotypeButton;
    private Button borderedMapStopShowingAnimalsWithDominatingGenotypeButton;
    private Button runaroundMapStopShowingAnimalsWithDominatingGenotypeButton;
    private String borderedMapEvolutionRule;
    private String runaroundMapEvolutionRule;

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

        Spinner<Integer> mapWidthSpinner = new Spinner<Integer>(1, 20, 15, 1);
        HBox mapWidthInputHBox = getIntegerSpinnerHBox(mapWidthSpinner, "Map width: ");

        Spinner<Integer> mapHeightSpinner = new Spinner<Integer>(1, 20, 15, 1);
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

        ObservableList<String> evolutionRules = FXCollections.observableArrayList("Magic", "Normal");

        Spinner<String> runaroundMapEvolutionRuleSpinner = new Spinner<String>(evolutionRules);
        Label runaroundMapEvolutionRuleLabel = new Label("Runaround map evolution rule: ");
        HBox runaroundMapEvolutionRuleInputHBox = new HBox(runaroundMapEvolutionRuleLabel, runaroundMapEvolutionRuleSpinner);

        Spinner<String> borderedMapEvolutionRuleSpinner = new Spinner<String>(evolutionRules);
        Label borderedMapEvolutionRuleLabel = new Label("Bordered map evolution rule: ");
        HBox borderedMapEvolutionRuleInputHBox = new HBox(borderedMapEvolutionRuleLabel, borderedMapEvolutionRuleSpinner);

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
            borderedMapEvolutionRule = borderedMapEvolutionRuleSpinner.getValue();
            runaroundMapEvolutionRule = runaroundMapEvolutionRuleSpinner.getValue();

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
                plantEnergyInputHBox, jungleRatioInputHBox, numberOfStartingAnimalsInputHBox, borderedMapEvolutionRuleInputHBox, runaroundMapEvolutionRuleInputHBox,
                startButton);
        inputVBox.setAlignment(Pos.CENTER_LEFT);

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
        runaroundMap.addMagicEventObserver(this);
        borderedMap.addMagicEventObserver(this);

        borderedMapEngine = new SimulationEngine(borderedMap, moveDelay, borderedMapEvolutionRule);
        borderedMapEngineThread = new Thread(borderedMapEngine);
        runaroundMapEngine = new SimulationEngine(runaroundMap, moveDelay, runaroundMapEvolutionRule);
        runaroundMapEngineThread = new Thread(runaroundMapEngine);

        borderedMapLabel = new Label("Bordered map");
        runaroundMapLabel = new Label("Runaround map");

        runaroundMapGridPane = new GridPane();
        borderedMapGridPane = new GridPane();
        fillGridPane(runaroundMapGridPane, runaroundMap);
        fillGridPane(borderedMapGridPane, borderedMap);

        borderedMapStartButton = new Button("Start");
        runaroundMapStartButton = new Button("Start");

        borderedMapStopButton = new Button("Stop");
        runaroundMapStopButton = new Button("Stop");

        borderedMapStartStopButtonsHBox = new HBox(borderedMapStartButton);
        runaroundMapStartStopButtonsHBox = new HBox(runaroundMapStartButton);

        borderedMapShowMapStatisticsButton = new Button("Map statistics");
        borderedMapShowTrackedAnimalStatisticsButton = new Button("Tracked animal statistics");

        runaroundMapShowMapStatisticsButton = new Button("Map statistics");
        runaroundMapShowTrackedAnimalStatisticsButton = new Button("Tracked animal statistics");

        borderedMapShowAnimalsWithDominatingGenotypeButton = new Button("Dominating genotype animals");
        runaroundMapShowAnimalsWithDominatingGenotypeButton = new Button("Dominating genotype animals");

        borderedMapStopShowingAnimalsWithDominatingGenotypeButton = new Button("Stop showing animals with dominating genotype");
        runaroundMapStopShowingAnimalsWithDominatingGenotypeButton = new Button("Stop showing animals with dominating genotype");

        borderedMapShowStatisticsButtonsHBox = new HBox(borderedMapShowAnimalsWithDominatingGenotypeButton, borderedMapShowTrackedAnimalStatisticsButton);
        runaroundMapShowStatisticsButtonsHBox = new HBox(runaroundMapShowAnimalsWithDominatingGenotypeButton, runaroundMapShowTrackedAnimalStatisticsButton);

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

        updateSeries(borderedMap, borderedMapNumberOfLivingAnimalsSeries, borderedMapNumberOfGrassesSeries, borderedMapAverageEnergyOfLivingAnimalsSeries,
                borderedMapAverageLifespanOfDeadAnimalsSeries, borderedMapAverageNumberOfChildrenOfLivingAnimalsSeries);

        updateSeries(runaroundMap, runaroundMapNumberOfLivingAnimalsSeries, runaroundMapNumberOfGrassesSeries, runaroundMapAverageEnergyOfLivingAnimalsSeries,
                runaroundMapAverageLifespanOfDeadAnimalsSeries, runaroundMapAverageNumberOfChildrenOfLivingAnimalsSeries);


        fullScene = new HBox(50, runaroundMapVBox, borderedMapVBox);

        borderedMapStartButton.setOnAction(event -> {
            borderedMapEngineThread = new Thread(borderedMapEngine);
            borderedMapEngineThread.start();

            borderedMapStartStopButtonsHBox.getChildren().clear();
            borderedMapStartStopButtonsHBox.getChildren().add(borderedMapStopButton);

            borderedMapShowStatisticsButtonsHBox.getChildren().remove(borderedMapShowAnimalsWithDominatingGenotypeButton);

            borderedMapActionHBox.getChildren().remove(borderedMapSaveStatisticsButton);
        });

        runaroundMapStartButton.setOnAction(event -> {
            runaroundMapEngineThread = new Thread(runaroundMapEngine);
            runaroundMapEngineThread.start();

            runaroundMapStartStopButtonsHBox.getChildren().clear();
            runaroundMapStartStopButtonsHBox.getChildren().add(runaroundMapStopButton);

            runaroundMapShowStatisticsButtonsHBox.getChildren().remove(runaroundMapShowAnimalsWithDominatingGenotypeButton);

            runaroundMapActionHBox.getChildren().remove(runaroundMapSaveStatisticsButton);
        });

        borderedMapStopButton.setOnAction(event -> {
            borderedMapEngineThread.stop();
            borderedMapEngineThread = new Thread(borderedMapEngine);
            borderedMapStartStopButtonsHBox.getChildren().clear();
            borderedMapStartStopButtonsHBox.getChildren().add(borderedMapStartButton);

            borderedMapShowStatisticsButtonsHBox.getChildren().add(borderedMapShowAnimalsWithDominatingGenotypeButton);

            borderedMapActionHBox.getChildren().add(borderedMapSaveStatisticsButton);
        });

        runaroundMapStopButton.setOnAction(event -> {
            runaroundMapEngineThread.stop();
            runaroundMapEngineThread = new Thread(runaroundMapEngine);
            runaroundMapStartStopButtonsHBox.getChildren().clear();
            runaroundMapStartStopButtonsHBox.getChildren().add(runaroundMapStartButton);

            runaroundMapShowStatisticsButtonsHBox.getChildren().add(runaroundMapShowAnimalsWithDominatingGenotypeButton);

            runaroundMapActionHBox.getChildren().add(runaroundMapSaveStatisticsButton);
        });

        borderedMapSaveStatisticsButton.setOnAction(event -> {
            saveStatisticsToFile(borderedMap);
        });

        runaroundMapSaveStatisticsButton.setOnAction(event -> {
            saveStatisticsToFile(runaroundMap);
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

        setShowAnimalsWithDominatingGenotypeButtonActions(borderedMapShowAnimalsWithDominatingGenotypeButton, borderedMapActionHBox,
                borderedMapStopShowingAnimalsWithDominatingGenotypeButton, borderedMapGridPane, borderedMap);

        setShowAnimalsWithDominatingGenotypeButtonActions(runaroundMapShowAnimalsWithDominatingGenotypeButton, runaroundMapActionHBox,
                runaroundMapStopShowingAnimalsWithDominatingGenotypeButton, runaroundMapGridPane, runaroundMap);

        setStopShowingAnimalsWithDominatingGenotypeButtonActions(borderedMapStopShowingAnimalsWithDominatingGenotypeButton, borderedMapActionHBox,
                borderedMapStartStopButtonsHBox, borderedMapShowStatisticsButtonsHBox, borderedMapSaveStatisticsButton, borderedMapGridPane, borderedMap);

        setStopShowingAnimalsWithDominatingGenotypeButtonActions(runaroundMapStopShowingAnimalsWithDominatingGenotypeButton, runaroundMapActionHBox,
                runaroundMapStartStopButtonsHBox, runaroundMapShowStatisticsButtonsHBox, runaroundMapSaveStatisticsButton, runaroundMapGridPane, runaroundMap);

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
            ancestorHBox.getChildren().remove(showTrackedAnimalStatisticsButton);
            ancestorHBox.getChildren().add(showMapStatisticsButton);

            chartBox.getChildren().clear();
            chartBox.getChildren().addAll(trackedAnimalGenomeLabel, trackedAnimalNumberOfChildrenLabel, trackedAnimalNumberOfDescendantsLabel, trackedAnimalDeathDayLabel);
        });
    }

    private void setShowMapStatisticsButtonActions(Button showMapStatisticsButton, HBox ancestorHBox, Button showTrackedAnimalStatisticsButton, VBox chartBox,
                                                   HBox chartHBox1, HBox chartHBox2, Label dominatingGenotypeLabel){
        showMapStatisticsButton.setOnAction(event -> {
            ancestorHBox.getChildren().remove(showMapStatisticsButton);
            ancestorHBox.getChildren().add(showTrackedAnimalStatisticsButton);

            chartBox.getChildren().clear();
            chartBox.getChildren().addAll(chartHBox1, chartHBox2, dominatingGenotypeLabel);
        });

    }

    private void setShowAnimalsWithDominatingGenotypeButtonActions(Button showAnimalsWithDominatingGenotypeButton, HBox actionHBox,
                                                                   Button stopShowingAnimalsWithDominatingGenotypeButton,
                                                                   GridPane gridPane, AbstractWorldMap map){
        showAnimalsWithDominatingGenotypeButton.setOnAction(event -> {
            actionHBox.getChildren().clear();
            actionHBox.getChildren().add(stopShowingAnimalsWithDominatingGenotypeButton);

            synchronized (LockObject.INSTANCE) {
                try {
                    showAnimalsWithDominatingGenotype(gridPane, map);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setStopShowingAnimalsWithDominatingGenotypeButtonActions(Button stopShowingAnimalsWithDominatingGenotypeButton, HBox actionHBox, HBox startStopHBox,
                                                                          HBox showStatisticsHBox, Button saveStatisticsButton, GridPane gridPane, AbstractWorldMap map){
        stopShowingAnimalsWithDominatingGenotypeButton.setOnAction(event -> {
            actionHBox.getChildren().clear();
            actionHBox.getChildren().add(startStopHBox);
            actionHBox.getChildren().add(showStatisticsHBox);
            actionHBox.getChildren().add(saveStatisticsButton);

            synchronized (LockObject.INSTANCE) {
                try {
                    updateGridPane(gridPane, map);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
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
        if (animal != borderedMapTrackedAnimal && animal != runaroundMapTrackedAnimal) {
            this.sendSignalToStopTrackingAnimal(animal);
            this.setTrackedAnimal(animal);
            this.updateTrackedAnimalStatistics(animal);
        }
    }

    private void sendSignalToStopTrackingAnimal(Animal animal){
        Animal trackedAnimal;
        if(animal.getMap().bordersRunaround()){
            trackedAnimal = runaroundMapTrackedAnimal;
        }
        else{
            trackedAnimal = borderedMapTrackedAnimal;
        }

        if (trackedAnimal == null) return;
        trackedAnimal.stopTracking();
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

    public void fillGridPane(GridPane gridPane, AbstractWorldMap map) throws FileNotFoundException {
        gridPane.setGridLinesVisible(true);
        Vector2d upperRightCorner = map.getUpperRightCorner();
        Vector2d lowerLeftCorner = map.getLowerLeftCorner();

        int width = upperRightCorner.getX() - lowerLeftCorner.getX() + 2; //bo odejmowanie i dodatkowe kolumna/wiersz na indeksy
        int height = upperRightCorner.getY() - lowerLeftCorner.getY() + 2;


        addRowsAndColumnsToGridPane(gridPane, width, height);

        addRowAndColumnDescriptionsToGridPane(gridPane, upperRightCorner, lowerLeftCorner, width, height);


            addAbstractWorldMapElementsToGridPane(gridPane, map, upperRightCorner, lowerLeftCorner);
    }


    private void addAbstractWorldMapElementsToGridPane(GridPane gridPane, AbstractWorldMap map, Vector2d upperRightCorner, Vector2d lowerLeftCorner) throws FileNotFoundException {
        Map<Vector2d, MapSection> positionSectionMap = map.getPositionSectionMap();

        for(MapSection section: positionSectionMap.values()) {
            Object objectAtSection = section.objectAt();
            if (objectAtSection == null) continue;
            if (! (objectAtSection instanceof IMapElement)) continue;
            IMapElement IMapElementAtSection = (IMapElement) objectAtSection;
            addGuiElementBoxToGridPane(gridPane, upperRightCorner, lowerLeftCorner, IMapElementAtSection);
        }
    }

    private void addAnimalsWithDominatingGenotypeToGridPane(GridPane gridPane, AbstractWorldMap map, Vector2d upperRightCorner, Vector2d lowerLeftCorner) throws FileNotFoundException {
        Map<Vector2d, MapSection> positionSectionMap = map.getPositionSectionMap();
        int numberOfAnimalsAdded = 0;
        int[] dominatingGenotype = map.getDominatingGenotype();
        for(MapSection section: positionSectionMap.values()) {
            Animal animalToShow;
            List<Animal> animalsWithDominatingGenotypeAtSection = section.getAnimalsWithGenotype(dominatingGenotype);

            if(animalsWithDominatingGenotypeAtSection.size() > 0) {
                animalToShow = animalsWithDominatingGenotypeAtSection.get(0);
            }
            else continue;

            if (animalToShow == null) continue;
            numberOfAnimalsAdded += 1;
            IMapElement IMapElementAtSection = animalToShow;
            addGuiElementBoxToGridPane(gridPane, upperRightCorner, lowerLeftCorner, IMapElementAtSection);
        }
        if(numberOfAnimalsAdded == 0){
            System.out.println("cos nie tak");
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
                if (map.bordersRunaround()) {
                    gridPane = runaroundMapGridPane;
                    numberOfLivingAnimalsSeries = runaroundMapNumberOfLivingAnimalsSeries;
                    numberOfGrassesSeries = runaroundMapNumberOfGrassesSeries;
                    averageEnergyOfLivingAnimalsSeries = runaroundMapAverageEnergyOfLivingAnimalsSeries;
                    averageLifespanOfDeadAnimalsSeries = runaroundMapAverageLifespanOfDeadAnimalsSeries;
                    averageNumberOfChildrenOfLivingAnimalsSeries = runaroundMapAverageNumberOfChildrenOfLivingAnimalsSeries;
                    dominatingGenotype = runaroundMapDominatingGenotypeLabel;
                    trackedAnimal = runaroundMapTrackedAnimal;
                } else {
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

    private void showAnimalsWithDominatingGenotype(GridPane gridPane, AbstractWorldMap map) throws FileNotFoundException {
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
        addAnimalsWithDominatingGenotypeToGridPane(gridPane, map, upperRightCorner, lowerLeftCorner);
//        }

        gridPane.setGridLinesVisible(true);
    }

    public void saveStatisticsToFile(AbstractWorldMap map){
        ObservableList<XYChart.Data<Number, Number>> numberOfLivingAnimalsList;
        ObservableList<XYChart.Data<Number, Number>> numberOfGrassesList;
        ObservableList<XYChart.Data<Number, Number>> averageEnergyOfLivingAnimalsList;
        ObservableList<XYChart.Data<Number, Number>> averageLifespanOfDeadAnimalsList;
        ObservableList<XYChart.Data<Number, Number>> averageNumberOfChildrenOfLivingAnimalsList;
        String pathName;

        if(map.bordersRunaround()){
            pathName = "statistics/runaroundMapOutputFile.csv";
            numberOfLivingAnimalsList = runaroundMapNumberOfLivingAnimalsSeries.getData();
            numberOfGrassesList = runaroundMapNumberOfGrassesSeries.getData();
            averageEnergyOfLivingAnimalsList = runaroundMapAverageEnergyOfLivingAnimalsSeries.getData();
            averageLifespanOfDeadAnimalsList = runaroundMapAverageLifespanOfDeadAnimalsSeries.getData();
            averageNumberOfChildrenOfLivingAnimalsList = runaroundMapAverageNumberOfChildrenOfLivingAnimalsSeries.getData();
        }
        else{
            pathName = "statistics/borderedMapOutputFile.csv";
            numberOfLivingAnimalsList = borderedMapNumberOfLivingAnimalsSeries.getData();
            numberOfGrassesList = borderedMapNumberOfGrassesSeries.getData();
            averageEnergyOfLivingAnimalsList = borderedMapAverageEnergyOfLivingAnimalsSeries.getData();
            averageLifespanOfDeadAnimalsList = borderedMapAverageLifespanOfDeadAnimalsSeries.getData();
            averageNumberOfChildrenOfLivingAnimalsList = borderedMapAverageNumberOfChildrenOfLivingAnimalsSeries.getData();
        }

        int dataLen = numberOfLivingAnimalsList.size();
        List<String[]> dataLines = new ArrayList<>();
        dataLines.add(new String[]{
                "Day",
                "Number of living animals",
                "Number of grasses",
                "Average energy of living animals",
                "Average lifespan of dead animals",
                "Average number of children of living animals"
        });

        for(int i = 0; i < dataLen; i++){
            String[] dataLine = {
                    numberOfLivingAnimalsList.get(i).getXValue().toString(),
                    numberOfLivingAnimalsList.get(i).getYValue().toString(),
                    numberOfGrassesList.get(i).getYValue().toString(),
                    averageEnergyOfLivingAnimalsList.get(i).getYValue().toString(),
                    averageLifespanOfDeadAnimalsList.get(i).getYValue().toString(),
                    averageNumberOfChildrenOfLivingAnimalsList.get(i).getYValue().toString()
            };
            dataLines.add(dataLine);
        }

        try {
            convertToCSVAndWriteDataToFile(dataLines, pathName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(";"));
    }

    public String escapeSpecialCharacters(String data) {
        data = data.replace(".", ",");
        return data;
    }

    public void convertToCSVAndWriteDataToFile(List<String[]> dataLines, String pathName) throws IOException {
        File csvOutputFile = new File(pathName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
    }

    public void magicHappened(AbstractWorldMap map){
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Magic Alert!");
//            int numberOfMagicEventsLeft = 3 - map.getNumberOfMagicEvents();
//            alert.setContentText("Number of possible magic events left: " + numberOfMagicEventsLeft);
//            if(map.bordersRunaround()){
//                alert.setHeaderText("Magic event happened on runaround map!!!");
//            }
//            else{
//                alert.setHeaderText("Magic event happened on bordered map!!!");
//            }
//            alert.show();
//        });
//        if(map.bordersRunaround()){
//                runaroundMapMagicLabel.setText();
//            }
//            else{
//                borderedMapMagicLabel.setText();
//            }
    }
}
