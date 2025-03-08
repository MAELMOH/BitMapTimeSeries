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

public class AppUI extends Application {

    private IoTimeSeries ioTimeSeries = new IoTimeSeries();
    private Sax sax = new Sax();
    private Matrice_Sax matrice = new Matrice_Sax();
    private BitMap bitMap = new BitMap();

    private List<Double> numbersList;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Diagramme de Signal");

        // Zone pour afficher le nom du fichier choisi
        TextField fileNameField = new TextField();
        fileNameField.setPromptText("Aucun fichier sélectionné");
        fileNameField.setEditable(false);

        // Bouton pour choisir un fichier
        Button fileButton = new Button("File");
        fileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileNameField.setText(selectedFile.getName());
                loadFile(selectedFile);
            }
        });

        // Zone pour écrire plusieurs valeurs
        TextField valueField = new TextField();
        valueField.setPromptText("Entrez des valeurs (ex: 1,3,5)");
        valueField.setPrefWidth(300);

        // Axes et diagramme
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Index");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Valeur");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Diagramme de Signal");
        lineChart.setCreateSymbols(false);

        // Zone pour afficher la liste transformée
        TextArea transformedListArea = new TextArea();
        transformedListArea.setEditable(false);
        transformedListArea.setPromptText("Liste transformée en lettres");
        transformedListArea.setPrefHeight(100);

        // Bouton pour appliquer les valeurs
        Button applyButton = new Button("Appliquer");
        applyButton.setOnAction(e -> {
            String input = valueField.getText();
            if (!input.isEmpty()) {
                try {
                    List<Double> values = parseValues(input);
                    plotSignalWithValues(lineChart, values);

                    if (numbersList != null) {
                        sax.saisirIntervalles(values);
                        sax.setNumbersList(numbersList);
                        sax.transfo();
                        transformedListArea.setText("Liste transformée en lettres : " + sax.getLettersList());
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Valeurs invalides : " + ex.getMessage());
                }
            }
        });

        // Bouton automatique et champ de saisie pour le nombre d'intervalles
        TextField intervalCountField = new TextField();
        intervalCountField.setPromptText("Nombre d'intervalles");
        intervalCountField.setPrefWidth(150);
        Button automaticButton = new Button("Automatique");
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPromptText("Liste transformée");
        resultArea.setPrefHeight(100);

        automaticButton.setOnAction(e -> {
            if (numbersList != null && !intervalCountField.getText().isEmpty()) {
                try {
                    lineChart.getData().clear(); // Nettoyer le graphe avant de tracer de nouvelles lignes
                    plotSignalWithValues(lineChart, new ArrayList<>());

                    int intervalCount = Integer.parseInt(intervalCountField.getText().trim());
                    List<Double> intervalBounds = calculateIntervals(numbersList, intervalCount);
                    sax.saisirIntervalles(intervalBounds);
                    sax.setNumbersList(numbersList);
                    sax.transfo();
                    resultArea.setText("Liste transformée en lettres : " + sax.getLettersList());
                    plotAutomaticIntervals(lineChart, intervalBounds);
                } catch (NumberFormatException ex) {
                    System.err.println("Nombre d'intervalles invalide : " + ex.getMessage());
                }
            } else {
                System.err.println("Veuillez charger un fichier et entrer un nombre d'intervalles.");
            }
        });

        // Zone pour la génération et l'affichage du bitmap
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

                bitMap.genererBitmap(occurrenceMatrix, "generated_bitmap");
                File bitmapFile = new File("tests/generated_bitmap.bmp");
                BufferedImage originalImage = ImageIO.read(bitmapFile);
                BufferedImage scaledImage = bitMap.scaleImage(originalImage, 200, 200);

                File scaledFile = new File("tests/generated_bitmap_image.bmp");
                ImageIO.write(scaledImage, "bmp", scaledFile);

                Image bitmapImage = new Image(scaledFile.toURI().toString());
                bitmapView.setImage(bitmapImage);
            } catch (Exception ex) {
                System.err.println("Erreur lors de la génération du bitmap : " + ex.getMessage());
            }
        });

        HBox bitmapControls = new HBox(10, lengthLabel, lengthField, letterCountLabel, letterCountChoiceBox, generateBitmapButton);
        bitmapControls.setPadding(new Insets(10));

        StackPane bitmapContainer = new StackPane(bitmapView);
        bitmapContainer.setPadding(new Insets(10));

        // Organisation de l'interface
        HBox fileBox = new HBox(10, fileButton, fileNameField);
        fileBox.setPadding(new Insets(10));

        HBox inputBox = new HBox(10, valueField, applyButton, intervalCountField, automaticButton);
        inputBox.setPadding(new Insets(10));

        VBox root = new VBox(10, fileBox, inputBox, lineChart, bitmapControls, bitmapContainer);
        root.setPadding(new Insets(10));
        VBox.setVgrow(lineChart, Priority.ALWAYS);

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFile(File file) {
        try {
            numbersList = ioTimeSeries.lireListe(file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier : " + e.getMessage());
        }
    }

    private void plotSignalWithValues(LineChart<Number, Number> lineChart, List<Double> yValues) {
        lineChart.getData().clear();

        // Tracer les valeurs du fichier
        if (numbersList != null) {
            XYChart.Series<Number, Number> signalSeries = new XYChart.Series<>();
            signalSeries.setName("Signal");
            for (int i = 0; i < numbersList.size(); i++) {
                signalSeries.getData().add(new XYChart.Data<>(i, numbersList.get(i)));
            }
            lineChart.getData().add(signalSeries);
        }

        // Tracer plusieurs lignes horizontales y = valeur
        for (double yValue : yValues) {
            XYChart.Series<Number, Number> valueSeries = new XYChart.Series<>();
            valueSeries.setName("y = " + yValue);
            for (int i = 0; i < numbersList.size(); i++) {
                valueSeries.getData().add(new XYChart.Data<>(i, yValue));
            }
            lineChart.getData().add(valueSeries);

            // Appliquer le style directement pour une ligne pointillée
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
            for (int i = 0; i < numbersList.size(); i++) {
                boundarySeries.getData().add(new XYChart.Data<>(i, bound));
            }
            lineChart.getData().add(boundarySeries);

            // Appliquer un style pour rendre la ligne pointillée
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




