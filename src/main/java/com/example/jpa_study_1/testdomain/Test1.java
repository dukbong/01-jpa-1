package com.example.jpa_study_1.testdomain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Test1 {

    @Id @GeneratedValue
    private Long id;
    private String name;
}

