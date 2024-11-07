package test.java;

import main.java.Sax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class SaxTest {
    private Sax sax;

    @BeforeEach
    public void setUp() {
        sax = new Sax();
    }

    @Test
    public void testSetNumbersList() {
        List<Double> numbers = Arrays.asList(1.0, 2.5, 3.6);
        sax.setNumbersList(numbers);
        assertEquals(numbers, sax.getNumbersList());
    }

    @Test
    public void testTransfoWithValidIntervals() {
        sax.setNumbersList(Arrays.asList(0.5, 1.5, 2.5));
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0));
        sax.transfo();
        List<Character> letters = sax.getLettersList();
        assertNotNull(letters);
        assertEquals(Arrays.asList('a', 'b', 'c'), letters);
    }

    @Test
    public void testTransfoWithEdgeCaseNumbers() {
        sax.setNumbersList(Arrays.asList(1.0, 2.0, 3.0));
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0));
        sax.transfo();
        List<Character> letters = sax.getLettersList();
        assertEquals(Arrays.asList('a', 'b', 'c'), letters);
    }

    @Test
    public void testTransfoWithEmptyNumberList() {
        sax.setNumbersList(Arrays.asList());
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0));
        sax.transfo();
        List<Character> letters = sax.getLettersList();
        assertNotNull(letters); // Vérifie que lettersList est initialisé
        assertTrue(letters.isEmpty());
    }

    @Test
    public void testTransfoWithEmptyIntervals() {
        sax.setNumbersList(Arrays.asList(0.5, 1.5, 2.5));
        sax.saisirIntervalles(Arrays.asList());
        sax.transfo();
        List<Character> letters = sax.getLettersList();
        assertNotNull(letters); // Vérifie que lettersList est initialisé
        assertTrue(letters.isEmpty());
    }

    @Test
    public void testGetLetterForNumberWithinIntervals() {
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0));
        sax.setNumbersList(Arrays.asList(0.5, 1.5, 2.5));
        sax.transfo();
        assertEquals('a', sax.getLetterForNumber(0.5));
        assertEquals('b', sax.getLetterForNumber(1.5));
        assertEquals('c', sax.getLetterForNumber(2.5));
    }

    @Test
    public void testGetLetterForNumberEdgeCases() {
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0));
        sax.setNumbersList(Arrays.asList(1.0, 2.0));
        sax.transfo();
        assertEquals('a', sax.getLetterForNumber(1.0));
        assertEquals('b', sax.getLetterForNumber(2.0));
    }

    @Test
    public void testSaisirIntervallesWithValidInput() {
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0, 3.0));
        List<Double> intervals = sax.getIntervalBounds();
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), intervals);
    }

    @Test
    public void testSaisirIntervallesWithDuplicates() {
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0, 2.0, 3.0));
        List<Double> intervals = sax.getIntervalBounds();
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), intervals);
    }

    @Test
    public void testAfficherTransformationWithoutTransfo() {
        sax.afficherTransformation();
        assertNull(sax.getLettersList());
    }

    @Test
    public void testAfficherTransformationWithTransfo() {
        sax.setNumbersList(Arrays.asList(0.5, 1.5, 2.5));
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0));
        sax.transfo();

        List<Character> letters = sax.getLettersList();
        assertNotNull(letters);
        assertEquals(Arrays.asList('a', 'b', 'c'), letters);
    }

    @Test
    public void testGetAlphabetAfterTransfo() {
        sax.setNumbersList(Arrays.asList(0.5, 1.5, 2.5));
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0));
        sax.transfo();

        char[] alphabet = sax.getAlphabet();
        assertNotNull(alphabet);
        assertEquals(3, alphabet.length);
        assertArrayEquals(new char[]{'a', 'b', 'c'}, alphabet);
    }

    @Test
    public void testGetIntervalBounds() {
        sax.saisirIntervalles(Arrays.asList(1.0, 2.0, 3.0));
        List<Double> intervalBounds = sax.getIntervalBounds();
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), intervalBounds);
    }
}
