package org.lite.quotes.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "quotes")
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
    
    @Column(name = "quote_text", nullable = false, columnDefinition = "TEXT")
    private String quoteText;
    
    private String source;
    
    private Integer year;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
