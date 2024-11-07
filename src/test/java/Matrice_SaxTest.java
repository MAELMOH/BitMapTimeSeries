package test.java;

import main.java.Matrice_Sax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class Matrice_SaxTest {
    private Matrice_Sax matriceSax;

    @BeforeEach
    public void setUp() {
        matriceSax = new Matrice_Sax();
    }

    @Test
    public void testGenererMatriceUneLettreWithValidData() {
        List<Character> alphabet = Arrays.asList('a', 'b', 'c', 'd');
        List<Double> intervalBounds = Arrays.asList(1.0, 2.0, 3.0);
        char[][] matrix = matriceSax.genererMatriceUneLettre(alphabet, intervalBounds);

        assertNotNull(matrix);
        assertEquals(2, matrix.length);       // 2 lignes pour 4 lettres (en fonction des dimensions calculées)
        assertEquals(2, matrix[0].length);    // 2 colonnes pour 4 lettres
        assertEquals('a', matrix[0][0]);
        assertEquals('d', matrix[1][1]);
    }

    @Test
    public void testGenererMatriceUneLettreWithEmptyAlphabet() {
        List<Character> alphabet = Arrays.asList();
        List<Double> intervalBounds = Arrays.asList(1.0, 2.0, 3.0);
        char[][] matrix = matriceSax.genererMatriceUneLettre(alphabet, intervalBounds);

        assertNotNull(matrix);
        assertEquals(0, matrix.length);       // Matrice vide car aucun caractère dans l'alphabet
    }

    @Test
    public void testGenererMatriceDeuxLettresWithValidAlphabet() {
        List<Character> alphabet = Arrays.asList('a', 'b', 'c');
        String[][] matrix = matriceSax.genererMatriceDeuxLettres(alphabet);

        assertNotNull(matrix);
        assertEquals(3, matrix.length);       // Taille de l'alphabet
        assertEquals(3, matrix[0].length);    // Taille de l'alphabet pour les combinaisons
        assertEquals("aa", matrix[0][0]);
        assertEquals("bc", matrix[1][2]);
    }

    @Test
    public void testGenererMatriceDeuxLettresWithEmptyAlphabet() {
        List<Character> alphabet = Arrays.asList();
        String[][] matrix = matriceSax.genererMatriceDeuxLettres(alphabet);

        assertNotNull(matrix);
        assertEquals(0, matrix.length);       // Matrice vide car aucun caractère dans l'alphabet
    }

    @Test
    public void testCompterOccurrencesUneLettreWithSingleLetterMatrix() {
        char[][] letterMatrix = {{'a', 'b'}, {'c', 'a'}};
        matriceSax.setWordsList(Arrays.asList("a", "b", "c", "a"));
        int[][] occurrences = matriceSax.compterOccurrencesUneLettre(letterMatrix);

        assertNotNull(occurrences);
        assertEquals(2, occurrences[0][0]);   // 'a' apparaît deux fois
        assertEquals(1, occurrences[0][1]);   // 'b' apparaît une fois
        assertEquals(1, occurrences[1][0]);   // 'c' apparaît une fois
    }

    @Test
    public void testCompterOccurrencesUneLettreWithNoOccurrences() {
        char[][] letterMatrix = {{'x', 'y'}, {'z', 'w'}};
        matriceSax.setWordsList(Arrays.asList("a", "b", "c"));
        int[][] occurrences = matriceSax.compterOccurrencesUneLettre(letterMatrix);

        assertNotNull(occurrences);
        assertEquals(0, occurrences[0][0]);   // Aucun caractère dans letterMatrix n'apparaît dans la liste
        assertEquals(0, occurrences[1][1]);
    }

    @Test
    public void testCompterOccurrencesDeuxLettresWithValidCombinationMatrix() {
        String[][] combinationMatrix = {{"aa", "ab"}, {"ba", "bb"}};
        matriceSax.setWordsList(Arrays.asList("aab", "bb", "aa", "ba"));
        int[][] occurrences = matriceSax.compterOccurrencesDeuxLettres(combinationMatrix);

        assertNotNull(occurrences);
        assertEquals(2, occurrences[0][0]);   // "aa" apparaît deux fois
        assertEquals(1, occurrences[0][1]);   // "ab" apparaît une fois
        assertEquals(1, occurrences[1][1]);   // "bb" apparaît une fois
    }

    @Test
    public void testCompterOccurrencesDeuxLettresWithNoOccurrences() {
        String[][] combinationMatrix = {{"cd", "ef"}, {"gh", "ij"}};
        matriceSax.setWordsList(Arrays.asList("aab", "bb", "aa"));
        int[][] occurrences = matriceSax.compterOccurrencesDeuxLettres(combinationMatrix);

        assertNotNull(occurrences);
        assertEquals(0, occurrences[0][0]);   // Aucun caractère dans combinationMatrix n'apparaît dans la liste
        assertEquals(0, occurrences[1][1]);
    }

    @Test
    public void testGenererMatriceUneLettreWithSingleInterval() {
        List<Character> alphabet = Arrays.asList('a', 'b');
        List<Double> intervalBounds = Arrays.asList(1.0);
        char[][] matrix = matriceSax.genererMatriceUneLettre(alphabet, intervalBounds);

        assertNotNull(matrix);
        assertEquals(1, matrix.length);       // Une seule ligne pour deux lettres
        assertEquals(2, matrix[0].length);    // Deux colonnes
        assertEquals('a', matrix[0][0]);
        assertEquals('b', matrix[0][1]);
    }

    @Test
    public void testGenererMatriceUneLettreWithMultipleIntervals() {
        List<Character> alphabet = Arrays.asList('a', 'b', 'c', 'd', 'e');
        List<Double> intervalBounds = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        char[][] matrix = matriceSax.genererMatriceUneLettre(alphabet, intervalBounds);

        assertNotNull(matrix);
        assertEquals(2, matrix.length);       // La matrice doit être générée avec deux lignes
        assertEquals(3, matrix[0].length);    // La matrice doit avoir trois colonnes
    }

    @Test
    public void testGenererMatriceDeuxLettresWithEmptyIntervals() {
        List<Character> alphabet = Arrays.asList('a');
        String[][] matrix = matriceSax.genererMatriceDeuxLettres(alphabet);

        assertNotNull(matrix);
        assertEquals(1, matrix.length);       // Une seule lettre, donc une seule ligne
        assertEquals(1, matrix[0].length);    // Une seule colonne
        assertEquals("aa", matrix[0][0]);
    }
}
