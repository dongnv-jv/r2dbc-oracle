package com.example.r2dbc.excel;

import com.example.r2dbc.entity.Student;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StudentExcelBuilder {

    private static final int MAX_ROWS_PER_SHEET = 1000000;

    public static Mono<ByteArrayInputStream> exportStudentsToExcel(Flux<Student> students) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook wb = new Workbook(out, "MyApp", "1.0");
        Worksheet[] currentSheet = {wb.newWorksheet("Students1")};
        int[] sheetIndex = {0};
        int[] rowIndex = {0};

        // Write headers to the first sheet
        writeHeaders(currentSheet[0], rowIndex[0]++);

        return students.flatMap(student -> {
            if (rowIndex[0] == MAX_ROWS_PER_SHEET) {
                // Move to the next sheet
                sheetIndex[0]++;
                currentSheet[0] = wb.newWorksheet("Students" + (sheetIndex[0] + 1));
                rowIndex[0] = 0;
                // Write headers to the new sheet
                writeHeaders(currentSheet[0], rowIndex[0]++);
            }

            // Write student data
            writeStudentData(currentSheet[0], rowIndex[0]++, student);
            return Mono.empty();
        }).then(Mono.fromCallable(() -> {
            try {
                wb.finish();
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private static void writeHeaders(Worksheet ws, int rowIndex) {
        ws.value(rowIndex, 0, "ID");
        ws.value(rowIndex, 1, "Name");
        ws.value(rowIndex, 2, "Age");
        ws.value(rowIndex, 3, "Major");
    }

    private static void writeStudentData(Worksheet ws, int rowIndex, Student student) {
        ws.value(rowIndex, 0, student.getId());
        ws.value(rowIndex, 1, student.getName());
        ws.value(rowIndex, 2, student.getAge());
        ws.value(rowIndex, 3, student.getMajor());
    }
}
