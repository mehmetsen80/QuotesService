package org.lite.quotes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lite.quotes.service.DataLoaderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Data Loader", description = "APIs for loading initial data")
public class DataLoaderController {

    private final DataLoaderService dataLoaderService;

    @Operation(summary = "Load people data from CSV", description = "Loads initial people data from the CSV file. Use force=true to delete existing data and reload.")
    @GetMapping(value = "/load/people", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loadPeopleData(
            @Parameter(description = "Force reload by deleting existing data", example = "false")
            @RequestParam(defaultValue = "false") boolean force) {
        log.info("Received request to load people data. Force reload: {}", force);
        try {
            int loadedCount = dataLoaderService.loadPeopleFromCsv(force);
            String message = String.format("Successfully loaded %d people from data" + (force ? " (forced reload)" : ""), loadedCount);
            log.info(message);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"" + message + "\", \"loadedCount\": " + loadedCount + "}");
        } catch (Exception e) {
            String errorMessage = "Failed to load people data: " + e.getMessage();
            log.error(errorMessage, e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"" + errorMessage + "\"}");
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