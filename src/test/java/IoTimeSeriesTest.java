package test.java;

import main.java.IoTimeSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class IoTimeSeriesTest {
    private IoTimeSeries ioTimeSeries;

    @BeforeEach
    public void setUp() {
        ioTimeSeries = new IoTimeSeries();
    }

    @Test
    public void testLireListeWithValidCSVData() throws IOException {
        // Création d'un fichier CSV temporaire avec des données valides
        Path path = Files.createTempFile("testFileValid", ".csv");
        Files.write(path, List.of("1.1", "2.2", "3.3"));

        ioTimeSeries.lireListe(path.toString());
        List<Double> numbers = ioTimeSeries.getNumbersList();

        assertNotNull(numbers);
        assertEquals(3, numbers.size());
        assertEquals(1.1, numbers.get(0));
        assertEquals(2.2, numbers.get(1));
        assertEquals(3.3, numbers.get(2));

        Files.delete(path); // Nettoyage du fichier temporaire
    }

    @Test
    public void testLireListeWithInvalidCSVData() throws IOException {
        // Création d'un fichier CSV temporaire avec des lignes invalides
        Path path = Files.createTempFile("testFileInvalid", ".csv");
        Files.write(path, List.of("1.1", "invalid", "2.2", "NaN", "3.3"));

        ioTimeSeries.lireListe(path.toString());
        List<Double> numbers = ioTimeSeries.getNumbersList();

        assertNotNull(numbers);
        assertEquals(3, numbers.size());  // Seules les lignes valides sont lues
        assertEquals(1.1, numbers.get(0));
        assertEquals(2.2, numbers.get(1));
        assertEquals(3.3, numbers.get(2));

        Files.delete(path); // Nettoyage du fichier temporaire
    }

    @Test
    public void testLireListeWithEmptyCSVFile() throws IOException {
        // Création d'un fichier CSV temporaire vide
        Path path = Files.createTempFile("testFileEmpty", ".csv");

        ioTimeSeries.lireListe(path.toString());
        List<Double> numbers = ioTimeSeries.getNumbersList();

        assertNotNull(numbers);
        assertTrue(numbers.isEmpty());  // Le fichier est vide, donc la liste doit être vide

        Files.delete(path); // Nettoyage du fichier temporaire
    }

    @Test
    public void testLireListeWithLargeNumbersCSV() throws IOException {
        // Création d'un fichier CSV temporaire avec de grands nombres
        Path path = Files.createTempFile("testFileLargeNumbers", ".csv");
        Files.write(path, List.of("1000000.0", "9999999.9", "-1000000.0"));

        ioTimeSeries.lireListe(path.toString());
        List<Double> numbers = ioTimeSeries.getNumbersList();

        assertNotNull(numbers);
        assertEquals(3, numbers.size());
        assertEquals(1000000.0, numbers.get(0));
        assertEquals(9999999.9, numbers.get(1));
        assertEquals(-1000000.0, numbers.get(2));

        Files.delete(path); // Nettoyage du fichier temporaire
    }

    @Test
    public void testLireListeWithNegativeNumbersCSV() throws IOException {
        // Création d'un fichier CSV temporaire avec des nombres négatifs
        Path path = Files.createTempFile("testFileNegativeNumbers", ".csv");
        Files.write(path, List.of("-1.1", "-2.2", "-3.3"));

        ioTimeSeries.lireListe(path.toString());
        List<Double> numbers = ioTimeSeries.getNumbersList();

        assertNotNull(numbers);
        assertEquals(3, numbers.size());
        assertEquals(-1.1, numbers.get(0));
        assertEquals(-2.2, numbers.get(1));
        assertEquals(-3.3, numbers.get(2));

        Files.delete(path); // Nettoyage du fichier temporaire
    }

    @Test
    public void testLireListeCSVFileNotFound() {
        assertThrows(IOException.class, () -> {
            ioTimeSeries.lireListe("nonexistent_file.csv");
        });
    }

    @Test
    public void testGetNumbersListWithoutReadingCSVFile() {
        // Vérifier que la liste des nombres est nulle si aucun fichier n'a été lu
        List<Double> numbers = ioTimeSeries.getNumbersList();
        assertNull(numbers);
    }
}
