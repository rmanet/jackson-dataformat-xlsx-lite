package com.github.sett4.dataformat.xlsx.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class XlsxWriter {
    Logger log = Logger.getLogger(XlsxWriter.class.getCanonicalName());

    private final OutputStream out;
    private final SXSSFWorkbook workbook;
    private final Sheet sheet;

    private Row row = null;
    private int rowIndex = -1;
    private int _nextColumnToWrite = 0;

    public XlsxWriter(OutputStream out) {
        this.out = out;
        this.workbook = new SXSSFWorkbook();
        this.sheet = workbook.createSheet();
    }

    public Row getRow() {
        if (row == null) {
            rowIndex++;
            row = sheet.createRow(rowIndex);
            _nextColumnToWrite = 0;
        }

        return row;
    }

    public void write(int columnIndex, String text) {
        createCell(columnIndex, Cell.CELL_TYPE_STRING).setCellValue(text);
    }

    public void write(int columnIndex, boolean state) {
        createCell(columnIndex, Cell.CELL_TYPE_BOOLEAN).setCellValue(state);
    }

    public void endRow() {
        row = null;
        _nextColumnToWrite = 0;
    }

    public void close() throws IOException {
        workbook.write(out);
    }

    public void writeColumnName(String name, int index) {
        getRow().createCell(index).setCellValue(name);
    }

    public void writeNull(int columnIndex) {
    	if (columnIndex == _nextColumnToWrite) {
    		++_nextColumnToWrite;
    	}
    }

    public void write(int columnIndex, float v) {
        createCell(columnIndex, Cell.CELL_TYPE_NUMERIC).setCellValue(v);
    }

    public void write(int columnIndex, double v) {
        createCell(columnIndex, Cell.CELL_TYPE_NUMERIC).setCellValue(v);
    }

    public void write(int columnIndex, int v) {
        createCell(columnIndex, Cell.CELL_TYPE_NUMERIC).setCellValue(v);
    }

    public void write(int columnIndex, long v) {
        createCell(columnIndex, Cell.CELL_TYPE_NUMERIC).setCellValue(v);
    }

    public Object getOutputTarget() {
        return out;
    }
    
    private Cell createCell(int columnIndex, int type) {
    	Cell cell = getRow().createCell(columnIndex, type);
    	if (columnIndex == _nextColumnToWrite) {
    		++_nextColumnToWrite;
    	}
    	return cell;
    }
    
    public int nextColumnIndex() {
        return _nextColumnToWrite;
    }
}
