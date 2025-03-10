package com.example.surveyservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "crotest")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CROTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    String name;

    String targetUrl;

    @ElementCollection
    List<String> variants;
}