package com.example.r2dbc.excel;

import com.example.r2dbc.entity.Employee;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EmployeeExcelBuilder {

    public static Mono<ByteArrayInputStream> generateExcelFromFlux(Flux<Employee> studentFlux) {
        return Mono.create(sink -> {
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            SheetWrapper sheetWrapper = new SheetWrapper(workbook);

            // Sử dụng một biến để theo dõi chỉ số hàng hiện tại
            int[] rowIndex = {1};

            // Subscribe to the Flux and write data to the Excel sheet
            studentFlux.subscribe(student -> {
                // Kiểm tra nếu số dòng vượt quá 1,000,000
                if (rowIndex[0] > 300000) {
                    sheetWrapper.createNewSheet(workbook);
                    rowIndex[0] = 1; // Đặt lại chỉ số hàng
                }
                Row row = sheetWrapper.sheet.createRow(rowIndex[0]++);
                row.createCell(0).setCellValue(student.getId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getDepartment());
            }, sink::error, () -> {
                // Chuyển đổi workbook thành ByteArrayInputStream khi Flux hoàn thành
                try {
                    workbook.write(out);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());
                    sink.success(byteArrayInputStream);
                } catch (IOException e) {
                    sink.error(e);
                } finally {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }


}
