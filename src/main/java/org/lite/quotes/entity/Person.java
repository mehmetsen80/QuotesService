package org.lite.quotes.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "people")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "full_name", nullable = false, unique = true)
    private String fullName;
    
    @Column(name = "known_as")
    private String knownAs;  // For alternative names or titles
    
    @Column(name = "birth_year")
    private Integer birthYear;
    
    @Column(name = "death_year")
    private Integer deathYear;
    
    private String nationality;
    
    private String description;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
