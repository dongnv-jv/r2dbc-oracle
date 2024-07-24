package com.example.r2dbc.controller;

import com.example.r2dbc.entity.Student;
import com.example.r2dbc.excel.StudentExcelBuilder;
import com.example.r2dbc.repository.StudentRepository;
import com.example.r2dbc.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.time.Duration;

/**
 * @author rishi
 */
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {


    private final StudentRepository studentRepository;

    private final StudentService studentService;

    @PostMapping
    public Mono<Student> saveEmployee(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @GetMapping("/{id}")
    public Mono<Student> findOne(@PathVariable("id") Long id) {
        return studentRepository.findById(id);
    }

    @GetMapping("/save")
    public Mono<Student> save() {

        return studentService.saveAll().last();
    }

    @PutMapping
    public Mono<Student> updateEmployee(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @DeleteMapping("/{id}")
    public Mono<Student> deleteEmployee(@PathVariable("id") Long id) {
        return studentRepository.findById(id)
                .doOnSuccess(student -> studentRepository.delete(student).subscribe());
    }

    @GetMapping
    public Flux<Student> findAll() {
        return studentService.get();
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> getFile() {

        ByteArrayInputStream workbook = StudentExcelBuilder.exportStudentsToExcel(
                studentService.getAll()).block(Duration.ofMinutes(3));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=customers.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new ByteArrayResource(workbook.readAllBytes()));

    }

    @GetMapping(value = "/download1", produces = "application/vnd.ms-excel")
    public Mono<ByteArrayResource> download() {
        Mono<ByteArrayInputStream> workbook = StudentExcelBuilder.exportStudentsToExcel(
                studentService.callStoredProcedure());

        return workbook.flatMap(inputStream -> {
            byte[] bytes = inputStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(bytes);
            return Mono.just(resource);
        });
    }
}
