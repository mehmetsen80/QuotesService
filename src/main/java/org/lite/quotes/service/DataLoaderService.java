package org.lite.quotes.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lite.quotes.entity.Person;
import org.lite.quotes.entity.Category;
import org.lite.quotes.repository.PersonRepository;
import org.lite.quotes.repository.CategoryRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataLoaderService {
    
    private final PersonRepository personRepository;
    private final CategoryRepository categoryRepository;

    public int loadPeopleFromCsv(boolean force) {
        log.info("Starting to load people data from CSV. Force reload: {}", force);
        
        if (force) {
            log.info("Force reload requested. Deleting all existing people data.");
            deleteAllPeople();
        }

        Set<String> existingNames = new HashSet<>();
        if (!force) {
            existingNames.addAll(personRepository.findAllFullNames());
            log.info("Found {} existing people in database", existingNames.size());
        }

        int loadedCount = 0;
        int skippedCount = 0;
        int errorCount = 0;
        Set<String> errorNames = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/data/people.csv")))) {
            
            // Skip header
            String line = reader.readLine();
            if (line == null) {
                log.warn("CSV file is empty");
                return 0;
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 7) {
                    log.warn("Skipping invalid line: {}", line);
                    continue;
                }

                String fullName = parts[0].trim();
                if (!force && existingNames.contains(fullName)) {
                    log.debug("Skipping duplicate name: {}", fullName);
                    skippedCount++;
                    continue;
                }

                try {
                    savePerson(fullName, parts);
                    loadedCount++;
                    log.debug("Loaded person: {}", fullName);
                } catch (DataIntegrityViolationException e) {
                    errorCount++;
                    errorNames.add(fullName);
                    log.warn("Failed to load person {} due to unique constraint violation", fullName);
                } catch (Exception e) {
                    errorCount++;
                    errorNames.add(fullName);
                    log.error("Failed to load person {}: {}", fullName, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error loading people data", e);
            throw new RuntimeException("Failed to load people data: " + e.getMessage(), e);
        }

        log.info("Data loading completed. Loaded: {}, Skipped: {}, Errors: {}", loadedCount, skippedCount, errorCount);
        if (!errorNames.isEmpty()) {
            log.warn("Failed to load {} people due to errors: {}", errorCount, errorNames);
        }
        return loadedCount;
    }

    @Transactional
    protected void deleteAllPeople() {
        personRepository.deleteAll();
    }

    @Transactional
    protected void savePerson(String fullName, String[] parts) {
        Person person = new Person();
        person.setFullName(fullName);
        person.setKnownAs(parts[1].trim());
        person.setBirthYear(parseYear(parts[2].trim()));
        person.setDeathYear(parseYear(parts[3].trim()));
        person.setNationality(parts[4].trim());
        person.setDescription(parts[5].trim());
        person.setCategory(parts[6].trim());
        personRepository.save(person);
    }

    @Transactional
    public void loadCategoriesFromCsv() {
        try {
            // Get existing categories
            Set<String> existingCategories = categoryRepository.findAll().stream()
                    .map(Category::getName)
                    .collect(Collectors.toSet());

            ClassPathResource resource = new ClassPathResource("data/category.csv");
            try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
                // Skip header
                reader.readNext();
                
                String[] line;
                List<Category> newCategories = new ArrayList<>();
                Set<String> duplicateCategories = new HashSet<>();
                
                while ((line = reader.readNext()) != null) {
                    String name = line[0];
                    
                    // Skip if the category already exists
                    if (existingCategories.contains(name)) {
                        duplicateCategories.add(name);
                        continue;
                    }
                    
                    Category category = new Category();
                    category.setName(name);
                    category.setDescription(line[1]);
                    newCategories.add(category);
                    
                    // Add to existing categories to prevent duplicates within the CSV
                    existingCategories.add(name);
                }
                
                if (!duplicateCategories.isEmpty()) {
                    log.warn("Skipped {} duplicate categories: {}", duplicateCategories.size(), duplicateCategories);
                }
                
                categoryRepository.saveAll(newCategories);
                log.info("Successfully loaded {} new categories from CSV", newCategories.size());
            }
        } catch (IOException | CsvValidationException e) {
            log.error("Error loading categories from CSV", e);
            throw new RuntimeException("Failed to load categories from CSV", e);
        }
    }

    private Integer parseYear(String yearStr) {
        if (yearStr == null || yearStr.trim().isEmpty() || yearStr.equals("null")) {
            return null;
        }
        try {
            return Integer.parseInt(yearStr.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid year format: {}", yearStr);
            return null;
        }
    }
} 