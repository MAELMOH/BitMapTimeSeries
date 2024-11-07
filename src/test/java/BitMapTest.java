package test.java;

import main.java.BitMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;

public class BitMapTest {
    private BitMap bitmap;

    @BeforeEach
    public void setUp() {
        bitmap = new BitMap();
    }

    @Test
    public void testGenererBitmapWithValidMatrix() throws IOException {
        int[][] occurrenceMatrix = {
                {10, 20},
                {30, 40}
        };
        String fileName = "testBitmap";

        // Créer le bitmap et vérifier que le fichier est bien généré
        bitmap.genererBitmap(occurrenceMatrix, fileName);
        Path path = Path.of("test4", fileName + ".bmp");
        assertTrue(Files.exists(path), "Le fichier bitmap devrait exister.");

        // Vérifier le contenu de l'image générée
        BufferedImage image = ImageIO.read(new File(path.toString()));
        assertEquals(2, image.getHeight());
        assertEquals(2, image.getWidth());

        // Nettoyage après le test
        Files.delete(path);
    }

    @Test
    public void testGenererBitmapWithEmptyMatrix() {
        int[][] emptyMatrix = new int[0][0];
        String fileName = "emptyBitmap";
        assertDoesNotThrow(() -> bitmap.genererBitmap(emptyMatrix, fileName));
        Path path = Path.of("test4", fileName + ".bmp");
        assertFalse(Files.exists(path), "Aucun fichier bitmap ne doit être généré pour une matrice vide.");
    }

    @Test
    public void testGenererBitmapWithSingleValueMatrix() throws IOException {
        int[][] singleValueMatrix = {{100}};
        String fileName = "singleValueBitmap";

        bitmap.genererBitmap(singleValueMatrix, fileName);
        Path path = Path.of("test4", fileName + ".bmp");
        assertTrue(Files.exists(path), "Le fichier bitmap pour une matrice à valeur unique devrait exister.");

        BufferedImage image = ImageIO.read(new File(path.toString()));
        assertEquals(1, image.getHeight());
        assertEquals(1, image.getWidth());

        // Vérifier la couleur pour un noir complet (valeur 100)
        int rgb = image.getRGB(0, 0);
        assertEquals(0x000000, rgb & 0xFFFFFF, "La couleur pour la valeur maximale doit être noire (0x000000).");

        Files.delete(path); // Nettoyage après le test
    }

    @Test
    public void testGenererBitmapWithDifferentValues() throws IOException {
        int[][] occurrenceMatrix = {
                {0, 50},
                {100, 25}
        };
        String fileName = "testDifferentValues";

        bitmap.genererBitmap(occurrenceMatrix, fileName);
        Path path = Path.of("test4", fileName + ".bmp");
        assertTrue(Files.exists(path), "Le fichier bitmap pour une matrice à valeurs différentes devrait exister.");

        BufferedImage image = ImageIO.read(new File(path.toString()));
        assertEquals(2, image.getHeight());
        assertEquals(2, image.getWidth());

        // Vérifier les couleurs des pixels
        assertEquals(0xFFFFFF, image.getRGB(0, 0) & 0xFFFFFF); // Blanc (0) -> 0xFFFFFF
        assertEquals(0x7F7F7F, image.getRGB(0, 1) & 0xFFFFFF); // Gris (50)
        assertEquals(0x000000, image.getRGB(1, 0) & 0xFFFFFF); // Noir (100) -> 0x000000
        assertEquals(0xBFBFBF, image.getRGB(1, 1) & 0xFFFFFF); // Gris clair (25)

        Files.delete(path); // Nettoyage après le test
    }

    @Test
    public void testTrouverMaxWithValidMatrix() {
        int[][] matrix = {
                {1, 5},
                {9, 3}
        };
        int max = bitmap.trouverMax(matrix);
        assertEquals(9, max, "La valeur maximale devrait être 9.");
    }

    @Test
    public void testTrouverMaxWithNegativeValues() {
        int[][] matrix = {
                {-1, -5},
                {-9, -3}
        };
        int max = bitmap.trouverMax(matrix);
        assertEquals(-1, max, "La valeur maximale devrait être -1 pour une matrice avec des valeurs négatives.");
    }

    @Test
    public void testTrouverMaxWithSingleElementMatrix() {
        int[][] matrix = {{42}};
        int max = bitmap.trouverMax(matrix);
        assertEquals(42, max, "La valeur maximale devrait être l'unique valeur dans la matrice (42).");
    }

    @Test
    public void testTrouverMaxWithEmptyMatrix() {
        int[][] emptyMatrix = new int[0][0];
        int max = bitmap.trouverMax(emptyMatrix);
        assertEquals(Integer.MIN_VALUE, max, "La valeur maximale d'une matrice vide devrait être Integer.MIN_VALUE.");
    }
}
