package org.lite.quotes.repository;

import org.lite.quotes.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByPersonId(Long personId);
    List<Quote> findByQuoteTextContainingIgnoreCase(String query);
}
