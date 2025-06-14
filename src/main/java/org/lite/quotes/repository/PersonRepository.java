package org.lite.quotes.repository;

import org.lite.quotes.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByFullNameContaining(String fullName);

    @Query("SELECT p.fullName FROM Person p")
    List<String> findAllFullNames();
}
