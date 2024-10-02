package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tests.checks.CheckingFileContents;

import java.util.List;

public class FilesParsingTest {

    private static final CheckingFileContents check = new CheckingFileContents();
    private final String zipDirectory = "files/archive.zip";

    @Test
    @DisplayName("Чтение и проверка содержимого ВСЕХ файлов из ZIP-архива (за один 'проход' архива)")
    void zipFileParsingAndReadingTest() throws Exception {
        check.checkingZip(zipDirectory, List.of("PDF", "CSV", "XLSX", "XLS", "JSON"));
    }

    @ValueSource(strings = {"XLSX", "CSV", "PDF", "JSON"})
    @ParameterizedTest(name = "Чтение и проверка содержимого {0}-файла")
    void readingPdfFromPDFTest(String extension) throws Exception {
        check.checkingZip(zipDirectory, List.of(extension));
    }
}