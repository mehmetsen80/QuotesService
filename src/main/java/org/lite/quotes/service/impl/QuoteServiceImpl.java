package org.lite.quotes.service.impl;

import java.util.List;

import org.lite.quotes.entity.Person;
import org.lite.quotes.entity.Quote;
import org.lite.quotes.repository.PersonRepository;
import org.lite.quotes.repository.QuoteRepository;
import org.lite.quotes.service.QuoteService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuoteServiceImpl implements QuoteService{

    private final PersonRepository personRepository;
    private final QuoteRepository quoteRepository;

    @Transactional
    @Override
    public Quote saveQuote(Quote quote) {
        return quoteRepository.save(quote);
    }

    @Override
    public List<Quote> getQuotesByPersonId(Long personId) {
        return quoteRepository.findByPersonId(personId);
    }

    @Override
    public List<Quote> searchQuotes(String query) {
        return quoteRepository.findByQuoteTextContainingIgnoreCase(query);
    }

    @Override
    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }
    
}
