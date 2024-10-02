package tests.checks;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import tests.FilesParsingTest;

import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static utils.ParsingFileName.getFileExtension;

public class CheckingFileContents {

    private final ClassLoader cl = FilesParsingTest.class.getClassLoader();

    public void checkingZip(String resDirectory, List<String> listExtensions) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream(resDirectory))) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                String fileExtension = getFileExtension(entry.getName());
                if (listExtensions.contains(fileExtension)) {
                    switch (fileExtension) {
                        case "XLSX":
                        case "XLS":
                            checkingXls(zis);
                            break;

                        case "CSV":
                            checkingCsv(zis);
                            break;

                        case "PDF":
                            checkingPdf(zis);
                            break;
                    }
                }
            }
        }
    }

    private void checkingXls(ZipInputStream zis) throws Exception {
         XLS xls = new XLS(zis);
         var sheet = xls.excel.getSheetAt(0);
         Assertions.assertTrue(sheet.getRow(0).getCell(0).getStringCellValue().contains("Просто"));
         Assertions.assertTrue(sheet.getRow(0).getCell(1).getStringCellValue().contains("тестовый"));
         Assertions.assertTrue(sheet.getRow(0).getCell(2).getStringCellValue().contains("файл"));
         Assertions.assertTrue(sheet.getRow(1).getCell(0).getStringCellValue().contains("в"));
         Assertions.assertTrue(sheet.getRow(1).getCell(1).getStringCellValue().contains("формате"));
         Assertions.assertTrue(sheet.getRow(1).getCell(2).getStringCellValue().contains("xls"));
    }

    private void checkingCsv(ZipInputStream zis) throws Exception {
        CSVReader csvReader = new CSVReader(new InputStreamReader(zis));
        List<String[]> data = csvReader.readAll();
        Assertions.assertEquals(3, data.size());
        Assertions.assertArrayEquals(new String[] {"Just", "test"}, data.get(0));
        Assertions.assertArrayEquals(new String[] {"file","in"},    data.get(1));
        Assertions.assertArrayEquals(new String[] {"csv","format"}, data.get(2));
    }

    private void checkingPdf(ZipInputStream zis) throws Exception {
        PDF pdf = new PDF(zis);
        Assertions.assertEquals(pdf.author, "VANDA");
        Assertions.assertEquals(pdf.numberOfPages, 1);
        Assertions.assertTrue(pdf.text.contains("Просто тестовый файл в формате PDF"));
    }
}
