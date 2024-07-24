package com.example.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("STUDENT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @Column("ID")
    private Long id;

    @Column("NAME")
    private String name;

    @Column(value = "AGE")
    private Integer age;

    @Column("MAJOR")
    private String major;

    public Student(String name, String major) {
        this.name = name;
        this.major = major;
    }
}
