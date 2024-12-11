//package main.java.ui;
//
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import main.java.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AppUI extends Application {
//
//    private IoTimeSeries ioTimeSeries = new IoTimeSeries();
//    private Sax sax = new Sax();
//    private Matrice_Sax matrice = new Matrice_Sax();
//    private BitMap bitMap = new BitMap();
//
//    private List<Double> numbersList = new ArrayList<>();
//    private List<Character> lettersList = new ArrayList<>();
//    private List<String> wordsList = new ArrayList<>();
//    private int[][] occurrenceMatrix;
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Application JavaFX - BitMap Time Series");
//
//        // Création des composants de l'interface
//        Label fileLabel = new Label("Sélectionnez un fichier :");
//        TextField filePathField = new TextField();
//        filePathField.setPromptText("Chemin du fichier...");
//        Button browseButton = new Button("Parcourir");
//        Button loadFileButton = new Button("Charger Fichier");
//
//        Label intervalLabel = new Label("Saisissez les intervalles (séparés par des virgules) :");
//        TextField intervalField = new TextField();
//        intervalField.setPromptText("Exemple : 10,20,30");
//
//        Label wordLengthLabel = new Label("Longueur des mots :");
//        TextField wordLengthField = new TextField();
//        wordLengthField.setPromptText("Exemple : 3");
//
//        Label lettersPerWordLabel = new Label("Nombre de lettres par mot (1 ou 2) :");
//        TextField lettersPerWordField = new TextField();
//        lettersPerWordField.setPromptText("1 ou 2");
//
//        Button transformButton = new Button("Transformer en Lettres");
//        Button generateMatrixButton = new Button("Générer la Matrice");
//        Button createBitmapButton = new Button("Créer le Bitmap");
//
//        TextArea outputArea = new TextArea();
//        outputArea.setEditable(false);
//        outputArea.setPrefHeight(200);
//
//        // Actions des boutons
//        browseButton.setOnAction(e -> {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Sélectionnez un fichier");
//            File selectedFile = fileChooser.showOpenDialog(primaryStage);
//            if (selectedFile != null) {
//                filePathField.setText(selectedFile.getAbsolutePath());
//            }
//        });
//
//        loadFileButton.setOnAction(e -> {
//            String filePath = filePathField.getText();
//            if (filePath.isEmpty()) {
//                outputArea.appendText("Veuillez sélectionner un fichier.\n");
//                return;
//            }
//            try {
//                ioTimeSeries.lireListe(filePath);
//                numbersList = ioTimeSeries.getNumbersList();
//                sax.setNumbersList(numbersList);
//                outputArea.appendText("Fichier chargé avec succès !\n");
//            } catch (IOException ex) {
//                outputArea.appendText("Erreur lors de la lecture du fichier : " + ex.getMessage() + "\n");
//            }
//        });
//
//        transformButton.setOnAction(e -> {
//            String intervalInput = intervalField.getText();
//            if (intervalInput.isEmpty()) {
//                outputArea.appendText("Veuillez saisir les intervalles.\n");
//                return;
//            }
//            try {
//                List<Double> intervals = parseIntervals(intervalInput);
//                sax.saisirIntervalles(intervals);
//                sax.transfo();
//                lettersList = sax.getLettersList();
//                outputArea.appendText("Transformation en lettres effectuée : " + lettersList + "\n");
//            } catch (NumberFormatException ex) {
//                outputArea.appendText("Erreur dans le format des intervalles : " + ex.getMessage() + "\n");
//            }
//        });
//
//        generateMatrixButton.setOnAction(e -> {
//            String wordLengthInput = wordLengthField.getText();
//            String lettersPerWordInput = lettersPerWordField.getText();
//            if (wordLengthInput.isEmpty() || lettersPerWordInput.isEmpty()) {
//                outputArea.appendText("Veuillez saisir la longueur des mots et le nombre de lettres par mot.\n");
//                return;
//            }
//            try {
//                int longueurMot = Integer.parseInt(wordLengthInput);
//                int nbLettMot = Integer.parseInt(lettersPerWordInput);
//                if (nbLettMot != 1 && nbLettMot != 2) {
//                    outputArea.appendText("Le nombre de lettres par mot doit être 1 ou 2.\n");
//                    return;
//                }
//
//                matrice.decouperEnMots(lettersList, longueurMot);
//                wordsList = matrice.getWordsList();
//                outputArea.appendText("Mots découpés : " + wordsList + "\n");
//
//                List<Character> alphabetList = new ArrayList<>();
//                for (char c : sax.getAlphabet()) {
//                    alphabetList.add(c);
//                }
//
//                if (nbLettMot == 1) {
//                    char[][] letterMatrix = matrice.genererMatriceUneLettre(alphabetList, sax.getIntervalBounds());
//                    occurrenceMatrix = matrice.compterOccurrencesUneLettre(letterMatrix);
//                } else {
//                    String[][] combinationMatrix = matrice.genererMatriceDeuxLettres(alphabetList);
//                    occurrenceMatrix = matrice.compterOccurrencesDeuxLettres(combinationMatrix);
//                }
//
//                outputArea.appendText("Matrice d'occurrences générée.\n");
//
//            } catch (NumberFormatException ex) {
//                outputArea.appendText("Erreur dans le format des entrées : " + ex.getMessage() + "\n");
//            }
//        });
//
//        createBitmapButton.setOnAction(e -> {
//            if (occurrenceMatrix == null) {
//                outputArea.appendText("Veuillez générer la matrice d'occurrences d'abord.\n");
//                return;
//            }
//            bitMap.genererBitmap(occurrenceMatrix, "output-bitmap");
//            outputArea.appendText("Bitmap généré avec succès !\n");
//        });
//
//        // Organisation de la disposition
//        GridPane gridPane = new GridPane();
//        gridPane.setPadding(new Insets(10));
//        gridPane.setVgap(8);
//        gridPane.setHgap(10);
//
//        // Placement des éléments dans le gridPane
//        gridPane.add(fileLabel, 0, 0);
//        gridPane.add(filePathField, 1, 0);
//        gridPane.add(browseButton, 2, 0);
//        gridPane.add(loadFileButton, 3, 0);
//
//        gridPane.add(intervalLabel, 0, 1);
//        gridPane.add(intervalField, 1, 1, 3, 1);
//
//        gridPane.add(wordLengthLabel, 0, 2);
//        gridPane.add(wordLengthField, 1, 2);
//
//        gridPane.add(lettersPerWordLabel, 0, 3);
//        gridPane.add(lettersPerWordField, 1, 3);
//
//        gridPane.add(transformButton, 0, 4);
//        gridPane.add(generateMatrixButton, 1, 4);
//        gridPane.add(createBitmapButton, 2, 4);
//
//        VBox layout = new VBox(10, gridPane, outputArea);
//        Scene scene = new Scene(layout, 700, 500);
//
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    /**
//     * Parse une chaîne d'intervalles séparés par des virgules en une liste de Double.
//     *
//     * @param input La chaîne d'intervalles.
//     * @return La liste des intervalles.
//     */
//    private List<Double> parseIntervals(String input) throws NumberFormatException {
//        String[] tokens = input.split(",");
//        List<Double> intervals = new ArrayList<>();
//        for (String token : tokens) {
//            intervals.add(Double.parseDouble(token.trim()));
//        }
//        return intervals;
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
//

//package main.java.ui;
//
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import main.java.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AppUI extends Application {
//
//    private IoTimeSeries ioTimeSeries = new IoTimeSeries();
//    private Sax sax = new Sax();
//    private Matrice_Sax matrice = new Matrice_Sax();
//    private BitMap bitMap = new BitMap();
//
//    private List<Double> numbersList = new ArrayList<>();
//    private List<Character> lettersList = new ArrayList<>();
//    private List<String> wordsList = new ArrayList<>();
//    private int[][] occurrenceMatrix;
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Application JavaFX - BitMap Time Series");
//
//        // Création des composants de l'interface
//        Label fileLabel = new Label("Sélectionnez un fichier :");
//        TextField filePathField = new TextField();
//        filePathField.setPromptText("Chemin du fichier...");
//        Button browseButton = new Button("Parcourir");
//        Button loadFileButton = new Button("Charger Fichier");
//
//        Label intervalLabel = new Label("Saisissez les intervalles (séparés par des virgules) :");
//        TextField intervalField = new TextField();
//        intervalField.setPromptText("Exemple : 10,20,30");
//
//        Label wordLengthLabel = new Label("Longueur des mots :");
//        TextField wordLengthField = new TextField();
//        wordLengthField.setPromptText("Exemple : 3");
//        Button transformButton = new Button("Transformer en Lettres");
//
//        Label lettersPerWordLabel = new Label("Nombre de lettres par mot (1 ou 2) :");
//        TextField lettersPerWordField = new TextField();
//        lettersPerWordField.setPromptText("1 ou 2");
//
//        Label bitmapNameLabel = new Label("Nom du fichier Bitmap :");
//        TextField bitmapNameField = new TextField();
//        bitmapNameField.setPromptText("Exemple : mon_bitmap");
//
//        Button generateMatrixButton = new Button("Générer la Matrice");
//        Button createBitmapButton = new Button("Créer le Bitmap");
//
//        TextArea outputArea = new TextArea();
//        outputArea.setEditable(false);
//        outputArea.setPrefHeight(200);
//
//        ImageView bitmapView = new ImageView(); // Pour afficher le bitmap généré
//        bitmapView.setFitWidth(400);
//        bitmapView.setPreserveRatio(true);
//
//        // Actions des boutons
//        browseButton.setOnAction(e -> {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Sélectionnez un fichier");
//            File selectedFile = fileChooser.showOpenDialog(primaryStage);
//            if (selectedFile != null) {
//                filePathField.setText(selectedFile.getAbsolutePath());
//            }
//        });
//
//        loadFileButton.setOnAction(e -> {
//            String filePath = filePathField.getText();
//            if (filePath.isEmpty()) {
//                outputArea.appendText("Veuillez sélectionner un fichier.\n");
//                return;
//            }
//            try {
//                ioTimeSeries.lireListe(filePath);
//                numbersList = ioTimeSeries.getNumbersList();
//                sax.setNumbersList(numbersList);
//                outputArea.appendText("Fichier chargé avec succès !\n");
//            } catch (IOException ex) {
//                outputArea.appendText("Erreur lors de la lecture du fichier : " + ex.getMessage() + "\n");
//            }
//        });
//
//        transformButton.setOnAction(e -> {
//            String intervalInput = intervalField.getText();
//            if (intervalInput.isEmpty()) {
//                outputArea.appendText("Veuillez saisir les intervalles.\n");
//                return;
//            }
//            try {
//                List<Double> intervals = parseIntervals(intervalInput);
//                sax.saisirIntervalles(intervals);
//                sax.transfo();
//                lettersList = sax.getLettersList();
//                outputArea.appendText("Transformation en lettres effectuée : " + lettersList + "\n");
//            } catch (NumberFormatException ex) {
//                outputArea.appendText("Erreur dans le format des intervalles : " + ex.getMessage() + "\n");
//            }
//        });
//
//        generateMatrixButton.setOnAction(e -> {
//            String wordLengthInput = wordLengthField.getText();
//            String lettersPerWordInput = lettersPerWordField.getText();
//            if (wordLengthInput.isEmpty() || lettersPerWordInput.isEmpty()) {
//                outputArea.appendText("Veuillez saisir la longueur des mots et le nombre de lettres par mot.\n");
//                return;
//            }
//            try {
//                int longueurMot = Integer.parseInt(wordLengthInput);
//                int nbLettMot = Integer.parseInt(lettersPerWordInput);
//                if (nbLettMot != 1 && nbLettMot != 2) {
//                    outputArea.appendText("Le nombre de lettres par mot doit être 1 ou 2.\n");
//                    return;
//                }
//
//                matrice.decouperEnMots(lettersList, longueurMot);
//                wordsList = matrice.getWordsList();
//                outputArea.appendText("Mots découpés : " + wordsList + "\n");
//
//                List<Character> alphabetList = new ArrayList<>();
//                for (char c : sax.getAlphabet()) {
//                    alphabetList.add(c);
//                }
//
//                if (nbLettMot == 1) {
//                    char[][] letterMatrix = matrice.genererMatriceUneLettre(alphabetList, sax.getIntervalBounds());
//                    occurrenceMatrix = matrice.compterOccurrencesUneLettre(letterMatrix);
//                } else {
//                    String[][] combinationMatrix = matrice.genererMatriceDeuxLettres(alphabetList);
//                    occurrenceMatrix = matrice.compterOccurrencesDeuxLettres(combinationMatrix);
//                }
//
//                outputArea.appendText("Matrice d'occurrences générée.\n");
//
//            } catch (NumberFormatException ex) {
//                outputArea.appendText("Erreur dans le format des entrées : " + ex.getMessage() + "\n");
//            }
//        });
//
//        createBitmapButton.setOnAction(e -> {
//            String bitmapName = bitmapNameField.getText();
//            if (bitmapName.isEmpty()) {
//                outputArea.appendText("Veuillez entrer un nom pour le fichier Bitmap.\n");
//                return;
//            }
//            if (occurrenceMatrix == null) {
//                outputArea.appendText("Veuillez générer la matrice d'occurrences d'abord.\n");
//                return;
//            }
//            try {
//                // Chemin complet du fichier bitmap
//                String filePath = bitmapName;
//                bitMap.genererBitmap(occurrenceMatrix, bitmapName);
//
//                outputArea.appendText("Bitmap généré avec succès : " + filePath + "\n");
//
//                // Charger et afficher l'image dans l'interface
//                File bitmapFile = new File( "tests/" + filePath + ".bmp");
//                if (bitmapFile.exists()) {
//                    Image bitmapImage = new Image(bitmapFile.toURI().toString());
//                    bitmapView.setImage(bitmapImage);
//                } else {
//                    outputArea.appendText("Erreur : Le fichier Bitmap n'a pas été trouvé au chemin : " + filePath + "\n");
//                }
//            } catch (Exception ex) {
//                outputArea.appendText("Erreur lors de la génération du Bitmap : " + ex.getMessage() + "\n");
//            }
//        });
//
//        // Organisation de la disposition
//        GridPane gridPane = new GridPane();
//        gridPane.setPadding(new Insets(10));
//        gridPane.setVgap(8);
//        gridPane.setHgap(10);
//
//        // Placement des éléments dans le gridPane
//        gridPane.add(fileLabel, 0, 0);
//        gridPane.add(filePathField, 1, 0, 5,1);
//        gridPane.add(browseButton, 6, 0);
//        gridPane.add(loadFileButton, 7, 0);
//
//        gridPane.add(intervalLabel, 0, 1);
//        gridPane.add(intervalField, 1, 1);
//
//        gridPane.add(wordLengthLabel, 0, 2);
//        gridPane.add(wordLengthField, 1, 2);
//        gridPane.add(transformButton, 5, 2);
//
//        gridPane.add(lettersPerWordLabel, 0, 3);
//        gridPane.add(lettersPerWordField, 1, 3);
//        gridPane.add(generateMatrixButton, 5, 3);
//
//        gridPane.add(bitmapNameLabel, 0, 4);
//        gridPane.add(bitmapNameField, 1, 4);
//        gridPane.add(createBitmapButton, 5, 4);
//
//        VBox layout = new VBox(10, gridPane, outputArea, bitmapView);
//        Scene scene = new Scene(layout, 1000, 600);
//
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    /**
//     * Parse une chaîne d'intervalles séparés par des virgules en une liste de Double.
//     *
//     * @param input La chaîne d'intervalles.
//     * @return La liste des intervalles.
//     */
//    private List<Double> parseIntervals(String input) throws NumberFormatException {
//        String[] tokens = input.split(",");
//        List<Double> intervals = new ArrayList<>();
//        for (String token : tokens) {
//            intervals.add(Double.parseDouble(token.trim()));
//        }
//        return intervals;
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}

//package main.java.ui;
//
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import main.java.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AppUI extends Application {
//
//    private IoTimeSeries ioTimeSeries = new IoTimeSeries();
//    private Sax sax = new Sax();
//    private Matrice_Sax matrice = new Matrice_Sax();
//    private BitMap bitMap = new BitMap();
//
//    private List<Double> numbersList = new ArrayList<>();
//    private List<Character> lettersList = new ArrayList<>();
//    private List<String> wordsList = new ArrayList<>();
//    private int[][] occurrenceMatrix;
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Application JavaFX - BitMap Time Series");
//
//        // Création des composants de l'interface
//        Label fileLabel = new Label("Sélectionnez un fichier :");
//        TextField filePathField = new TextField();
//        filePathField.setPromptText("Chemin du fichier...");
//        Button browseButton = new Button("Parcourir");
//        Button loadFileButton = new Button("Charger Fichier");
//
//        Label intervalLabel = new Label("Saisissez les intervalles (séparés par des virgules) :");
//        TextField intervalField = new TextField();
//        intervalField.setPromptText("Exemple : 10,20,30");
//
//        Label wordLengthLabel = new Label("Longueur des mots :");
//        TextField wordLengthField = new TextField();
//        wordLengthField.setPromptText("Exemple : 3");
//        Button transformButton = new Button("Transformer en Lettres");
//
//        Label lettersPerWordLabel = new Label("Nombre de lettres par mot (1 ou 2) :");
//        TextField lettersPerWordField = new TextField();
//        lettersPerWordField.setPromptText("1 ou 2");
//
//        Label bitmapNameLabel = new Label("Nom du fichier Bitmap :");
//        TextField bitmapNameField = new TextField();
//        bitmapNameField.setPromptText("Exemple : mon_bitmap");
//
//        Button generateMatrixButton = new Button("Générer la Matrice");
//        Button createBitmapButton = new Button("Créer le Bitmap");
//
//        TextArea outputArea = new TextArea();
//        outputArea.setEditable(false);
//        outputArea.setPrefHeight(200);
//
//        ImageView bitmapView = new ImageView(); // Pour afficher le bitmap généré
//        bitmapView.setFitWidth(400); // Largeur fixe
//        bitmapView.setFitHeight(400); // Hauteur fixe
//        bitmapView.setPreserveRatio(true); // Préserve le ratio pour éviter la déformation
//        bitmapView.setSmooth(true); // Active le lissage pour éviter la pixellisation
//        bitmapView.setCache(true); // Améliore les performances pour l'affichage d'images
//
//        // Ajouter le bitmapView dans un conteneur centré
//        StackPane bitmapContainer = new StackPane(bitmapView);
//        bitmapContainer.setPrefHeight(400);
//        bitmapContainer.setStyle("-fx-alignment: center;");
//
//        // Actions des boutons
//        browseButton.setOnAction(e -> {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Sélectionnez un fichier");
//            File selectedFile = fileChooser.showOpenDialog(primaryStage);
//            if (selectedFile != null) {
//                filePathField.setText(selectedFile.getAbsolutePath());
//            }
//        });
//
//        loadFileButton.setOnAction(e -> {
//            String filePath = filePathField.getText();
//            if (filePath.isEmpty()) {
//                outputArea.appendText("Veuillez sélectionner un fichier.\n");
//                return;
//            }
//            try {
//                ioTimeSeries.lireListe(filePath);
//                numbersList = ioTimeSeries.getNumbersList();
//                sax.setNumbersList(numbersList);
//                outputArea.appendText("Fichier chargé avec succès !\n");
//            } catch (IOException ex) {
//                outputArea.appendText("Erreur lors de la lecture du fichier : " + ex.getMessage() + "\n");
//            }
//        });
//
//        transformButton.setOnAction(e -> {
//            String intervalInput = intervalField.getText();
//            if (intervalInput.isEmpty()) {
//                outputArea.appendText("Veuillez saisir les intervalles.\n");
//                return;
//            }
//            try {
//                List<Double> intervals = parseIntervals(intervalInput);
//                sax.saisirIntervalles(intervals);
//                sax.transfo();
//                lettersList = sax.getLettersList();
//                outputArea.appendText("Transformation en lettres effectuée : " + lettersList + "\n");
//            } catch (NumberFormatException ex) {
//                outputArea.appendText("Erreur dans le format des intervalles : " + ex.getMessage() + "\n");
//            }
//        });
//
//        generateMatrixButton.setOnAction(e -> {
//            String wordLengthInput = wordLengthField.getText();
//            String lettersPerWordInput = lettersPerWordField.getText();
//            if (wordLengthInput.isEmpty() || lettersPerWordInput.isEmpty()) {
//                outputArea.appendText("Veuillez saisir la longueur des mots et le nombre de lettres par mot.\n");
//                return;
//            }
//            try {
//                int longueurMot = Integer.parseInt(wordLengthInput);
//                int nbLettMot = Integer.parseInt(lettersPerWordInput);
//                if (nbLettMot != 1 && nbLettMot != 2) {
//                    outputArea.appendText("Le nombre de lettres par mot doit être 1 ou 2.\n");
//                    return;
//                }
//
//                matrice.decouperEnMots(lettersList, longueurMot);
//                wordsList = matrice.getWordsList();
//                outputArea.appendText("Mots découpés : " + wordsList + "\n");
//
//                List<Character> alphabetList = new ArrayList<>();
//                for (char c : sax.getAlphabet()) {
//                    alphabetList.add(c);
//                }
//
//                if (nbLettMot == 1) {
//                    char[][] letterMatrix = matrice.genererMatriceUneLettre(alphabetList, sax.getIntervalBounds());
//                    occurrenceMatrix = matrice.compterOccurrencesUneLettre(letterMatrix);
//                } else {
//                    String[][] combinationMatrix = matrice.genererMatriceDeuxLettres(alphabetList);
//                    occurrenceMatrix = matrice.compterOccurrencesDeuxLettres(combinationMatrix);
//                }
//
//                outputArea.appendText("Matrice d'occurrences générée.\n");
//
//            } catch (NumberFormatException ex) {
//                outputArea.appendText("Erreur dans le format des entrées : " + ex.getMessage() + "\n");
//            }
//        });
//
//        createBitmapButton.setOnAction(e -> {
//            String bitmapName = bitmapNameField.getText();
//            if (bitmapName.isEmpty()) {
//                outputArea.appendText("Veuillez entrer un nom pour le fichier Bitmap.\n");
//                return;
//            }
//            if (occurrenceMatrix == null) {
//                outputArea.appendText("Veuillez générer la matrice d'occurrences d'abord.\n");
//                return;
//            }
//            try {
//                // Chemin complet du fichier bitmap
//                String filePath = bitmapName;
//                bitMap.genererBitmap(occurrenceMatrix, filePath);
//
//                outputArea.appendText("Bitmap généré avec succès : " + filePath + "\n");
//
//                // Charger et afficher l'image dans l'interface
//                File bitmapFile = new File("tests/" + filePath + ".bmp");
//                if (bitmapFile.exists()) {
//                    Image bitmapImage = new Image(bitmapFile.toURI().toString());
//                    bitmapView.setImage(bitmapImage);
//                } else {
//                    outputArea.appendText("Erreur : Le fichier Bitmap n'a pas été trouvé au chemin : " + filePath + "\n");
//                }
//            } catch (Exception ex) {
//                outputArea.appendText("Erreur lors de la génération du Bitmap : " + ex.getMessage() + "\n");
//            }
//        });
//
//        // Organisation de la disposition
//        GridPane gridPane = new GridPane();
//        gridPane.setPadding(new Insets(10));
//        gridPane.setVgap(8);
//        gridPane.setHgap(10);
//
//        // Placement des éléments dans le gridPane
//        gridPane.add(fileLabel, 0, 0);
//        gridPane.add(filePathField, 1, 0, 5, 1);
//        gridPane.add(browseButton, 6, 0);
//        gridPane.add(loadFileButton, 7, 0);
//
//        gridPane.add(intervalLabel, 0, 1);
//        gridPane.add(intervalField, 1, 1);
//
//        gridPane.add(wordLengthLabel, 0, 2);
//        gridPane.add(wordLengthField, 1, 2);
//        gridPane.add(transformButton, 5, 2);
//
//        gridPane.add(lettersPerWordLabel, 0, 3);
//        gridPane.add(lettersPerWordField, 1, 3);
//        gridPane.add(generateMatrixButton, 5, 3);
//
//        gridPane.add(bitmapNameLabel, 0, 4);
//        gridPane.add(bitmapNameField, 1, 4);
//        gridPane.add(createBitmapButton, 5, 4);
//
//        VBox layout = new VBox(10, gridPane, outputArea, bitmapContainer);
//        layout.setPadding(new Insets(10));
//        layout.setSpacing(10);
//
//        Scene scene = new Scene(layout, 1000, 700);
//
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private List<Double> parseIntervals(String input) throws NumberFormatException {
//        String[] tokens = input.split(",");
//        List<Double> intervals = new ArrayList<>();
//        for (String token : tokens) {
//            intervals.add(Double.parseDouble(token.trim()));
//        }
//        return intervals;
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}


package main.java.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppUI extends Application {

    private IoTimeSeries ioTimeSeries = new IoTimeSeries();
    private Sax sax = new Sax();
    private Matrice_Sax matrice = new Matrice_Sax();
    private BitMap bitMap = new BitMap();

    private List<Double> numbersList = new ArrayList<>();
    private List<Character> lettersList = new ArrayList<>();
    private List<String> wordsList = new ArrayList<>();
    private int[][] occurrenceMatrix;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Application JavaFX - BitMap Time Series");

        // Création des composants de l'interface
        Label fileLabel = new Label("Sélectionnez un fichier :");
        TextField filePathField = new TextField();
        filePathField.setPromptText("Chemin du fichier...");
        Button browseButton = new Button("Parcourir");
        Button loadFileButton = new Button("Charger Fichier");

        Label intervalLabel = new Label("Saisissez les intervalles (séparés par des virgules) :");
        TextField intervalField = new TextField();
        intervalField.setPromptText("Exemple : 10,20,30");

        Label wordLengthLabel = new Label("Longueur des mots :");
        TextField wordLengthField = new TextField();
        wordLengthField.setPromptText("Exemple : 3");
        Button transformButton = new Button("Transformer en Lettres");

        Label lettersPerWordLabel = new Label("Nombre de lettres par mot (1 ou 2) :");
        TextField lettersPerWordField = new TextField();
        lettersPerWordField.setPromptText("1 ou 2");

        Label bitmapNameLabel = new Label("Nom du fichier Bitmap :");
        TextField bitmapNameField = new TextField();
        bitmapNameField.setPromptText("Exemple : mon_bitmap");

        Button generateMatrixButton = new Button("Générer la Matrice");
        Button createBitmapButton = new Button("Créer le Bitmap");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        // Configuration de l'affichage du bitmap
        ImageView bitmapView = new ImageView(); // Pour afficher le bitmap généré
        bitmapView.setFitWidth(500); // Largeur fixe
        bitmapView.setFitHeight(500); // Hauteur fixe
        bitmapView.setPreserveRatio(true); // Préserve le ratio
        bitmapView.setSmooth(true); // Active le lissage
        bitmapView.setCache(true); // Améliore les performances

        // Conteneur pour centrer le bitmap
        StackPane bitmapContainer = new StackPane(bitmapView);
        bitmapContainer.setStyle("-fx-alignment: center;");
        bitmapContainer.setPrefHeight(500);
        bitmapContainer.setPrefWidth(500);

        // Actions des boutons
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionnez un fichier");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        loadFileButton.setOnAction(e -> {
            String filePath = filePathField.getText();
            if (filePath.isEmpty()) {
                outputArea.appendText("Veuillez sélectionner un fichier.\n");
                return;
            }
            try {
                ioTimeSeries.lireListe(filePath);
                numbersList = ioTimeSeries.getNumbersList();
                sax.setNumbersList(numbersList);
                outputArea.appendText("Fichier chargé avec succès !\n");
            } catch (IOException ex) {
                outputArea.appendText("Erreur lors de la lecture du fichier : " + ex.getMessage() + "\n");
            }
        });

        transformButton.setOnAction(e -> {
            String intervalInput = intervalField.getText();
            if (intervalInput.isEmpty()) {
                outputArea.appendText("Veuillez saisir les intervalles.\n");
                return;
            }
            try {
                List<Double> intervals = parseIntervals(intervalInput);
                sax.saisirIntervalles(intervals);
                sax.transfo();
                lettersList = sax.getLettersList();
                outputArea.appendText("Transformation en lettres effectuée : " + lettersList + "\n");
            } catch (NumberFormatException ex) {
                outputArea.appendText("Erreur dans le format des intervalles : " + ex.getMessage() + "\n");
            }
        });

        generateMatrixButton.setOnAction(e -> {
            String wordLengthInput = wordLengthField.getText();
            String lettersPerWordInput = lettersPerWordField.getText();
            if (wordLengthInput.isEmpty() || lettersPerWordInput.isEmpty()) {
                outputArea.appendText("Veuillez saisir la longueur des mots et le nombre de lettres par mot.\n");
                return;
            }
            try {
                int longueurMot = Integer.parseInt(wordLengthInput);
                int nbLettMot = Integer.parseInt(lettersPerWordInput);
                if (nbLettMot != 1 && nbLettMot != 2) {
                    outputArea.appendText("Le nombre de lettres par mot doit être 1 ou 2.\n");
                    return;
                }

                matrice.decouperEnMots(lettersList, longueurMot);
                wordsList = matrice.getWordsList();
                outputArea.appendText("Mots découpés : " + wordsList + "\n");

                List<Character> alphabetList = new ArrayList<>();
                for (char c : sax.getAlphabet()) {
                    alphabetList.add(c);
                }

                if (nbLettMot == 1) {
                    char[][] letterMatrix = matrice.genererMatriceUneLettre(alphabetList, sax.getIntervalBounds());
                    occurrenceMatrix = matrice.compterOccurrencesUneLettre(letterMatrix);
                } else {
                    String[][] combinationMatrix = matrice.genererMatriceDeuxLettres(alphabetList);
                    occurrenceMatrix = matrice.compterOccurrencesDeuxLettres(combinationMatrix);
                }

                outputArea.appendText("Matrice d'occurrences générée.\n");

            } catch (NumberFormatException ex) {
                outputArea.appendText("Erreur dans le format des entrées : " + ex.getMessage() + "\n");
            }
        });

        createBitmapButton.setOnAction(e -> {
            String bitmapName = bitmapNameField.getText();
            if (bitmapName.isEmpty()) {
                outputArea.appendText("Veuillez entrer un nom pour le fichier Bitmap.\n");
                return;
            }
            if (occurrenceMatrix == null) {
                outputArea.appendText("Veuillez générer la matrice d'occurrences d'abord.\n");
                return;
            }
            try {
                // Chemin complet du fichier bitmap
                String filePath = bitmapName;
                bitMap.genererBitmap(occurrenceMatrix, bitmapName);

                outputArea.appendText("Bitmap généré avec succès : " + filePath + "\n");

                // Charger et afficher l'image dans l'interface
                File bitmapFile = new File("tests/" + filePath + ".bmp");
                if (bitmapFile.exists()) {
//                    Image bitmapImage = new Image(bitmapFile.toURI().toString());

//                    BufferedImage originalImage = ImageIO.read(bitmapFile);
//
//                    int targetWidth = 500;
//                    int targetHeight = 500;
//                    BufferedImage resizedImage = bitMap.resizeBitmap(originalImage, targetWidth, targetHeight);
//
//                    File resizedFile = new File("tests/" + filePath + "_resized.bmp");
//                    ImageIO.write(resizedImage, "bmp", resizedFile);
//
//                    Image bitmapImage = new Image(resizedFile.toURI().toString());
//
//
//
////                    bitmapView.setSmooth(false);
////                    bitmapView.setFitWidth(bitmapImage.getWidth());
////                    bitmapView.setFitHeight(bitmapImage.getHeight());
//                    bitmapView.setImage(bitmapImage);
//                    bitmapView.setFitWidth(bitmapImage.getWidth());
//                    bitmapView.setFitHeight(bitmapImage.getHeight());
//                    bitmapView.setPreserveRatio(true);
//                    bitmapView.setSmooth(false);

                    BufferedImage originalImage = ImageIO.read(bitmapFile);

                    // Agrandir l'image en mémoire
                    int targetWidth = 500;  // Largeur cible
                    int targetHeight = 500; // Hauteur cible
                    BufferedImage scaledImage = bitMap.scaleImage(originalImage, targetWidth, targetHeight);

                    // Sauvegarder l'image agrandie dans un fichier temporaire
                    File scaledFile = new File("tests/" + filePath + "_scaled.bmp");
                    ImageIO.write(scaledImage, "bmp", scaledFile);

                    // Charger l'image agrandie dans l'ImageView
                    Image bitmapImage = new Image(scaledFile.toURI().toString());
                    bitmapView.setImage(bitmapImage);

                    // Ajuster les dimensions de l'ImageView
                    bitmapView.setFitWidth(targetWidth);
                    bitmapView.setFitHeight(targetHeight);
                    bitmapView.setPreserveRatio(true); // Conserver les proportions
                    bitmapView.setSmooth(false); // Désactiver le lissage
                } else {
                    outputArea.appendText("Erreur : Le fichier Bitmap n'a pas été trouvé au chemin : " + filePath + "\n");
                }
            } catch (Exception ex) {
                outputArea.appendText("Erreur lors de la génération du Bitmap : " + ex.getMessage() + "\n");
            }
        });

        // Organisation de la disposition
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        // Placement des éléments dans le gridPane
        gridPane.add(fileLabel, 0, 0);
        gridPane.add(filePathField, 1, 0, 5, 1);
        gridPane.add(browseButton, 6, 0);
        gridPane.add(loadFileButton, 7, 0);

        gridPane.add(intervalLabel, 0, 1);
        gridPane.add(intervalField, 1, 1);

        gridPane.add(wordLengthLabel, 0, 2);
        gridPane.add(wordLengthField, 1, 2);
        gridPane.add(transformButton, 5, 2);

        gridPane.add(lettersPerWordLabel, 0, 3);
        gridPane.add(lettersPerWordField, 1, 3);
        gridPane.add(generateMatrixButton, 5, 3);

        gridPane.add(bitmapNameLabel, 0, 4);
        gridPane.add(bitmapNameField, 1, 4);
        gridPane.add(createBitmapButton, 5, 4);

        VBox layout = new VBox(10, gridPane, outputArea, bitmapContainer);
        layout.setPadding(new Insets(10));
        layout.setSpacing(10);
        layout.setStyle("-fx-alignment: center;");

        Scene scene = new Scene(layout, 1000, 800);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Parse une chaîne d'intervalles séparés par des virgules en une liste de Double.
     *
     * @param input La chaîne d'intervalles.
     * @return La liste des intervalles.
     */
    private List<Double> parseIntervals(String input) throws NumberFormatException {
        String[] tokens = input.split(",");
        List<Double> intervals = new ArrayList<>();
        for (String token : tokens) {
            intervals.add(Double.parseDouble(token.trim()));
        }
        return intervals;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
