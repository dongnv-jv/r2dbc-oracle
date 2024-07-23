package com.example.r2dbc.controller;

import com.example.r2dbc.entity.Employee;
import com.example.r2dbc.excel.EmployeeExcelBuilder;
import com.example.r2dbc.repository.EmployeeRepository;
import com.example.r2dbc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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


  private final EmployeeRepository employeeRepository;

  private final EmployeeService employeeService;

  @PostMapping
  public Mono<Employee> saveEmployee(@RequestBody Employee employee){
    return employeeRepository.save(employee);
  }

  @GetMapping("/{id}")
  public Mono<Employee> findOne(@PathVariable("id") Long id){
    return employeeRepository.findById(id);
  }
  @GetMapping("/save")
  public Mono<Employee> save(){

    return employeeService.saveAll().last();
  }

  @PutMapping
  public Mono<Employee> updateEmployee(@RequestBody Employee employee){
    return employeeRepository.save(employee);
  }

  @DeleteMapping("/{id}")
  public Mono<Employee> deleteEmployee(@PathVariable("id") Long id){
    return employeeRepository.findById(id)
        .doOnSuccess(employee -> employeeRepository.delete(employee).subscribe());
  }

  @GetMapping
  public Flux<Employee> findAll(){
    return employeeService.get();
  }
  @GetMapping("/download")
  public ResponseEntity<ByteArrayResource> getFile() {

    ByteArrayInputStream workbook= EmployeeExcelBuilder.generateExcelFromFlux(
            employeeService.getAll()).block(Duration.ofMinutes(3));
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
    Mono<ByteArrayInputStream> workbook= EmployeeExcelBuilder.generateExcelFromFlux(
            employeeService.getLimitedEmployees(700000));

    return workbook.flatMap(inputStream -> {
        byte[] bytes = inputStream.readAllBytes();
        ByteArrayResource resource = new ByteArrayResource(bytes);
        return Mono.just(resource);
    });
  }
}
