package org.lite.quotes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.lite.quotes.entity.Person;
import org.lite.quotes.model.ErrorResponse;
import org.lite.quotes.model.PersonResponse;
import org.lite.quotes.repository.PersonRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@Slf4j
@RequestMapping("/api/people")
@RequiredArgsConstructor
@Tag(name = "People", description = "APIs for managing people information")
public class PeopleController {
    
    private final PersonRepository personRepository;
    private final Random random = new Random();

    @Operation(summary = "Get random person", description = "Returns a random person's information from the database")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved random person",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PersonResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "fullName": "Albert Einstein",
                      "knownAs": "The Relativity Genius",
                      "birthYear": 1879,
                      "deathYear": 1955,
                      "nationality": "German",
                      "description": "German physicist known for his scientific and philosophical quotes",
                      "category": "Scientists"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No people found in database",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "message": "No people found in the database",
                      "code": "NO_PEOPLE_FOUND",
                      "timestamp": "2024-03-19T10:30:22.123",
                      "path": "/api/people/random"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "message": "An unexpected error occurred while retrieving random person",
                      "code": "INTERNAL_SERVER_ERROR",
                      "timestamp": "2024-03-19T10:30:22.123",
                      "path": "/api/people/random"
                    }
                    """
                )
            )
        )
    })
    @GetMapping(value = "/random", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponse> getRandomPerson() {
        List<Person> allPeople = personRepository.findAll();
        
        if (allPeople.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Person randomPerson = allPeople.get(random.nextInt(allPeople.size()));
        PersonResponse response = new PersonResponse(
            randomPerson.getFullName(),
            randomPerson.getKnownAs(),
            randomPerson.getBirthYear(),
            randomPerson.getDeathYear(),
            randomPerson.getNationality(),
            randomPerson.getDescription(),
            randomPerson.getCategory()
        );
        
        return ResponseEntity.ok(response);
    }
} 