package org.lite.quotes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lite.quotes.service.DataLoaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Data Loader", description = "APIs for loading initial data")
public class DataLoaderController {

    private final DataLoaderService dataLoaderService;

    @Operation(summary = "Load people data from CSV", description = "Loads initial people data from the CSV file")
    @GetMapping("/load/people")
    public ResponseEntity<String> loadPeopleData() {
        try {
            dataLoaderService.loadPeopleFromCsv();
            return ResponseEntity.ok("Successfully loaded people data");
        } catch (Exception e) {
            log.error("Error loading people data", e);
            return ResponseEntity.internalServerError().body("Failed to load people data: " + e.getMessage());
        }
    }

    @Operation(summary = "Load categories data from CSV", description = "Loads initial categories data from the CSV file")
    @GetMapping("/load/categories")
    public ResponseEntity<String> loadCategoriesData() {
        try {
            dataLoaderService.loadCategoriesFromCsv();
            return ResponseEntity.ok("Successfully loaded categories data");
        } catch (Exception e) {
            log.error("Error loading categories data", e);
            return ResponseEntity.internalServerError().body("Failed to load categories data: " + e.getMessage());
        }
    }
} 