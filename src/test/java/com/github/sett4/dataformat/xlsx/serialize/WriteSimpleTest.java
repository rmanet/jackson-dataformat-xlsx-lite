package com.github.sett4.dataformat.xlsx.serialize;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.sett4.dataformat.xlsx.ModuleTestBase;
import com.github.sett4.dataformat.xlsx.XlsxGenerator;
import com.github.sett4.dataformat.xlsx.XlsxMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WriteSimpleTest extends ModuleTestBase {
    private final ObjectMapper MAPPER = mapperForXlsx();

    public XlsxMapper mapperForXlsx() {
        return new XlsxMapper();
    }

    @Test
    public void testSchema() throws Exception {
        FiveMinuteUser user = new FiveMinuteUser("Silu", "Seppala", false, Gender.MALE, 123,
                new byte[]{1, 2, 3, 4, 5}, BigDecimal.ZERO);
        File file = File.createTempFile("jackson-xlsx-test", ".xlsx");
        System.out.println(file);

        XlsxMapper mapper = new XlsxMapper();
        mapper.enable(XlsxGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        CsvSchema schema = mapper.schemaFor(FiveMinuteUser.class).withHeader();
        SequenceWriter sequenceWriter = mapper.writer(schema).writeValues(file);
        sequenceWriter.write(user);
        sequenceWriter.write(user);
        sequenceWriter.write(user);
        sequenceWriter.write(user);
        sequenceWriter.write(user);
        sequenceWriter.write(user);
        sequenceWriter.write(user);
        sequenceWriter.flush();
        sequenceWriter.close();

    }

    @Test
    public void testSimpleExplicit() throws Exception {
        CsvSchema schema = CsvSchema.builder()
                .addColumn("firstName")
                .addColumn("lastName")
                .addColumn("gender")
                .addColumn("userImage")
                .addColumn("verified")
                .addColumn("i")
                .addColumn("bigDecimal")
                .setUseHeader(true)
                .build();

        // from base, default order differs:
        // @JsonPropertyOrder({"firstName", "lastName", "gender" ,"verified", "userImage"})

        FiveMinuteUser user = new FiveMinuteUser("Silu", "Seppala", false, Gender.MALE, 123,
                new byte[]{1, 2, 3, 4, 5}, new BigDecimal("1234567890123456789012345678901234567890"));
        File file = File.createTempFile("jackson-xlsx-test", ".xlsx");
        System.out.println(file);
        OutputStream outputStream = new FileOutputStream(file);
        MAPPER.writer(schema).writeValue(outputStream, user);
        outputStream.close();

        Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        int activeSheetIndex = workbook.getActiveSheetIndex();
        Sheet sheet = workbook.getSheetAt(activeSheetIndex);

        assertEquals("firstName", sheet.getRow(0).getCell(0).getStringCellValue());
        assertEquals("lastName", sheet.getRow(0).getCell(1).getStringCellValue());
        assertEquals("gender", sheet.getRow(0).getCell(2).getStringCellValue());
        assertEquals("userImage", sheet.getRow(0).getCell(3).getStringCellValue());
        assertEquals("verified", sheet.getRow(0).getCell(4).getStringCellValue());
        assertEquals("i", sheet.getRow(0).getCell(5).getStringCellValue());
        assertEquals(new BigDecimal("1234567890123456789012345678901234567890").doubleValue(), sheet.getRow(1).getCell(6).getNumericCellValue());

        file.delete();
    }

    @Test
    public void testFeature_NumbersAsString() throws IOException {
        XlsxMapper mapper = new XlsxMapper();
        mapper.enable(XlsxGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        CsvSchema schema = mapper.schemaFor(FiveMinuteUser.class).withHeader();

        FiveMinuteUser user = new FiveMinuteUser("Silu", "Seppala", false, Gender.MALE, 123,
                new byte[]{1, 2, 3, 4, 5}, new BigDecimal("1234567890123456789012345678901234567890"));
        File file = File.createTempFile("jackson-xlsx-test", ".xlsx");
        OutputStream outputStream = new FileOutputStream(file);
        mapper.writer(schema).writeValue(outputStream, user);
        outputStream.close();

        Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        int activeSheetIndex = workbook.getActiveSheetIndex();
        Sheet sheet = workbook.getSheetAt(activeSheetIndex);
        assertEquals(123.0, sheet.getRow(1).getCell(5).getNumericCellValue());
        assertEquals(Cell.CELL_TYPE_NUMERIC, sheet.getRow(1).getCell(5).getCellType());
        assertEquals("1234567890123456789012345678901234567890", sheet.getRow(1).getCell(6).getStringCellValue());
        assertEquals(Cell.CELL_TYPE_STRING, sheet.getRow(1).getCell(6).getCellType());

        file.delete();
    }
    
    @Test
    public void testWithoutHeader() throws IOException {
    	XlsxMapper mapper = new XlsxMapper();
    	CsvSchema.Builder builder = CsvSchema.builder();
    	builder.setUseHeader(false);
    	CsvSchema schema = builder.build();
    	
    	List<List<Object>> list = new ArrayList<>();
    	List<Object> row = new ArrayList<>();
    	row.add("Silu");
    	row.add("Seppala");
    	row.add(false);
    	row.add(Gender.MALE);
    	row.add(123);
    	row.add(new byte[]{1, 2, 3, 4, 5});
    	row.add(new BigDecimal("1234567890123456789012345678901234567890"));
    	list.add(row);
    	
    	File file = File.createTempFile("jackson-xlsx-test", ".xlsx");
    	
        OutputStream outputStream = new FileOutputStream(file);
        mapper.writer(schema).writeValue(outputStream, list);
        outputStream.close();
        
        Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        int activeSheetIndex = workbook.getActiveSheetIndex();
        Sheet sheet = workbook.getSheetAt(activeSheetIndex);
        assertEquals(Cell.CELL_TYPE_STRING, sheet.getRow(0).getCell(0).getCellType());
        assertEquals("Silu", sheet.getRow(0).getCell(0).getStringCellValue());
        assertEquals(Cell.CELL_TYPE_NUMERIC, sheet.getRow(0).getCell(4).getCellType());
        assertEquals(123.0, sheet.getRow(0).getCell(4).getNumericCellValue());
        assertEquals(Cell.CELL_TYPE_NUMERIC, sheet.getRow(0).getCell(6).getCellType());
        assertEquals(new BigDecimal("1234567890123456789012345678901234567890").doubleValue(), sheet.getRow(0).getCell(6).getNumericCellValue());
    	
        file.delete();
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    @JsonPropertyOrder({"id", "amount"})
    static class Entry {
        public String id;
        public double amount;

        public Entry(String id, double amount) {
            this.id = id;
            this.amount = amount;
        }
    }

    @JsonPropertyOrder({"id", "amount"})
    static class Entry2 {
        public String id;
        public float amount;

        public Entry2(String id, float amount) {
            this.id = id;
            this.amount = amount;
        }
    }
}
