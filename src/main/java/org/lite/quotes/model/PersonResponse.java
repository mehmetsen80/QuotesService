package org.lite.quotes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {
    private String fullName;
    private String knownAs;
    private Integer birthYear;
    private Integer deathYear;
    private String nationality;
    private String description;
    private String category;
} 