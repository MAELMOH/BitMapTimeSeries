import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * La classe BitMap permet de générer une image bitmap en niveaux de gris à partir
 * d'une matrice d'occurrences. Elle normalise les valeurs d'occurrence entre 0 et 100,
 * où 0 représente le blanc et 100 représente le noir, pour créer une visualisation
 * graphique des données.
 */
public class BitMap {

    /**
     * Génère un fichier bitmap à partir d'une matrice d'occurrences.
     *
     * @param occurrenceMatrix La matrice d'occurrences à convertir en image.
     * @param fileName Le nom du fichier de sortie sans extension.
     */
    public void genererBitmap(int[][] occurrenceMatrix, String fileName) {
        int maxVal = trouverMax(occurrenceMatrix); // Trouver la valeur maximale dans la matrice

        // Création de la nouvelle matrice normalisée entre 0 et 100
        int rows = occurrenceMatrix.length;
        int cols = occurrenceMatrix[0].length; // Récupérer le nombre de colonnes
        int[][] normalizedMatrix = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                normalizedMatrix[i][j] = (int) ((occurrenceMatrix[i][j] / (double) maxVal) * 100);
            }
        }

        // Affichage de la matrice normalisée avant de générer le bitmap
        afficherMatrice(normalizedMatrix);

        // Générer le bitmap à partir de la matrice normalisée
        BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Calculer la valeur de gris (0 -> blanc, 100 -> noir)
                int colorValue = 255 - (normalizedMatrix[i][j] * 255 / 100); // 0 -> blanc, 100 -> noir
                int rgb = (colorValue << 16) | (colorValue << 8) | colorValue; // Convertir en RGB
                image.setRGB(j, i, rgb); // Assigner la couleur dans l'image
            }
        }

        // Vérifier si le dossier "tests" existe, sinon le créer
        Path outputDir = Paths.get("test4");
        if (!Files.exists(outputDir)) {
            try {
                Files.createDirectory(outputDir);
            } catch (IOException e) {
                System.err.println("Erreur lors de la création du dossier 'tests' : " + e.getMessage());
            }
        }

        // Enregistrer l'image bitmap dans le dossier "tests"
        File outputFile = new File(outputDir.toString(), fileName + ".bmp");
        try {
            ImageIO.write(image, "bmp", outputFile);
            System.out.println("Fichier bitmap généré : " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du fichier bitmap : " + e.getMessage());
        }
    }

    /**
     * Affiche la matrice normalisée dans un format lisible.
     *
     * @param matrix La matrice normalisée à afficher.
     */
    private void afficherMatrice(int[][] matrix) {
        System.out.println("Matrice normalisée (0 = blanc, 100 = noir) :");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j]);
                if (j < matrix[i].length - 1) {
                    System.out.print(" | "); // Ajout du séparateur entre les colonnes
                }
            }
            System.out.println(); // Nouvelle ligne après chaque rangée
        }
    }

    /**
     * Trouve la valeur maximale dans la matrice d'occurrence.
     *
     * @param matrix La matrice d'occurrence pour laquelle la valeur maximale est recherchée.
     * @return La valeur maximale trouvée dans la matrice.
     */
    private int trouverMax(int[][] matrix) {
        int max = Integer.MIN_VALUE;
        for (int[] row : matrix) {
            for (int val : row) {
                if (val > max) {
                    max = val;
                }
            }
        }
        return max;
    }
}
