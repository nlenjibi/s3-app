package com.example.bem13.model;

public class Quote {

    private final Integer id;
    private final String text;
    private final String author;
    private final String category;

    public Quote(Integer id, String text, String author, String category) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.category = category;
    }

    public Integer getId() { return id; }
    public String getText() { return text; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
}
