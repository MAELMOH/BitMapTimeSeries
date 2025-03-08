package main.java.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import main.java.IoTimeSeries;
import main.java.Sax;
import main.java.Matrice_Sax;
import main.java.BitMap;

public class AppClusterUI extends Application {

    private IoTimeSeries ioTimeSeries = new IoTimeSeries();
    private Sax sax = new Sax();
    private Matrice_Sax matrice = new Matrice_Sax();
    private BitMap bitMap = new BitMap();

    private TextField directoryField;
    private TextField intervalCountField;
    private TextField lengthField;
    private ChoiceBox<Integer> letterCountChoiceBox;
    private TextField manualIntervalsField;
    private TextArea resultArea;
    private ListView<HBox> bitmapsListView = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Clustering Interface - Répertoire");

        // Sélection du répertoire
        directoryField = new TextField();
        directoryField.setPromptText("Sélectionnez un répertoire");
        directoryField.setEditable(false);
        Button dirButton = new Button("Choisir Répertoire");
        dirButton.setOnAction(e -> choisirRepertoire(primaryStage));

        // Nombre d'intervalles automatiques
        intervalCountField = new TextField();
        intervalCountField.setPromptText("Nombre d'intervalles");

        // Bornes manuelles (optionnelles)
        manualIntervalsField = new TextField();
        manualIntervalsField.setPromptText("Bornes (ex: 1,3,5)");

        // Longueur des mots et nombre de lettres par mot
        lengthField = new TextField();
        lengthField.setPromptText("Longueur des mots");

        letterCountChoiceBox = new ChoiceBox<>();
        letterCountChoiceBox.getItems().addAll(1, 2);
        letterCountChoiceBox.setValue(1);

        // Bouton GO pour traiter le répertoire
        Button goButton = new Button("GO");
        goButton.setOnAction(e -> traiterRepertoire());

        // Zone de résultats (affichage des listes normalisées)
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(200);

        // Bouton pour générer les bitmaps
        Button generateBitmapsButton = new Button("Générer Bitmaps");
        generateBitmapsButton.setOnAction(e -> genererEtAfficherBitmaps());

        // Mise en page
        VBox root = new VBox(10,
                new HBox(10, dirButton, directoryField),
                new HBox(10, new Label("Intervalles automatiques:"), intervalCountField),
                new HBox(10, new Label("ou Bornes manuelles:"), manualIntervalsField),
                new HBox(10, new Label("Longueur mot:"), lengthField, new Label("Nb Lettre:"), letterCountChoiceBox, goButton),
                resultArea,
                generateBitmapsButton,
                new Label("Bitmaps Générés :"),
                bitmapsListView
        );
        root.setPadding(new Insets(10));

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void choisirRepertoire(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDir = directoryChooser.showDialog(primaryStage);
        if (selectedDir != null) {
            directoryField.setText(selectedDir.getName()); // Affiche uniquement le nom du dossier
        }
    }

    private void traiterRepertoire() {
        resultArea.clear();
        bitmapsListView.getItems().clear(); // Effacer les bitmaps précédents

        File directory = new File(directoryField.getText());
        if (!directory.isDirectory()) {
            resultArea.setText("Veuillez sélectionner un répertoire valide.");
            return;
        }

        try {
            int intervalCount = Integer.parseInt(intervalCountField.getText().trim());
            int wordLength = Integer.parseInt(lengthField.getText().trim());
            int letterCount = letterCountChoiceBox.getValue();

            List<String> results = new ArrayList<>();

            for (File file : Objects.requireNonNull(directory.listFiles((dir, name) -> name.endsWith(".csv")))) {
                List<Double> numbersList = ioTimeSeries.lireListe(file.getAbsolutePath());

                // Définir les intervalles automatiques ou manuels
                List<Double> intervals;
                if (!manualIntervalsField.getText().trim().isEmpty()) {
                    intervals = parseValues(manualIntervalsField.getText());
                } else {
                    intervals = calculateIntervals(numbersList, intervalCount);
                }

                sax.saisirIntervalles(intervals);
                sax.setNumbersList(numbersList);
                sax.transfo();

                // Découper en mots
                matrice.decouperEnMots(sax.getLettersList(), wordLength);

                // Générer la matrice d'occurrence
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

                // Normaliser la matrice
                int[][] normalizedMatrix = normaliserMatrice(occurrenceMatrix, trouverMax(occurrenceMatrix));
                List<Integer> normalizedList = flattenMatrix(normalizedMatrix);

                // Ajouter les résultats à la liste
                results.add(file.getName() + " : " + normalizedList.toString());
            }

            // Afficher tous les résultats
            resultArea.setText(String.join("\n", results));

        } catch (NumberFormatException ex) {
            resultArea.setText("Erreur : Veuillez entrer des valeurs valides pour les paramètres.");
        } catch (Exception ex) {
            resultArea.setText("Erreur lors du traitement : " + ex.getMessage());
        }
    }

