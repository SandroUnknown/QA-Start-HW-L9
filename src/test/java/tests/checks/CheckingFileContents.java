package tests.checks;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import tests.FilesParsingTest;
import tests.models.Resume;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static tests.models.Resume.Gender.MALE;
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

                        case "JSON":
                            checkingJson(zis);
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

    private void checkingJson(ZipInputStream zis) throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = zis.read(buffer)) != -1) {
            byteStream.write(buffer, 0, length);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = new ByteArrayInputStream(byteStream.toByteArray());
        Resume json = objectMapper.readValue(inputStream, Resume.class);

        Assertions.assertEquals(json.getName(),         "Иванов Иван Иванович");
        Assertions.assertEquals(json.getDateOfBirth(),  "1991-07-31");
        Assertions.assertEquals(json.getGender(),              MALE);

        Resume.WorkExperience work = json.getWorkExperiences().get(0);
        Assertions.assertEquals(work.getCompanyName(),  "ООО 'Шоколадная фабрика'");
        Assertions.assertEquals(work.getPosition(),     "Главный бухгалтер");
        Assertions.assertEquals(work.getStartDate(),    "2009-05-19");
        Assertions.assertEquals(work.getEndDate(),      "2012-12-28");

        work = json.getWorkExperiences().get(1);
        Assertions.assertEquals(work.getCompanyName(),  "ООО 'Яндекс'");
        Assertions.assertEquals(work.getPosition(),     "Генеральный директор");
        Assertions.assertEquals(work.getStartDate(),    "2013-03-14");
        Assertions.assertEquals(work.getEndDate(),      "2016-08-30");

        work = json.getWorkExperiences().get(2);
        Assertions.assertEquals(work.getCompanyName(),  "ООО 'Сбербанк'");
        Assertions.assertEquals(work.getPosition(),     "Топ-менеджер");
        Assertions.assertEquals(work.getStartDate(),    "2016-10-10");
        Assertions.assertEquals(work.getEndDate(),      "2018-02-18");

        work = json.getWorkExperiences().get(3);
        Assertions.assertEquals(work.getCompanyName(),  "ЗАО 'Супер-Компания'");
        Assertions.assertEquals(work.getPosition(),     "Коммерческий директор");
        Assertions.assertEquals(work.getStartDate(),    "2018-04-15");
        Assertions.assertEquals(work.getEndDate(),      "2024-03-31");
    }
}