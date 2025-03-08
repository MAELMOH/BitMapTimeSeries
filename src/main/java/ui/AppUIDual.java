package main.java.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

public class AppUIDual extends Application {
    private IoTimeSeries ioTimeSeries1 = new IoTimeSeries();
    private Sax sax1 = new Sax();
    private Matrice_Sax matrice1 = new Matrice_Sax();
    private BitMap bitMap1 = new BitMap();

    private IoTimeSeries ioTimeSeries2 = new IoTimeSeries();
    private Sax sax2 = new Sax();
    private Matrice_Sax matrice2 = new Matrice_Sax();
    private BitMap bitMap2 = new BitMap();

    private List<Double> numbersList1;
    private List<Double> numbersList2;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Diagramme de Signal - Double Interface");

        // Première interface
        VBox interface1 = createInterface(ioTimeSeries1, sax1, matrice1, bitMap1, "Instance 1");
        // Deuxième interface
        VBox interface2 = createInterface(ioTimeSeries2, sax2, matrice2, bitMap2, "Instance 2");

        // Organisation globale
        HBox root = new HBox(20, interface1, interface2);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 1600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInterface(IoTimeSeries ioTimeSeries, Sax sax, Matrice_Sax matrice, BitMap bitMap, String instanceName) {
        TextField fileNameField = new TextField();
        fileNameField.setPromptText("Aucun fichier sélectionné");
        fileNameField.setEditable(false);

        Button fileButton = new Button("File");
        fileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                fileNameField.setText(selectedFile.getName());
                loadFile(ioTimeSeries, selectedFile);
            }
        });

        TextField valueField = new TextField();
        valueField.setPromptText("Entrez des valeurs (ex: 1,3,5)");
        valueField.setPrefWidth(300);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Index");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Valeur");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Diagramme de Signal");
        lineChart.setCreateSymbols(false);

        Button applyButton = new Button("Appliquer");
        applyButton.setOnAction(e -> {
            String input = valueField.getText();
            if (!input.isEmpty()) {
                try {
                    List<Double> values = parseValues(input);
                    plotSignalWithValues(lineChart, values, ioTimeSeries == ioTimeSeries1 ? numbersList1 : numbersList2);
                    if (ioTimeSeries == ioTimeSeries1 ? numbersList1 != null : numbersList2 != null) {
                        sax.saisirIntervalles(values);
                        sax.setNumbersList(ioTimeSeries == ioTimeSeries1 ? numbersList1 : numbersList2);
                        sax.transfo();
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Valeurs invalides : " + ex.getMessage());
                }
            }
        });

        TextField intervalCountField = new TextField();
        intervalCountField.setPromptText("Nombre d'intervalles");
        intervalCountField.setPrefWidth(150);

        Button automaticButton = new Button("Automatique");

        automaticButton.setOnAction(e -> {
            if ((ioTimeSeries == ioTimeSeries1 ? numbersList1 != null : numbersList2 != null) && !intervalCountField.getText().isEmpty()) {
                try {
                    lineChart.getData().clear();
                    plotSignalWithValues(lineChart, new ArrayList<>(), ioTimeSeries == ioTimeSeries1 ? numbersList1 : numbersList2);
                    int intervalCount = Integer.parseInt(intervalCountField.getText().trim());
                    List<Double> intervalBounds = calculateIntervals(ioTimeSeries == ioTimeSeries1 ? numbersList1 : numbersList2, intervalCount);
                    sax.saisirIntervalles(intervalBounds);
                    sax.setNumbersList(ioTimeSeries == ioTimeSeries1 ? numbersList1 : numbersList2);
                    sax.transfo();
                    plotAutomaticIntervals(lineChart, intervalBounds);
                } catch (NumberFormatException ex) {
                    System.err.println("Nombre d'intervalles invalide : " + ex.getMessage());
                }
            } else {
                System.err.println("Veuillez charger un fichier et entrer un nombre d'intervalles.");
            }
        });

        Label lengthLabel = new Label("Longueur");
        TextField lengthField = new TextField();
        lengthField.setPromptText("Longueur des mots");

        Label letterCountLabel = new Label("NB Lettre");
        ChoiceBox<Integer> letterCountChoiceBox = new ChoiceBox<>();
        letterCountChoiceBox.getItems().addAll(1, 2);
        letterCountChoiceBox.setValue(1);

        Button generateBitmapButton = new Button("Générer Bitmap");

        ImageView bitmapView = new ImageView();
        bitmapView.setFitWidth(200);
        bitmapView.setFitHeight(200);
        bitmapView.setPreserveRatio(true);

        TextArea matrixDisplay = new TextArea();
        matrixDisplay.setEditable(false);
        matrixDisplay.setPromptText("Matrice Normalisée");
        matrixDisplay.setPrefWidth(200);
        matrixDisplay.setPrefHeight(200);

        generateBitmapButton.setOnAction(e -> {
            String lengthInput = lengthField.getText();
            Integer letterCount = letterCountChoiceBox.getValue();
            if (lengthInput.isEmpty() || letterCount == null || sax.getLettersList() == null) {
                System.err.println("Veuillez entrer une longueur valide et transformer la liste.");
                return;
            }
            try {
                int wordLength = Integer.parseInt(lengthInput);
                matrice.decouperEnMots(sax.getLettersList(), wordLength);
                int[][] occurrenceMatrix;
                List<Character> alphabetList = new ArrayList<>();
                for (char c : sax.getAlphabet()) {
                    alphabetList.add(c);
                }
                if (letterCount == 1) {
                    char[][] letterMatrix = matrice.genererMatriceUneLettre(alphabetList, sax.getIntervalBounds());
                    occurrenceMatrix = matrice.compterOccurrencesUneLettre(letterMatrix);
                } else {
                    String[][] combinationMatrix = matrice.genererMatriceDeuxLettres(alphabetList);
                    occurrenceMatrix = matrice.compterOccurrencesDeuxLettres(combinationMatrix);
                }
                bitMap.genererBitmap(occurrenceMatrix, "generated_bitmap_" + instanceName);
                File bitmapFile = new File("tests/generated_bitmap_" + instanceName + ".bmp");
                BufferedImage originalImage = ImageIO.read(bitmapFile);
                BufferedImage scaledImage = bitMap.scaleImage(originalImage, 200, 200);
                File scaledFile = new File("tests/generated_bitmap_image_" + instanceName + ".bmp");
                ImageIO.write(scaledImage, "bmp", scaledFile);
                Image bitmapImage = new Image(scaledFile.toURI().toString());
                bitmapView.setImage(bitmapImage);

                // Afficher la matrice normalisée de manière présentable
                StringBuilder matrixText = new StringBuilder();
                int maxDigits = 0;
                for (int[] row : bitMap.getNormalizedMatrix()) {
                    for (int val : row) {
                        maxDigits = Math.max(maxDigits, String.valueOf(val).length());
                    }
                }
                for (int[] row : bitMap.getNormalizedMatrix()) {
                    for (int val : row) {
                        matrixText.append(String.format("| %" + maxDigits + "d | ", val));
                    }
                    matrixText.append("\n");
                }
                matrixDisplay.setText(matrixText.toString());
            } catch (Exception ex) {
                System.err.println("Erreur lors de la génération du bitmap : " + ex.getMessage());
            }
        });

        HBox bitmapControls = new HBox(10, lengthLabel, lengthField, letterCountLabel, letterCountChoiceBox, generateBitmapButton);
        bitmapControls.setPadding(new Insets(10));

        HBox bitmapAndMatrix = new HBox(10, matrixDisplay, new StackPane(bitmapView));
        bitmapAndMatrix.setPadding(new Insets(10));

        HBox fileBox = new HBox(10, fileButton, fileNameField);
        fileBox.setPadding(new Insets(10));

        HBox inputBox = new HBox(10, valueField, applyButton, intervalCountField, automaticButton);
        inputBox.setPadding(new Insets(10));

        VBox root = new VBox(10, fileBox, inputBox, lineChart, bitmapControls, bitmapAndMatrix);
        root.setPadding(new Insets(10));
        VBox.setVgrow(lineChart, Priority.ALWAYS);

        return root;
    }

    private void loadFile(IoTimeSeries ioTimeSeries, File file) {
        try {
            if (ioTimeSeries == ioTimeSeries1) {
                numbersList1 = ioTimeSeries.lireListe(file.getAbsolutePath());
            } else {
                numbersList2 = ioTimeSeries.lireListe(file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier : " + e.getMessage());
        }
    }

    private void plotSignalWithValues(LineChart<Number, Number> lineChart, List<Double> yValues, List<Double> numbersList) {
        lineChart.getData().clear();
        if (numbersList != null) {
            XYChart.Series<Number, Number> signalSeries = new XYChart.Series<>();
            signalSeries.setName("Signal");
            for (int i = 0; i < numbersList.size(); i++) {
                signalSeries.getData().add(new XYChart.Data<>(i, numbersList.get(i)));
            }
            lineChart.getData().add(signalSeries);
        }
        for (double yValue : yValues) {
            XYChart.Series<Number, Number> valueSeries = new XYChart.Series<>();
            valueSeries.setName("y = " + yValue);
            for (int i = 0; i < numbersList.size(); i++) {
                valueSeries.getData().add(new XYChart.Data<>(i, yValue));
            }
            lineChart.getData().add(valueSeries);
            Platform.runLater(() -> {
                if (valueSeries.getNode() != null) {
                    valueSeries.getNode().lookup(".chart-series-line").setStyle(
                            "-fx-stroke-dash-array: 5 5; -fx-stroke-width: 1.5; -fx-stroke: gray;");
                }
            });
        }
    }

    private void plotAutomaticIntervals(LineChart<Number, Number> lineChart, List<Double> bounds) {
        for (double bound : bounds) {
            XYChart.Series<Number, Number> boundarySeries = new XYChart.Series<>();
            boundarySeries.setName("y = " + bound);
            for (int i = 0; i < (numbersList1 != null ? numbersList1 : numbersList2).size(); i++) {
                boundarySeries.getData().add(new XYChart.Data<>(i, bound));
            }
            lineChart.getData().add(boundarySeries);
            Platform.runLater(() -> {
                if (boundarySeries.getNode() != null) {
                    boundarySeries.getNode().lookup(".chart-series-line").setStyle(
                            "-fx-stroke-dash-array: 5 5; -fx-stroke-width: 1.5; -fx-stroke: gray;");
                }
            });
        }
    }

    private List<Double> calculateIntervals(List<Double> list, int n) {
        List<Double> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList);
        int size = sortedList.size();
        int baseSize = size / n;
        int remainder = size % n;
        List<Double> bounds = new ArrayList<>();
        int currentIndex = 0;
        for (int i = 0; i < n; i++) {
            currentIndex += baseSize;
            if (i < remainder) {
                currentIndex++;
            }
            if (currentIndex < size) {
                bounds.add(sortedList.get(currentIndex - 1));
            }
        }
        return bounds;
    }

    private List<Double> parseValues(String input) {
        List<Double> values = new ArrayList<>();
        String[] tokens = input.split(",");
        for (String token : tokens) {
            values.add(Double.parseDouble(token.trim()));
        }
        return values;
    }

    public static void main(String[] args) {
        launch(args);
    }
}