package com.example.r2dbc.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class SheetWrapper {
    Sheet sheet;
    int sheetIndex;

    SheetWrapper(Workbook workbook) {
        this.sheet = workbook.createSheet("Students1");
        this.sheetIndex = 1;
        createHeaderRow();
    }

    void createHeaderRow() {
        Row headerRow = this.sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Age");
        headerRow.createCell(2).setCellValue("City");
    }

    void createNewSheet(Workbook workbook) {
        this.sheet = workbook.createSheet("Students" + (++this.sheetIndex));
        createHeaderRow();
    }
}
