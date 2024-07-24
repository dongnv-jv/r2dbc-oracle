package com.example.r2dbc.service;

import com.example.r2dbc.entity.Student;
import com.example.r2dbc.repository.StudentRepository;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import oracle.r2dbc.OracleR2dbcTypes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DatabaseClient databaseClient;
    private final @Qualifier("111") ConnectionFactory connectionFactory;


    public Flux<Student> saveAll() {
        Flux<Student> studentFlux = Flux.range(1, 1_000_000)
                .map(i -> new Student("Dong " + i, "Student-" + i));
        return studentRepository.saveAll(studentFlux);

    }

    public Flux<Student> getAll() {
        return r2dbcEntityTemplate.select(Student.class).all();
    }

    public Flux<Student> getLimitedEmployees(int limit) {
        return r2dbcEntityTemplate.select(Student.class)
                .matching(Query.query(Criteria.empty()).limit(limit))
                .all();
    }

    public Flux<Student> get() {
        return databaseClient
                .sql("select * from employee")
                .map((row, rowMetadata) -> new Student(row.get("name", String.class), row.get("department", String.class))
                )
                .all();
    }


    public Flux<Student> callStoredProcedure() {
        String query = "CALL PKG_GET_ALL_STUDENT.PROC_GET_ALL_STUDENT(:P_CUR)";

        return databaseClient.inConnectionMany(
                connection -> {
                    Statement statement = connection.createStatement(query)
                            .bind(0, Parameters.out(OracleR2dbcTypes.REF_CURSOR));

                    return Flux.from(statement.execute())
                            .flatMap(result -> result
                                    .map(outParameters -> outParameters.get("P_CUR")))
                            .cast(Result.class)
                            .flatMap(result -> result.map(
                                    row -> new Student(
                                            row.get("ID", Long.class),
                                            row.get("name", String.class),
                                            row.get("age", Integer.class),
                                            row.get("major", String.class))));


                });
    }
}
