package main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale pour exécuter l'application.
 */
public class Main {
    /**
     * Point d'entrée de l'application.
     *
     * @param args les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        // Création des objets des différentes classes
        Matrice_Sax matrice = new Matrice_Sax();
        Sax sax = new Sax();
        IoTimeSeries ioTimeSeries = new IoTimeSeries();

        // Lecture de la liste de nombres depuis un fichier
        String fileName = "liste4.csv";

        try {
            // Lecture des nombres à partir du fichier
            ioTimeSeries.lireListe(fileName);
            sax.setNumbersList(ioTimeSeries.getNumbersList());
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier: " + e.getMessage());
            return;
        }

        // Saisie des intervalles
        sax.saisirIntervalles();

        // Transformation des nombres en lettres
        sax.transfo();

        // Affichage des lettres générées
        sax.afficherTransformation();

        // Saisie de la longueur du mot
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez la longueur des mots souhaitée : ");
        int longueurMot = scanner.nextInt();

        // Découpage en mots selon la longueur spécifiée
        matrice.decouperEnMots(sax.getLettersList(), longueurMot);

        // Affichage des mots générés
        matrice.afficherMots();

        // Demande du nombre de lettres par mot (1 ou 2)
        System.out.print("Entrez le nombre de lettres par mot (1 ou 2) : ");
        int nbLettMot = scanner.nextInt();

        // Génération de la matrice d'occurrence
        List<Character> alphabetList = new ArrayList<>();
        for (char c : sax.getAlphabet()) {
            alphabetList.add(c);
        }

        // Transformation de la matrice d'occurrence
        char[][] letterMatrix = null;
        String[][] combinationMatrix = null;
        int[][] occurrenceMatrix = null;

        // Générer la matrice des lettres ou des combinaisons selon le choix de l'utilisateur
        if (nbLettMot == 1) {
            letterMatrix = matrice.genererMatriceUneLettre(alphabetList, sax.getIntervalBounds());
            occurrenceMatrix = matrice.compterOccurrencesUneLettre(letterMatrix);
        } else if (nbLettMot == 2) {
            combinationMatrix = matrice.genererMatriceDeuxLettres(alphabetList);
            occurrenceMatrix = matrice.compterOccurrencesDeuxLettres(combinationMatrix);
        } else {
            System.err.println("Le nombre de lettres par mot doit être 1 ou 2.");
            return;
        }

        // Génération du bitmap à partir de la matrice d'occurrence
        BitMap bitMap = new BitMap();
        bitMap.genererBitmap(occurrenceMatrix, "bitmap-246-"+longueurMot+"-"+nbLettMot);

        System.out.println("Le fichier bitmap a été généré avec succès.");
    }
}
