package com.example.r2dbc.repository;

import com.example.r2dbc.entity.Student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author rishi
 */
public interface StudentRepository extends ReactiveCrudRepository<Student, Long> {
}
