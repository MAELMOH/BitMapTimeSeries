package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe main.java.IoTimeSeries permet de lire une série de nombres à partir d'un fichier
 * texte et de stocker ces valeurs dans une liste de nombres en virgule flottante.
 * Elle peut être utilisée pour lire des séries temporelles ou des données numériques
 * à partir de fichiers.
 */
public class IoTimeSeries {

    /**
     * Liste de nombres en virgule flottante représentant les valeurs lues
     * depuis le fichier.
     */
    private List<Double> numbersList = new ArrayList<>();

    /**
     * Lit les nombres à partir d'un fichier texte spécifié et les stocke dans la liste
     * numbersList. Chaque ligne du fichier doit contenir un nombre valide en format
     * décimal. Les lignes avec des formats invalides sont ignorées.
     *
     * @param fileName le chemin du fichier à lire.
     * @throws IOException si une erreur d'entrée/sortie survient lors de la lecture du fichier.
     */
    public List<Double> lireListe(String fileName) throws IOException {
        numbersList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    numbersList.add(Double.parseDouble(line));
                } catch (NumberFormatException e) {
                    // Ligne ignorée si le format est invalide
                }
            }
        }
        return numbersList;
    }

    /**
     * Retourne la liste des nombres en virgule flottante qui ont été lus depuis le fichier.
     *
     * @return une liste de Double représentant les valeurs du fichier.
     */
    public List<Double> getNumbersList() {
        return numbersList;
    }

    /*
    public static void main(String[] args) {
        main.java.IoTimeSeries ioTimeSeries = new main.java.IoTimeSeries();
        String fileName = "nombres.txt";
        // String fileName = "nombres_2.csv";

        try {
            ioTimeSeries.lireListe(fileName);
            List<Double> list = ioTimeSeries.getNumbersList();
            System.out.println("Contenu du fichier:");
            for (Double number : list) {
                System.out.println(number);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier: " + e.getMessage());
        }
    }
    */
}
