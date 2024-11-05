import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * La classe Sax permet de transformer une liste de nombres en une liste de lettres
 * basée sur des intervalles définis par l'utilisateur. Elle utilise l'algorithme
 * Symbolic Aggregate approXimation (SAX) pour simplifier la série temporelle en
 * séquence de symboles, facilitant ainsi l'analyse des motifs.
 */
public class Sax {

    private List<Double> numbersList;
    private List<Character> lettersList;
    private List<Double> intervalBounds;
    private char[] alphabet;

    /**
     * Définit la liste de nombres à transformer.
     *
     * @param numbersList La liste de nombres fournie par IoTimeSeries.
     */
    public void setNumbersList(List<Double> numbersList) {
        this.numbersList = numbersList;
    }

    /**
     * Transforme la liste de nombres en une liste de lettres en fonction des intervalles.
     * Affiche les intervalles et leurs lettres associées pour référence.
     * Vérifie si les listes de nombres et d'intervalles sont valides avant la transformation.
     */
    public void transfo() {
        if (numbersList == null || numbersList.isEmpty()) {
            System.out.println("La liste de nombres est vide ou non initialisée.");
            return;
        }

        if (intervalBounds == null || intervalBounds.isEmpty()) {
            System.out.println("La liste des bornes d'intervalles est vide. Veuillez entrer des bornes valides.");
            return;
        }

        // Créer l'alphabet selon le nombre d'intervalles (NB_lettre = intervalBounds.size() + 1)
        int NB_lettre = intervalBounds.size() + 1;
        alphabet = new char[NB_lettre];
        for (int i = 0; i < NB_lettre; i++) {
            alphabet[i] = (char) ('a' + i);
        }

        // Afficher les intervalles avec la lettre associée
        System.out.println("Intervalles et lettres associées :");
        for (int i = 0; i < intervalBounds.size(); i++) {
            if (i == 0) {
                System.out.println(alphabet[i] + " : valeur =< " + intervalBounds.get(i));
            } else {
                System.out.println(alphabet[i] + " : ]" + intervalBounds.get(i - 1) + " - " + intervalBounds.get(i) + "]");
            }
        }
        System.out.println(alphabet[NB_lettre - 1] + " : valeur > " + intervalBounds.get(intervalBounds.size() - 1));

        // Transformer chaque nombre en lettre selon l'intervalle
        lettersList = new ArrayList<>();
        for (double number : numbersList) {
            char letter = getLetterForNumber(number);
            lettersList.add(letter);
        }
    }

    /**
     * Retourne la lettre associée à un nombre donné en fonction des intervalles.
     *
     * @param number Le nombre à transformer.
     * @return La lettre correspondant à l'intervalle du nombre.
     */
    private char getLetterForNumber(double number) {
        for (int i = 0; i < intervalBounds.size(); i++) {
            if (i == 0 && number <= intervalBounds.get(i)) {
                // Pour la première borne, inclure <=
                return alphabet[i];
            } else if (i > 0 && number > intervalBounds.get(i - 1) && number <= intervalBounds.get(i)) {
                // Pour les bornes suivantes, inclure > pour la borne inférieure et <= pour la borne supérieure
                return alphabet[i];
            }
        }
        // Si la valeur est supérieure à toutes les bornes, on retourne la dernière lettre
        return alphabet[alphabet.length - 1];
    }

    /**
     * Permet à l'utilisateur de saisir les bornes d'intervalles manuellement.
     * Les valeurs sont saisies une par une et ajoutées à la liste des intervalles.
     * La saisie est arrêtée par l'utilisateur en entrant une lettre.
     */
    public void saisirIntervalles() {
        intervalBounds = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Saisissez les bornes des intervalles (une valeur à la fois, tapez une lettre pour arrêter) :");

        while (true) {
            System.out.print("Entrez une valeur ou une lettre pour arrêter : ");
            String input = scanner.nextLine();

            // Arrête la saisie si une lettre est entrée
            try {
                double valeur = Double.parseDouble(input);
                if (intervalBounds.contains(valeur)) {
                    System.out.println("Cette valeur a déjà été saisie. Veuillez entrer une valeur différente.");
                } else {
                    intervalBounds.add(valeur);
                }
            } catch (NumberFormatException e) {
                if (!intervalBounds.isEmpty()) {
                    System.out.println("Saisie terminée.");
                    break;
                } else {
                    System.out.println("Aucune borne valide n'a été saisie. Veuillez entrer au moins une borne.");
                }
            }
        }

        // Trier les intervalles
        Collections.sort(intervalBounds);
    }

    /**
     * Affiche la liste transformée de lettres résultant de la transformation.
     * Vérifie que la transformation a été effectuée avant l'affichage.
     */
    public void afficherTransformation() {
        if (lettersList != null) {
            System.out.print("Liste transformée : ");
            for (Character c : lettersList) {
                System.out.print(c + " ");
            }
            System.out.println();
        } else {
            System.out.println("La transformation n'a pas encore été effectuée.");
        }
    }

    /**
     * Récupère la liste de lettres résultant de la transformation.
     *
     * @return La liste de lettres transformée.
     */
    public List<Character> getLettersList() {
        return lettersList;
    }

    /**
     * Récupère l'alphabet utilisé pour la transformation.
     *
     * @return Le tableau de caractères représentant l'alphabet.
     */
    public char[] getAlphabet() {
        return alphabet;
    }

    /**
     * Récupère la liste des bornes d'intervalles.
     *
     * @return La liste de bornes d'intervalles.
     */
    public List<Double> getIntervalBounds() {
        return intervalBounds;
    }
}