    private void genererEtAfficherBitmaps() {
        File directory = new File(directoryField.getText());
        if (!directory.isDirectory()) {
            resultArea.setText("Veuillez sélectionner un répertoire valide.");
            return;
        }

        try {
            int intervalCount = Integer.parseInt(intervalCountField.getText().trim());

            for (File file : Objects.requireNonNull(directory.listFiles((dir, name) -> name.endsWith(".csv")))) {
                List<Double> numbersList = ioTimeSeries.lireListe(file.getAbsolutePath());

                // Récupérer les bornes d'intervalles
                List<Double> intervals;
                if (!manualIntervalsField.getText().trim().isEmpty()) {
                    intervals = parseValues(manualIntervalsField.getText());
                } else {
                    intervals = calculateIntervals(numbersList, intervalCount);
                }

                sax.saisirIntervalles(intervals);
                sax.setNumbersList(numbersList);
                sax.transfo();

                // Découper en mots
                matrice.decouperEnMots(sax.getLettersList(), Integer.parseInt(lengthField.getText()));

                // Générer la matrice d'occurrence
                int[][] occurrenceMatrix;
                List<Character> alphabetList = new ArrayList<>();
                for (char c : sax.getAlphabet()) {
                    alphabetList.add(c);
                }

                if (letterCountChoiceBox.getValue() == 1) {
                    char[][] letterMatrix = matrice.genererMatriceUneLettre(alphabetList, sax.getIntervalBounds());
                    occurrenceMatrix = matrice.compterOccurrencesUneLettre(letterMatrix);
                } else {
                    String[][] combinationMatrix = matrice.genererMatriceDeuxLettres(alphabetList);
                    occurrenceMatrix = matrice.compterOccurrencesDeuxLettres(combinationMatrix);
                }

                // Normaliser la matrice
                int[][] normalizedMatrix = normaliserMatrice(occurrenceMatrix, trouverMax(occurrenceMatrix));

                // Convertir la liste normalisée en matrice carrée (numIntervals x numIntervals)
                int numIntervals = sax.getIntervalBounds().size() + 1; // NB_lettre = intervalBounds.size() + 1
                int[][] squareMatrix = convertListToSquareMatrix(flattenMatrix(normalizedMatrix), numIntervals);

                // Afficher la matrice carrée sur la console
                System.out.println("Matrice carrée pour " + file.getName() + ":");
                for (int[] row : squareMatrix) {
                    System.out.println(Arrays.toString(row));
                }

                // Générer le bitmap
                String fileName = file.getName().replace(".csv", "");
                bitMap.genererBitmap(squareMatrix, fileName); // Génère le fichier bitmap

                // Charger l'image bitmap
                BufferedImage originalImage = ImageIO.read(new File("tests/" + fileName + ".bmp"));
                BufferedImage scaledImage = bitMap.scaleImage(originalImage, 150, 150); // Redimensionner l'image
                Image fxImage = SwingFXUtils.toFXImage(scaledImage, null);

                // Créer un ImageView pour l'image
                ImageView imageView = new ImageView(fxImage);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(150);

                // Ajouter le nom du fichier à côté de l'image
                Label label = new Label(file.getName());
                HBox bitmapBox = new HBox(10, label, imageView); // Conteneur horizontal pour le nom et l'image

                // Ajouter le conteneur à la liste déroulante
                bitmapsListView.getItems().add(bitmapBox);
            }

        } catch (IOException | NumberFormatException ex) {
            resultArea.setText("Erreur : " + ex.getMessage());
        } catch (Exception ex) {
            resultArea.setText("Erreur lors du traitement : " + ex.getMessage());
        }
    }

    private List<Double> parseValues(String input) {
        List<Double> values = new ArrayList<>();
        for (String token : input.split(",")) {
            values.add(Double.parseDouble(token.trim()));
        }
        return values;
    }

    private List<Double> calculateIntervals(List<Double> list, int n) {
        List<Double> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList);

        int baseSize = sortedList.size() / n;
        int remainder = sortedList.size() % n;

        List<Double> bounds = new ArrayList<>();
        int index = 0;

        for (int i = 0; i < n; i++) {
            index += baseSize;
            if (i < remainder) index++;
            if (index < sortedList.size()) {
                bounds.add(sortedList.get(index - 1));
            }
        }

        return bounds;
    }

    private int[][] normaliserMatrice(int[][] matrix, int max) {
        if (max == 0) max = 1; // Éviter la division par zéro
        int[][] normalized = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                normalized[i][j] = (int) ((matrix[i][j] / (double) max) * 100);
            }
        }
        return normalized;
    }

    private int trouverMax(int[][] matrix) {
        return Arrays.stream(matrix).flatMapToInt(Arrays::stream).max().orElse(0);
    }

    private List<Integer> flattenMatrix(int[][] matrix) {
        List<Integer> flatList = new ArrayList<>();
        for (int[] row : matrix) {
            flatList.addAll(Arrays.stream(row).boxed().toList());
        }
        return flatList;
    }

    private int[][] convertListToSquareMatrix(List<Integer> list, int size) {
        int[][] matrix = new int[size][size];
        int index = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (index < list.size()) {
                    matrix[i][j] = list.get(index++);
                } else {
                    matrix[i][j] = 0; // Remplir avec des zéros si nécessaire
                }
            }
        }

        return matrix;
    }

    public static void main(String[] args) {
        launch(args);
    }
}







