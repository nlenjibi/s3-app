package com.example.bem13.controller;

import com.example.bem13.model.Quote;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
public class QuoteController {

    private static final List<Quote> QUOTES = List.of(
        new Quote(1,  "Code is like humor. When you have to explain it, it's bad.", "Cory House", "Engineering"),
        new Quote(2,  "First, solve the problem. Then, write the code.", "John Johnson", "Engineering"),
        new Quote(3,  "Experience is the name everyone gives to their mistakes.", "Oscar Wilde", "Wisdom"),
        new Quote(4,  "Any fool can write code a computer understands. Good programmers write code humans understand.", "Martin Fowler", "Engineering"),
        new Quote(5,  "Simplicity is the soul of efficiency.", "Austin Freeman", "Engineering"),
        new Quote(6,  "Infrastructure as code means your infrastructure is as buggy as your code.", "Unknown", "Cloud & DevOps"),
        new Quote(7,  "You build it, you run it.", "Werner Vogels", "Cloud & DevOps"),
        new Quote(8,  "Automate everything. If it's not automated, it's manual. If it's manual, it's a risk.", "Gene Kim", "Cloud & DevOps"),
        new Quote(9,  "The best way to predict the future is to implement it.", "David Heinemeier Hansson", "Engineering"),
        new Quote(10, "Move fast and fix things. Or fix things before you move fast.", "Unknown", "Cloud & DevOps")
    );

    private final Random random = new Random();

    @GetMapping("/api/quotes")
    public List<Quote> getQuotes() {
        return QUOTES;
    }

    @GetMapping("/api/quotes/random")
    public Quote randomQuote() {
        return QUOTES.get(random.nextInt(QUOTES.size()));
    }

    @GetMapping("/api/quotes/{id}")
    public ResponseEntity<Quote> getQuoteById(@PathVariable int id) {
        return QUOTES.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
