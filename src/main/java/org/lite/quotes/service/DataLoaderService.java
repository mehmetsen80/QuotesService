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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void loadPeopleFromCsv(boolean force) {
        try {
            if (force) {
                log.info("Force reload requested. Deleting all existing people data.");
                personRepository.deleteAll();
            }

            // Get existing people's full names
            Set<String> existingFullNames = personRepository.findAll().stream()
                    .map(Person::getFullName)
                    .collect(Collectors.toSet());

            ClassPathResource resource = new ClassPathResource("data/people.csv");
            try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
                // Skip header
                reader.readNext();
                
                String[] line;
                List<Person> newPeople = new ArrayList<>();
                Set<String> duplicateNames = new HashSet<>();
                
                while ((line = reader.readNext()) != null) {
                    String fullName = line[0];
                    
                    // Skip if the name already exists and we're not forcing reload
                    if (!force && existingFullNames.contains(fullName)) {
                        duplicateNames.add(fullName);
                        continue;
                    }
                    
                    Person person = new Person();
                    person.setFullName(fullName);
                    person.setKnownAs(line[1]);
                    person.setBirthYear(parseYear(line[2]));
                    person.setDeathYear(parseYear(line[3]));
                    person.setNationality(line[4]);
                    person.setDescription(line[5]);
                    person.setCategory(line[6]);
                    newPeople.add(person);
                    
                    // Add to existing names to prevent duplicates within the CSV
                    existingFullNames.add(fullName);
                }
                
                if (!duplicateNames.isEmpty()) {
                    log.warn("Skipped {} duplicate names: {}", duplicateNames.size(), duplicateNames);
                }
                
                personRepository.saveAll(newPeople);
                log.info("Successfully loaded {} new people from CSV", newPeople.size());
            }
        } catch (IOException | CsvValidationException e) {
            log.error("Error loading people from CSV", e);
            throw new RuntimeException("Failed to load people from CSV", e);
        }
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
        if (yearStr == null || yearStr.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
} 