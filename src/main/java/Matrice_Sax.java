package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La classe main.java.Matrice_Sax permet de gérer les opérations liées aux mots et matrices générés
 * à partir de séries de lettres. Elle permet de découper les séries en mots, de générer
 * des matrices basées sur des lettres et des combinaisons de lettres, et de compter
 * les occurrences des lettres ou des combinaisons dans ces matrices.
 */
public class Matrice_Sax {

    private List<String> wordsList;
    private Map<String, Integer> occurrencesMap;

    /**
     * Découpe une liste de lettres en mots de longueur spécifiée.
     *
     * @param lettersList La liste des lettres à découper en mots.
     * @param longueurMot La longueur de chaque mot.
     */
    public void decouperEnMots(List<Character> lettersList, int longueurMot) {
        wordsList = new ArrayList<>();

        if (lettersList == null || lettersList.isEmpty()) {
            System.out.println("La liste des lettres est vide ou non initialisée.");
            return;
        }

        // Découpe la liste de lettres en mots
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < lettersList.size(); i++) {
            word.append(lettersList.get(i));
            // Quand la longueur du mot est atteinte, ajoute le mot à la liste et réinitialise le StringBuilder
            if ((i + 1) % longueurMot == 0) {
                wordsList.add(word.toString());
                word.setLength(0); // Réinitialise pour le mot suivant
            }
        }

        // Si le dernier mot n'est pas complet, on l'ajoute à la liste
        if (word.length() > 0) {
            wordsList.add(word.toString());
        }
    }

    /**
     * Affiche les mots générés après le découpage.
     */
    public void afficherMots() {
        if (wordsList != null && !wordsList.isEmpty()) {
            System.out.println("Mots générés :");
            for (String word : wordsList) {
                System.out.println(word);
            }
        } else {
            System.out.println("Aucun mot n'a été généré.");
        }
    }

    /**
     * Génère une matrice en fonction des lettres et intervalles fournis, et du nombre
     * de lettres par mot souhaité.
     *
     * @param alphabet La liste des lettres de l'alphabet.
     * @param intervalBounds La liste des bornes d'intervalles.
     * @param nbLettMot Le nombre de lettres par mot (1 ou 2).
     */
    public void genererMatrice(List<Character> alphabet, List<Double> intervalBounds, int nbLettMot) {
        if (nbLettMot == 1) {
            genererMatriceUneLettre(alphabet, intervalBounds);
        } else if (nbLettMot == 2) {
            genererMatriceDeuxLettres(alphabet);
        }
    }

    /**
     * Génère une matrice de lettres pour des mots d'une lettre et affiche les résultats.
     *
     * @param alphabet La liste des lettres de l'alphabet.
     * @param intervalBounds La liste des bornes d'intervalles.
     * @return La matrice de lettres générée.
     */
    public char[][] genererMatriceUneLettre(List<Character> alphabet, List<Double> intervalBounds) {
        int NB_lettres = intervalBounds.size() + 1;

        int columns;
        if (NB_lettres <= 2) {
            columns = 2;
        } else if (NB_lettres <= 4) {
            columns = 2;
        } else if (NB_lettres <= 9) {
            columns = 3;
        } else if (NB_lettres <= 16) {
            columns = 4;
        } else if (NB_lettres <= 25) {
            columns = 5;
        } else {
            columns = (int) Math.ceil(Math.sqrt(NB_lettres));
        }

        int rows = (int) Math.ceil((double) NB_lettres / columns);

        System.out.println("Matrice générée :");

        char[][] letterMatrix = new char[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int index = row * columns + col;
                if (index < NB_lettres) {
                    letterMatrix[row][col] = alphabet.get(index);
                    System.out.print(letterMatrix[row][col]);
                    if (col < columns - 1 && (index + 1) < NB_lettres) {
                        System.out.print(" | ");
                    }
                }
            }
            System.out.println();
        }

        return letterMatrix;
    }

    /**
     * Génère une matrice de combinaisons de deux lettres et affiche les résultats.
     *
     * @param alphabet La liste des lettres de l'alphabet.
     * @return La matrice de combinaisons de deux lettres générée.
     */
    public String[][] genererMatriceDeuxLettres(List<Character> alphabet) {
        System.out.println("Matrice avec combinaisons de deux lettres :");

        String[][] combinationMatrix = new String[alphabet.size()][alphabet.size()];
        for (int i = 0; i < alphabet.size(); i++) {
            StringBuilder rowString = new StringBuilder();
            for (int j = 0; j < alphabet.size(); j++) {
                combinationMatrix[i][j] = "" + alphabet.get(i) + alphabet.get(j);
                rowString.append(combinationMatrix[i][j]).append(" ");
            }
            System.out.println(rowString.toString().trim());
        }

        return combinationMatrix;
    }

    /**
     * Compte les occurrences des lettres dans la matrice de même taille.
     *
     * @param letterMatrix La matrice des lettres.
     * @return La matrice des occurrences pour chaque lettre.
     */
    public int[][] compterOccurrencesUneLettre(char[][] letterMatrix) {
        int rows = letterMatrix.length;
        int cols = letterMatrix[0].length;
        int[][] occurrencesMatrix = new int[rows][cols];
        occurrencesMap = new HashMap<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                occurrencesMap.put(String.valueOf(letterMatrix[row][col]), 0);
            }
        }

        for (String word : wordsList) {
            for (char letter : word.toCharArray()) {
                if (occurrencesMap.containsKey(String.valueOf(letter))) {
                    occurrencesMap.put(String.valueOf(letter), occurrencesMap.get(String.valueOf(letter)) + 1);
                }
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                occurrencesMatrix[row][col] = occurrencesMap.get(String.valueOf(letterMatrix[row][col]));
            }
        }

        System.out.println("Matrice des occurrences :");

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                System.out.print(occurrencesMatrix[row][col]);
                if (col < cols - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }

        return occurrencesMatrix;
    }

    /**
     * Compte les occurrences des combinaisons de deux lettres dans la matrice de même taille.
     *
     * @param combinationMatrix La matrice des combinaisons de deux lettres.
     * @return La matrice des occurrences pour chaque combinaison de deux lettres.
     */
    public int[][] compterOccurrencesDeuxLettres(String[][] combinationMatrix) {
        int rows = combinationMatrix.length;
        int cols = combinationMatrix[0].length;
        int[][] occurrencesMatrix = new int[rows][cols];
        occurrencesMap = new HashMap<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                occurrencesMap.put(combinationMatrix[row][col], 0);
            }
        }

        for (String word : wordsList) {
            for (int i = 0; i < word.length() - 1; i++) {
                String combination = "" + word.charAt(i) + word.charAt(i + 1);
                if (occurrencesMap.containsKey(combination)) {
                    occurrencesMap.put(combination, occurrencesMap.get(combination) + 1);
                }
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                occurrencesMatrix[row][col] = occurrencesMap.get(combinationMatrix[row][col]);
            }
        }

        System.out.println("Matrice des occurrences (combinaisons de deux lettres) :");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(occurrencesMatrix[i][j] + " ");
            }
            System.out.println();
        }

        return occurrencesMatrix;
    }

    /**
     * Définit la liste de mots pour l'analyse des occurrences.
     * Cette liste est utilisée dans les méthodes de comptage des occurrences
     * afin de vérifier la fréquence des lettres ou des combinaisons de lettres.
     *
     * @param wordsList La liste de mots à analyser
     */
    public void setWordsList(List<String> wordsList) {
        this.wordsList = wordsList;
    }
}
