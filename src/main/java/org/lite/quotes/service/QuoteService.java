package org.lite.quotes.service;

import java.util.List;

import org.lite.quotes.entity.Person;
import org.lite.quotes.entity.Quote;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public interface QuoteService {
    
    @Transactional
    Quote saveQuote(Quote quote);
    List<Person> getAllPeople() ;
    List<Quote> getQuotesByPersonId(Long personId);
    List<Quote> searchQuotes(String query);
}
